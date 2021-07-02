package com.codingzx.geektimehomework.jvm._01;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author codingzx
 * @description
 * @date 2021/7/1 20:11
 */
public class Myclassloader extends ClassLoader {

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        try {
            InputStream in = getResourceAsStream("Hello.xlass");
            byte[] bytes = new byte[in.available()];
            byte[] datas = new byte[bytes.length];
            in.read(bytes, 0, bytes.length);
            for (int i = 0; i < bytes.length; i++) {
                datas[i] = (byte) (255 - bytes[i]);
            }
            return defineClass(name, datas, 0, datas.length);
        } catch (IOException e) {
            e.printStackTrace();
            throw new ClassNotFoundException();
        }
    }

    public static void main(String[] args) {
        Myclassloader myclassloader = new Myclassloader();
        Class<?> myclass = null;
        try {
            myclass = myclassloader.findClass("Hello");
            Method mymethod = myclass.getMethod("hello", null);
            mymethod.invoke(myclass.newInstance(), null);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }


}
