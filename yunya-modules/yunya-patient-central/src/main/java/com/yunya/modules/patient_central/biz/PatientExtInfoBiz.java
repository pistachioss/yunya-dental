package com.yunya.modules.patient_central.biz;

import com.google.common.collect.Lists;
import com.yunya.feign.report.domain.query.base.DateRangeQueryForm;
import com.yunya.feign.report.domain.vo.BasePatientBehaviorTagVO;
import com.yunya.feign.system.RemoteSystemServiceFeign;
import com.yunya.framework.common.biz.BaseBiz;
import com.yunya.framework.common.utils.DateUtil;
import com.yunya.framework.common.utils.StringHelper;
import com.yunya.models.patient_central.PatientExtInfo;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.patient_central.mapper.PatientExtInfoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

/**
 * 简单介绍:</br> 患者其他信息（标签,疾病史,过敏原） 业务成
 *
 * @author: WY
 * @date 2020/7/28 13:33
 * @description: 患者其他信息（标签,疾病史,过敏原） 增删查改
 * @since: 1.0.0
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class PatientExtInfoBiz extends BaseBiz<PatientExtInfoMapper, PatientExtInfo> {

    @Autowired
    private PatientExtInfoMapper patientExtInfoMapper;
    @Autowired
    private RemoteSystemServiceFeign systemServiceFeign;

    public List<PatientExtInfo> listByTagType(Collection<Integer> type) {
        Example example = new Example(PatientExtInfo.class);
        example.createCriteria()
                .andIn("dictItemId", type);
        return patientExtInfoMapper.selectByExample(example);
    }

    /**
     * 查询患者诊疗需求标签
     *
     * @param query
     * @return
     */
    public List<BasePatientBehaviorTagVO> findPatientTreatIntentionChangeTag(DateRangeQueryForm query) {
        List<BasePatientBehaviorTagVO> result = Lists.newArrayList();
        List<DictionaryItem> dictItems = systemServiceFeign.findDictItemByTypeName("患者诊疗需求");
        if (StringHelper.isNotEmpty(dictItems)) {
            Map<Integer, String> dictItemMap = dictItems.stream().collect(toMap(DictionaryItem::getId, DictionaryItem::getName));
            List<Integer> dictItemIds = dictItems.stream().filter(vo->DateUtil.betweenAnd(DateUtil.format(vo.getUpdTime()), query.getStartDate(), query.getEndDate())).map(DictionaryItem::getId).collect(Collectors.toList());
            List<PatientExtInfo> patientExtInfos = mapper.selectPatientTreatIntentionTag(query, dictItemIds);
            patientExtInfos.forEach(ext -> {
                // 空串表示无效标签，作为行为标签的变更事件
                String tagName = dictItemMap.getOrDefault(ext.getDictItemId(), StringHelper.EMPTY);
                BasePatientBehaviorTagVO vo = new BasePatientBehaviorTagVO();
                vo.setPatientId(ext.getPatientId());
                vo.setTagName(tagName);
                result.add(vo);
            });
        }
        return result;
    }
}
