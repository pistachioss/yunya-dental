package com.yunya.feign.report.domain.vo;

import com.github.pagehelper.PageInfo;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * 简介：支持动态表头
 *
 * @author: chenlin
 * @Description: 支持动态表头
 * @Date: 2021/4/7 9:50
 * @since: 1.0.0
 */
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class DynamicHeaderPageInfo<T> extends PageInfo<T> implements Serializable {

    public DynamicHeaderPageInfo(List<T> list) {
        super(list);
    }

    public DynamicHeaderPageInfo() {
    }

    /** 表头 */
    @ApiModelProperty("表头")
    private String[] header;

    /** 表头关联映射*/
    @ApiModelProperty("表头关联映射")
    private Map<String, String> map;

    @ApiModelProperty("上下层表头关联映射")
    private Map<String, List<String>> contextMap;
}
