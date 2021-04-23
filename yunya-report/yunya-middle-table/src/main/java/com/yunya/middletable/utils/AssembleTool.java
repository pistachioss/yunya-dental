package com.yunya.middletable.utils;


import com.yunya.framework.common.utils.StringHelper;
import com.yunya.middletable.config.DuiBaConfig;
import com.yunya.models.employee_attend.FieldInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * @program: yunya-dental
 * @description: Url工具类
 * @author: LHB
 * @create: 2021-04-22 09:36
 **/
@Component
public class AssembleTool {

	@Autowired
	private DuiBaConfig duiBaConfig;

	/**
	 * 生成拼接参数之后的url连接地址
	 * @param urlPar url
	 * @param params 参数列表
	 * @return 返回拼接之后的url
	 */
	public static String assembleUrl(String urlPar,Map<String, String> params){
		Logger log = Logger.getLogger("assembleUrl");
		StringBuilder str = new StringBuilder(urlPar);
		if (str.toString().endsWith("?")) {

		} else if(str.toString().contains("?")){
			str.append("&");
		}else{
			str.append("?");
		}

		for (Map.Entry<String, String> entry : params.entrySet()) {
			try {
				if (entry.getValue() == null || entry.getValue().length() == 0) {
					str.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
				} else {
					str.append(entry.getKey()).append("=").append(URLEncoder.encode(entry.getValue(), "utf-8")).append("&");
				}
			} catch (Exception e) {
				log.info("assembleUrl:="+e);
			}
		}
		return str.toString();
	}

	public Map<String, String> toRequestMap(Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Map<String, String> result = new HashMap<>();
		if (obj != null) {
			Class<?> aClass = obj.getClass();
			Field[] declaredFields = aClass.getDeclaredFields();
			if (declaredFields.length > 0) {
				for (Field field : declaredFields) {
					putParams(result,field,obj);
				}
				result.put("appSecret",duiBaConfig.getAppSecret());
				String sign = SignTool.sign(result);
				result.remove("appSecret");
				result.put("sign",sign);
			}
		}
		return result;
	}

	public void putParams(Map<String,String> result, Field field, Object obj) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
		String fieldName = field.getName();
		String fieldType = field.getGenericType().toString();
		if (fieldType.equals("class java.lang.String")) {
			if (fieldName.equals("transfer")) {
				String s = this.reflectString(fieldName, obj);
				if (s == null || s.length() == 0) {
					return ;
				}
				result.put(fieldName,s);
			}
			result.put(fieldName,this.reflectString(fieldName,obj));
		} else if (fieldType.equals("class java.lang.Integer")) {
			result.put(fieldName,String.valueOf(this.reflectInteger(fieldName,obj)));
		} else if (fieldType.equals("class java.lang.Double")) {
			result.put(fieldName,String.valueOf(this.reflectDouble(fieldName,obj)));
		} else if (fieldType.equals("class java.lang.Boolean")) {
			result.put(fieldName,String.valueOf(this.reflectBoolean(fieldName,obj)));
		} else if (fieldType.equals("boolean")) {
			result.put(fieldName,String.valueOf(this.reflectBool(fieldName,obj)));
		} else if (fieldType.equals("class java.util.Date")) {
			result.put(fieldName,String.valueOf(this.reflectDate(fieldName,obj).getTime()));
		} else if (fieldType.equals("class java.lang.Long")) {
			result.put(fieldName,String.valueOf(this.reflectLong(fieldName,obj)));
		}
	}


	/**
	 * 反射Date类型字段值
	 * @param fieldName
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private Date reflectDate(String fieldName,Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = obj.getClass().getMethod("get" + getMethodName(fieldName));
		Date date = (Date) method.invoke(obj);
		if (date == null) {
			return new Date();
		}
		return date;
	}

	/**
	 * 反射String类型字段值
	 * @param fieldName
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private Boolean reflectBoolean(String fieldName,Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
			Method method = obj.getClass().getMethod(fieldName);
			Boolean string = (Boolean) method.invoke(obj);
			return string;
	}

	/**
	 * 反射boolean类型字段值
	 * @param fieldName
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private boolean reflectBool(String fieldName,Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
			Method method = obj.getClass().getMethod(fieldName);
			boolean b = (boolean) method.invoke(obj);
			return b;
	}

	/**
	 * 反射Long类型字段值
	 * @param fieldName
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private Long reflectLong(String fieldName,Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
		Method method = obj.getClass().getMethod("get" + getMethodName(fieldName));
		Long string = (Long) method.invoke(obj);
		return string;
	}

	/**
	 * 反射String类型字段值
	 * @param fieldName
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private String reflectString(String fieldName,Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
			Method method = obj.getClass().getMethod("get" + getMethodName(fieldName));
			String string = (String) method.invoke(obj);
			return string;
	}

	/**
	 * 反射Intege类型字段值
	 * @param fieldName
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private Integer reflectInteger(String fieldName,Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
			Method method = obj.getClass().getMethod("get" + getMethodName(fieldName));
			Integer result = (Integer) method.invoke(obj);
			return result;
	}

	/**
	 * 反射Double类型字段值
	 * @param fieldName
	 * @param obj
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 */
	private Double reflectDouble(String fieldName,Object obj) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
			Method method = obj.getClass().getMethod("get" + getMethodName(fieldName));
			Double d = (Double) method.invoke(obj);
			return d;
	}



	/**
	 * 获取方法名称，首字母大写
	 * @param fieldName 字段名
	 * @return 返回方法名
	 */
	private String getMethodName(String fieldName) {
		byte[] bytes = fieldName.getBytes();
		bytes[0] = (byte) (bytes[0] - 'a' + 'A');
		return new String(bytes);
	}
	
}
