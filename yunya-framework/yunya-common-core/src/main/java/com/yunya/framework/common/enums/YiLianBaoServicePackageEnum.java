package com.yunya.framework.common.enums;

import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.StringHelper;

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

    public static String contains(String name) {
        String result = null;
        if (StringHelper.isNotEmpty(name)) {
            YiLianBaoServicePackageEnum[] values = values();
            int count = 0;
            for (YiLianBaoServicePackageEnum item : values) {
                if (StringHelper.contains(name, item.getName())) {
                    result = item.getName();
                    count++;
                }
            }
            if (count == values.length - 1) {
                // 全部命中时
                throw new ClientServiceException("【" + ADULT_ORALCARE_PACKAGE.getName() + "】和【" + CHILD_ORALCARE_PACKAGE.getName() + "】不能同时选择", OperationCodeConstants.PARAMETERS_IS_ILLEGAL);
            }
        }
        return result;
    }
}
