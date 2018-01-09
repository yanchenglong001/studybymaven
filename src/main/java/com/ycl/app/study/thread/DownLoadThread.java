package com.ycl.app.study.thread;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.concurrent.CountDownLatch;

public class DownLoadThread implements Runnable {
    private long startPos;

    private long endPos;

    private MultiTheradDownLoad task = null;

    private RandomAccessFile downloadfile = null;

    private int id;

    private File tmpfile = null;

    private RandomAccessFile rantmpfile = null;

    private CountDownLatch latch = null;

    public DownLoadThread(long startPos, long endPos, MultiTheradDownLoad task,
            int id, File tmpfile, CountDownLatch latch) {
        this.startPos = startPos;
        this.endPos = endPos;
        this.task = task;
        this.tmpfile = tmpfile;
        try {
            this.downloadfile = new RandomAccessFile(this.task.getFilename(),
                    "rw");
            this.rantmpfile = new RandomAccessFile(this.tmpfile, "rw");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        this.id = id;
        this.latch = latch;
    }

    public void run() {
        HttpURLConnection httpcon = null;
        InputStream is = null;
        int length = 0;

        System.out.println("the thread " + id + " has started!!");

        while (true) {
            try {
                httpcon = (HttpURLConnection) task.getUrl().openConnection();
                MultiTheradDownLoad.setHeader(httpcon);
                //防止网络阻塞，设置指定的超时时间；单位都是ms。超过指定时间，就会抛出异常
                httpcon.setReadTimeout(20000);//读取数据的超时设置
                httpcon.setConnectTimeout(20000);//连接的超时设置

                if (startPos < endPos) {
                    //向服务器请求指定区间段的数据，这是实现断点续传的根本。
                    httpcon.setRequestProperty("Range",
                        "bytes=" + startPos + "-" + endPos);
                    System.out.println("Thread " + id + " the total size:---- "
                            + (endPos - startPos));
                    downloadfile.seek(startPos);

                    if (httpcon.getResponseCode() != HttpURLConnection.HTTP_OK
                            && httpcon
                                    .getResponseCode() != HttpURLConnection.HTTP_PARTIAL) {
                        this.task.setBool(true);
                        httpcon.disconnect();
                        downloadfile.close();
                        System.out
                                .println("the thread ---" + id + " has done!!");
                        latch.countDown();//计数器自减
                        break;
                    }

                    is = httpcon.getInputStream();//获取服务器返回的资源流

                    long count = 0l;
                    byte[] buf = new byte[1024];

                    while (!this.task.isBool()
                            && (length = is.read(buf)) != -1) {
                        count += length;
                        downloadfile.write(buf, 0, length);

                        //不断更新每个线程下载资源的起始位置，并写入临时文件；为断点续传做准备
                        startPos += length;
                        rantmpfile.seek(8 * id + 8);
                        rantmpfile.writeLong(startPos);
                    }
                    System.out.println(
                        "the thread " + id + " total load count: " + count);

                    //关闭流
                    is.close();
                    httpcon.disconnect();
                    downloadfile.close();
                    rantmpfile.close();
                }
                latch.countDown();//计数器自减
                System.out.println("the thread " + id + " has done!!");
                break;

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
