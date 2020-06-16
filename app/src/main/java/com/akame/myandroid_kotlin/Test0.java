package com.akame.myandroid_kotlin;

import java.util.ArrayList;
import java.util.List;

public class Test0 {
    public static void main(String[] args) {
      /*  final ThreadLocal<String> local = new ThreadLocal<>();
        local.set("张三");
        System.out.println("线程一："+local.get());
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                System.out.println("线程二："+local.get());
            }
        };
        new Thread(runnable).start();*/

        Integer i1 = Integer.valueOf(124656).intValue();
        Integer i2 = Integer.valueOf(124656).intValue();
        System.out.println("qq1"+(i1.equals(i2)));
    }

    private void test(){
        List<? extends A> list = new ArrayList<B>();
        A a = list.get(0);
        A t = new A();

    }

    class A {

    }

    class B extends A {

    }
}
