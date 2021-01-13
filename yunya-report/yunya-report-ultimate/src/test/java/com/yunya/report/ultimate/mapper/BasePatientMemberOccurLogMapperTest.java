package com.yunya.report.ultimate.mapper;

import com.yunya.feign.report.domain.query.StatementPatientCardRechargeDetailInfoQuery;
import com.yunya.feign.report.domain.query.StatementPatientCardRefundDetailQuery;
import com.yunya.feign.report.domain.vo.StatementPatientCardRechargeDetailVO;
import com.yunya.feign.report.domain.vo.StatementPatientCardRefundDetailVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

/**
 * 简介:
 *
 * @author: chow
 * @date: 2021/1/11 11:16
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BasePatientMemberOccurLogMapperTest {

  @Autowired private BasePatientMemberOccurLogMapper patientMemberOccurLogMapper;

  @Test
  public void find() {
    StatementPatientCardRechargeDetailInfoQuery query =
        new StatementPatientCardRechargeDetailInfoQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setStartDate("2020-10-01");
    query.setEndDate("2020-12-31");
    query.setCardType((byte) 0);
    query.setPatientKeyword("王x");
    query.setCardNum("H0004000041");
    query.setRechargeStartDate("2020-12-28");
    query.setRechargeEndDate("2020-12-28");
    List<StatementPatientCardRechargeDetailVO> vos =
        patientMemberOccurLogMapper.selectPatientCardRechargeDetailList(query);
    System.out.println(vos);
  }

  @Test
  public void find1() {
    StatementPatientCardRefundDetailQuery query = new StatementPatientCardRefundDetailQuery();
    query.setOrgId(35);
    query.setDateType((byte) 0);
    query.setStartDate("2020-10-01");
    query.setEndDate("2020-12-31");
    query.setCardType((byte) 0);
    // query.setPatientKeyword("王x");
    // query.setCardNum("H0004000041");
    // query.setRefundStartDate("");
    // query.setRefundEndDate("");
    List<StatementPatientCardRefundDetailVO> vos =
        patientMemberOccurLogMapper.selectPatientCardRefundDetailList(query);
    System.out.println(vos);
  }
}
