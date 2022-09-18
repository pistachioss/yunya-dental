package com.yunya.feign.ivy_mini.domain.vo;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.List;

/**
 * 简介:
 *
 * @author: ylx
 * @date: 2022/6/20
 * @description:
 */
@Data
public class CustomerListVO {
    private String errcode;
    private String errmsg;

    private List<CustomerVO> accountList;
}
