package com.yunya.modules.system.mapper;

import com.yunya.models.system.SysMenu;
import com.yunya.modules.system.form.MenuElementForm;
import com.yunya.modules.system.form.query.SysMenuQueryForm;
import com.yunya.modules.system.vo.SysMenuVO;
import com.yunya.modules.system.vo.tree.SysMenuElementTreeVO;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface SysMenuMapper extends Mapper<SysMenu> {

  /**
   * 根据条件查询系统菜单列表
   *
   * @param queryForm 参数封装
   * @return list
   */
  List<SysMenuVO> selectSysMenuListByExample(@Param("queryForm") SysMenuQueryForm queryForm);

  /**
   * 根据条件查询系统菜单及功能列表
   *
   * @param queryForm 参数封装
   * @return
   */
  List<SysMenuElementTreeVO> selectMenuElementTreeByExample(
      @Param("queryForm") MenuElementForm queryForm);
}
