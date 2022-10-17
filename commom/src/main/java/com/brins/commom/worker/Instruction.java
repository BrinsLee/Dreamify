package com.brins.commom.worker;

import android.os.Bundle;
import android.os.Message;

/**
 * Created by buronehuang on 2019/3/29.
 */

public class Instruction {

    public int what;

    public int arg1;

    public int arg2;

    public Object obj;


    /*package*/ static final int FLAG_IN_USE = 1 << 0;

    /*package*/ int flags;

    /*package*/ long when;

    /*package*/ Bundle data;

    /*package*/ WorkScheduler target;

    /*package*/ Runnable callback;

    /*package*/ Instruction next;


    private static final Object sPoolSync = new Object();
    private static Instruction sPool; // kg-suppress AST.SINGLETON not singleton
    private static int sPoolSize = 0;
    private static final int MAX_POOL_SIZE = 50;
    private static boolean gCheckRecycle = true;

    public static Instruction obtain() {
        synchronized (sPoolSync) {
            if (sPool != null) {
                Instruction m = sPool;
                sPool = m.next;
                m.next = null;
                m.flags = 0; // clear in-use flag
                sPoolSize--;
                return m;
            }
        }
        return new Instruction();
    }

    public static Instruction obtain(Instruction orig) {
        Instruction m = obtain();
        m.what = orig.what;
        m.arg1 = orig.arg1;
        m.arg2 = orig.arg2;
        m.obj = orig.obj;
        if (orig.data != null) {
            m.data = new Bundle(orig.data);
        }
        m.target = orig.target;
        m.callback = orig.callback;
        return m;
    }

    public static Instruction obtain(WorkScheduler h) {
        Instruction m = obtain();
        m.target = h;
        return m;
    }

    public static Instruction obtain(WorkScheduler h, Runnable callback) {
        Instruction m = obtain();
        m.target = h;
        m.callback = callback;
        return m;
    }

    public static Instruction obtain(WorkScheduler h, int what) {
        Instruction m = obtain();
        m.target = h;
        m.what = what;
        return m;
    }

    public static Instruction obtain(WorkScheduler h, int what, Object obj) {
        Instruction m = obtain();
        m.target = h;
        m.what = what;
        m.obj = obj;
        return m;
    }

    public static Instruction obtain(WorkScheduler h, int what, int arg1, int arg2) {
        Instruction m = obtain();
        m.target = h;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        return m;
    }

    public static Instruction obtain(WorkScheduler h, int what,
                                     int arg1, int arg2, Object obj) {
        Instruction m = obtain();
        m.target = h;
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.obj = obj;
        return m;
    }

    public Message toMessage() {
        Message m = new Message();
        m.setTarget(null);
        m.what = what;
        m.arg1 = arg1;
        m.arg2 = arg2;
        m.obj = obj;
        return m;
    }

    /**
     * Return a instruction instance to the global pool.
     * <p>
     * You MUST NOT touch the instruction after calling this function because it has
     * effectively been freed.  It is an error to recycle a instruction that is currently
     * enqueued or that is in the process of being delivered to a Scheduler.
     * </p>
     */
    public void recycle() {
        if (isInUse()) {
            if (gCheckRecycle) {
                throw new IllegalStateException("This instruction cannot "
                        + "be recycled because it is still in use.");
            }
            return;
        }
        recycleUnchecked();
    }

    /**
     * Recycles a instruction that may be in-use.
     * Used internally by the InstructionQueue and WorkScheduler
     * when disposing of queued instruction.
     */
    void recycleUnchecked() {
        // Mark the message as in use while it remains in the recycled object pool.
        // Clear out all other details.
        flags = FLAG_IN_USE;
        what = 0;
        arg1 = 0;
        arg2 = 0;
        obj = null;
        when = 0;
        target = null;
        callback = null;
        data = null;

        synchronized (sPoolSync) {
            if (sPoolSize < MAX_POOL_SIZE) {
                next = sPool;
                sPool = this;
                sPoolSize++;
            }
        }
    }

    boolean isInUse() {
        return ((flags & FLAG_IN_USE) == FLAG_IN_USE);
    }

    void markInUse() {
        flags |= FLAG_IN_USE;
    }

    /**
     * Return the targeted delivery time of this message, in milliseconds.
     */
    public long getWhen() {
        return when;
    }

    public void setTarget(WorkScheduler target) {
        this.target = target;
    }

    public WorkScheduler getTarget() {
        return target;
    }

    public Runnable getCallback() {
        return callback;
    }

    /**
     * Obtains a Bundle of arbitrary data associated with this
     * event, lazily creating it if necessary. Set this value by calling
     * {@link #setData(Bundle)}.
     * @see #peekData()
     * @see #setData(Bundle)
     */
    public Bundle getData() {
        if (data == null) {
            data = new Bundle();
        }

        return data;
    }

    /**
     * Like getData(), but does not lazily create the Bundle.  A null
     * is returned if the Bundle does not already exist.  See
     * {@link #getData} for further information on this.
     * @see #getData()
     * @see #setData(Bundle)
     */
    public Bundle peekData() {
        return data;
    }

    /**
     * Sets a Bundle of arbitrary data values. Use arg1 and arg2 members
     * as a lower cost way to send a few simple integer values, if you can.
     * @see #getData()
     * @see #peekData()
     */
    public void setData(Bundle data) {
        this.data = data;
    }

    /**
     * Sends this Instruction to the Scheduler specified by {@link #getTarget}.
     * Throws a null pointer exception if this field has not been set.
     */
    public void sendToTarget() {
        target.sendInstruction(this);
    }

}
