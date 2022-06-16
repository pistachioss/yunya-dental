package com.yunya365.mini.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import com.yunya.feign.ivy_mini.domain.form.ModifyAddressForm;
import com.yunya.feign.ivy_mini.domain.model.AddAddressModel;
import com.yunya.feign.ivy_mini.domain.vo.AddressListVO;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya365.mini.entity.FansReceiveAddress;
import com.yunya365.mini.mapper.FansReceiveAddressMapper;
import com.yunya365.mini.service.IFansReceiveAddressService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static com.yunya365.mini.enums.IvyMiniError.*;

/**
 * <p>
 * 用户收货地址表 服务实现类
 * </p>
 *
 * @author xiangyang
 * @since 2022-05-20
 */
@Service
public class FansReceiveAddressServiceImpl extends ServiceImpl<FansReceiveAddressMapper, FansReceiveAddress> implements IFansReceiveAddressService {

    @Override
    public void addAddress(AddAddressModel model) {
        Integer fansId = Integer.valueOf(BaseContextHandler.getUserID());
        FansReceiveAddress address = BeanCopierUtils.generalCopyBean(model, FansReceiveAddress.class);
        address.setFansId(fansId);
        if (model.getDefaultStatus()) {
            updateDefaultStatus(fansId);
        }
        baseMapper.insert(address);
    }

    @Override
    public void modifyAddress(ModifyAddressForm form) {
        Integer fansId = Integer.valueOf(BaseContextHandler.getUserID());
        FansReceiveAddress receiveAddress = baseMapper.selectById(form.getId());
        if (Objects.isNull(receiveAddress)) {
            throw ClientServiceException.wrap(RECEIVE_NOT_EXIST);
        }
        FansReceiveAddress address = BeanCopierUtils.generalCopyBean(form, FansReceiveAddress.class);
        if (form.getDefaultStatus()) {
            updateDefaultStatus(fansId);
        }
        baseMapper.updateById(address);
    }

    @Override
    public List<AddressListVO> listAddress() {
        Integer fansId = Integer.valueOf(BaseContextHandler.getUserID());
        List<FansReceiveAddress> list = baseMapper.selectList(Wrappers.lambdaQuery(FansReceiveAddress.class)
                .eq(FansReceiveAddress::getFansId, fansId));
        return BeanCopierUtils.listGeneralCopyBean(list, AddressListVO.class);
    }

    @Override
    public void deleteAddress(Integer receiveId) {
        FansReceiveAddress receiveAddress = baseMapper.selectById(receiveId);
        if (Objects.isNull(receiveAddress)) {
            throw ClientServiceException.wrap(RECEIVE_NOT_EXIST);
        }
        baseMapper.deleteById(receiveId);
    }

    @Override
    public FansReceiveAddress getDefaultAddress(Integer fansId) {
        return ChainWrappers.lambdaQueryChain(baseMapper)
                .eq(FansReceiveAddress::getFansId, fansId)
                .eq(FansReceiveAddress::getDefaultStatus, 1).one();
    }

    private void updateDefaultStatus(Integer fansId) {
        ChainWrappers.lambdaUpdateChain(baseMapper)
                .set(FansReceiveAddress::getDefaultStatus, 0)
                .eq(FansReceiveAddress::getFansId, fansId)
                .eq(FansReceiveAddress::getDefaultStatus, 1).update();
    }
}
