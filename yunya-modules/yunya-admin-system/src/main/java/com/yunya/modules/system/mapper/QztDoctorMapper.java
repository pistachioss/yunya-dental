package com.yunya.modules.system.mapper;


import com.yunya.models.system.QztDoctor;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface QztDoctorMapper extends Mapper<QztDoctor> {
    QztDoctor selectByUserId(@Param("userId") Integer userId);
}