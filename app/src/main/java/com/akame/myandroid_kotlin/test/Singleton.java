package com.akame.myandroid_kotlin.test;

public class Singleton {
    /**
     * 标准的单利模式
     */
    private static final Singleton ourInstance = new Singleton();

    public static Singleton getInstance() {
        return ourInstance;
    }

    private Singleton() {


    }

    public static void main(String[] args) {
        System.out.println("---"+ourInstance);
    }


}
