package com.yunya.framework.common.utils;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

import static com.yunya.framework.common.utils.MD5Util.MD5;

/**
 * 腾讯地图工具类
 */
@Slf4j
public class TencentLocUtl {

    /** 腾讯地图应用key*/
    private static final String KEY = "EU2BZ-4K7RJ-AJCFC-FHBH2-HQ4KK-FQBJX";
    /** 腾讯地图秘钥 */
    private static final String SECRET_KEY = "wKZnfekJiYSiMIGLwI4gGYapwpKivFRJ";
    /** 中国国籍代码*/
    private static final String CHINA_CODE = "156";
    /** 直辖市*/
    private static List<String> municipality = Arrays.asList("北京市","天津市","上海市","重庆市");

    public static String getCityByLoc(String lng, String lat) {
        StringBuilder builder = new StringBuilder();
        JSONObject result = getLocation(lng, lat);
        JSONObject adInfo = result.getJSONObject("ad_info");
        if (!ObjectUtils.isEmpty(adInfo)) {
            String nationCode = adInfo.getString("nation_code");
            if (!CHINA_CODE.equals(nationCode)) {
                JSONObject address = result.getJSONObject("address_component");
                String nation = address.getString("nation");
                if (StringHelper.isNotEmpty(nation)) {
                    builder.append(nation);
                }
                String level1 = address.getString("ad_level_1");
                if (StringHelper.isNotEmpty(level1)) {
                    builder.append(level1);
                }
                String level2 = address.getString("ad_level_2");
                if (StringHelper.isNotEmpty(level2)) {
                    builder.append(level2);
                }
            } else {
                String province = adInfo.getString("province");
                if (StringHelper.isNotEmpty(province)) {
                    builder.append(province);
                }
                String city = adInfo.getString("city");
                if (StringHelper.isNotEmpty(city) && !municipality.contains(city)) {
                    builder.append(city);
                }
            }
        }
        if ("Ocean".equals(builder.toString())) {
            builder = new StringBuilder();
        }
        return builder.toString();
    }
    /**
     * 通过经纬度获取位置
     * @param lng
     * @param lat
     * @return
     */
    public static JSONObject getLocation(String lng, String lat) {
        if (StringHelper.isEmpty(lng) || StringHelper.isEmpty(lat)) {
            return new JSONObject();
        }
        log.info("tencent map param: longitude: {}, latitude: {}", lng, lat);
        Map<String, Object> resultMap = new HashMap<>();
        // 参数解释：lng：经度，lat：维度。KEY：腾讯地图key，get_poi：返回状态。1返回，0不返回
        String urlString = "https://apis.map.qq.com/ws/geocoder/v1?key=" + KEY + "&location=" + lat + "," + lng + "&sig=" + getTxMapSig(lng, lat);
        String result = "";
        try {
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setDoOutput(true);
            // 腾讯地图使用GET
            conn.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line;
            // 获取地址解析结果
            while ((line = in.readLine()) != null) {
                result += line + "\n";
            }
            in.close();
            log.info("tencent map result: {}", result);
        } catch (Exception e) {
            log.error("TencentLocUtl.getLocation error: {}", e);
        }

        // 转JSON格式
        return JSONObject.parseObject(result).getJSONObject("result");
    }

    private static String getTxMapSig (String lng, String lat) {
        return MD5Util.getStringMD5("/ws/geocoder/v1?key=" + KEY + "&location=" + lat + "," + lng + SECRET_KEY);
    }

    public static void main(String[] args) {

        // 测试
        String lng = "107.23";//经度
        String lat = "29.1";//维度
//        String lng = null;
//        String lat = null;
        System.out.println(getCityByLoc(lng, lat));
    }
}
