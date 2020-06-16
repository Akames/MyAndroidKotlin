package com.akame.commonlib.utils;

import android.app.Activity;

import com.akame.commonlib.CommonLib;
import com.akame.commonlib.RegionVo;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Administrator
 * @Date: 2018/10/24
 * @Description: 时间&&地区选择器
 */
public class PickerManager {
    private static OptionsPickerView pickerView;

    public static void location(Activity activity, SelectCallBack selectCallBack) {
        List<RegionVo> regionList = getRegionList(); //获取的到总的省市区
        List<String> options1Items = new ArrayList<>();
        List<List<String>> options1Items2 = new ArrayList<>();
        List<List<List<String>>> options1Items3 = new ArrayList<>();
        for (int i = 0; i < regionList.size(); i++) {
            options1Items.add(regionList.get(i).getName()); //添加省
            List<RegionVo.City> provinceList = regionList.get(i).getCity();//获取到目标的市
            List<List<String>> cities = new ArrayList<>(); //实例化目标市列表
            List<String> city = new ArrayList<>();
            for (int j = 0; j < provinceList.size(); j++) {
                List<String> ares = provinceList.get(j).getArea(); //获取目标的区列表
                List<String> citiess = new ArrayList<>();//实例化目标区列表
                for (int k = 0; k < ares.size(); k++) {
                    citiess.add(ares.get(k)); // 添加到区列表
                }
                city.add(provinceList.get(j).getName());//添加到市列表
                cities.add(citiess);//添加该市所有的区
            }
            options1Items2.add(city);//添加所有市
            options1Items3.add(cities);//添加所有区
        }
        pickerView = new OptionsPickerBuilder(activity, (options1, option2, options3, v) -> {
            //返回的分别是三个级别的选中位置
                /*String tx = options1Items.get(options1)
                        + options1Items2.get(options1).get(option2)
                        + options1Items3.get(options1).get(option2).get(options3);*/
            selectCallBack.onOptionsSelect(options1Items.get(options1), options1Items2.get(options1).get(option2), options1Items3.get(options1).get(option2).get(options3));
        }).build();
        pickerView.setPicker(options1Items, options1Items2, options1Items3);
        pickerView.show();
    }

    private static List<RegionVo> getRegionList() {
        try {
            InputStreamReader isr = new InputStreamReader(CommonLib.getApplication().getAssets().open("citylist.json"), "UTF-8");
            BufferedReader br = new BufferedReader(isr);
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            br.close();
            isr.close();
            String json = sb.toString();
            Type type = new TypeToken<List<RegionVo>>() {
            }.getType();
            return GsonUtil.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public interface SelectCallBack {
        void onOptionsSelect(String province, String city, String area);
    }

    public void cancel() {
        pickerView = null;
    }
}
