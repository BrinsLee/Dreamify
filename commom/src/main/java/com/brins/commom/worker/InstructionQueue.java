package com.brins.commom.worker;

import android.os.SystemClock;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by buronehuang on 2019/3/29.
 */

public class InstructionQueue {

    private final Object mWaiter = new Object();

    private boolean mBlocked;

    private boolean mWakeCalled;

    private Instruction mInstructions;

    private List<Instruction> mPending;

    InstructionQueue() {

    }

    private void wakeup() {
        synchronized (mWaiter) {
            mWakeCalled = true;
            mWaiter.notify();
        }
    }

    private void waitingTimeUp(int mills) {
        if (mills == 0) return;
        synchronized (mWaiter) {
            try {
                if (!mWakeCalled) {
                    if (mills < 0) {
                        mWaiter.wait();
                    } else {
                        mWaiter.wait(mills);
                    }
                }
                mWakeCalled = false;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean tryOccupyTargetLocked(Instruction inst) {
        return inst.target.tryOccupy();
    }

    private void freeTargetLocked(Instruction inst) {
        inst.target.free();
    }

    void notifyInstructionFinish(Instruction inst) {
        synchronized (this) {
            freeTargetLocked(inst);
            inst.recycleUnchecked();
            boolean needWake = mBlocked
                    && mPending != null
                    && mPending.size() > 0;
            if (needWake) {
                wakeup();
            }
        }
    }

    Instruction next() {
        int nextPollTimeoutMillis = 0;
        for (;;) {
            waitingTimeUp(nextPollTimeoutMillis);

            synchronized (this) {
                // firstly schedule the pending instruction, if appropriate.
                if (mPending != null && mPending.size() > 0) {
                    Iterator<Instruction> it = mPending.iterator();
                    Instruction pe;
                    while (it.hasNext()) {
                        pe = it.next();
                        if (tryOccupyTargetLocked(pe)) {
                            it.remove();
                            mBlocked = false;
                            return pe;
                        }
                    }
                }

                final long now = SystemClock.uptimeMillis();
                Instruction inst = mInstructions;

                if (inst != null) {
                    long gap = inst.when - now;
                    if (gap > 0) {
                        // next instruction not times up, will block
                        nextPollTimeoutMillis = (int) Math.min(gap, Integer.MAX_VALUE);
                    } else {
                        mInstructions = inst.next;
                        inst.next = null;
                        inst.markInUse(); // 没必要？
                        mBlocked = false;
                        if (tryOccupyTargetLocked(inst)) {
                            // occupy success, return and schedule the instruction
                            return inst;
                        } else {
                            // occupy fail, the target is scheduling another instruction.
                            // collect it on pending queue.
                            if (mPending == null) {
                                mPending = new ArrayList<>();
                            }
                            mPending.add(inst);
                            // consider there may be some other instructions (who's time is up) in
                            // the queue, we still need to continue the next looking. so don't wait.
                            nextPollTimeoutMillis = 0;
                            continue;
                        }
                    }
                } else {
                    // no more instructions, wait until being notified.
                    nextPollTimeoutMillis = -1;
                }
                mBlocked = true;
            }
        }
    }

    boolean enqueue(Instruction inst, long when) {
        if (inst.target == null) {
            throw new IllegalArgumentException("Instruction must have a target.");
        }
        if (inst.isInUse()) {
            throw new IllegalStateException(inst + " This instruction is already in use.");
        }
        synchronized (this) {
            inst.markInUse();
            inst.when = when;
            Instruction p = mInstructions;
            boolean needWake = false;
            if (p == null || when == 0 || when < p.when) {
                // New head, wake up the event queue if blocked.
                inst.next = p;
                mInstructions = inst;
                needWake = mBlocked;
            } else {
                Instruction prev;
                for (;;) {
                    prev = p;
                    p = p.next;
                    if (p == null || when < p.when) {
                        break;
                    }
                }
                inst.next = p;
                prev.next = inst;
            }
            if (needWake) {
                wakeup();
            }
        }
        return true;
    }

    boolean hasInstructions(WorkScheduler ws, int what, Object object) {
        if (ws == null) {
            return false;
        }
        synchronized (this) {
            Instruction p = mInstructions;
            while (p != null) {
                if (p.target == ws && p.what == what && (object == null || p.obj == object)) {
                    return true;
                }
                p = p.next;
            }
            if (mPending != null && mPending.size() > 0) {
                Iterator<Instruction> it = mPending.iterator();
                while (it.hasNext()) {
                    p = it.next();
                    if (p.target == ws && p.what == what && (object == null || p.obj == object)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    boolean hasInstructions(WorkScheduler ws, Runnable r, Object object) {
        if (ws == null) {
            return false;
        }
        synchronized (this) {
            Instruction p = mInstructions;
            while (p != null) {
                if (p.target == ws && p.callback == r && (object == null || p.obj == object)) {
                    return true;
                }
                p = p.next;
            }
            if (mPending != null && mPending.size() > 0) {
                Iterator<Instruction> it = mPending.iterator();
                while (it.hasNext()) {
                    p = it.next();
                    if (p.target == ws && p.callback == r && (object == null || p.obj == object)) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    boolean hasInstructions(WorkScheduler ws) {
        if (ws == null) {
            return false;
        }
        synchronized (this) {
            Instruction p = mInstructions;
            while (p != null) {
                if (p.target == ws) {
                    return true;
                }
                p = p.next;
            }
            if (mPending != null && mPending.size() > 0) {
                Iterator<Instruction> it = mPending.iterator();
                while (it.hasNext()) {
                    p = it.next();
                    if (p.target == ws) {
                        return true;
                    }
                }
            }
            return false;
        }
    }

    void removeInstructions(WorkScheduler ws, int what, Object object) {
        if (ws == null) {
            return;
        }
        synchronized (this) {
            Instruction p = mInstructions;

            // Remove all instructions at front.
            while (p != null && p.target == ws && p.what == what
                    && (object == null || p.obj == object)) {
                Instruction n = p.next;
                mInstructions = n;
                p.recycleUnchecked();
                p = n;
            }

            // Remove all instructions after front.
            while (p != null) {
                Instruction n = p.next;
                if (n != null) {
                    if (n.target == ws && n.what == what
                            && (object == null || n.obj == object)) {
                        Instruction nn = n.next;
                        n.recycleUnchecked();
                        p.next = nn;
                        continue;
                    }
                }
                p = n;
            }

            // Remove all instructions from pending
            if (mPending != null && mPending.size() > 0) {
                Iterator<Instruction> it = mPending.iterator();
                while (it.hasNext()) {
                    Instruction in = it.next();
                    if (in.target == ws && in.what == what
                            && (object == null || in.obj == object)) {
                        in.recycleUnchecked();
                        it.remove();
                    }
                }
            }
        }
    }

    void removeInstructions(WorkScheduler ws, Runnable r, Object object) {
        if (ws == null || r == null) {
            return;
        }
        synchronized (this) {
            Instruction p = mInstructions;

            // Remove all messages at front.
            while (p != null && p.target == ws && p.callback == r
                    && (object == null || p.obj == object)) {
                Instruction n = p.next;
                mInstructions = n;
                p.recycleUnchecked();
                p = n;
            }

            // Remove all messages after front.
            while (p != null) {
                Instruction n = p.next;
                if (n != null) {
                    if (n.target == ws && n.callback == r
                            && (object == null || n.obj == object)) {
                        Instruction nn = n.next;
                        n.recycleUnchecked();
                        p.next = nn;
                        continue;
                    }
                }
                p = n;
            }

            // Remove all instructions from pending
            if (mPending != null && mPending.size() > 0) {
                Iterator<Instruction> it = mPending.iterator();
                while (it.hasNext()) {
                    Instruction in = it.next();
                    if (in.target == ws && in.callback == r
                            && (object == null || in.obj == object)) {
                        in.recycleUnchecked();
                        it.remove();
                    }
                }
            }
        }
    }

    void removeCallbacksAndInstructions(WorkScheduler ws, Object object) {
        if (ws == null) {
            return;
        }
        synchronized (this) {
            Instruction p = mInstructions;

            // Remove all messages at front.
            while (p != null && p.target == ws
                    && (object == null || p.obj == object)) {
                Instruction n = p.next;
                mInstructions = n;
                p.recycleUnchecked();
                p = n;
            }

            // Remove all messages after front.
            while (p != null) {
                Instruction n = p.next;
                if (n != null) {
                    if (n.target == ws && (object == null || n.obj == object)) {
                        Instruction nn = n.next;
                        n.recycleUnchecked();
                        p.next = nn;
                        continue;
                    }
                }
                p = n;
            }

            // Remove all instructions from pending
            if (mPending != null && mPending.size() > 0) {
                Iterator<Instruction> it = mPending.iterator();
                while (it.hasNext()) {
                    Instruction in = it.next();
                    if (in.target == ws && (object == null || in.obj == object)) {
                        in.recycleUnchecked();
                        it.remove();
                    }
                }
            }
        }
    }




}


