package com.yunya.feign.wechat.domain.vo;

import lombok.Data;

import java.util.List;

/**
 * @description:
 * @author: xy
 * @date 2021/3/26 10:14
 **/
@Data
public class WxKfAllVo {
    private List<WxKfOnlineVo> kf_online_list;
    private List<WxKfListVo> kf_list;
}
