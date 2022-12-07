package com.yunya.modules.appointment.controller.app;

import com.yunya.feign.appointment.domain.query.ReservationLimitAppQuery;
import com.yunya.feign.appointment.domain.query.ReservationLimitQuery;
import com.yunya.feign.appointment.vo.ReservationLimitVO;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.BeanCopierUtils;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.modules.appointment.biz.web.ReservationLimitBiz;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.text.ParseException;
import java.util.Date;

/**
 * @program: yunya-dental
 * @description: 预约意向登记控制
 **/
@RestController
@RequestMapping("/reservation/limit")
@Api(tags = "预约意向登记流量管理-app")
public class ReservationLimitAppController {

    @Resource
    private ReservationLimitBiz limitBiz;

    @ApiOperation("获取当月预约登记流量")
    @PostMapping("/app")
    public ResponseResult<ReservationLimitVO> list(@RequestBody @Validated ReservationLimitAppQuery query) throws ParseException {
        ReservationLimitQuery limitAppQuery = BeanCopierUtils.generalCopyBean(query, ReservationLimitQuery.class);
        limitAppQuery.setConfigDate(new Date());
        return ResponseUtil.success(limitBiz.list(limitAppQuery));
    }

}
