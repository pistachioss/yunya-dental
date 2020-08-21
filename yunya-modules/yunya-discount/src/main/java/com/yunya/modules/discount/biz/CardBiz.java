package com.yunya.modules.discount.biz;

import com.github.pagehelper.*;
import com.google.common.collect.*;
import com.yunya.feign.discount.domain.bo.*;
import com.yunya.feign.discount.domain.model.*;
import com.yunya.feign.discount.domain.query.*;
import com.yunya.feign.discount.domain.vo.*;
import com.yunya.feign.system.*;
import com.yunya.feign.system.vo.*;
import com.yunya.framework.common.biz.*;
import com.yunya.framework.common.model.*;
import com.yunya.framework.common.utils.*;
import com.yunya.models.discount.*;
import com.yunya.modules.discount.config.*;
import com.yunya.modules.discount.enums.*;
import com.yunya.modules.discount.mapper.*;
import lombok.extern.slf4j.*;
import org.springframework.stereotype.*;
import org.springframework.transaction.annotation.*;

import javax.annotation.*;
import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;

import static com.yunya.modules.discount.enums.DiscountError.*;
import static java.util.stream.Collectors.*;

/**
 * 描述:
 *
 * @author xiangyang
 * @create 2020-08-17
 */
@Service
@Slf4j
public class CardBiz extends BaseBiz<CardMapper, Card> {

    @Resource
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private CouponCommonInfoMapper couponMapper;
    @Resource
    private CouponAllocateMapper allocateMapper;

    /**
     * 产品生成分配分页查询
     *
     * @param query 查询参数
     * @return 分页结果
     */
    public PageInfo<GenerateAllocatePageVo> getCouponAllocatePage(CouponAllocateQuery query) {
        Page<GenerateAllocatePageBo> page = PageHelper.startPage(query.getPageNum(), query.getPageSize());
        //分页查询
        couponMapper.listBatchAllocateByParam(query.getKeyword(), query.getCouponTypeList());
        //属性转换
        List<GenerateAllocatePageVo> list = page.getResult().stream().map(this::allocateBoConvertVo).collect(toList());
        PageInfo<GenerateAllocatePageVo> pageInfo = new PageInfo<>(list);
        pageInfo.setTotal(page.getTotal());
        pageInfo.setPageNum(page.getPageNum());
        return pageInfo;
    }

    @Transactional(rollbackFor = Exception.class)
    public ResponseResult generateAllocate(GenerateAllocateModel allocateModel) throws InterruptedException {
        String couponCode = allocateModel.getCouponCode();
        LocalDateTime submitDate = allocateModel.getSubmitDate();
        Integer couponId = allocateModel.getCouponId();
        //所有组织的卡券分配信息
        List<ClinicAllocateModel> allocateList = allocateModel.getAllocateList();
        List<Integer> couponAllocateIds = allocateList.stream().map(ClinicAllocateModel::getCouponAllocateId)
                                                  .collect(toList());
        //1. 校验卡券是否生成
        int count = allocateMapper.countGenerateByParam(couponId, submitDate);
        if (count > 0) {
            log.warn("【卡券生成失败】：[{}]该批次[{}]已有诊所生成卡券", couponId, submitDate);
            return ResponseUtil.error(CARD_IS_GENERATED);
        }
        int allocateCount = mapper.countByAllocateId(couponAllocateIds);
        if (allocateCount > 0) {
            log.warn("【卡券生成失败】：[{}]该批次[{}]已有诊所生成卡券", couponId, submitDate);
            return ResponseUtil.error(CARD_IS_GENERATED);
        }
        //卡券最后一条记录
        int maxNum = mapper.getMaxNumByCouponId(couponId);
        //2. 计算每个诊所卡券生成信息
        List<AllocateNumBo> numBoList = calculateNumber(allocateList, maxNum);
        //3. 创建线程分配卡券
        ThreadPoolManager poolManager = ThreadPoolManager.getsInstance();
        CountDownLatch latch = new CountDownLatch(numBoList.size());
        for (AllocateNumBo numBo : numBoList) {
            Future<Integer> taskResult = poolManager.submit(() -> {
                return createEntity(numBo, allocateModel, numBo.getStartIndex(), numBo.getCount(), latch);
            });
        }
        latch.await();
        //停止线程
        poolManager.shutdown(5, TimeUnit.SECONDS);
        return null;
    }

    private Integer createEntity(AllocateNumBo numBo, GenerateAllocateModel allocateModel,
                              int startIndex, int count, CountDownLatch latch) {
        try {
            Card card;
            AtomicInteger generateNum = new AtomicInteger(startIndex);
            List<Card> entityList = Lists.newArrayListWithCapacity(count);
            for (int i = 0; i < count; i++) {
                card = new Card();
                card.setOrgId(numBo.getOrgId());
                card.setCouponId(allocateModel.getCouponId());
                card.setCouponAllocateId(numBo.getCouponAllocateId());
                card.setCardNumber(String.format(allocateModel.getCouponCode() + "%06d", generateNum.incrementAndGet()));
                entityList.add(card);
            }
            return entityList.size();
        } finally {
            log.info("组织[{}]卡券信息已初始化", numBo.getOrgId());
            latch.countDown();
        }
    }

    public List<AllocateNumBo> calculateNumber(List<ClinicAllocateModel> allocateList, int maxNum) {
        AllocateNumBo numBo;
        List<AllocateNumBo> list = Lists.newArrayListWithCapacity(allocateList.size());
        for (ClinicAllocateModel model : allocateList) {
            numBo = AllocateNumBo.getInstance();
            numBo.setOrgId(model.getOrgId());
            numBo.setCouponAllocateId(model.getCouponAllocateId());
            numBo.setStartIndex(maxNum+1);
            numBo.setCount(model.getAllocateNum());
            maxNum += model.getAllocateNum();
            list.add(numBo);
        }
        return list;
    }


    private GenerateAllocatePageVo allocateBoConvertVo(GenerateAllocatePageBo bo) {
        //属性复制
        GenerateAllocatePageVo vo = BeanCopierUtils.generalCopyBean(bo, GenerateAllocatePageVo.class);
        //提交人信息
        SysUserInfoDetail submitUser = systemServiceFeign.findSysUserEmployeeInfoByUserId(bo.getSubmitUserId());
        //配给人信息
        if (bo.getAllocateUserId() != null) {
            SysUserInfoDetail allocateUser = systemServiceFeign.findSysUserEmployeeInfoByUserId(bo.getAllocateUserId());
            vo.setAllocateUserName(allocateUser == null ? null : allocateUser.getName());
        }
        vo.setSubmitterName(submitUser == null ? null : submitUser.getName());
        vo.setCouponTypeName(CouponTypeEnum.getValue(bo.getCouponType()));
        return vo;
    }
}
