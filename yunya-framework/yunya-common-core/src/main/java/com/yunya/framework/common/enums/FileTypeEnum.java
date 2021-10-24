package com.yunya.framework.common.enums;

import java.util.Objects;

/**
 * 简介：文件类型枚举
 *
 * @author: chenlin
 * @Description:
 * @Date: 2021/10/19 16:15
 * @since: 1.0.0
 */
public enum FileTypeEnum {
    PDF("PDF", (byte)1),
    DOC("DOC", (byte)2),
    JPG("JPG", (byte)3),
    PNG( "PNG", (byte)4),
    DOCX("DOCX", (byte)5);

    private final String value;
    private final Byte code;

    FileTypeEnum(String value, Byte code) {
        this.value = value;
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public Byte getCode() {
        return code;
    }

    public static Byte getCode(String value) {
        if (value != null) {
            for (FileTypeEnum item : values()) {
                if (Objects.equals(item.getValue(), value.toUpperCase())) {
                    return item.getCode();
                }
            }
        }
        return null;
    }

    public static String getValue(Byte code) {
        if (code != null) {
            for (FileTypeEnum item : values()) {
                if (Objects.equals(item.getCode(), code)) {
                    return item.getValue();
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
