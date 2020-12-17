package com.yunya.models.treatment_other;

import java.util.Date;
import javax.persistence.*;

@Table(name = "periodontal_records")
public class PeriodontalRecords {
    @Id
    private Integer id;

    /**
     * 门诊id
     */
    @Column(name = "company_id")
    private Integer companyId;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 医生ID
     */
    @Column(name = "doctor_id")
    private Integer doctorId;

    /**
     * 检查日期
     */
    @Column(name = "check_date")
    private Date checkDate;

    @Column(name = "Data_PdB1")
    private String dataPdb1;

    @Column(name = "Data_PdL1")
    private String dataPdl1;

    @Column(name = "Data_FI1")
    private String dataFi1;

    @Column(name = "Data_M1")
    private String dataM1;

    @Column(name = "Data_BIB1")
    private String dataBib1;

    @Column(name = "Data_BIL1")
    private String dataBil1;

    @Column(name = "Data_BIL2")
    private String dataBil2;

    @Column(name = "Data_BIB2")
    private String dataBib2;

    @Column(name = "Data_FI2")
    private String dataFi2;

    @Column(name = "Data_M2")
    private String dataM2;

    @Column(name = "Data_PdL2")
    private String dataPdl2;

    @Column(name = "Data_PdB2")
    private String dataPdb2;

    /**
     * 0:否 1：是
     */
    @Column(name = "is_delete")
    private Integer isDelete;

    @Column(name = "crt_id")
    private Integer crtId;

    @Column(name = "crt_time")
    private Date crtTime;

    @Column(name = "upd_id")
    private Integer updId;

    @Column(name = "upd_time")
    private Date updTime;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * 获取门诊id
     *
     * @return company_id - 门诊id
     */
    public Integer getCompanyId() {
        return companyId;
    }

    /**
     * 设置门诊id
     *
     * @param companyId 门诊id
     */
    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    /**
     * 获取患者ID
     *
     * @return patient_id - 患者ID
     */
    public Integer getPatientId() {
        return patientId;
    }

    /**
     * 设置患者ID
     *
     * @param patientId 患者ID
     */
    public void setPatientId(Integer patientId) {
        this.patientId = patientId;
    }

    /**
     * 获取医生ID
     *
     * @return doctor_id - 医生ID
     */
    public Integer getDoctorId() {
        return doctorId;
    }

    /**
     * 设置医生ID
     *
     * @param doctorId 医生ID
     */
    public void setDoctorId(Integer doctorId) {
        this.doctorId = doctorId;
    }

    /**
     * 获取检查日期
     *
     * @return check_date - 检查日期
     */
    public Date getCheckDate() {
        return checkDate;
    }

    /**
     * 设置检查日期
     *
     * @param checkDate 检查日期
     */
    public void setCheckDate(Date checkDate) {
        this.checkDate = checkDate;
    }

    /**
     * @return Data_PdB1
     */
    public String getDataPdb1() {
        return dataPdb1;
    }

    /**
     * @param dataPdb1
     */
    public void setDataPdb1(String dataPdb1) {
        this.dataPdb1 = dataPdb1;
    }

    /**
     * @return Data_PdL1
     */
    public String getDataPdl1() {
        return dataPdl1;
    }

    /**
     * @param dataPdl1
     */
    public void setDataPdl1(String dataPdl1) {
        this.dataPdl1 = dataPdl1;
    }

    /**
     * @return Data_FI1
     */
    public String getDataFi1() {
        return dataFi1;
    }

    /**
     * @param dataFi1
     */
    public void setDataFi1(String dataFi1) {
        this.dataFi1 = dataFi1;
    }

    /**
     * @return Data_M1
     */
    public String getDataM1() {
        return dataM1;
    }

    /**
     * @param dataM1
     */
    public void setDataM1(String dataM1) {
        this.dataM1 = dataM1;
    }

    /**
     * @return Data_BIB1
     */
    public String getDataBib1() {
        return dataBib1;
    }

    /**
     * @param dataBib1
     */
    public void setDataBib1(String dataBib1) {
        this.dataBib1 = dataBib1;
    }

    /**
     * @return Data_BIL1
     */
    public String getDataBil1() {
        return dataBil1;
    }

    /**
     * @param dataBil1
     */
    public void setDataBil1(String dataBil1) {
        this.dataBil1 = dataBil1;
    }

    /**
     * @return Data_BIL2
     */
    public String getDataBil2() {
        return dataBil2;
    }

    /**
     * @param dataBil2
     */
    public void setDataBil2(String dataBil2) {
        this.dataBil2 = dataBil2;
    }

    /**
     * @return Data_BIB2
     */
    public String getDataBib2() {
        return dataBib2;
    }

    /**
     * @param dataBib2
     */
    public void setDataBib2(String dataBib2) {
        this.dataBib2 = dataBib2;
    }

    /**
     * @return Data_FI2
     */
    public String getDataFi2() {
        return dataFi2;
    }

    /**
     * @param dataFi2
     */
    public void setDataFi2(String dataFi2) {
        this.dataFi2 = dataFi2;
    }

    /**
     * @return Data_M2
     */
    public String getDataM2() {
        return dataM2;
    }

    /**
     * @param dataM2
     */
    public void setDataM2(String dataM2) {
        this.dataM2 = dataM2;
    }

    /**
     * @return Data_PdL2
     */
    public String getDataPdl2() {
        return dataPdl2;
    }

    /**
     * @param dataPdl2
     */
    public void setDataPdl2(String dataPdl2) {
        this.dataPdl2 = dataPdl2;
    }

    /**
     * @return Data_PdB2
     */
    public String getDataPdb2() {
        return dataPdb2;
    }

    /**
     * @param dataPdb2
     */
    public void setDataPdb2(String dataPdb2) {
        this.dataPdb2 = dataPdb2;
    }

    /**
     * 获取0:否 1：是
     *
     * @return is_delete - 0:否 1：是
     */
    public Integer getIsDelete() {
        return isDelete;
    }

    /**
     * 设置0:否 1：是
     *
     * @param isDelete 0:否 1：是
     */
    public void setIsDelete(Integer isDelete) {
        this.isDelete = isDelete;
    }

    /**
     * @return crt_id
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * @param crtId
     */
    public void setCrtId(Integer crtId) {
        this.crtId = crtId;
    }

    /**
     * @return crt_time
     */
    public Date getCrtTime() {
        return crtTime;
    }

    /**
     * @param crtTime
     */
    public void setCrtTime(Date crtTime) {
        this.crtTime = crtTime;
    }

    /**
     * @return upd_id
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * @param updId
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * @return upd_time
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * @param updTime
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}