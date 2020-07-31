package com.yunya365.aliyunoss.form;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Data
@ToString
@ApiModel("资源文件获取URL 参数模型")
public class OssUrlForm {

    @ApiModelProperty(value = "saas模式：注册公司ID，定制模式：为0或固定值", dataType = "int")
    private Integer companyId;

    @ApiModelProperty(value = "资源分类ID，1:诊所,2:员工,3:患者,4:优惠活动，其它将放入0:临时文件夹", dataType = "int")
    private Integer ossCategory;

    @ApiModelProperty(value = "资源分类类别所对应对象ID", dataType = "int")
    private Integer objectId;

    @ApiModelProperty("资源文件存储名称，包括后缀")
    private String ossFilename;

    @ApiModelProperty(value = "是否缩略图", dataType = "Boolean")
    private Boolean isThumb;
}
