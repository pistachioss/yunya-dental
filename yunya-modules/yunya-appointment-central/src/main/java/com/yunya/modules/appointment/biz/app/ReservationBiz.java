package com.yunya.modules.appointment.biz.app;

import com.yunya.feign.appointment.domain.model.ReservationModel;
import com.yunya.feign.appointment.domain.query.ReservationCodeQuery;
import com.yunya.feign.appointment.domain.query.ReservationQuery;
import com.yunya.feign.appointment.vo.ReservationVo;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.vo.OrganizationInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.OperationCodeConstants;
import com.yunya.framework.common.context.BaseContextHandler;
import com.yunya.framework.common.exception.ClientServiceException;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.EntityUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.appointment.Reservation;
import com.yunya.modules.appointment.biz.web.ReservationLimitBiz;
import com.yunya.modules.appointment.code.AppointmentError;
import com.yunya.modules.appointment.mapper.ReservationMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservationBiz extends BaseBiz<ReservationMapper, Reservation> {

    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;
    @Resource
    private ReservationLimitBiz limitBiz;

    @Autowired
    private ReservationCodeBiz reservationCodeBiz;

    @Transactional(rollbackFor = Exception.class)
    public ResponseResult add(ReservationModel model) {
        ReservationCodeQuery query = new ReservationCodeQuery();
        query.setCode(model.getCode());
        if (!reservationCodeBiz.find(query)) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_FAIL.getCode(),AppointmentError.APPOINTMENT_FAIL.getMessage(),null);
        }
        Reservation build = EntityUtils.build(model, Reservation.class);
        build.setCrtName(BaseContextHandler.getUsername());
        //获取剩余号失败
        if (!limitBiz.tryAcquire(build.getReservationLimitId())) {
            throw ClientServiceException.wrap(AppointmentError.APPOINT_REMAINING_LACK);
        }
        int status = mapper.insertSelective(build);
        if (status <= 0) {
            return ResponseUtil.fail(AppointmentError.APPOINTMENT_FAIL.getCode(),AppointmentError.APPOINTMENT_FAIL.getMessage(),null);
        }
        return ResponseUtil.success(build.getId());
    }

    public ResponseResult update(ReservationModel model) {
        Reservation build1 = mapper.selectByPrimaryKey(model.getId());
        if (build1 == null){
            throw new ClientServiceException("修改的数据不存在！", OperationCodeConstants.DATA_NOT_EXIST);
        }
        Reservation build = EntityUtils.build(model, Reservation.class);
        build.setUpdName(BaseContextHandler.getUsername());
        build.setUpdTime(new Date(System.currentTimeMillis()));
        int status = mapper.updateByPrimaryKeySelective(build);
        return ResponseUtil.success(status);
    }

    public ResponseResult delete(Integer id) {
        int status = mapper.deleteByPrimaryKey(id);
        return ResponseUtil.success(status);
    }

    public List<ReservationVo> list(ReservationQuery query) {
        List<ReservationVo> results = mapper.findByCondition(query);
//        if (StringHelper.isNotEmpty(results)) {
//            setOrgInfo(results);
//        }
        return results;
    }
    private void setOrgInfo(List<ReservationVo> results) {
        List<Integer> orgIds = results.stream().mapToInt(ReservationVo::getOrgId).boxed().collect(Collectors.toList());
        List<OrganizationInfoDetail> orgInfos = systemServiceFeign.findOrgInfoInIds(orgIds);
        if (StringHelper.isNotEmpty(orgInfos)) {
            results.stream().forEach(ReservationVo -> {
                OrganizationInfoDetail orgInfo = orgInfos.stream().filter(e -> e.getId().equals(ReservationVo.getOrgId())).findFirst().get();
                ReservationVo.setOrgName(orgInfo.getBrandName() + "(" + orgInfo.getAbbreviation() + ")");
            });
        }
    }

    /**
     *  查询已提交预约
     * @param orgName 门诊
     * @param configDate 配置日期
     * @param whole 是否查询整月
     * @param yearMonth 整月
     */
    public List<Reservation> submittedLimit(String orgName, List<LocalDate> configDate, boolean whole, LocalDate yearMonth) {
        return mapper.submittedLimit(orgName, whole, configDate, yearMonth);
    }
}
