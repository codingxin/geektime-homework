package com.codingzx.geektimehomework.jvm._01;

import sun.misc.Launcher;

import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;


/**
 * @author codingzx
 * @description
 * @date 2021/6/30 20:58
 */
public class JvmClassLoaderPrintPath {
    public static void main(String[] args) {
        URL[] urls = Launcher.getBootstrapClassPath().getURLs();
        System.out.println("启动类加载器");
        for (URL url : urls) {
            System.out.println(" == > " + url.toExternalForm());
        }
        //  扩展类加载器
        printClassLoader("扩展类加载器", JvmClassLoaderPrintPath.class.getClassLoader().getParent());
        //  应用类加载器
        printClassLoader("应用类加载器", JvmClassLoaderPrintPath.class.getClassLoader());

    }

    public static void printClassLoader(String name, ClassLoader CL) {
        if (CL != null) {
            System.out.println(name + " ClassLoader -> " + CL.toString());
            printURLClassLoader(CL);
        } else {
            System.out.println(name + " ClassLoader -> " + null);
        }
    }

    public static void printURLClassLoader(ClassLoader CL) {
        Object ucp = insightField(CL, "ucp");
        Object path = insightField(ucp, "path");
        ArrayList ps = (ArrayList) path;
        for (Object p : ps) {
            System.out.println("   == >   " + p.toString());
        }
    }

    private static Object insightField(Object object, String fieldName) {
        try {
            Field f = null;
            if (object instanceof URLClassLoader) {
                f = URLClassLoader.class.getDeclaredField(fieldName);
            } else {
                f = object.getClass().getDeclaredField(fieldName);
            }
            f.setAccessible(true);
            return f.get(object);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
