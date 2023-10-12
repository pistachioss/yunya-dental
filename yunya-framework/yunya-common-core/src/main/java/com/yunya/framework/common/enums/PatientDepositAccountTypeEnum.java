package com.yunya.framework.common.enums;

import com.yunya.framework.common.utils.StringHelper;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * 特殊入账方式枚举
 *
 * @author: chenlin
 * @date: 2023/2/3 19:48
 * @description:
 * @since: 1.0.0
 */
public enum PatientDepositAccountTypeEnum {
    /** 会员卡 */
    MEMBER(false, 0, "会员卡", "H", 5),
    /** 预付款 */
    NORMAL_PREPAYMENT(false, 1, "预付款", "Y", 6),
    /** 正畸预付款 */
    ORTHADANTIC_PREPAYMENT(true, 2, "正畸预付款", "ZY", 100),
    /** 美白预付款 */
    WHITENING_PREPAYMENT(true, 3, "美白预付款", "MY", 101),
//    /** 种植预付款 */
//    IMPLANT_PREPAYMENT(true,4, "种植预付款", "ZZY", null),
    /** 全程医疗支付 */
    QCYL_PREPAYMENT(false,4, "全程医疗支付", "QCYL", 102),
    ;

    /** 是否专项 */
    private Boolean isSpecial;

    /** 分类类型 */
    private Integer type;

    /** 分类名称 */
    private String name;

    /** 生成编号时使用的前缀 */
    private String prefix;

    /** 关联入账方式id */
    private Integer accountItemId;

    PatientDepositAccountTypeEnum(Boolean isSpecial, Integer type, String name, String prefix, Integer accountItemId) {
        this.isSpecial = isSpecial;
        this.type = type;
        this.name = name;
        this.prefix = prefix;
        this.accountItemId = accountItemId;
    }

    public static Stream<PatientDepositAccountTypeEnum> values(boolean isSpecial) {
        return Stream.of(values()).filter(item->item.isSpecial.equals(isSpecial));
    }

    public static List<PatientDepositAccountTypeEnum> prepaymentValues() {
        return Stream.of(NORMAL_PREPAYMENT, ORTHADANTIC_PREPAYMENT, WHITENING_PREPAYMENT).collect(Collectors.toList());
    }

    /**
     * 是否属于预付款账号类型
     *
     * @param type
     * @return
     */
    public static boolean isPrepaymentType(Integer type) {
        return prepaymentValues().stream().filter(item->item.equals(type)).findAny().isPresent();
    }

    /**
     * 是否属于专项预付款账号类型
     *
     * @param type
     * @return
     */
    public static boolean isSpPrepaymentType(Integer type) {
        return values(true).filter(item->item.equals(type)).findAny().isPresent();
    }

    /**
     * 根据accountItemId返回关联的储蓄账号类型
     *
     * @param accountItemId
     * @return
     */
    public static PatientDepositAccountTypeEnum getTypeEnumRelId(Integer accountItemId) {
        if (StringHelper.isNotNull(accountItemId)) {
            for (PatientDepositAccountTypeEnum item : depositAccounts()) {
                if (accountItemId.equals(item.getAccountItemId())) {
                    return item;
                }
            }
        }
        return null;
    }

    public static List<PatientDepositAccountTypeEnum> depositAccounts() {
        return Stream.of(MEMBER, NORMAL_PREPAYMENT, ORTHADANTIC_PREPAYMENT, WHITENING_PREPAYMENT).collect(Collectors.toList());
    }

    /**
     * 判断给定的accountItemId是否关联预付款类型type
     *
     * @param accountItemId
     * @return
     */
    public static Boolean isPrepaymentRelId(Integer accountItemId) {
        if (StringHelper.isNotNull(accountItemId)) {
            for (PatientDepositAccountTypeEnum item : values()) {
                if (accountItemId.equals(item.getAccountItemId())) {
                    return true;
                }
            }
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public Integer getType() {
        return type;
    }

    public String getPrefix() {
        return prefix;
    }

    public Integer getAccountItemId() {
        return accountItemId;
    }

    public static PatientDepositAccountTypeEnum getTypeEnum(Integer type) {
        if (type != null) {
            for (PatientDepositAccountTypeEnum item : values()) {
                if (Objects.equals(item.getType(), type)) {
                    return item;
                }
            }
        }
        return null;
    }

    public boolean equals(Integer type)
    {
        return this.type.equals(type);
    }
}
