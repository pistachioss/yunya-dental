package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.BillCategoryIncomeQuery;
import com.yunya.feign.report.domain.query.BillDetailIncomeDetailQuery;
import com.yunya.feign.report.domain.query.EmployeePersonalActualWorkloadDetailQuery;
import com.yunya.feign.report.domain.query.EmployeeWorkloadQuery;
import com.yunya.feign.report.domain.vo.BillTariffIncomeDetailVO;
import com.yunya.feign.report.domain.vo.CategoryInfoIncomeVO;
import com.yunya.feign.report.domain.vo.EmployeePersonalActualWorkloadDetailVO;
import com.yunya.feign.report.domain.vo.EmployeeWorkloadVO;
import com.yunya.models.report.BaseBillDetail;
import org.apache.ibatis.annotations.Param;
import tk.mybatis.mapper.common.Mapper;

import java.util.List;

public interface BaseBillDetailMapper extends Mapper<BaseBillDetail> {

  /**
   * 根据条件查询账单项目收入明细列表
   *
   * @param query 查询条件
   * @return list
   */
  List<BillTariffIncomeDetailVO> selectBillDetailIncomeList(
      @Param("query") BillDetailIncomeDetailQuery query);

  /**
   * 根据条件查询员工工作量列表
   *
   * @param query 查询条件(按月查)
   * @return list
   */
  List<EmployeeWorkloadVO> selectEmployeeWorkloadListByMonth(
      @Param("query") EmployeeWorkloadQuery query);

  /**
   * 根据条件查询员工工作量列表
   *
   * @param query 查询条件(按年查)
   * @return list
   */
  List<EmployeeWorkloadVO> selectEmployeeWorkloadListByYear(
      @Param("query") EmployeeWorkloadQuery query);

  /**
   * 根据条件查询项目分类收入汇总列表
   *
   * @param query 查询条件
   * @return List<CategoryInfoIncomeVO>
   */
  List<CategoryInfoIncomeVO> selectCategoryIncomeList(
      @Param("query") BillCategoryIncomeQuery query);

  /**
   * 根据条件查询员工个人实收工作量明细列表
   *
   * @param query 查询条件
   * @return List<EmployeePersonalActualWorkloadDetailVO>
   */
  List<EmployeePersonalActualWorkloadDetailVO> selectEmployeePersonalActualWorkloadDetailList(
      @Param("query") EmployeePersonalActualWorkloadDetailQuery query);
}
