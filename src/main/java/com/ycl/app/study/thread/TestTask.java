package com.ycl.app.study.thread;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

/**
 * 学习线程
 * @author Administrator
 *
 */
public class TestTask implements Callable<String> {

    private Map<String, Object> datas = new HashMap<String, Object>();

    CountDownLatch latch;

    public TestTask(Map<String, Object> datas, CountDownLatch latch) {
        this.datas = datas;
        this.latch = latch;
    }

    public String call() throws Exception {

        if (datas != null) {
            for (Map.Entry<String, Object> entry : datas.entrySet()) {
                entry.getValue();
            }
        }

        latch.countDown();

        System.out.println(Thread.currentThread().getName());
        return String.valueOf("aaaa");
    }

}
