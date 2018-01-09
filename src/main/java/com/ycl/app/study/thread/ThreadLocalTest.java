package com.ycl.app.study.thread;

public class ThreadLocalTest {
    private static ThreadLocal<Integer> numLocal = new ThreadLocal<Integer>() {
        @Override
        public Integer initialValue() {
            return 0;
        }
    };

    private Integer num = 0;

    // ②获取下一个序列值  
    public int getNextNum() {
        numLocal.set(numLocal.get() + 1);
        return numLocal.get();
    }

    public int getNextNum1() {
        num++;
        return num;
    }

    public static void main(String[] args) {
        ThreadLocalTest sn = new ThreadLocalTest();
        // ③ 3个线程共享sn，各自产生序列号  
        TestClient t1 = new TestClient(sn);
        TestClient t2 = new TestClient(sn);
        TestClient t3 = new TestClient(sn);
        //        // ③ 3个线程共享sn，各自产生序列号  
        //        TestClient t1 = new TestClient(new ThreadLocalTest());
        //        TestClient t2 = new TestClient(new ThreadLocalTest());
        //        TestClient t3 = new TestClient(new ThreadLocalTest());
        t1.start();
        t2.start();
        t3.start();

    }

    private static class TestClient extends Thread {
        private ThreadLocalTest sn;

        public TestClient(ThreadLocalTest sn) {
            this.sn = sn;
        }

        @Override
        public void run() {
            for (int i = 0; i < 3; i++) {
                // ④每个线程打出3个序列值  
                //                System.out.println("thread[" + Thread.currentThread().getName()
                //                        + "] --> sn.getNextNum()[" + sn.getNextNum() + "]");
                System.out.println("thread[" + Thread.currentThread().getName()
                        + "] --> sn.getNextNum1()[" + sn.getNextNum1() + "]");
                System.out.println(
                    "===================================================");
            }
        }
    }
}
