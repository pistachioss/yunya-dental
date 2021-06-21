package com.yunya.report.ultimate.task;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.yunya.models.report.*;
import com.yunya.report.ultimate.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.reducing;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;
import static java.util.stream.Collectors.toSet;

/**
 * @description:
 * @author: xy
 * @date 2021/6/2 9:11
 **/
@Component
@Slf4j
@RestController
public class PatientManageTask {
    @Resource
    private BasePatientMapper basePatientMapper;
    @Resource
    private BaseBillMapper baseBillMapper;
    @Resource
    private BaseBillPayMapper baseBillPayMapper;
    @Resource
    private BasePatientMemberMapper basePatientMemberMapper;
    @Resource
    private BasePatientMemberOccurLogMapper patientMemberOccurLogMapper;
    @Resource
    private BaseTreatmentProcessMapper baseTreatmentProcessMapper;
    @Resource
    private PatientManageMapper patientManageMapper;
    @Resource(name = "customizeThreadPool")
    private ExecutorService taskThreadPool;

    @Scheduled(cron = "00 01 00 * * ?")
    @RequestMapping("/white/patientManage")
    public void patientTask() {
        long start = System.currentTimeMillis();
        Integer countTable = patientManageMapper.countTable();
        if (countTable <= 0) {
            this.init();
            long end = System.currentTimeMillis();
            log.info("患者管理新增时长[{}]分钟[{}]秒", (end - start) / 60000, ((end - start) % 60000) / 1000);
        } else {
            this.update();
            long end = System.currentTimeMillis();
            log.info("患者管理更新时长[{}]分钟[{}]秒", (end - start) / 60000, ((end - start) % 60000) / 1000);
        }
    }
    private void init() {
        CompletableFuture<List<BasePatient>> cf1 = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(BasePatient.class);
            example.selectProperties("patientId");
            return basePatientMapper.selectByExample(example);
        }, taskThreadPool);
        CompletableFuture<List<BasePatientMember>> cf2 = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(BasePatientMember.class);
            example.selectProperties("memberLevelId", "memberLevelName", "principalAmount", "bonusAmount", "type", "patientId");
            return basePatientMemberMapper.selectByExample(example);
        }, taskThreadPool);
        CompletableFuture<List<BaseBill>> cf3 = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(BaseBill.class);
            example.selectProperties("patientId", "receivedAmount", "debtAmount");
            return baseBillMapper.selectByExample(example);
        }, taskThreadPool);
        CompletableFuture<List<BaseTreatmentProcess>> cf4 = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(BaseTreatmentProcess.class);
            example.selectProperties("patientId");
            example.createCriteria().andIsNotNull("treatStartTime");
            return baseTreatmentProcessMapper.selectByExample(example);
        }, taskThreadPool);
        CompletableFuture.allOf(cf1, cf2, cf3, cf4).join();
        //计算患者余额
        List<BasePatientMember> basePatientMembers = cf2.join();
        Map<Integer, PatientManage> memberMap = memberMap(basePatientMembers);
        //计算账号费用
        List<BaseBill> bills = cf3.join();
        Map<Integer, PatientManage> billMap = billMap(bills);
        List<BaseTreatmentProcess> processes = cf4.join();
        Map<Integer, Long> processMap = processes.stream().collect(groupingBy(BaseTreatmentProcess::getPatientId, counting()));
        List<BasePatient> cf1Res = cf1.join();
        List<PatientManage> resultList = cf1Res.stream().map(key -> {
            Integer patientId = key.getPatientId();
            PatientManage patientManage = memberMap.get(patientId);
            PatientManage patientManage1 = billMap.get(patientId);
            Long treatCount = processMap.getOrDefault(patientId, 0L);
            if (patientManage != null) {
                patientManage.setPatientId(patientId);
                patientManage.setNumberOfVisits(treatCount.intValue());
                if (patientManage1 != null) {
                    patientManage.setCumulativeConsumption(patientManage1.getCumulativeConsumption());
                    patientManage.setTotalArrears(patientManage1.getTotalArrears());
                }
                return patientManage;
            } else if (patientManage1 != null) {
                patientManage1.setPatientId(patientId);
                patientManage1.setNumberOfVisits(treatCount.intValue());
                return patientManage1;
            } else {
                PatientManage patientManage2 = new PatientManage();
                patientManage2.setPatientId(patientId);
                patientManage2.setNumberOfVisits(treatCount.intValue());
                return patientManage2;
            }
        }).collect(toList());
        this.insertList(resultList);
    }

    private void insertList(List<PatientManage> resultList) {
        List<List<PatientManage>> partition = Lists.partition(resultList, 100);
        List<CompletableFuture<Void>> futures1 = Lists.newArrayListWithCapacity(partition.size());
        partition.forEach(list1 -> futures1.add(CompletableFuture.runAsync(() -> {
            log.info("新增患者管理线程：{}", Thread.currentThread().getName());
            patientManageMapper.insertList(list1);
        }, taskThreadPool)));
        CompletableFuture<Void> allFuture1 = CompletableFuture.allOf(futures1.toArray(new CompletableFuture[0]));
        allFuture1.thenAcceptAsync(cf -> futures1.forEach(CompletableFuture::join)).join();
    }

    private Map<Integer, PatientManage> memberMap(List<BasePatientMember> basePatientMembers) {
        return basePatientMembers.stream()
                .collect(groupingBy(BasePatientMember::getPatientId, collectingAndThen(toList(),
                        list -> {
                            PatientManage patientManage = new PatientManage();
                            Optional<BasePatientMember> first = list.stream().filter(obj -> obj.getType() == 0).findFirst();
                            first.ifPresent(obj -> {
                                patientManage.setMemberLevelId(obj.getMemberLevelId());
                                patientManage.setMemberLevelName(obj.getMemberLevelName());
                            });
                            Map<Integer, BigDecimal> collect = list.stream().collect(groupingBy(BasePatientMember::getType
                                    , reducing(BigDecimal.ZERO, obj -> obj.getPrincipalAmount().add(obj.getBonusAmount())
                                            , BigDecimal::add)));
                            patientManage.setMemberBalance(collect.get(0));
                            patientManage.setPrincipalBalance(collect.get(1));
                            return patientManage;
                        })));
    }

    private Map<Integer, PatientManage> billMap(List<BaseBill> bills) {
        return bills.stream().collect(groupingBy(BaseBill::getPatientId, collectingAndThen(toList(), list -> {
            PatientManage patientManage = new PatientManage();
            BigDecimal totalConsumeAmount = list.stream().map(BaseBill::getReceivedAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal totalOweAmount = list.stream().map(BaseBill::getDebtAmount).reduce(BigDecimal.ZERO, BigDecimal::add);
            patientManage.setCumulativeConsumption(totalConsumeAmount);
            patientManage.setTotalArrears(totalOweAmount);
            return patientManage;
        })));
    }

    private void update() {
        String startDate = LocalDate.now().minusDays(1).toString();
        CompletableFuture<List<BasePatient>> cf1 = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(BasePatient.class);
            example.selectProperties("patientId");
            example.createCriteria().andGreaterThanOrEqualTo("patientCrtTime", startDate);
            return basePatientMapper.selectByExample(example);
        }, taskThreadPool);
        CompletableFuture<List<BasePatientMember>> cf2 = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(BasePatientMemberOccurLog.class);
            example.selectProperties("patientId");
            example.createCriteria().andGreaterThanOrEqualTo("occurDate", startDate);
            List<BasePatientMemberOccurLog> list = patientMemberOccurLogMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.stream().map(BasePatientMemberOccurLog::getPatientId).collect(toSet());
            }
            return Sets.newHashSet();
        }, taskThreadPool).thenApplyAsync(patientIds -> {
            if (CollectionUtils.isNotEmpty(patientIds)) {
                Example example = new Example(BasePatientMember.class);
                example.selectProperties("memberLevelId", "memberLevelName", "principalAmount", "bonusAmount", "type", "patientId");
                example.createCriteria().andIn("patientId", patientIds).orGreaterThanOrEqualTo("cardOpeningDate", startDate);
                return basePatientMemberMapper.selectByExample(example);
            }
            return Lists.newArrayList();
        });
        CompletableFuture<List<BaseBill>> cf3 = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(BaseBillPay.class);
            example.selectProperties("billId");
            example.createCriteria().andGreaterThanOrEqualTo("payeeDate", startDate);
            List<BaseBillPay> list = baseBillPayMapper.selectByExample(example);
            if (CollectionUtils.isNotEmpty(list)) {
                return list.stream().map(BaseBillPay::getBillId).collect(toSet());
            }
            return Sets.newHashSet();
        }, taskThreadPool).thenApplyAsync(billIds -> {
            if (CollectionUtils.isNotEmpty(billIds)) {
                Example example = new Example(BaseBill.class);
                example.selectProperties("patientId");
                example.createCriteria().andIn("billId", billIds);
                List<BaseBill> baseBills = baseBillMapper.selectByExample(example);
                return baseBills.stream().map(BaseBill::getPatientId).collect(toSet());
            }
            return Lists.newArrayList();
        }).thenApplyAsync(patientIds -> {
            if (CollectionUtils.isNotEmpty(patientIds)) {
                Example example = new Example(BaseBill.class);
                example.selectProperties("patientId", "receivedAmount", "debtAmount");
                example.createCriteria().andIn("patientId", patientIds);
                return baseBillMapper.selectByExample(example);
            }
            return Lists.newArrayList();
        });
        CompletableFuture<List<BaseTreatmentProcess>> cf4 = CompletableFuture.supplyAsync(() -> {
            Example example = new Example(BaseTreatmentProcess.class);
            example.selectProperties("patientId");
            example.createCriteria().andGreaterThanOrEqualTo("treatStartTime", startDate);
            List<BaseTreatmentProcess> list = baseTreatmentProcessMapper.selectByExample(example);
            return list.stream().map(BaseTreatmentProcess::getPatientId).collect(toSet());
        }, taskThreadPool).thenApplyAsync(patientIds -> {
            if (CollectionUtils.isNotEmpty(patientIds)) {
                Example example = new Example(BaseTreatmentProcess.class);
                example.selectProperties("patientId");
                example.createCriteria().andIn("patientId", patientIds)
                        .andIsNotNull("treatStartTime");
                return baseTreatmentProcessMapper.selectByExample(example);
            }
            return Lists.newArrayList();
        });
        CompletableFuture<List<PatientManage>> cf5 = CompletableFuture.supplyAsync(() -> {
            return patientManageMapper.selectAll();
        }, taskThreadPool);
        CompletableFuture.allOf(cf1, cf2, cf3, cf4, cf5).join();

        //计算患者余额
        List<BasePatientMember> basePatientMembers = cf2.join();
        Map<Integer, PatientManage> memberMap = memberMap(basePatientMembers);
        //计算账号费用
        List<BaseBill> bills = cf3.join();
        Map<Integer, PatientManage> billMap = billMap(bills);
        List<BaseTreatmentProcess> processes = cf4.join();
        Map<Integer, Long> processMap = processes.stream().collect(groupingBy(BaseTreatmentProcess::getPatientId, counting()));
        List<BasePatient> cf1Res = cf1.join();
        List<PatientManage> manages = cf5.join();
        Map<Integer, PatientManage> collect = manages.stream().collect(toMap(PatientManage::getPatientId, Function.identity()));
        Set<Integer> addIds = cf1Res.stream().map(BasePatient::getPatientId).collect(toSet());
        //新增的患者
        if (CollectionUtils.isNotEmpty(addIds)) {
            List<PatientManage> insertList = addIds.stream()
                    .filter(id -> collect.get(id) == null)
                    .map(id -> {
                        PatientManage member = memberMap.get(id);
                        PatientManage bill = billMap.get(id);
                        Long treatCount = processMap.getOrDefault(id, 0L);
                        PatientManage patientManage = new PatientManage();
                        patientManage.setPatientId(id);
                        patientManage.setNumberOfVisits(treatCount.intValue());
                        if (member != null) {
                            patientManage.setMemberLevelId(member.getMemberLevelId());
                            patientManage.setMemberLevelName(member.getMemberLevelName());
                            patientManage.setMemberBalance(member.getMemberBalance());
                            patientManage.setPrincipalBalance(member.getPrincipalBalance());
                        }
                        if (bill != null) {
                            patientManage.setCumulativeConsumption(bill.getCumulativeConsumption());
                            patientManage.setTotalArrears(bill.getTotalArrears());
                        }
                        return patientManage;
                    }).collect(toList());
            log.info("需要新增的患者数量：{}", insertList.size());
            this.insertList(insertList);
        }
        Set<Integer> updateIds = Sets.newHashSet(billMap.keySet());
        updateIds.addAll(memberMap.keySet());
        updateIds.addAll(billMap.keySet());
        updateIds.addAll(processMap.keySet());
        List<PatientManage> updateList = updateIds.stream()
                .filter(id -> collect.get(id) != null)
                .map(id -> {
                    PatientManage update = collect.get(id);
                    PatientManage member = memberMap.get(id);
                    PatientManage bill = billMap.get(id);
                    Long treatCount = processMap.get(id);
                    if (treatCount != null) {
                        update.setNumberOfVisits(treatCount.intValue());
                    }
                    if (member != null) {
                        update.setMemberLevelId(member.getMemberLevelId());
                        update.setMemberLevelName(member.getMemberLevelName());
                        update.setMemberBalance(member.getMemberBalance());
                        update.setPrincipalBalance(member.getPrincipalBalance());
                    }
                    if (bill != null) {
                        update.setCumulativeConsumption(bill.getCumulativeConsumption());
                        update.setTotalArrears(bill.getTotalArrears());
                    }
                    return update;
                }).collect(toList());
        log.info("需要更新的患者数量：{}", updateList.size());
        this.updateList(updateList);
    }

    private void updateList(List<PatientManage> resultList) {
        List<List<PatientManage>> partition = Lists.partition(resultList, 100);
        List<CompletableFuture<Void>> futures1 = Lists.newArrayListWithCapacity(partition.size());
        partition.forEach(list1 -> futures1.add(CompletableFuture.runAsync(() -> {
            log.info("更新患者管理线程：{}", Thread.currentThread().getName());
            patientManageMapper.updateList(list1);
        }, taskThreadPool)));
        CompletableFuture<Void> allFuture1 = CompletableFuture.allOf(futures1.toArray(new CompletableFuture[0]));
        allFuture1.thenAcceptAsync(cf -> futures1.forEach(CompletableFuture::join)).join();
    }
}
