package com.codingzx.geektimehomework.concurren;

import com.codingzx.geektimehomework.annotinon.MetricTime;
import lombok.SneakyThrows;
import lombok.Synchronized;
import org.springframework.util.StringUtils;

import java.util.concurrent.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.codingzx.geektimehomework.concurren.SimpleUtil.countRunTime;
import static com.codingzx.geektimehomework.concurren.SimpleUtil.sum;

/**
 * @author codingzx
 * @description
 * @date 2021/8/8 16:29
 */
public class Homework {

    private static ExecutorService pool = Executors.newSingleThreadExecutor();


    /**
     * 方式1  DownLatch
     * <p>
     * 申请n个  暂停主线程 ，
     * DownLatch 为 0 时 才继续执行主线程
     */
    public static class DownLatch {
        @MetricTime("DownLatch")
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();

            DownLatch latch = new DownLatch();
            CountDownLatch downLatch = new CountDownLatch(1);
            String result = latch.test(downLatch);
            downLatch.await();
            countRunTime(start, result);
        }

        public String test(CountDownLatch downLatch) throws ExecutionException, InterruptedException {
            downLatch.countDown();
            Future<String> submit = pool.submit(() -> System.out.println("我是CountDownLatch线程" + Thread.currentThread().getName()), sum());
            pool.shutdown();
            return submit.get();
        }
    }

    /**
     * 方式1  Barrier
     * <p>
     * 子线程申请 n个  则 子线程都完成再执行回调函数
     * 不影响主线程
     */
    public static class Barrier {

        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();
            Barrier barrier = new Barrier();
            CyclicBarrier cyclicBarrier = new CyclicBarrier(10, new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    System.out.println("回调>>" + Thread.currentThread().getName());
                    System.out.println("回调>>线程组执行结束");
                    System.out.println("==>各个子线程执行结束。。。。");
                }
            });
            String result = barrier.test(cyclicBarrier);
            countRunTime(start, result);
        }

        public String test(CyclicBarrier cyclicBarrier) throws Exception {
            Future<String> submit = pool.submit(() -> System.out.println("我是Barrier线程" + Thread.currentThread().getName()), sum());
            cyclicBarrier.await();
            pool.shutdown();
            return submit.get();
        }

    }


    /**
     * 方式3 Semaphore
     * <p>
     * 信号量
     * 申请 一个  然后释放一个继续执行驻现场
     *
     * @author codingzx
     */
    public static class MySemaphore {
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();

            MySemaphore mySemaphore = new MySemaphore();
            Semaphore semaphore = new Semaphore(1);
            String result = mySemaphore.test(semaphore);
            semaphore.release();
            countRunTime(start, result);
        }

        public String test(Semaphore semaphore) throws InterruptedException, ExecutionException {
            Future<String> submit = pool.submit(() -> System.err.println("我是Semaphore线程" + Thread.currentThread().getName()), sum());
            // 可以申请多个信号量
            semaphore.acquire();
            pool.shutdown();
            return submit.get();
        }
    }


    /**
     * 方式4
     * LockSupport
     * Object.wait()和LockSupport.park()的区别
     * （1）Object.wait()方法需要在synchronized块中执行；
     * （2）LockSupport.park()可以在任意地方执行；
     * （3）Object.wait()方法声明抛出了中断异常，调用者需要捕获或者再抛出；
     * （4）LockSupport.park()不需要捕获中断异常
     * （5）Object.wait()不带超时的，需要另一个线程执行notify()来唤醒，但不一定继续执行后续内容；
     * （6）LockSupport.park()不带超时的，需要另一个线程执行unpark()来唤醒，一定会继续执行后续内容；
     * （7）如果在wait()之前执行了notify()会怎样？抛出IllegalMonitorStateException异常；
     * （8）如果在park()之前执行了unpark()会怎样？线程不会被阻塞，直接跳过park()，继续执行后续内容；
     * <p>
     * <p>
     * park调用后一定会消耗掉permit，无论unpark操作先做还是后做。 如果中断状态为true，那么park无法阻塞。
     * <p>
     * unpark会使得permit为1，并唤醒处于阻塞的线程。
     * <p>
     * interrupt()会使得中断状态为true，并调用unpark。
     * <p>
     * sleep() / wait() / join()调用后一定会消耗掉中断状态，无论interrupt()操作先做还是后做。
     * <p>
     * 关于这一点，“如果中断状态为true，那么park无法阻塞”。在AQS源码里的acquireQueued里，由于acquireQueued是阻塞式的抢锁，线程可能重复着 阻塞->被唤醒 的过程
     * 所以在这个过程中，如果遇到了中断，一定要用Thread.interrupted()将中断状态消耗掉，并将这个中断状态暂时保存到一个局部变量中去。不然只要遇到中断一次后，线程在抢锁失败后却无法阻塞了
     * <p>
     * <p>
     * 只要permit为1或者中断状态为true，那么执行park就不能够阻塞线程。park只可能消耗掉permit，但不会去消耗掉中断状态。unpark一定会将permit置为1，
     * 如果线程阻塞，再将其唤醒。从实现可见，无论调用几次unpark，permit只能为1。
     *
     * @author codingzx
     */
    public static class MyLockSupport {
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();
            MyLockSupport myLockSupport = new MyLockSupport();
            String result = myLockSupport.test();
            LockSupport.park();
            countRunTime(start, result);
        }

        public String test() throws InterruptedException, ExecutionException {

//            FutureTask<String> futureTask = new FutureTask<>(new Callable<String>() {
//                @Override
//                public String call() throws Exception {
//                    return sum();
//                }
//            });
//
//            new Thread(futureTask).start();
//
//            System.out.println("result is "+ futureTask.get());
//            futureTask.cancel(false);

            Future<String> submit = pool.submit(() -> System.err.println("我是LockSupport线程" + Thread.currentThread().getName()), "test");
            LockSupport.unpark(Thread.currentThread());
            // 主线程 如果先 unpark ，再 执行 park 不会阻塞
            pool.shutdown();
            return submit.get();
        }
    }

    /**
     * 方式5  Lock
     *
     * @author codingzx
     */
    public static class MyLock {
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();

            MyLock myLock = new MyLock();
            Lock lock = new ReentrantLock();
            lock.lock();
            String result;
            try {
                result = myLock.test();
            } finally {
                lock.unlock();
            }
            countRunTime(start, result);
        }

        public String test() throws InterruptedException, ExecutionException {
            Future<String> submit = pool.submit(() -> System.err.println("我是Lock线程" + Thread.currentThread().getName()), sum());
            pool.shutdown();
            return submit.get();
        }
    }


    /**
     * 方式6  synchronized  重量级锁 锁类对象
     *
     * @author codingzx
     */
    public static class SynLock {
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();

            SynLock synLock = new SynLock();
            String result;
            synchronized (SynLock.class) {
                result = synLock.test();
            }
            countRunTime(start, result);
        }

        public String test() throws InterruptedException, ExecutionException {
            Future<String> submit = pool.submit(() -> System.err.println("我是synchronized线程" + Thread.currentThread().getName()), sum());
            pool.shutdown();
            return submit.get();
        }
    }

    /**
     * 方式7 ReentrantReadWriteLock  读写锁
     * <p>
     * 读锁   + 读锁
     * 写锁   + 读锁 锁可以降级不能升级
     * 写锁   + 写锁 不行
     * <p>
     * 独占写锁
     *
     * @author codingzx
     */
    public static class ReadWriteLock {
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();

            ReadWriteLock readWriteLock = new ReadWriteLock();


            ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
            ReentrantReadWriteLock.ReadLock readLock = lock.readLock();
            ReentrantReadWriteLock.WriteLock writeLock = lock.writeLock();


            writeLock.lock();
            String result;
            try {
                writeLock.lock();
                result = readWriteLock.test();
            } finally {
                writeLock.unlock();
//                readLock.unlock();
            }
            countRunTime(start, result);
        }

        public String test() throws InterruptedException, ExecutionException {
            Future<String> submit = pool.submit(() -> System.err.println("我是ReentrantReadWriteLock线程" + Thread.currentThread().getName()), sum());
            pool.shutdown();
            return submit.get();
        }
    }

    /**
     * 方式8  读写锁   读锁 + 读锁
     *
     * @author codingzx
     */
    public static class ReadWriteLock2 {
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();
            ReadWriteLock2 myLock = new ReadWriteLock2();
            ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();
            ReentrantReadWriteLock.ReadLock readLock = readWriteLock.readLock();
            String result;
            readLock.lock();
            try {
                readLock.lock();
                result = myLock.test();
            } finally {
                readLock.unlock();
            }
            countRunTime(start, result);
        }

        public String test() throws InterruptedException, ExecutionException {
            Future<String> submit = pool.submit(() -> System.err.println("我是ReentrantReadWriteLock线程" + Thread.currentThread().getName()), "test");
            pool.shutdown();
            return submit.get();
        }
    }


    /**
     * 方式9   主线程里面判断
     * 1. 返回结果 是否为空
     * 2. 判断存活的线程是否 > 2
     * 3.
     *
     * @date 2021/4/11
     */
    public static class OtherWay {
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();

            OtherWay a = new OtherWay();
            String result = a.test();
            while (true) {
//                if (StringUtils.hasText(result)) {
//                    break;
//                }

                if (Thread.activeCount() > 2) {
                    Thread.yield();
                } else {
                    break;
                }
            }
            countRunTime(start, result);
        }


        public String test() throws ExecutionException, InterruptedException {
            Future<String> submit = pool.submit(() -> System.err.println("我是test线程" + Thread.currentThread().getName()), sum());
            pool.shutdown();
            return submit.get();
        }
    }


    /**
     * 方式10  join
     *
     * @author codingzx
     */
    public static class ThreadJoin {
        public static void main(String[] args) throws Exception {
            long start = System.currentTimeMillis();

            ThreadJoin threadJoin = new ThreadJoin();
            String result = threadJoin.test();
//            Thread thread = new Thread();
//            thread.join();
            countRunTime(start, result);
        }

        public String test() throws ExecutionException, InterruptedException {
            Future<String> submit = pool.submit(() -> System.err.println("我是test线程" + Thread.currentThread().getName()), sum());
            pool.shutdown();
            return submit.get();
        }
    }


    // =================================继承实现多线程=================================================================

    /**
     * 方式11    实现runnable  join
     *
     * @author codingzx
     * @date 2021/4/11
     */
    public static class C implements Runnable {
        private String test;

        public static void main(String[] args) throws Exception {
            C c = new C();
            Thread thread = new Thread(c);
            thread.start();
            thread.join();
            System.err.println("我是main线程,结果=" + c.getTest());
        }

        @Override
        public void run() {
            System.err.println("我是test线程" + Thread.currentThread().getName());
            this.test = "test";
        }

        public String getTest() {
            return test;
        }
    }

    /**
     * 方式12  继承Thread  join
     *
     * @author codingzx
     * @date 2021/4/11
     */
    public static class D extends Thread {
        private String test;

        @Override
        public void run() {
            System.err.println("我是test线程" + Thread.currentThread().getName());
            this.test = "test";
        }

        public static void main(String[] args) throws Exception {
            D d = new D();
            d.start();
            d.join();
            System.err.println("我是main线程,结果=" + d.test);
        }
    }

    /**
     * 方式13  实现callable    获取又返回的几个
     *
     * @author codingzx
     * @date 2021/4/11
     */
    public static class E implements Callable<String> {

        public static void main(String[] args) throws Exception {
            E e = new E();
            FutureTask<String> futureTask = new FutureTask<>(e);
            new Thread(futureTask).start();
            System.err.println("我是main线程,结果=" + futureTask.get());
        }

        @Override
        public String call() throws Exception {
            System.err.println("我是test线程" + Thread.currentThread().getName());
            return "test";
        }
    }

}
