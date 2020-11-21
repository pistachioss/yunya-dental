package com.yunya.modules.treatment.other.mapper;

import com.yunya.feign.treatment_other.domain.query.XRayFilmQuery;
import com.yunya.feign.treatment_other.domain.vo.ToothRootCountVo;
import com.yunya.feign.treatment_other.domain.vo.ToothRootVo;
import com.yunya.models.treatment_other.XRayFilm;
import com.yunya.feign.treatment_other.domain.vo.XRayFilmVo;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface XRayFilmMapper extends Mapper<XRayFilm> {
    /**
     * 查询图片列表
     * @param query 查找条件
     * @return 返回数据列表
     */
    List<XRayFilmVo> findList(@Param("query") XRayFilmQuery query);

    /**
     * 批量添加图片
     * @param list 图片列表
     * @return 返回去影响行数
     */
    Integer addBatch(@Param("list") List<XRayFilm> list);

    /**
     * 条件查询牙根尖图列表
     * @param patientId 患者ID
     * @param toothNo 牙位编号(请求类型为2时,必传)
     * @return 牙根尖图列表
     */
    List<ToothRootVo>findToothRootPhotos(
                                         @Param("patientId") Integer patientId,
                                         @Param("toothNo") Integer toothNo);

    /**
     * 牙位根尖片数量(APP)用
     * @param patientId 患者ID
     * @return 返回列表
     */
    List<ToothRootCountVo> findToothRootCountByPatientId(@Param("patientId") Integer patientId);
}