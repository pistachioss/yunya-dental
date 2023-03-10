package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 * @author xiangyang
 * @date 2020/10/10
 */
public enum ChoiceBenefitTypeEnum {
	/**
	 * 卡券优惠
	 */
	CARD_BENEFIT(0, "卡券优惠"),
	/**
	 * 授权折扣）
	 */
	AUTH_BENEFIT(1, "授权折扣"),
	/**
	 * 混搭优惠（价目使用卡券+商品使用折扣）
	 */
	MIX_MATCH_BENEFIT(2, "混搭优惠"),

	;

	private Integer code;
	private String value;

	ChoiceBenefitTypeEnum(Integer code, String value) {
		this.code = code;
		this.value = value;
	}

	public Integer getCode() {
		return code;
	}
	public String getValue() {
		return value;
	}

	/**
	 * 根据code获取value
	 * @param code code
	 * @return value
	 */
	public static String getValue(Integer code)
	{
		if(code != null)
		{
			for(ChoiceBenefitTypeEnum choiceBenefitTypeEnum : values())
			{
				if(Objects.equals(choiceBenefitTypeEnum.getCode(), code))
				{
					return choiceBenefitTypeEnum.getValue();
				}
			}
		}
		return null;
	}

	public boolean equals(Integer code)
	{
		return this.code.equals(code);
	}
}
