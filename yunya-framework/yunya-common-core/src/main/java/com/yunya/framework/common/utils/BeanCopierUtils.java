package com.yunya.framework.common.utils;

import com.google.common.collect.Lists;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.cglib.beans.BeanCopier;

import java.io.Serializable;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
public class BeanCopierUtils {

    private BeanCopierUtils() {
    }

    public static <S extends Serializable, T extends Serializable> T generalCopyBean(S source, Class<T> targetClazz) {
        return copySingleBean(source, targetClazz);
    }

    public static <S extends Serializable, T extends Serializable> List<T> listGeneralCopyBean(List<S> source, Class<T> targetClazz) {
        if (CollectionUtils.isNotEmpty(source)) {
            return Lists.newArrayList();
        }
        return source.stream().map(obj -> copySingleBean(obj, targetClazz)).collect(toList());
    }

    private static <S extends Serializable, T extends Serializable> T copySingleBean(S source, Class<T> targetClazz) {
        try {
            T t = targetClazz.newInstance();
            BeanCopier copier = BeanCopier.create(source.getClass(), targetClazz, false);
            copier.copy(source, t, null);
            return t;
        } catch (Exception e) {
            throw new ClientServiceException("对象属性转换异常", OperationCodeConstants.BEAN_CONVERT_ERROR);
        }
    }

}
