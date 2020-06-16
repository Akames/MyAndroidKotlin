package com.akame.myandroid_kotlin;

import java.util.ArrayList;
import java.util.List;

public class JsonTest {

    public static void main(String[] args) {
//        Map<String,Object> map = new HashMap<>();
//        map.put("name","张三");
//        map.put("age",8);
//        List<String> ob = new ArrayList<>();
//        ob.add("学生");
//        ob.add("画家");
//        ob.add("钢琴家");
//        map.put("object",ob);
//
//        String jsonData = new Gson().toJson(map);
//        System.out.println(jsonData);


        List<String> list = new ArrayList<>();
        list.add("1");
        list.add("2");
        list.add("3");
        list.add("4");
        list.add("5");

        for (String i : list) {
            list.remove(i);
            System.out.println(i + "----" + list.size());
            break;
        }

        int number = number(20);
        System.out.println(number);
    }

    private static int number(int number) {
        if (number == 1 || number == 2) {
            return 1;
        }
        return number(number - 1) + number(number - 2);
    }
}
