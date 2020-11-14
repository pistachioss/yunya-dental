package com.yunya.modules.employeeattend.biz;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.yunya.feign.employee_attend.form.AttendanceDeviceBindingQueryForm;
import com.yunya.feign.employee_attend.model.AttendanceDeviceBindingModel;
import com.yunya.feign.employee_attend.vo.AttendanceDeviceBindingVO;
import com.yunya.feign.rabbitmq.RemoteRabbitMqServiceFeign;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.feign.system.form.SysUserEmployeeModel;
import com.yunya.feign.system.vo.SysUserInfoDetail;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.constant.RedisConstants;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.redis.util.RedisUtils;
import com.yunya.models.employee_attend.AttendanceDeviceBinding;
import com.yunya.modules.employeeattend.mapper.AttendanceDeviceBindingMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

import static com.yunya.framework.common.constant.OperationCodeConstants.*;

/**
 * 简介：考勤设备绑定业务层
 *
 * @author: chenlin
 * @Description: 考勤设备绑定业务层
 * @Date: 2020/11/5 9:29
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class AttendanceDeviceBindingBiz extends BaseBiz<AttendanceDeviceBindingMapper, AttendanceDeviceBinding> {
    /** 注入对象 */
    @Autowired
    private RemoteSystemServiceFeign remoteSystemServiceFeign;
    @Autowired
    @Resource
    private RemoteRabbitMqServiceFeign mqServiceFeign;
    /** 注入对象 */
    @Autowired
    private RedisUtils redisUtils;
    /** 设备绑定短信验证码过期时间 */
    private static final long DEVICE_BINDING_AUTH_EXPIRE = 60;
    /** 设备已绑定 */
    private final static Integer DEVICE_BINDED = 1;
    /** 设备已解绑 */
    private final static Integer DEVICE_UNBIND = 0;

    /**
     * 分页查询员工考勤设备绑定列表
     *
     * @param queryForm 查询参数
     * @return
     */
    public PageInfo<AttendanceDeviceBindingVO> findBindingDeviceEmployeeList(AttendanceDeviceBindingQueryForm queryForm) {
        Map<Integer, Integer> attendanceDeviceBindingVOMap = new LinkedHashMap<>();

        // 日期范围过滤
        List<AttendanceDeviceBindingVO> attendanceDeviceBindingVOList = mapper.findBindingDeviceList(queryForm);
        attendanceDeviceBindingVOList.forEach(attendanceDeviceBindingVO -> {
            Integer userId = attendanceDeviceBindingVO.getUserId();
            Integer count = attendanceDeviceBindingVOMap.get(userId);
            if (count == null) {
                count = 0;
            }
            attendanceDeviceBindingVOMap.put(userId, ++count);
        });

        // 绑定次数过滤
        Integer bindingCount = queryForm.getBindingCount();
        if (bindingCount != null) {
            Iterator<Map.Entry<Integer, Integer>> it = attendanceDeviceBindingVOMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<Integer, Integer> entry = it.next();
                Integer count = entry.getValue();
                if (!count.equals(bindingCount)) {
                    it.remove();
                }
            }
        }

        List<AttendanceDeviceBindingVO> attendanceDeviceBindingVOS = attendanceDeviceBindingVOList.stream().filter(attendanceDeviceBindingVO -> {
            if (DEVICE_BINDED.equals(attendanceDeviceBindingVO.getBindingStatus())) {
                return true;
            }
            return false;
        }).collect(Collectors.toList());

        attendanceDeviceBindingVOS.forEach(attendanceDeviceBindingVO -> {
            Integer userId = attendanceDeviceBindingVO.getUserId();
            if (attendanceDeviceBindingVOMap.containsKey(userId)) {
                Integer count = attendanceDeviceBindingVOMap.get(userId);
                attendanceDeviceBindingVO.setBindingCount(count);
            }
        });

        // 必须查询有记录的员工
        List<AttendanceDeviceBindingVO> result = new ArrayList<>();
        // 员工姓名过滤
        SysUserEmployeeModel model = new SysUserEmployeeModel();
        model.setWhetherPage(queryForm.getWhetherPage());
        model.setPageNum(queryForm.getPageNum());
        model.setPageSize(queryForm.getPageSize());
        model.setKeyWord(queryForm.getUserName());
        Byte[] userStatus = {0, 1, 3};
        //离职状态
        model.setWorkStatus(userStatus);
        if (attendanceDeviceBindingVOMap!=null && !attendanceDeviceBindingVOMap.isEmpty()) {
            Set<Integer> userIds = attendanceDeviceBindingVOMap.keySet();
            model.setUserIds(userIds.toArray(new Integer[0]));
            List<SysUserInfoDetail> sysUserInfoDetailList = remoteSystemServiceFeign.findSysUserEmployeeInfoList(model);

            //组装主数据
            sysUserInfoDetailList.forEach(sysUserInfoDetail->{
                Integer userId = sysUserInfoDetail.getUserId();
                for (AttendanceDeviceBindingVO deviceBindingVO : attendanceDeviceBindingVOS) {
                    Integer bindUserId = deviceBindingVO.getUserId();
                    if (userId.equals(bindUserId)) {
                        deviceBindingVO.setUserName(sysUserInfoDetail.getName());
                        result.add(deviceBindingVO);
                        break;
                    }
                }
            });
        }
        return new PageInfo<>(result);
    }

    /**
     * 根据userid分页查询员工的考勤设备的绑定记录列表
     *
     * @param userId 用户id
     * @param queryForm 查询参数
     * @return
     */
    public PageInfo<AttendanceDeviceBindingVO> findEmployeeBindingDeviceList(Integer userId, AttendanceDeviceBindingQueryForm queryForm) {
        queryForm.setUserId(userId);
        if (queryForm.getWhetherPage()) {
            PageHelper.startPage(queryForm.getPageNum(), queryForm.getPageSize());
        }
        List<AttendanceDeviceBindingVO> attendanceDeviceBindingVOList = mapper.findBindingDeviceList(queryForm);
        return new PageInfo<>(attendanceDeviceBindingVOList);
    }

    /**
     * 根据userid查询员工当前绑定的考勤设备信息
     *
     * @param userId 用户id
     * @return
     */
    public AttendanceDeviceBindingVO findEmployeeBindingDevice(Integer userId) {
        SysUserInfoDetail sysUserInfoDetail = remoteSystemServiceFeign.findSysUserEmployeeInfoByUserId(userId);
        String name = sysUserInfoDetail.getName();
        String mobilePhone = sysUserInfoDetail.getMobilePhone();
        AttendanceDeviceBindingVO attendanceDeviceBindingVO = findEmployeeBindingDeviceById(userId);
        if (attendanceDeviceBindingVO != null) {
            attendanceDeviceBindingVO.setMobile(mobilePhone);
            attendanceDeviceBindingVO.setUserName(name);
        }
        return attendanceDeviceBindingVO;
    }

    private AttendanceDeviceBindingVO findEmployeeBindingDeviceById(Integer userId) {
        AttendanceDeviceBindingQueryForm queryForm = new AttendanceDeviceBindingQueryForm();
        queryForm.setWhetherPage(false);
        queryForm.setUserId(userId);
        queryForm.setBindingStatus(DEVICE_BINDED);
        List<AttendanceDeviceBindingVO> attendanceDeviceBindingVOList = mapper.findBindingDeviceList(queryForm);
        if (attendanceDeviceBindingVOList==null || attendanceDeviceBindingVOList.isEmpty()) {
            return null;
        }
        return attendanceDeviceBindingVOList.get(0);
    }

    /**
     * 考勤设备绑定
     *
     * @param attendanceDeviceBindingModel 考勤地址设置模型
     * @return
     */
    public ResponseResult deviceBinding(AttendanceDeviceBindingModel attendanceDeviceBindingModel) {
        // 短信验证码校验
        String verifyCode = attendanceDeviceBindingModel.getVerifyCode();
        String key = RedisConstants.ATTENDANCE_DEVICE_BINDING_AUTHORIZATION + attendanceDeviceBindingModel.getMobile();
        if (!redisUtils.hasKey(key)) {
            return ResponseUtil.fail(DATA_NOT_EXIST,"验证码过期,请重新发送",null);
        }
        String authCodeCache = redisUtils.get(key);
        if (!authCodeCache.equals(verifyCode)) {
            return ResponseUtil.fail(PARAMETERS_IS_ILLEGAL,"验证码无效,请重新发送",null);
        }
        redisUtils.delete(key);

        // 新增绑定记录
        Integer userId = attendanceDeviceBindingModel.getUserId();
        AttendanceDeviceBindingVO lastAttendanceDeviceBindingVO = findEmployeeBindingDeviceById(userId);
        String lastDeviceNumber = "";
        String firstNumber = attendanceDeviceBindingModel.getDeviceNumber();
        if (lastAttendanceDeviceBindingVO !=null) {
            lastDeviceNumber = lastAttendanceDeviceBindingVO.getDeviceNumber();
            firstNumber = lastAttendanceDeviceBindingVO.getFirstNumber();
        }
        AttendanceDeviceBinding attendanceDeviceBinding = new AttendanceDeviceBinding();
        Date now = new Date(System.currentTimeMillis());
        attendanceDeviceBinding.setBindingTime(now);
        attendanceDeviceBinding.setUserId(userId);
        attendanceDeviceBinding.setBindingStatus(DEVICE_BINDED);
        attendanceDeviceBinding.setCrtId(userId);
        attendanceDeviceBinding.setCrtTime(now);
        attendanceDeviceBinding.setUpdId(userId);
        attendanceDeviceBinding.setUpdTime(now);
        attendanceDeviceBinding.setDeviceNumber(attendanceDeviceBindingModel.getDeviceNumber());
        attendanceDeviceBinding.setFirstNumber(firstNumber);
        attendanceDeviceBinding.setOldNumber(lastDeviceNumber);
        mapper.insertSelective(attendanceDeviceBinding);

        // 将上次绑定解绑
        if (lastAttendanceDeviceBindingVO != null) {
            AttendanceDeviceBinding lastAttendanceDeivceBinding = new AttendanceDeviceBinding();
            lastAttendanceDeivceBinding.setUpdTime(now);
            lastAttendanceDeivceBinding.setUpdId(userId);
            lastAttendanceDeivceBinding.setBindingStatus(DEVICE_UNBIND);
            lastAttendanceDeivceBinding.setId(lastAttendanceDeviceBindingVO.getId());
            mapper.updateByPrimaryKeySelective(lastAttendanceDeivceBinding);

        }
        return ResponseUtil.success(null);
    }

    /**
     * @param mobile 手机号
     * 发送设备绑定短信验证码
     */
    public ResponseResult authorizationCode(String mobile) {
        String  messageCode = this.messageCodeGenerator();
        String key = RedisConstants.ATTENDANCE_DEVICE_BINDING_AUTHORIZATION + mobile;
        if (redisUtils.hasKey(key)) {
            return ResponseUtil.fail(OBJECT_EDIT_FAIL,"短信验证码已发送,稍后再试",null);
        }
        redisUtils.set(key, messageCode, DEVICE_BINDING_AUTH_EXPIRE);
        // @TODO 发送短信

        return ResponseUtil.success("短信验证码已发送",messageCode);
    }

    /**
     * 随机生成六位数，并且每位数都不重复
     * @return 返回短信验证码
     */
    private String messageCodeGenerator() {
        int[] array = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9};
        Random rand = new Random();
        for (int i = 10; i > 1; i--) {
            int index = rand.nextInt(i);
            int tmp = array[index];
            array[index] = array[i - 1];
            array[i - 1] = tmp;
        }
        int result = 0;
        for (int i = 0; i < 6; i++) {
            result = result * 10 + array[i];
        }
        if (String.valueOf(result).length() == 6) {
            return String.valueOf(result);
        } else {
            return String.valueOf(messageCodeGenerator());
        }
    }
}
