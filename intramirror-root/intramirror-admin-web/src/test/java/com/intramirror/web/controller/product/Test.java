package com.intramirror.web.controller.product;

/**
 * Created on 2018/4/11.
 *
 * @author 123
 */
public class Test implements Runnable {
    public void run() {
//                synchronized (this) {
//                    for (int i = 0; i < 5; i++) {
//                        System.out.println(Thread.currentThread().getName() + " synchronized loop " + i);
//                    }
//                }

        try {
            hh();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Test t1 = new Test();
        Thread ta = new Thread(t1, "A");
        Thread tb = new Thread(t1, "B");
        ta.start();
        tb.start();
    }

    public synchronized void hh() throws InterruptedException {
        for (int i = 0; i < 20; i++) {
            System.out.println("---"+Thread.currentThread().getName() + " synchronized loop " + i);
            Thread.sleep(1000L);
        }
    }
}
