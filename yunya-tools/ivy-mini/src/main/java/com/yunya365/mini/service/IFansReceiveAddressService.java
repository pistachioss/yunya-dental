package com.yunya365.mini.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.yunya.feign.ivy_mini.domain.form.ModifyAddressForm;
import com.yunya.feign.ivy_mini.domain.model.AddAddressModel;
import com.yunya.feign.ivy_mini.domain.vo.AddressListVO;
import com.yunya365.mini.entity.FansReceiveAddress;

import java.util.List;

/**
 * <p>
 * 用户收货地址表 服务类
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-20
 */
public interface IFansReceiveAddressService extends IService<FansReceiveAddress> {

    /**
     * 添加地址
     * @param model:
     */
    void addAddress(AddAddressModel model);

    /**
     * 修改地址
     * @param form:
     */
    void modifyAddress(ModifyAddressForm form);

    /**
     * 收货列表
     * @return List<AddressListVO>
     */
    List<AddressListVO> listAddress();

    /**
     * 删除地址
     * @param receiveId:
     */
    void deleteAddress(Integer receiveId);

    /**
     * 获取用户默认地址
     * @param fansId:
     * @return AddressListVO
     */
    FansReceiveAddress getDefaultAddress(Integer fansId);
}
