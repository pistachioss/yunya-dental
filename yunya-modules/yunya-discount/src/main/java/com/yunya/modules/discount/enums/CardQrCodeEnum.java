package com.yunya.modules.discount.enums;

public enum CardQrCodeEnum {
    QR_CODE_NORMAL(0, "正常"),
    QR_CODE_INVALID(1, "失效"),
    QR_CODE_EXPIRED(2, "过期"),
    QR_CODE_DESTROY(3, "核销"),
    QR_CODE_OTHER(4, "其他"),
    ;

    private Integer code;
    private String value;

    CardQrCodeEnum(Integer code, String value) {
        this.code = code;
        this.value = value;
    }

    public Integer getCode() {
        return code;
    }
    public String getValue() {
        return value;
    }

    public boolean equals(Integer code)
    {
        return this.code.equals(code);
    }
}
