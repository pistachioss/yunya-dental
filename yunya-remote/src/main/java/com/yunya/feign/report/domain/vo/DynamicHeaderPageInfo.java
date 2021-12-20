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

    /** 表头，注意顺序 */
    @ApiModelProperty("表头：name1,name2,name3，注意顺序")
    private String[] header;

    /** 单层表头关联映射：Map<id, name1,name2,name3>，注意顺序，其中id数据行中的对象的键值一一对应*/
    @ApiModelProperty("单层表头关联映射：Map<id, name1,name2,name3>，注意顺序，其中id数据行中的对象的键值一一对应")
    private Map<String, String> map;

    /**两层表头，上下层之间的关联关系：Map<id, [name1,name2,name]> ，注意顺序，其中id数据行中的对象的键值一一对应*/
    @ApiModelProperty("两层表头，上下层之间的关联关系：Map<id, [name1,name2,name]>，注意顺序，其中id数据行中的对象的键值一一对应")
    private Map<String, List<String>> contextMap;
}
