package com.yunya.modules.system.mapper;


import com.yunya.models.system.BjDoctor;
import com.yunya.models.system.QztDoctor;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

public interface BjDoctorMapper extends Mapper<BjDoctor> {
    BjDoctor selectByUserId(@Param("userId") Integer userId);
}