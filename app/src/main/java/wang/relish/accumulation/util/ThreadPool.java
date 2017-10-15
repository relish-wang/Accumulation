package wang.relish.accumulation.util;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池
 *
 * @author Relish Wang
 * @since 2017/08/02
 */
public enum ThreadPool {

    DATABASE(1, "Database Task", false);    // db IO，必须为单线程线程池


    private final ThreadPoolExecutor mExecutor;

    ThreadPool(final int threadCount, final String name, final boolean isStack) {
        BlockingQueue<Runnable> queue;
        if (isStack) {
            queue = new LinkedBlockingStack<>();
        } else {
            queue = new LinkedBlockingQueue<>();
        }

        mExecutor = new ThreadPoolExecutor(
                threadCount,
                threadCount,
                30,
                TimeUnit.SECONDS,
                queue,
                new ThreadFactory() {
                    @Override
                    public Thread newThread(@NonNull Runnable r) {
                        return new Thread(r, name);
                    }
                });

        mExecutor.allowCoreThreadTimeOut(true);
    }

    /* package */ ExecutorService getExecutorService() {
        return mExecutor;
    }

    public final Future<?> execute(Runnable runnable) {
        return mExecutor.submit(new RunnableTask(runnable));
    }


    private static final class RunnableTask implements Runnable {

        private static final Handler HANDLER = new Handler(Looper.getMainLooper());

        private final Runnable mRunnable;

        private RunnableTask(Runnable runnable) {
            if (runnable == null) throw new IllegalArgumentException("runnable is null!");
            mRunnable = runnable;
        }

        @Override
        public void run() {
            try {
                mRunnable.run();
            } catch (final Throwable t) {

                throw new RuntimeException("Thread throws exception!!!", t);
            }
        }
    }

    private static final class LinkedBlockingStack<T> extends LinkedBlockingDeque<T> {
        @Override
        public boolean add(T t) {
            super.addFirst(t);
            return true;
        }

        @Override
        public boolean offer(T t) {
            return super.offerFirst(t);
        }

        @Override
        public void put(T t) throws InterruptedException {
            super.putFirst(t);
        }

        @Override
        public boolean offer(T t, long timeout, TimeUnit unit) throws InterruptedException {
            return super.offerFirst(t, timeout, unit);
        }
    }
}