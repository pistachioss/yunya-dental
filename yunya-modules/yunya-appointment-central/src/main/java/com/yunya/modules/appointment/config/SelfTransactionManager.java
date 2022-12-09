package com.yunya.modules.appointment.config;

import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;

import javax.annotation.Resource;

@Component
public class SelfTransactionManager {
    //获取事务源
    @Resource
    private PlatformTransactionManager platformTransactionManager;

    @Resource
    private TransactionDefinition transactionDefinition;
    /**
     * 手动开启事务
     */
    public TransactionStatus begin() {
        TransactionStatus transactionStatus = platformTransactionManager.getTransaction(transactionDefinition);
        return transactionStatus;
    }

    /**
     * 提交事务
     */
    public void commit(TransactionStatus transactionStatus) {
        platformTransactionManager.commit(transactionStatus);
    }

    /**
     * 回滚事务
     */
    public void rollBack(TransactionStatus transactionStatus) {
        platformTransactionManager.rollback(transactionStatus);
    }
}
