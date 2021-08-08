package com.codingzx.geektimehomework.concurren;

/**
 * @author codingzx
 * @description
 * @date 2021/8/8 16:44
 */
public class SimpleUtil {

    public static String sum() {
        return String.valueOf(fibo2(36));
    }

    public static int fibo(int a) {
        if (a < 2)
            return 1;
        return fibo(a - 1) + fibo(a - 2);
    }

    public static int fibo2(int a) {
        if (a < 0) return 0;
        if (a == 1) return 1;
        int f1 = 0;
        int f2 = 1;
        int result = 0;
        int i = 2;
        while (i++ < a) {
            result = f1 + f2;
            f1 = f2;
            f2 = result;
        }
        return result;

    }


    public static void countRunTime(long start, String result) {
        System.out.println("我是main线程,子线程计算阶乘结果为" + result);
        System.out.println("使用时间：" + (System.currentTimeMillis() - start) + " ms");
        System.out.println("主线程结束");
    }


}
