package com.yunya.models.report;

import javax.persistence.*;

@Table(name = "base_account_item")
public class BaseAccountItem {
    /**
     * 支付方式明细ID
     */
    @Id
    @Column(name = "account_item_id")
    private Integer accountItemId;

    /**
     * 支付方式明细名称
     */
    @Column(name = "account_item_name")
    private String accountItemName;

    /**
     * 支付方式分类ID
     */
    @Column(name = "account_type_id")
    private Integer accountTypeId;

    /**
     * 支付方式分类名称
     */
    @Column(name = "account_type_name")
    private String accountTypeName;

    /**
     * 获取支付方式明细ID
     *
     * @return account_item_id - 支付方式明细ID
     */
    public Integer getAccountItemId() {
        return accountItemId;
    }

    /**
     * 设置支付方式明细ID
     *
     * @param accountItemId 支付方式明细ID
     */
    public void setAccountItemId(Integer accountItemId) {
        this.accountItemId = accountItemId;
    }

    /**
     * 获取支付方式明细名称
     *
     * @return account_item_name - 支付方式明细名称
     */
    public String getAccountItemName() {
        return accountItemName;
    }

    /**
     * 设置支付方式明细名称
     *
     * @param accountItemName 支付方式明细名称
     */
    public void setAccountItemName(String accountItemName) {
        this.accountItemName = accountItemName;
    }

    /**
     * 获取支付方式分类ID
     *
     * @return account_type_id - 支付方式分类ID
     */
    public Integer getAccountTypeId() {
        return accountTypeId;
    }

    /**
     * 设置支付方式分类ID
     *
     * @param accountTypeId 支付方式分类ID
     */
    public void setAccountTypeId(Integer accountTypeId) {
        this.accountTypeId = accountTypeId;
    }

    /**
     * 获取支付方式分类名称
     *
     * @return account_type_name - 支付方式分类名称
     */
    public String getAccountTypeName() {
        return accountTypeName;
    }

    /**
     * 设置支付方式分类名称
     *
     * @param accountTypeName 支付方式分类名称
     */
    public void setAccountTypeName(String accountTypeName) {
        this.accountTypeName = accountTypeName;
    }
}