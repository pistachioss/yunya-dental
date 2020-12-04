package com.yunya365.aliyunoss.enums;

public enum BucketFolderEnum {

    ERROR(0, "其他，设置错误时", "error"),
    CLINIC(1, "门诊，比如诊所形象图、证照", "clinic"),
    EMPLOYEE(2, "员工，比如头像、证照", "employee"),
    PATIENT(3, "患者，比如头像、照片影像", "patient"),
    DISCOUNT(4, "优惠产品，比如设计搞、说明文档", "discount"),
    ATTENDENCE(5, "考勤，比如请假说明图片", "attendence");


    private Integer id;
    private String name;
    private String folder;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getBaseFolder(Integer objectId) {
        return folder + '/' + objectId + '/';
    }

    public String getBaseFolder(Integer companyId, Integer objectId) {
        String sub = getBaseFolder(objectId);
        if(companyId < 1){
            return sub;
        }
        return companyId.toString() + '/' + sub;
    }

    BucketFolderEnum(Integer id, String name, String folder) {
        this.id = id;
        this.name = name;
        this.folder = folder;
    }

    @Override
    public String toString() {
        return "BucketFolderEnum{" + "id=" + id + ", name='" + name + '\'' + '}';
    }
}
