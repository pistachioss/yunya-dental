package com.yunya.framework.common.enums;

import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author: chenlin
 * @date: 2022/12/14 16:12
 * @description: 西湖益联保服务套餐
 * @since: 1.0.0
 */
public enum YiLianBaoServicePackageEnum {

    ADULT_ORALCARE_PACKAGE(1, "成人口腔护理套餐"),
    CHILD_ORALCARE_PACKAGE(2, "儿童口腔护理套餐"),
    ;

    /** 套餐简码 */
    private Integer code;
    /** 套餐名称 */
    private String name;

    YiLianBaoServicePackageEnum(Integer code, String name) {
        this.code = code;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Integer getCode() {
        return code;
    }

    public static String getName(Integer code) {
        if (code != null) {
            for (YiLianBaoServicePackageEnum item : values()) {
                if (Objects.equals(item.getCode(), code)) {
                    return item.getName();
                }
            }
        }
        return null;
    }

    public boolean equals(Integer code)
    {
        return this.code.equals(code);
    }

    /**
     * 枚举值被包含在，当包含两个及其以上枚举值时报错
     *
     * @param name
     * @return
     */
    public static String contains(String name) {
        String result = null;
        if (StringHelper.isNotEmpty(name)) {
            YiLianBaoServicePackageEnum[] values = values();
            List<String> names = new ArrayList<>();
            for (YiLianBaoServicePackageEnum item : values) {
                if (StringHelper.contains(name, item.getName())) {
                    names.add(item.getName());
                }
            }
            if (names.size()>1 && names.size()==values.length) {
                // 全部命中时
                throw new ClientServiceException("【" + names.get(0) + "】和【" + names.get(1) + "】不能同时选择", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            }
            result = names.get(0);
        }
        return result;
    }
}
