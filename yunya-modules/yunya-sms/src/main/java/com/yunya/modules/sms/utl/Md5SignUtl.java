package com.yunya.modules.sms.utl;

import org.apache.commons.codec.digest.DigestUtils;

import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2020/12/18 13:02
 * @since: 1.0.0
 */
public class Md5SignUtl {

    private static String getSignText(Object obj) {
        if (obj instanceof Collection) {
            return getListSignText((Collection<Object>) obj);
        } else if (obj instanceof Map) {
            return getMapSignText((Map) obj);
        } else if (obj instanceof Number) {
            return obj.toString();
        } else if (obj != null) {
            return obj.toString();
        } else {
            return "";
        }
    }

    private static String getListSignText(Collection list) {
        StringBuffer paramBuffer = new StringBuffer();
        for (Object obj : list) {
            paramBuffer.append(getSignText(obj)).append("|");
        }
        return paramBuffer.substring(0, paramBuffer.length() - 1);
    }

    private static String getMapSignText(Map map) {
        String[] keys = (String[]) map.keySet().toArray(new String[0]);
        // 1. 参数名按照ASCII码表升序排序
        Arrays.sort(keys);
        // 2. 按照排序拼接参数名与参数值
        StringBuffer paramBuffer = new StringBuffer();
        for (String key : keys) {
            paramBuffer.append(key).append("=").append(getSignText(map.get(key)) == null ? "" : getSignText(map.get(key))).append("&");
        }
        return paramBuffer.substring(0, paramBuffer.length() - 1);
    }

    static String md5(Map<String, Object> params, String key, String charsetName) throws Exception {
        String paramStr = getMapSignText(params);
        paramStr += "&key=" + key;
        return DigestUtils.md5Hex(paramStr.getBytes(charsetName));
    }
}
