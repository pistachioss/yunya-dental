package com.yunya.models.treatment_other;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.Date;

@Table(name = "x_upload_file")
public class XUploadFile {
    @Id
    private Integer id;

    /**
     * 文件名
     */
    @Column(name = "file_name")
    private String fileName;

    /**
     * 文件类型：1-pdf; 2-doc; 3-jgp; 4-png; ; 5-docx
     */
    @Column(name = "file_type")
    private Byte fileType;

    /**
     * 数据源id
     */
    @Column(name = "source_id")
    private Integer sourceId;

    /**
     * 数据来源：1-treat_plan；2-其他
     */
    @Column(name = "source_type")
    private Byte sourceType;

    /**
     * 文件资源定位路径
     */
    @Column(name = "file_location")
    private String fileLocation;

    /**
     * 文件完整路径
     */
    @Column(name = "file_path")
    private String filePath;

    /**
     * 是否启用
     */
    private Boolean inservice;

    /**
     * 创建人
     */
    @Column(name = "crt_id")
    private Integer crtId;

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
     * 获取文件名
     *
     * @return file_name - 文件名
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * 设置文件名
     *
     * @param fileName 文件名
     */
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    /**
     * 获取文件类型：1-pdf; 2-doc; 3-jgp; 4-png; 5-docx
     *
     * @return file_type - 文件类型：1-pdf; 2-doc; 3-jgp; 4-png; 5-docx
     */
    public Byte getFileType() {
        return fileType;
    }

    /**
     * 设置文件类型：1-pdf; 2-doc; 3-jgp; 4-png; 5-docx
     *
     * @param fileType 文件类型：1-pdf; 2-doc; 3-jgp; 4-png; 5-docx
     */
    public void setFileType(Byte fileType) {
        this.fileType = fileType;
    }

    /**
     * 获取数据源id
     *
     * @return source_id - 数据源id
     */
    public Integer getSourceId() {
        return sourceId;
    }

    /**
     * 设置数据源id
     *
     * @param sourceId 数据源id
     */
    public void setSourceId(Integer sourceId) {
        this.sourceId = sourceId;
    }

    /**
     * 获取数据来源：1-treat_plan；2-其他
     *
     * @return source_type - 数据来源：1-treat_plan；2-其他
     */
    public Byte getSourceType() {
        return sourceType;
    }

    /**
     * 设置数据来源：1-treat_plan；2-其他
     *
     * @param sourceType 数据来源：1-treat_plan；2-其他
     */
    public void setSourceType(Byte sourceType) {
        this.sourceType = sourceType;
    }

    /**
     * 获取文件资源定位路径
     *
     * @return file_location - 文件资源定位路径
     */
    public String getFileLocation() {
        return fileLocation;
    }

    /**
     * 设置文件资源定位路径
     *
     * @param fileLocation 文件资源定位路径
     */
    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    /**
     * 获取文件完整路径
     *
     * @return file_path - 文件完整路径
     */
    public String getFilePath() {
        return filePath;
    }

    /**
     * 设置文件完整路径
     *
     * @param filePath 文件完整路径
     */
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    /**
     * @return inservice
     */
    public Boolean getInservice() {
        return inservice;
    }

    /**
     * @param inservice
     */
    public void setInservice(Boolean inservice) {
        this.inservice = inservice;
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
}