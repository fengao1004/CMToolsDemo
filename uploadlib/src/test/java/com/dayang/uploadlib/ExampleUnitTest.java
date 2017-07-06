package com.dayang.uploadlib;

import android.os.SystemClock;

import org.junit.Test;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    int a;

    @Test
    public void addition_isCorrect() throws Exception {
        try {
            ExecutorService cachedThreadPool = Executors.newFixedThreadPool(5);
            for (a = 0; a < 10; a++) {
                cachedThreadPool.execute(new Runnable() {
                    public void run() {
                        try {
                            for (int i = 0; i <= 10; i++) {
//                            Thread.sleep(1 * 500);
                                System.out.println("time:_ " + new Date().getTime() + " a:_ " + a + "  name:_ " + Thread.currentThread().getName());
                            }
                        } catch (Exception e) {
                            System.out.println("error!!!!!!!~~~~~~       " + e.getMessage());
                        }
                    }
                });

                new Thread().start();
            }
        } catch (Exception e) {
            System.out.println("error@@@@@@@@@@@      " + e.getMessage());
        }
    }
}