package com.yunya.feign.discount.domain.form;

import com.yunya.feign.discount.valid.CardSoldGroupSequenceProvider;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.hibernate.validator.group.GroupSequenceProvider;

import javax.validation.constraints.*;
import java.io.Serializable;
import java.util.List;

/**
 * @author xiangyang
 * @date 2020/8/26
 */
@Data
public class MiniCardSoldForm {
   private CardSoldForm form;
    private Integer orgId;
    private Integer loginUserId;
}
