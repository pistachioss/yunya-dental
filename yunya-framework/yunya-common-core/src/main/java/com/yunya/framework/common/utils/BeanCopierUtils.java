package com.yunya.framework.common.utils;

import org.springframework.cglib.beans.*;
import java.io.*;

/**
 * @author xiangyang
 * @date 2020/8/19
 */
public class BeanCopierUtils {

    private BeanCopierUtils() {
    }

    public static <S extends Serializable, T extends Serializable> T generalCopyBean(S source, Class<T> targetClazz) {
        try {
            T t = targetClazz.newInstance();
            BeanCopier copier = BeanCopier.create(source.getClass(), targetClazz, false);
            copier.copy(source, t, null);
            return t;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
