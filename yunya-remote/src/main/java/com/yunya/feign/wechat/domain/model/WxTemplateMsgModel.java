package com.yunya.feign.wechat.domain.model;

import com.yunya.feign.wechat.enums.TemplateEnum;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

/**
 * @author xiangyang
 * @date 2021/04/20
 */
@Data
public class WxTemplateMsgModel {

  /**
   *  <keword(1~n), value>
   */
  @ApiModelProperty(value = "推送消息需要的参数，可不填", required = false)
  private Map<String, Object> paramMap;
  @ApiModelProperty(value = "推送给哪个患者", required = true)
  @NotNull(message = "被推送患者不能为空")
  private Integer patientId;
  @ApiModelProperty(value = "推送类型，根据业务选择", required = true)
  @NotNull(message = "推送类型不能为空！")
  private TemplateEnum templateEnum;
}
