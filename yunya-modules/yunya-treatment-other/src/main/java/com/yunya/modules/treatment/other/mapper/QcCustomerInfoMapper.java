package com.yunya.modules.treatment.other.mapper;

import com.yunya.models.treatment_other.QcCustomerInfo;
import tk.mybatis.mapper.common.Mapper;

public interface QcCustomerInfoMapper extends Mapper<QcCustomerInfo> {

    void saveByPrimaryKeySelective(QcCustomerInfo data);
}