package com.yunya.feign.ivy_mini.domain.bo;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 手机验证码缓存
 *
 * @author xiangyang
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsCacheBO {
    private String phoneNumber;
    private String captcha;
    private LocalDateTime createTime;
}
