package com.yunya.middletable.model.credits_shop;

import java.util.Map;

/**
 * @program: yunya-dental
 * @description:
 * @author: LHB
 * @create: 2021-04-22 15:53
 **/
abstract class AbstractCredit {
    public Map<String, String> toRequestMap(String appSecret, String signUrl) {
        return null;
    }

    void putIfNotEmpty(Map<String, String> map, String key, String value){
        if(value==null || value.length()==0){
            return;
        }
        map.put(key, value);
    }
    String getString(Object o){
        if(o==null){
            return "";
        }
        return o.toString();
    }
}
