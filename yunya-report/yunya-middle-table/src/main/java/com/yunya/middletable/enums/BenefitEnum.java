package com.yunya.middletable.enums;

import java.util.Objects;

public enum BenefitEnum {
	CARD_BENEFIT(0, "产品优惠"),
	AUTH_BENEFIT(1, "授权优惠"),
	;
	private Integer code;
	private String value;

	BenefitEnum(Integer code, String value) {
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
			for(BenefitEnum benefitEnum : values())
			{
				if(Objects.equals(benefitEnum.getCode(), code))
				{
					return benefitEnum.getValue();
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
