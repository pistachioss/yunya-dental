package com.yunya.modules.discount.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;


import java.util.Date;

/**
 * @author 杨柳絮
 * @className CouponAllocateVO
 * @description
 * @date 2020/8/21 12:56
 */
@Data
public class CouponAllocateVO {
    private Integer id;
    /**
     * 卡券配给数量
     */
    private Integer allocateNum;
    /**
     * 配给时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date allocateDate;
    /**
     * 分配人Id
     */
    private Integer allocateUserId;
    /**
     * 分配人姓名
     */
    private String allocateUserName;
    /**
     * 是否配给
     */
    private Boolean isAllocate;

    private Integer crtId;
}
