package com.brins.commom.worker;

import android.os.Process;
import android.os.SystemClock;

/**
 * @author lipeilin
 * @date 2022/10/15
 * @desc
 */
public class WorkScheduler {

    public interface Callback {
        boolean handleInstruction(Instruction inst);
    }

    public static final class Serializer {

        private boolean mOccupied;

        final boolean tryOccupy() {
            if (mOccupied) {
                return false;
            }
            mOccupied = true;
            return true;
        }

        final void free() {
            mOccupied = false;
        }

    }

    private final InstructionQueue mQueue;

    private final String mName;

    private final Callback mCallback;

    private final Serializer mSerializer;

    /*package*/ int mPriority;

    public WorkScheduler() {
        this(null, null);
    }

    public WorkScheduler(String name) {
        this(name, null);
    }

    public WorkScheduler(Callback callback) {
        this(null, callback);
    }

    public WorkScheduler(String name, Callback callback) {
        this(name, callback, null);
    }

    public WorkScheduler(String name, Callback callback, Serializer serializer) {
        mQueue = ScheduleManager.get().mQueue;
        mName = name;
        mCallback = callback;
        mSerializer = serializer != null ? serializer : new Serializer();
        mPriority = Process.THREAD_PRIORITY_DEFAULT;
    }

    public void dispatchInstruction(Instruction inst) {
        if (inst.callback != null) {
            inst.callback.run();
        } else {
            if (mCallback == null ||
                !mCallback.handleInstruction(inst)) {
                handleInstruction(inst);
            }
        }
    }

    final boolean tryOccupy() {
        return mSerializer.tryOccupy();
    }

    final void free() {
        mSerializer.free();
    }

    public final Serializer getSerializer() {
        return mSerializer;
    }

    public final void setPriority(int priority) {
        mPriority = priority;
    }

    public void handleInstruction(Instruction inst) {

    }

    public final Instruction obtainInstruction() {
        return Instruction.obtain(this);
    }

    public final Instruction obtainInstruction(int what) {
        return Instruction.obtain(this, what);
    }

    public final Instruction obtainInstruction(int what, Object obj) {
        return Instruction.obtain(this, what, obj);
    }

    public final Instruction obtainInstruction(int what, int arg1, int arg2) {
        return Instruction.obtain(this, what, arg1, arg2);
    }

    public final Instruction obtainInstruction(int what, int arg1, int arg2, Object obj) {
        return Instruction.obtain(this, what, arg1, arg2, obj);
    }

    private static Instruction getPostInstruction(Runnable r) {
        Instruction m = Instruction.obtain();
        m.callback = r;
        return m;
    }

    private static Instruction getPostInstruction(Runnable r, Object token) {
        Instruction m = Instruction.obtain();
        m.callback = r;
        m.obj = token;
        return m;
    }

    public final boolean post(Runnable r) {
        return sendInstructionDelayed(getPostInstruction(r), 0);
    }

    public final boolean postAtTime(Runnable r, long uptimeMillis) {
        return sendInstructionAtTime(getPostInstruction(r), uptimeMillis);
    }

    public final boolean postAtTime(Runnable r, Object token, long uptimeMillis) {
        return sendInstructionAtTime(getPostInstruction(r, token), uptimeMillis);
    }

    public final boolean postDelayed(Runnable r, long delayMillis) {
        return sendInstructionDelayed(getPostInstruction(r), delayMillis);
    }

    public final void removeCallbacks(Runnable r) {
        mQueue.removeInstructions(this, r, null);
    }

    public final void removeCallbacks(Runnable r, Object token) {
        mQueue.removeInstructions(this, r, token);
    }

    public final boolean sendInstruction(Instruction inst) {
        return sendInstructionDelayed(inst, 0);
    }

    public final boolean sendEmptyInstruction(int what) {
        return sendEmptyInstructionDelayed(what, 0);
    }

    public final boolean sendEmptyInstructionDelayed(int what, long delayMillis) {
        Instruction inst = Instruction.obtain();
        inst.what = what;
        return sendInstructionDelayed(inst, delayMillis);
    }

    public final boolean sendEmptyInstructionAtTime(int what, long uptimeMillis) {
        Instruction inst = Instruction.obtain();
        inst.what = what;
        return sendInstructionAtTime(inst, uptimeMillis);
    }

    public final boolean sendInstructionDelayed(Instruction inst, long delayMillis) {
        if (delayMillis < 0) {
            delayMillis = 0;
        }
        return sendInstructionAtTime(inst, SystemClock.uptimeMillis() + delayMillis);
    }

    public final boolean sendInstructionAtFront(Instruction inst) {
        return sendInstructionAtTime(inst, 0);
    }

    public boolean sendInstructionAtTime(Instruction inst, long uptimeMillis) {
        InstructionQueue queue = mQueue;
        if (queue == null) {
            return false;
        }
        return enqueueInstruction(queue, inst, uptimeMillis);
    }

    private boolean enqueueInstruction(InstructionQueue queue, Instruction inst, long uptimeMillis) {
        inst.target = this;
        return queue.enqueue(inst, uptimeMillis);
    }

    public final void removeInstructions(int what) {
        mQueue.removeInstructions(this, what, null);
    }

    public final void removeInstructions(int what, Object object) {
        mQueue.removeInstructions(this, what, object);
    }

    public final void removeCallbacksAndInstructions(Object token) {
        mQueue.removeCallbacksAndInstructions(this, token);
    }

    public final boolean hasInstructions(int what) {
        return mQueue.hasInstructions(this, what, null);
    }

    public final boolean hasInstructions(int what, Object object) {
        return mQueue.hasInstructions(this, what, object);
    }
}
