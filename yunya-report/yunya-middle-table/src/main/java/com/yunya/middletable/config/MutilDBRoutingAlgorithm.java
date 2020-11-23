package com.yunya.middletable.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.Collection;
import java.util.HashSet;

@Slf4j
public class MutilDBRoutingAlgorithm implements HintShardingAlgorithm<String> {

	@Override
	public Collection<String> doSharding(
			Collection<String> availableTargetNames, HintShardingValue<String> shardingValue) {

		log.info("shardingValue：{}", shardingValue);
		log.info("availableTargetNames：{}", availableTargetNames);
		Collection<String> shardingResult = new HashSet<>();
		for (String value : shardingValue.getValues()) {
			if (availableTargetNames.contains(value)) {
				shardingResult.add(value);
			}
		}
		return shardingResult;
	}
}
