package com.yunya.framework.common.enums;

import java.util.Arrays;
import java.util.List;

/**
 * @auther: xy
 * @date: 2023/5/17
 */
public enum ProfessionEnum {
    Profession_001("卫生", "医生护理"),
    Profession_002("教师,文教", "科研教师"),
    Profession_003("治安人员", "公务员"),
    Profession_004("IT工程师", "it信息技术"),
    Profession_005("建筑工程业（土木工程）", "建筑地产"),
    Profession_006("新闻、出版、广告业,娱乐业,资讯", "传媒文艺"),
    Profession_007("金融业", "金融业"),
    Profession_008("服务业", "咨询培训"),
    Profession_009("餐旅业", "旅游休闲"),
    Profession_010("交通运输业", "交通运输"),
    Profession_011("军人", "军人"),
    Profession_012("制造业", "制造业"),
    Profession_013("个体经营,微商", "创业/自由职业"),
    Profession_014("xxx", "其他"),
    ;

    private String code;
    private String value;

    ProfessionEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    /**
     * 根据code获取value
     *
     * @param code code
     * @return value
     */
    public static String getValue(String code) {
        if (code != null) {
            for (ProfessionEnum useWayEnum : values()) {
                List<String> codes = Arrays.asList(useWayEnum.getCode().split(","));
                if (codes.contains(code)) {
                    return useWayEnum.getValue();
                }
            }
        }
        return Profession_014.getValue();
    }

    public String getCode() {
        return code;
    }

    public String getValue() {
        return value;
    }

    public boolean equals(String code) {
        return this.code.equals(code);
    }
}
