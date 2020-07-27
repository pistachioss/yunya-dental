package com.clinic.discount.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Table(name = "voucher_detail")
@ApiModel("代金券明细")
public class VoucherDetail {
    /**
     * 主键
     */
    @Id
    @ApiModelProperty("主键ID")
    @GeneratedValue(generator = "JDBC")
    private Integer id;

    /**
     * 代金券ID
     */
    @Column(name = "voucher_id")
    @ApiModelProperty("代金券ID")
    @NotNull(message = "代金券ID不能为空")
    private Integer voucherId;

    /**
     * 类型 0:价目表类目,1:价目表明细,2:商品类目,3:商品明细,4全部范围
     */
    @ApiModelProperty("类型 0:价目表类目,1:价目表明细,2:商品类目,3:商品明细,4全部范围")
    @NotNull(message = "范围类型不能为空")
    private Integer type;

    /**
     * 明细ID 根据type类型来查找
     */
    @Column(name = "detail_id")
    @ApiModelProperty("明细ID 根据type类型来查找")
    private Integer detailId;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建人姓名
     */
    @Column(name = "crt_name")
    private String crtName;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 更新人
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 更新时间
     */
    @Column(name = "upd_name")
    private String updName;

    /**
     * 更新时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 乐观锁
     */
    private Integer revision;

    /**
     * 获取主键
     *
     * @return id - 主键
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键
     *
     * @param id 主键
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取代金券ID
     *
     * @return voucher_id - 代金券ID
     */
    public Integer getVoucherId() {
        return voucherId;
    }

    /**
     * 设置代金券ID
     *
     * @param voucherId 代金券ID
     */
    public void setVoucherId(Integer voucherId) {
        this.voucherId = voucherId;
    }

    /**
     * 获取类型 0:价目表类目,1:价目表明细,2:商品类目,3:商品明细,4全部范围
     *
     * @return type - 类型 0:价目表类目,1:价目表明细,2:商品类目,3:商品明细,4全部范围
     */
    public Integer getType() {
        return type;
    }

    /**
     * 设置类型 0:价目表类目,1:价目表明细,2:商品类目,3:商品明细,4全部范围
     *
     * @param type 类型 0:价目表类目,1:价目表明细,2:商品类目,3:商品明细,4全部范围
     */
    public void setType(Integer type) {
        this.type = type;
    }

    /**
     * 获取明细ID 根据type类型来查找
     *
     * @return detail_id - 明细ID 根据type类型来查找
     */
    public Integer getDetailId() {
        return detailId;
    }

    /**
     * 设置明细ID 根据type类型来查找
     *
     * @param detailId 明细ID 根据type类型来查找
     */
    public void setDetailId(Integer detailId) {
        this.detailId = detailId;
    }

    /**
     * 获取创建人
     *
     * @return crt_id - 创建人
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人
     *
     * @param crtId 创建人
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * 获取创建人姓名
     *
     * @return crt_name - 创建人姓名
     */
    public String getCrtName() {
        return crtName;
    }

    /**
     * 设置创建人姓名
     *
     * @param crtName 创建人姓名
     */
    public void setCrtName(String crtName) {
        this.crtName = crtName;
    }

    /**
     * 获取创建时间
     *
     * @return crt_time - 创建时间
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * 设置创建时间
     *
     * @param crtTime 创建时间
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * 获取更新人
     *
     * @return upd_id - 更新人
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置更新人
     *
     * @param updId 更新人
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取更新时间
     *
     * @return upd_name - 更新时间
     */
    public String getUpdName() {
        return updName;
    }

    /**
     * 设置更新时间
     *
     * @param updName 更新时间
     */
    public void setUpdName(String updName) {
        this.updName = updName;
    }

    /**
     * 获取更新时间
     *
     * @return upd_time - 更新时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置更新时间
     *
     * @param updTime 更新时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }

    /**
     * 获取乐观锁
     *
     * @return revision - 乐观锁
     */
    public Integer getRevision() {
        return revision;
    }

    /**
     * 设置乐观锁
     *
     * @param revision 乐观锁
     */
    public void setRevision(Integer revision) {
        this.revision = revision;
    }
}