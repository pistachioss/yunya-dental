package com.yunya.framework.common.utils;

import com.google.common.collect.Lists;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.beans.BeanCopier;
import org.springframework.cglib.core.Converter;

import java.io.*;
import java.util.List;

import static java.util.stream.Collectors.*;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
public class BeanCopierUtils {

    private BeanCopierUtils() {
    }

    public static <S, T> T generalCopyBean(S source, Class<T> targetClazz) {
        return copySingleBean(source, targetClazz,false,null);
    }

    public static <S, T> T generalCopyBean(S source, Class<T> targetClazz, Converter converter) {
        return copySingleBean(source, targetClazz, true, converter);
    }

    public static <S, T> List<T> listGeneralCopyBean(List<S> source, Class<T> targetClazz) {
        if (CollectionUtils.isEmpty(source)) {
            return Lists.newArrayList();
        }
        return source.stream().map(obj -> copySingleBean(obj, targetClazz, false, null)).collect(toList());
    }

    public static <S, T> List<T> listGeneralCopyBean(List<S> source, Class<T> targetClazz, Converter converter) {
        if (CollectionUtils.isEmpty(source)) {
            return Lists.newArrayList();
        }
        return source.stream().map(obj -> copySingleBean(obj, targetClazz, true, converter)).collect(toList());
    }

    private static <S, T> T copySingleBean(S source, Class<T> targetClazz,
                                           boolean useConvert, Converter converter) {
        try {
            T t = targetClazz.newInstance();
            BeanCopier copier = BeanCopier.create(source.getClass(), targetClazz, useConvert);
            copier.copy(source, t, converter);
            return t;
        } catch (Exception e) {
            throw new ClientServiceException(OperationCodeConstants.BEAN_CONVERT_ERROR, "对象属性转换异常", e);
        }
    }

    public static<T> T deepClone(T src) throws IOException, ClassNotFoundException {
        Object obj = null;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        objectOutputStream.writeObject(src);
        objectOutputStream.close();
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
        obj = objectInputStream.readObject();
        objectInputStream.close();
        return (T) obj;
    }

}
