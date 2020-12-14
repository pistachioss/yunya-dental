package com.yunya.models.treatment_other;

import java.util.Date;
import javax.persistence.*;

public class Periodontalrecords {
    @Id
    @Column(name = "PeriodontalRecordID")
    private String periodontalrecordid;

    @Column(name = "CompID")
    private String compid;

    @Column(name = "ClinID")
    private String clinid;

    @Column(name = "PatientID")
    private String patientid;

    @Column(name = "EmpID")
    private String empid;

    @Column(name = "CheckDate")
    private Date checkdate;

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

    @Column(name = "CreateUser")
    private String createuser;

    @Column(name = "CreateTime")
    private Date createtime;

    @Column(name = "UpdateUser")
    private String updateuser;

    @Column(name = "UpdateTime")
    private Date updatetime;

    @Column(name = "IsDeleted")
    private Boolean isdeleted;

    /**
     * @return PeriodontalRecordID
     */
    public String getPeriodontalrecordid() {
        return periodontalrecordid;
    }

    /**
     * @param periodontalrecordid
     */
    public void setPeriodontalrecordid(String periodontalrecordid) {
        this.periodontalrecordid = periodontalrecordid;
    }

    /**
     * @return CompID
     */
    public String getCompid() {
        return compid;
    }

    /**
     * @param compid
     */
    public void setCompid(String compid) {
        this.compid = compid;
    }

    /**
     * @return ClinID
     */
    public String getClinid() {
        return clinid;
    }

    /**
     * @param clinid
     */
    public void setClinid(String clinid) {
        this.clinid = clinid;
    }

    /**
     * @return PatientID
     */
    public String getPatientid() {
        return patientid;
    }

    /**
     * @param patientid
     */
    public void setPatientid(String patientid) {
        this.patientid = patientid;
    }

    /**
     * @return EmpID
     */
    public String getEmpid() {
        return empid;
    }

    /**
     * @param empid
     */
    public void setEmpid(String empid) {
        this.empid = empid;
    }

    /**
     * @return CheckDate
     */
    public Date getCheckdate() {
        return checkdate;
    }

    /**
     * @param checkdate
     */
    public void setCheckdate(Date checkdate) {
        this.checkdate = checkdate;
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
     * @return CreateUser
     */
    public String getCreateuser() {
        return createuser;
    }

    /**
     * @param createuser
     */
    public void setCreateuser(String createuser) {
        this.createuser = createuser;
    }

    /**
     * @return CreateTime
     */
    public Date getCreatetime() {
        return createtime;
    }

    /**
     * @param createtime
     */
    public void setCreatetime(Date createtime) {
        this.createtime = createtime;
    }

    /**
     * @return UpdateUser
     */
    public String getUpdateuser() {
        return updateuser;
    }

    /**
     * @param updateuser
     */
    public void setUpdateuser(String updateuser) {
        this.updateuser = updateuser;
    }

    /**
     * @return UpdateTime
     */
    public Date getUpdatetime() {
        return updatetime;
    }

    /**
     * @param updatetime
     */
    public void setUpdatetime(Date updatetime) {
        this.updatetime = updatetime;
    }

    /**
     * @return IsDeleted
     */
    public Boolean getIsdeleted() {
        return isdeleted;
    }

    /**
     * @param isdeleted
     */
    public void setIsdeleted(Boolean isdeleted) {
        this.isdeleted = isdeleted;
    }
}