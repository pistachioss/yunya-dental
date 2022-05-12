package com.yunya.framework.common.utils;

import org.springframework.util.ObjectUtils;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 简介：
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/10/26 14:37
 * @since: 1.0.0
 */
public class SortUtil<T> {

    /**
     * 排序
     *
     * @return
     */
    public static <T> List<T> sort(List<T> list, Comparator comparator) {
        if (StringHelper.isNotEmpty(list)) {
            return (List<T>) list.stream().sorted(comparator).collect(Collectors.toList());
        }
        return list;
    }

    /**
     * 多字段排序（正序）
     *
     * @param keyExtractors 排序字段的lambda列表
     * @param <T>
     * @param <U>
     * @return
     */
    public static <T, U extends Comparable<? super U>> Comparator<T> comparing(
            Function<? super T, ? extends U>... keyExtractors)
    {
        Objects.requireNonNull(keyExtractors);
        return (Comparator<T> & Serializable)
                (c1, c2) -> {
                    for (Function<? super T, ? extends U> keyExtractor : keyExtractors) {
                        U a1 = keyExtractor.apply(c1);
                        U a2 = keyExtractor.apply(c2);
                        if (!ObjectUtils.isEmpty(a1) && ObjectUtils.isEmpty(a2)) {
                            return 1;
                        } else if (ObjectUtils.isEmpty(a1) && !ObjectUtils.isEmpty(a2)) {
                            return -1;
                        } else if (ObjectUtils.isEmpty(a1) && ObjectUtils.isEmpty(a2)) {
                            return 0;
                        }
                        int cmp = a1.compareTo(a2);
                        if (cmp != 0) {
                            return cmp;
                        }
                    }
                    return 0;
                };
    }
}
