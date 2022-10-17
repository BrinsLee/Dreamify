package com.brins.commom.worker;

import android.os.Process;
import com.brins.commom.utils.KGThreadPool;


/**
 * Created by buronehuang on 2019/3/29.
 */

final class ScheduleManager {

    private static class Holder {
        static final ScheduleManager INSTANCE = new ScheduleManager();
    }

    static ScheduleManager get() {
        return Holder.INSTANCE;
    }


    final InstructionQueue mQueue;

    private ScheduleManager() {
        mQueue = new InstructionQueue();
        new Thread(new Runnable() { // kg-suppress REGULAR.THREAD loop thread
            @Override
            public void run() {
                loop();
            }
        }, "ScheduleManager").start();
    }

    private void loop() {
        // give a higher priority to loop instructions
        try {
            Process.setThreadPriority(Process.THREAD_PRIORITY_URGENT_DISPLAY);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final InstructionQueue queue = mQueue;
        for (;;) {
            Instruction inst = queue.next();
            if (inst == null) {
                break;
            }
            execute(ExecuteTask.obtain(queue, inst));
        }
        throw new RuntimeException("Should not happened !!!");
    }

    private void execute(Runnable task) {
        KGThreadPool.getInstance().execute(task);
    }

    private static class ExecuteTask implements Runnable {

        private InstructionQueue queue;
        private Instruction instruction;

        private ExecuteTask next;
        private static ExecuteTask sTaskPool;
        private static int sPoolSize = 0;
        private static final int MAX_POOL_SIZE = 30;

        /**
         * 若对该字段有疑问，请参考以下要点：
         * 1.根据Happens-Before的程序次序原则分析同一线程内的先行发生关系
         * 2.根据Happens-Before的volatile约定分析不同线程间的先行发生关系
         * 3.根据Happens-Before的传递性进行分析
         */
        private static volatile boolean safeguard;

        static ExecuteTask obtain(InstructionQueue q, Instruction inst) {
            synchronized (ExecuteTask.class) {
                ExecuteTask task;
                if (sTaskPool != null) {
                    task = sTaskPool;
                    sTaskPool = task.next;
                    task.next = null;
                    sPoolSize--;
                } else {
                    task = new ExecuteTask();
                }
                task.queue = q;
                task.instruction = inst;
                return task;
            }
        }

        void recycle() {
            queue = null;
            instruction = null;
            synchronized (ExecuteTask.class) {
                if (sPoolSize < MAX_POOL_SIZE) {
                    next = sTaskPool;
                    sTaskPool = this;
                    sPoolSize++;
                }
            }
        }

        private void changeThreadPriority(int p) {
            try {
                Process.setThreadPriority(p);
            } catch (Exception ignore) {
                // ignore this exception
            }
        }

        @Override
        public void run() {
            changeThreadPriority(instruction.target.mPriority);

            boolean read = safeguard;
            instruction.target.dispatchInstruction(instruction);
            safeguard = !read;

            queue.notifyInstructionFinish(instruction);

            recycle();
            // reset the priority. threads from KGThreadPool hold
            // a Process.THREAD_PRIORITY_DEFAULT priority (up to now)
            changeThreadPriority(Process.THREAD_PRIORITY_DEFAULT);
        }
    }

}
