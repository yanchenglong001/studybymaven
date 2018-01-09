package com.ycl.app.study.thread;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ExecutorServiceTest {

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(2);
        ExecutorService service = Executors.newFixedThreadPool(2);
        List<Future<String>> futures = new ArrayList<Future<String>>();
        try {
            for (int i = 0; i < 2; i++) {
                Future<String> future = service
                        .submit(new TestTask(null, latch));
                futures.add(future);

            }
            latch.await();
            System.out.println("===========");
            service.shutdown();
            for (Future<String> future : futures) {
                System.out.println("future.get() = " + future.get());
            }
        } catch (InterruptedException e) {

            e.printStackTrace();
        } catch (ExecutionException e) {

            e.printStackTrace();
        }
    }

}
