package com.akame.commonlib.utils;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class TUtil {
    /**
     * 获取泛型对象
     *
     * @return
     */
    public static <T> T getT(Object o, int i) {
        try {
            /**
             * getGenericSuperclass() : 获得带有泛型的父类
             * ParameterizedType ： 参数化类型，即泛型
             * getActualTypeArguments()[] : 获取参数化类型的数组，泛型可能有多个
             */
            Type type = o.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType) {
                return ((Class<T>) ((ParameterizedType) type).getActualTypeArguments()[i]).newInstance();
            } else {
                return null;
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassCastException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 通过类名获取Class对象
     *
     * @param className
     * @return
     */
    public static Class<?> forName(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }
}
