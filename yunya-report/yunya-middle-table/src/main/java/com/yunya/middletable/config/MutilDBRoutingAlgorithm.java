package com.yunya.middletable.config;

import org.apache.shardingsphere.api.sharding.hint.HintShardingAlgorithm;
import org.apache.shardingsphere.api.sharding.hint.HintShardingValue;

import java.util.Collection;
import java.util.HashSet;

public class MutilDBRoutingAlgorithm implements HintShardingAlgorithm<String> {

  @Override
  public Collection<String> doSharding(
      Collection<String> availableTargetNames, HintShardingValue<String> shardingValue) {

    System.out.println("shardingValue=" + shardingValue);
    System.out.println("availableTargetNames=" + availableTargetNames);
    Collection<String> shardingResult = new HashSet<>();
    for (String value : shardingValue.getValues()) {
      if (availableTargetNames.contains(value)) {
        shardingResult.add(value);
      }
    }
    return shardingResult;
  }
}
