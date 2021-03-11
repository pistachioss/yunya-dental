package com.yunya.modules.system.biz;

import cn.hutool.core.bean.BeanUtil;
import com.yunya.feign.system.form.AppVersionAddForm;
import com.yunya.feign.system.form.AppVersionCheckForm;
import com.yunya.feign.system.form.AppVersionEditForm;
import com.yunya.feign.system.vo.AppVersionVO;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.model.ResponseResult;
import com.yunya.framework.common.utils.ResponseUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.system.AppVersion;
import com.yunya.modules.system.mapper.AppVersionMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @program: yunya-dental
 * @description: APP端版本控制
 * @author: LHB
 * @create: 2021-03-09 16:30
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class AppVersionBiz extends BaseBiz<AppVersionMapper, AppVersion> {
    /**
     * 新增版本信息
     * @param form 新增表单信息
     * @return ResponseResult
     */
    public ResponseResult<AppVersionAddForm> addAppVersion(AppVersionAddForm form) {
        int i = this.mapper.addAppVersion(form);
        if (i > 0) {
            return ResponseUtil.success();
        }
        return ResponseUtil.success("新增版本信息失败",null);
    }

    /**
     * 版本更新检查
     * @param form 参数表单
     * @return 版本信息
     */
    public ResponseResult<AppVersionVO> checkUpdate(AppVersionCheckForm form) {
        AppVersionVO appVersionVO = this.mapper.lastReleaseApp(form);
        if (appVersionVO != null) {
            String applicationVersion = form.getApplicationVersion();
            String releaseVersion = appVersionVO.getReleaseVersion();
            Boolean aBoolean = this.checkVersion(applicationVersion, releaseVersion);
            if (aBoolean == null) {
                return ResponseUtil.success("版本号不能为空！",null);
            } else if (aBoolean) {
                return ResponseUtil.success(appVersionVO);
            } else {
                return ResponseUtil.success();
            }
        }
        return ResponseUtil.success();
    }

    /**
     * 根据系统名称查询版本信息列表
     * @param osName 系统名称
     * @return 信息列表
     */
    public ResponseResult<List<AppVersion>> findVersionList(String osName) {
        if (StringHelper.isNotBlank(osName)) {
            AppVersion query = new AppVersion();
            query.setOsName(osName);
            List<AppVersion> select = this.mapper.select(query);
            return ResponseUtil.success(select);
        }
        return ResponseUtil.success();
    }

    /**
     * 更新APP版本信息
     * @param form 表单
     * @return ResponseResult
     */
    public ResponseResult editAppVersion(AppVersionEditForm form) {
        AppVersion editForm = new AppVersion();
        BeanUtil.copyProperties(form,editForm);
        int updateByPrimaryKeySelective = this.mapper.updateByPrimaryKeySelective(editForm);
        if (updateByPrimaryKeySelective > 0) {
            return ResponseUtil.success();
        }
        return ResponseUtil.success("更新失败",null);
    }

    /**
     * 判断APP是否升级更新
     * @param applicationVersion 当前运行的程序版本号
     * @param releaseVersion 最新版本号
     * @return true-执行更新；false-不执行更新
     */
    private Boolean checkVersion(String applicationVersion,String releaseVersion) {
        final int lENGTH = 3;
        if (StringHelper.isEmpty(applicationVersion) || StringHelper.isEmpty(releaseVersion)) {
            return null;
        }
        Integer[] applicationVersionIntArr = this.paseVersion(applicationVersion);
        Integer[] releaseVersionIntArr = this.paseVersion(releaseVersion);
        for (int i = 0; i < lENGTH; i++) {
            if (releaseVersionIntArr[i] > applicationVersionIntArr[i]) {
               return true;
            }
        }
        return false;
    }

    /**
     * 将字符串版本号转化为整型数组
     * @param versionStr 字符串版本号
     * @return Integer[]
     */
    private Integer[] paseVersion(String versionStr) {
        if (StringHelper.isNotBlank(versionStr)) {
            String[] versionStrs = versionStr.split("\\.");
            if (versionStrs != null && versionStrs.length > 0) {
                Integer[] versionArrs = new Integer[versionStrs.length];
                for (int i = 0; i < versionStrs.length; i++) {
                    versionArrs[i] = Integer.valueOf(versionStrs[i]);
                }
                return versionArrs;
            }
        }
        return new Integer[0];
    }
}
