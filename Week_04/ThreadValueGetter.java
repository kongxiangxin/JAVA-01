import java.util.Random;
import java.util.concurrent.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.LockSupport;
import java.util.concurrent.locks.ReentrantLock;

public class ThreadValueGetter {

    public static Random random = new Random();

    public static void main(String[] args) throws Exception {
        method1();
        method2();
        method3();
        method4();
        method5();
        method6();
        method7();
        method8();
        method9();
        method10();
        method11();
        method12();
        method13();
        method14();
        method15();
        method16();
    }

    static Integer calc() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return random.nextInt(1000);
    }

    static void method1() throws ExecutionException, InterruptedException {
        //FutureTask
        FutureTask<Integer> task = new FutureTask<>(() -> {
            return calc();
        });
        new Thread(task).start();
        Integer ret = task.get();
        System.out.println("result1 = " + ret);
    }

    static void method2() throws InterruptedException {
        //Thread.join
        Integer[] ret = new Integer[1];
        Thread t1 = new Thread(() -> {
            ret[0] = calc();
        });
        t1.start();
        t1.join();
        System.out.println("result2 = " + ret[0]);
    }

    static void method3() throws InterruptedException {
        //Thread.sleep足够时间
        Integer[] ret = new Integer[1];
        new Thread(() -> {
            ret[0] = calc();
        }).start();

        Thread.sleep(2000);
        System.out.println("result3 = " + ret[0]);
    }

    static void method4() throws InterruptedException {
        //子线程先锁住资源，执行完释放锁，主线程拿到锁表示子线程执行完成
        Integer[] ret = new Integer[1];
        new Thread(() -> {
            synchronized (ret) {
                ret[0] = calc();
            }
        }).start();
        //保证子线程先拿到锁
        Thread.sleep(100);

        synchronized (ret) {
            System.out.println("result4 = " + ret[0]);
        }
    }

    static void method5() throws InterruptedException {
        //wait & notify 代替Thread.join
        Integer[] ret = new Integer[1];
        new Thread(() -> {
            synchronized (ret) {
                ret[0] = calc();
                ret.notify();
            }
        }).start();
        synchronized (ret) {
            ret.wait();
            System.out.println("result5 = " + ret[0]);
        }
    }

    static void method6() {
        //interrupt代替wait
        Integer[] ret = new Integer[1];
        Thread mainThread = Thread.currentThread();
        new Thread(() -> {
            ret[0] = calc();
            mainThread.interrupt();
        }).start();
        synchronized (ret) {
            try {
                ret.wait();
            } catch (InterruptedException e) {
            }
            System.out.println("result6 = " + ret[0]);
        }
    }

    static void method7() throws InterruptedException {
        //Lock + await + signal代替synchronized + wait + notify
        Integer[] ret = new Integer[1];

        Lock lock = new ReentrantLock();
        Condition isDone = lock.newCondition();

        new Thread(() -> {
            ret[0] = calc();
            lock.lock();
            isDone.signalAll();
            lock.unlock();
        }).start();

        lock.lock();
        isDone.await();
        lock.unlock();
        System.out.println("result7 = " + ret[0]);
    }

    static void method8() throws InterruptedException {
        //CountDownLatch
        Integer[] ret = new Integer[1];

        CountDownLatch countDownLatch = new CountDownLatch(1);

        new Thread(() -> {
            ret[0] = calc();
            countDownLatch.countDown();
        }).start();

        countDownLatch.await();
        System.out.println("result8 = " + ret[0]);
    }

    static void method9() throws InterruptedException, BrokenBarrierException {
        //CyclicBarrier
        Integer[] ret = new Integer[1];

        CyclicBarrier cyclicBarrier = new CyclicBarrier(2);

        new Thread(() -> {
            ret[0] = calc();
            try {
                cyclicBarrier.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
        }).start();

        cyclicBarrier.await();
        System.out.println("result9 = " + ret[0]);
    }

    static void method10() throws InterruptedException, BrokenBarrierException {
        //Semaphore
        Integer[] ret = new Integer[1];

        Semaphore semaphore = new Semaphore(1);

        new Thread(() -> {
            try {
                semaphore.acquire();
                ret[0] = calc();
                semaphore.release();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        //保证子线程先拿到许可
        Thread.sleep(100);
        semaphore.acquire();
        System.out.println("result10 = " + ret[0]);
    }

    static void method11() throws ExecutionException, InterruptedException {
        //CompletableFuture
        Integer ret = CompletableFuture.supplyAsync(ThreadValueGetter::calc)
                .get();
        System.out.println("result11 = " + ret);
    }

    static void method12() throws ExecutionException, InterruptedException {
        //ExecutorService.submit，其实还是Future
        ExecutorService executorService = Executors.newFixedThreadPool(1);
        Future<Integer> future = executorService.submit(() -> calc());

        System.out.println("result12 = " + future.get());

        executorService.shutdown();
    }

    static void method13() throws InterruptedException {
        //SynchronousQueue put & take
        SynchronousQueue queue = new SynchronousQueue();
        new Thread(() -> {
            try {
                queue.put(calc());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Object ret = queue.take();
        System.out.println("result13 = " + ret);
    }
    static void method14() throws InterruptedException {
        //Exchanger
        Exchanger<Integer> exchanger = new Exchanger<>();

        new Thread(() -> {
            try {
                exchanger.exchange(calc());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();

        Integer ret = exchanger.exchange(0);
        System.out.println("result14 = " + ret);
    }
    static void method15() throws InterruptedException {
        //死循环里判断线程是否执行完
        Integer[] ret = new Integer[1];
        new Thread(() -> {
            ret[0] = calc();
        }).start();

        while (true){
            if(ret[0] != null){
                break;
            }
            Thread.yield();
        }
        System.out.println("result15 = " + ret[0]);
    }

    static void method16() {
        //LockSupport.park & unpark
        Thread mainThread = Thread.currentThread();
        Integer[] ret = new Integer[1];
        Thread t1 = new Thread(() -> {
            ret[0] = calc();
            LockSupport.unpark(mainThread);
        });
        t1.start();

        LockSupport.park();
        System.out.println("result16 = " + ret[0]);
    }
}