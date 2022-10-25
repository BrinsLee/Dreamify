package com.brins.commom.dialog;

import android.app.Dialog;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created by buronehuang on 2019/1/17.
 */

public class DialogRegulator {

    private static class Holder {
        static final DialogRegulator INSTANCE = new DialogRegulator();
    }

    public static DialogRegulator get() {
        return Holder.INSTANCE;
    }

    private static final String TAG = DialogRegulator.class.getSimpleName();

    private final Handler scheduler = new Handler(Looper.getMainLooper());

    private final Delayer delayer = new Delayer();

    private final Object orderLock = new Object();

    private LinkedList<DocileMember> orderQueue = new LinkedList<>();

    public void join(@NonNull DocileMember member) {
        float authority = member.getAuthority();
        if (authority >= DialogAuthority.ORDER) {
            enqueueOrder(member);
        } else {
            specialExecute(member);
        }
    }

    public int getOrderQueueSize() {
        return orderQueue.size();
    }

    /**
     * 代码作者老司机
     * 我只是代码的搬运工
     * @param member
     */
    private void specialExecute(DocileMember member) {
        float authority = member.getAuthority();

        if (authority == DialogAuthority.STORMY) {
            member.display();

        } else if (authority == DialogAuthority.LAZY) {
            // it's not much necessary to hold its lock
            if (orderQueue.size() <= 0) {
                enqueueOrder(member);
            }
        }
    }

    private void enqueueOrder(DocileMember member) {
        synchronized (orderLock) {
            // one member, one slot
            if (orderQueue.contains(member)) {
                log("already in queue, return.");
                return;
            }
            member.setStopMonitor(stopMonitor);
            orderQueue.add(member);
            if (orderQueue.size() > 1) {
                // someone arrive here before me, it should be
                // showing now, waiting for it's STOP action
                log("member enqueueOrder and appended.");
                reorderLocked();
            } else {
                // here should be none showing obj, show me
                log("member enqueueOrder and " +
                        "being the heard, schedule now.");
                scheduleNextToDisplay(member.isNeedDelayShow() ? delayer.getHeadDelay() : 0);
            }
        }
    }

    private void reorderLocked() {
        int orderSize = orderQueue.size();
        DocileMember head = orderQueue.peek();
        DocileMember ordering = orderQueue.peekLast();
        Iterator<DocileMember> iterator = orderQueue.iterator();
        int insertIndex;
        if (head.isDisplaying()) {
            if (orderSize < 3) {
                return;
            }
            iterator.next(); // ignore the heard
            insertIndex = 0;
        } else {
            // head is not displaying, so resorts the hold queue
            insertIndex = -1;
        }
        while (iterator.hasNext()) {
            ++insertIndex;
            if (ordering.compareTo(iterator.next()) > 0) {
                break;
            }
        }
        if (insertIndex < orderSize - 1) {
            orderQueue.add(insertIndex, orderQueue.pollLast());
        }

        log("after reorder, queue = " +
                Arrays.deepToString(orderQueue.toArray()));
    }

    private DocileMember.StopMonitor stopMonitor = new DocileMember.StopMonitor() {
        @Override
        public void onStop(DocileMember member) {
            dequeueOrder(member);
        }
    };

    private void dequeueOrder(DocileMember member) {
        synchronized (orderLock) {
            DocileMember heard = orderQueue.peek();
            if (heard == null || heard != member) {
                // only the in-queue and heard member
                // can perform the following action
                log("stopping member is NOT the" +
                        " heard of the order queue !!!");
                return;
            }
            member.setStopMonitor(null);
            orderQueue.poll();
            scheduleNextToDisplay(delayer.getFollowDelay());
        }
    }

    private void scheduleNextToDisplay(long delay) {
        log("delay = " + delay);
        if (delay <= 0) {
            performNextDisplay();
        } else {
            scheduler.postDelayed(displayNextTask, delay);
        }
    }

    private Runnable displayNextTask = new Runnable() {
        @Override
        public void run() {
            performNextDisplay();
        }
    };

    private void performNextDisplay() {
        synchronized (orderLock) {
            DocileMember member = orderQueue.peek();
            if (member != null) {
                member.display();
            }
        }
    }

    public void removeSubsequents(DocileMember member) {
        synchronized (orderLock) {
            int size = orderQueue.size();
            int pos = orderQueue.indexOf(member);
            if (pos >= 0 && pos < size - 1) {
                int behindSize = size - 1 - pos;
                for (int i = 0; i < behindSize; i++) {
                    orderQueue.pollLast();
                }
            }
        }
    }

    public boolean removeSelf(DocileMember member) {
        synchronized (orderLock) {
            if (orderQueue.contains(member) && !member.isDisplaying()) {
                member.setStopMonitor(null);
                return orderQueue.remove(member);
            }
            return false;
        }
    }


    private static void log(String msg) {
        Log.d(TAG, msg);
    }


    /**
     * a boy who decides the delay duration
     */
    static class Delayer {

        private static final int DELAY_NORMAL = 200;

        private static final int DELAY_BOOT = 1200;

        long getHeadDelay() {
            /*long start = IBootMagicBox.get().getBootCompleteTime();
            if (start > 0) {
                long pass = SystemClock.elapsedRealtime() - start;
                if (pass >= 0) {
                    long stage = DELAY_BOOT - pass;
                    if (stage > 0) {
                        return stage;
                    }
                }
            }*/
            return DELAY_NORMAL;
        }

        long getFollowDelay() {
            return 0;
        }
    }

    /**
     * member of the authority queue, wrapper of dialog
     */
    static class DocileMember implements Comparable {

        @Override
        public int compareTo(@NonNull Object another) {
            float num = this.authority - ((DocileMember) another).authority;
            if (num > 0) {
                return 1;
            } else if (num < 0) {
                return -1;
            }
            return 0;
        }

        interface StopMonitor {
            void onStop(DocileMember member);
        }

        private final Dialog target;
        private float authority;
        private StopMonitor listener;

        private boolean needDelayShow = true;

        DocileMember(Dialog dialog) {
            this.target = dialog;
        }

        float getAuthority() {
            return authority;
        }

        void setStopMonitor(StopMonitor listener) {
            this.listener = listener;
        }

        void display() {
            isShowDialog = true;
            target.show();
            mHasShow = true;
        }

        public boolean isNeedDelayShow() {
            return needDelayShow;
        }

        public void setNeedDelayShow(boolean needDelayShow) {
            this.needDelayShow = needDelayShow;
        }

        boolean isDisplaying() {
            return target.isShowing();
        }

        void pushToShow(float authority) {
            this.authority = authority;
            DialogRegulator.get().join(DocileMember.this);
        }

        void notifyStop() {
            isShowDialog = false;
            if (listener != null) {
                listener.onStop(DocileMember.this);
            }
        }

        void clearSubsequents() {
            DialogRegulator.get().removeSubsequents(DocileMember.this);
        }

        boolean cancelShow(){
            return DialogRegulator.get().removeSelf(DocileMember.this);
        }

        boolean canShowNow() {
            return DialogRegulator.get().getOrderQueueSize() <= 0;
        }

        @Override
        public String toString() {
            return "Member-" + authority;
        }
    }

    private static boolean mHasShow = false;// 业务控制弹窗数量

    public static boolean isHasShow() {
        return mHasShow;
    }

    private static boolean isShowDialog = false;// 当前是否有dialog再显示

    public boolean isShowDialog() {
        return isShowDialog;
    }

}
