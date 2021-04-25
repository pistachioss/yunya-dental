package com.yunya.middletable.utils;

import javax.servlet.http.HttpServletRequest;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.util.*;

/**
 * @program: yunya-dental
 * @description: 登录工具类
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
public class SignTool {

	/**
	 * 登录request有效性校验
	 * @param appSecret  兑吧秘钥
	 * @param request
	 * @return
	 */
	public static boolean signVerify(String appSecret,HttpServletRequest request){
		Map<String, String[]> map=request.getParameterMap();
		Map<String, String> data=new HashMap<String, String>();
		for(String key:map.keySet()){
			data.put(key, map.get(key)[0]);
		}
		return signVerify(appSecret, data);
	}

	/**
	 * 请求参数校验
	 * @param appSecret  秘钥
	 * @param params 参数
	 * @return
	 */
	public static boolean signVerify(String appSecret,Map<String, String> params){
		Map<String, String> map=new HashMap<String, String>();
		map.put("appSecret", appSecret);
		
		for(String key:params.keySet()){
			if(!key.equals("sign")){
				map.put(key, params.get(key));
			}
		}
		
		String sign=sign(map);
		if(sign.equals(params.get("sign"))){
			return true;
		}
		return false;
	}

	/**
	 * 转换为十六进制字符串
	 * @param messageDigest
	 * @return
	 */
	private static String toHexValue(byte[] messageDigest) {
		if (messageDigest == null) {
			return "";
		}
		StringBuilder hexValue = new StringBuilder();
		for (byte aMessageDigest : messageDigest) {
			int val = 0xFF & aMessageDigest;
			if (val < 16) {
				hexValue.append("0");
			}
			hexValue.append(Integer.toHexString(val));
		}
		return hexValue.toString();
	}
	/**
	 * 登录
	 * @param params 参数
	 * @return 返回处理后的登录url
	 */
	public static String sign(Map<String,String> params){
		List<String> keys=new ArrayList<String>(params.keySet());
		Collections.sort(keys);
		String string="";
		for(String s:keys){
			string+=params.get(s);
		}
		String sign="";
		try {
			sign = toHexValue(encryptMD5(string.getBytes(Charset.forName("utf-8"))));
		} catch (Exception e) {
			e.printStackTrace();
			throw new RuntimeException("md5 error");
		}

		return sign;
	}

	/**
	 * 拼接登录URL
	 * @param params 请求参数
	 * @param sign  签名
	 * @param duibaUrl 兑吧路径
	 * @return
	 */
	public static String signRequestUrl(Map<String,String> params, String sign, String duibaUrl) {
		// 拼接登录URL
		StringBuffer sb = new StringBuffer();
		sb.append(duibaUrl);
		sb.append("uid=" + params.get("uid") + "&");
		sb.append("credits=" + params.get("credits") + "&");
		sb.append("appKey=" + params.get("appKey") + "&");
		sb.append("timestamp=" + params.get("timestamp") + "&");
		sb.append("dcustom=" + URLEncoder.encode(params.get("dcustom"))  + "&");
		sb.append("sign=" + sign);
		return sb.toString();
	}

	/**
	 * MD5加密数据
	 * @param data 数据
	 * @return
	 * @throws Exception
	 */
	private static byte[] encryptMD5(byte[] data)throws Exception{
		MessageDigest md5 = MessageDigest.getInstance("MD5");
		md5.update(data);
		return md5.digest();
	}

	
	public static void main(String[] args) {
		String appKey="uyjUSAhoswNscUbAoqcxM2EDsiJ";
		String appSecret="2XYKYmPyTfT44JBvb6MLCNFmffJA";

		Map<String, String> params=new HashMap<String, String>();

		params.put("uid","oZRpos16w9Gku_lBYeyOyREzlofs");
		params.put("credits","1000");
		params.put("appKey",appKey);
		params.put("appSecret",appSecret);
		params.put("timestamp",String.valueOf(System.currentTimeMillis()));
		String dcustomParams = "patientId=" + 107877;
		params.put("dcustom", URLEncoder.encode(URLEncoder.encode(dcustomParams)));

		String sign=sign(params);

		params.put("sign", sign);

		params.remove("appSecret");

		String s = signRequestUrl(params, sign, "https://activity.m.duiba.com.cn/autoLogin/autologin?");

		System.out.println("url = " + s);
		System.out.println(sign);
		System.out.println(signVerify(appSecret, params));

	}
}
