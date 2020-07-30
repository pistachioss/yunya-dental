package com.yunya.models.emr;

import java.util.Date;
import javax.persistence.*;

@Table(name = "medical_orthodontics_record")
public class MedicalOrthodonticsRecord {
    /**
     * 主键ID
     */
    @Id
    private Integer id;

    /**
     * 患者ID
     */
    @Column(name = "patient_id")
    private Integer patientId;

    /**
     * 面部对称 0:基本对称,1:不对称
     */
    @Column(name = "facial_symmetry")
    private Boolean facialSymmetry;

    /**
     * 露龈笑 0：无，1：轻度，2：严重
     */
    @Column(name = "gummy_smile")
    private Byte gummySmile;

    /**
     * 颏部偏移 0：正常，1：左偏，2：右偏
     */
    @Column(name = "chin_deflection")
    private Byte chinDeflection;

    /**
     * 唇肌 0：正常，1：紧张，2：松弛
     */
    @Column(name = "lip_muscle")
    private Byte lipMuscle;

    /**
     * 侧貌 0：直面型，1：凸面型，2：凹面型
     */
    private Byte profile;

    /**
     * 颏部外形 0：正常，1：不足，2：过度
     */
    @Column(name = "chin_shape")
    private Byte chinShape;

    /**
     * 牙列
     */
    private String dentition;

    /**
     * 磨牙左侧程度 I，II，III
     */
    @Column(name = "dentition_left")
    private Byte dentitionLeft;

    /**
     * 磨牙右侧程度 I，II，III
     */
    @Column(name = "dentition_right")
    private Byte dentitionRight;

    /**
     * 前牙覆盖 0：浅，1：I，2：II，3：III
     */
    @Column(name = "anterior_overjet")
    private Byte anteriorOverjet;

    /**
     * 前牙覆合  0：浅，1：I，2：II，3：III
     */
    @Column(name = "anterior_overbite")
    private Byte anteriorOverbite;

    /**
     * 反合具体情况（0-无，1-有）
     */
    private Boolean malocclusion;

    /**
     * 反合牙位
     */
    @Column(name = "malocclusion_tooth_bit")
    private String malocclusionToothBit;

    /**
     * 上颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度
     */
    @Column(name = "arch_maxilla_up")
    private Byte archMaxillaUp;

    /**
     * 下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     */
    @Column(name = "arch_maxilla_up_level")
    private Byte archMaxillaUpLevel;

    /**
     * 颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度下
     */
    @Column(name = "arch_maxilla_down")
    private Byte archMaxillaDown;

    /**
     * 下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     */
    @Column(name = "arch_maxilla_down_level")
    private Byte archMaxillaDownLevel;

    /**
     * spee曲线 0：正常，1：中度，2：重度，3：反向
     */
    private Byte spee;

    /**
     * 牙列中线上颌偏移情况 0-左，1-右
     */
    @Column(name = "arch_middle_maxilla_up")
    private Byte archMiddleMaxillaUp;

    /**
     * 上颌偏移距离
     */
    @Column(name = "up_offset")
    private Byte upOffset;

    /**
     * 牙列中线下颌偏移情况 0-左，1-右
     */
    @Column(name = "arch_middle_maxilla_down")
    private Byte archMiddleMaxillaDown;

    /**
     * 下颌偏移距离
     */
    @Column(name = "down_offset")
    private Byte downOffset;

    /**
     * 口腔卫生 0：差，1：一般，2：好
     */
    @Column(name = "oral_hygiene")
    private Byte oralHygiene;

    /**
     * 舌头状态 0：正常，1：舌肌松弛，
     */
    @Column(name = "tongue_status")
    private String tongueStatus;

    /**
     * 舌头功能 0： 前伸，1：侧方吐舌
     */
    @Column(name = "tongue_function")
    private String tongueFunction;

    /**
     * 牙龈状态 0：无退缩，1：轻微推送，2：广泛退缩
     */
    @Column(name = "gingiva_status")
    private Byte gingivaStatus;

    /**
     * 唇系统状态 0：正常，1：异常
     */
    @Column(name = "labial_frenum_status")
    private Boolean labialFrenumStatus;

    /**
     * 舌系统状态 0：正常，1：异常
     */
    @Column(name = "tongue_frenum_status")
    private Boolean tongueFrenumStatus;

    /**
     * 习惯 0-无；1-吮吸；2-唇咬；3-口呼吸；4-偏侧咀嚼；5-其他；
     */
    private String habit;

    /**
     * 习惯记录
     */
    @Column(name = "habit_remark")
    private String habitRemark;

    /**
     * 扁桃体 0：I，1：II，2：III，3：未见
     */
    private Byte tonsil;

    /**
     * 开口运动0：左偏，1：右偏，2：绞索，3：受限，4：绞痛，5：正常
     */
    @Column(name = "mouth_open")
    private String mouthOpen;

    /**
     * 张口受限程度0：无，1：I，2：II，3：III
     */
    @Column(name = "mouth_open_limit")
    private Byte mouthOpenLimit;

    /**
     * 左颞下颌关节0：开口，1：闭口，
     */
    @Column(name = "tmj_left")
    private Byte tmjLeft;

    /**
     * 左颞下颌关节开闭程度 0：轻微，1：明显
     */
    @Column(name = "tmj_left_level")
    private Byte tmjLeftLevel;

    /**
     * 左颞下颌关节 是否弹响 0 -否；1 -是
     */
    @Column(name = "left_is_clicking")
    private Boolean leftIsClicking;

    /**
     * 右颞下颌关节0：开口，1：闭口，2：轻微，3：明显，4：弹响
     */
    @Column(name = "tmj_right")
    private Byte tmjRight;

    /**
     * 右颞下颌关节开闭程度 0：轻微，1：明显
     */
    @Column(name = "tmj_right_level")
    private Byte tmjRightLevel;

    /**
     * 右颞下颌关节 是否弹响 0 -否；1 -是
     */
    @Column(name = "right_is_clicking")
    private Boolean rightIsClicking;

    /**
     * 肌肉接触0：阴性,1:阳性
     */
    private Boolean muscle;

    /**
     * 0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     */
    @Column(name = "muscle_left")
    private String muscleLeft;

    /**
     * 0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     */
    @Column(name = "muscle_right")
    private String muscleRight;

    /**
     * 其他
     */
    private String other;

    /**
     * 创建人ID
     */
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人ID
     */
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改时间
     */
    @Column(name = "upd_time")
    private Date updTime;

    /**
     * 获取主键ID
     *
     * @return id - 主键ID
     */
    public Integer getId() {
        return id;
    }

    /**
     * 设置主键ID
     *
     * @param id 主键ID
     */
    public void setId(Integer id) {
        this.id = id;
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
     * 获取面部对称 0:基本对称,1:不对称
     *
     * @return facial_symmetry - 面部对称 0:基本对称,1:不对称
     */
    public Boolean getFacialSymmetry() {
        return facialSymmetry;
    }

    /**
     * 设置面部对称 0:基本对称,1:不对称
     *
     * @param facialSymmetry 面部对称 0:基本对称,1:不对称
     */
    public void setFacialSymmetry(Boolean facialSymmetry) {
        this.facialSymmetry = facialSymmetry;
    }

    /**
     * 获取露龈笑 0：无，1：轻度，2：严重
     *
     * @return gummy_smile - 露龈笑 0：无，1：轻度，2：严重
     */
    public Byte getGummySmile() {
        return gummySmile;
    }

    /**
     * 设置露龈笑 0：无，1：轻度，2：严重
     *
     * @param gummySmile 露龈笑 0：无，1：轻度，2：严重
     */
    public void setGummySmile(Byte gummySmile) {
        this.gummySmile = gummySmile;
    }

    /**
     * 获取颏部偏移 0：正常，1：左偏，2：右偏
     *
     * @return chin_deflection - 颏部偏移 0：正常，1：左偏，2：右偏
     */
    public Byte getChinDeflection() {
        return chinDeflection;
    }

    /**
     * 设置颏部偏移 0：正常，1：左偏，2：右偏
     *
     * @param chinDeflection 颏部偏移 0：正常，1：左偏，2：右偏
     */
    public void setChinDeflection(Byte chinDeflection) {
        this.chinDeflection = chinDeflection;
    }

    /**
     * 获取唇肌 0：正常，1：紧张，2：松弛
     *
     * @return lip_muscle - 唇肌 0：正常，1：紧张，2：松弛
     */
    public Byte getLipMuscle() {
        return lipMuscle;
    }

    /**
     * 设置唇肌 0：正常，1：紧张，2：松弛
     *
     * @param lipMuscle 唇肌 0：正常，1：紧张，2：松弛
     */
    public void setLipMuscle(Byte lipMuscle) {
        this.lipMuscle = lipMuscle;
    }

    /**
     * 获取侧貌 0：直面型，1：凸面型，2：凹面型
     *
     * @return profile - 侧貌 0：直面型，1：凸面型，2：凹面型
     */
    public Byte getProfile() {
        return profile;
    }

    /**
     * 设置侧貌 0：直面型，1：凸面型，2：凹面型
     *
     * @param profile 侧貌 0：直面型，1：凸面型，2：凹面型
     */
    public void setProfile(Byte profile) {
        this.profile = profile;
    }

    /**
     * 获取颏部外形 0：正常，1：不足，2：过度
     *
     * @return chin_shape - 颏部外形 0：正常，1：不足，2：过度
     */
    public Byte getChinShape() {
        return chinShape;
    }

    /**
     * 设置颏部外形 0：正常，1：不足，2：过度
     *
     * @param chinShape 颏部外形 0：正常，1：不足，2：过度
     */
    public void setChinShape(Byte chinShape) {
        this.chinShape = chinShape;
    }

    /**
     * 获取牙列
     *
     * @return dentition - 牙列
     */
    public String getDentition() {
        return dentition;
    }

    /**
     * 设置牙列
     *
     * @param dentition 牙列
     */
    public void setDentition(String dentition) {
        this.dentition = dentition;
    }

    /**
     * 获取磨牙左侧程度 I，II，III
     *
     * @return dentition_left - 磨牙左侧程度 I，II，III
     */
    public Byte getDentitionLeft() {
        return dentitionLeft;
    }

    /**
     * 设置磨牙左侧程度 I，II，III
     *
     * @param dentitionLeft 磨牙左侧程度 I，II，III
     */
    public void setDentitionLeft(Byte dentitionLeft) {
        this.dentitionLeft = dentitionLeft;
    }

    /**
     * 获取磨牙右侧程度 I，II，III
     *
     * @return dentition_right - 磨牙右侧程度 I，II，III
     */
    public Byte getDentitionRight() {
        return dentitionRight;
    }

    /**
     * 设置磨牙右侧程度 I，II，III
     *
     * @param dentitionRight 磨牙右侧程度 I，II，III
     */
    public void setDentitionRight(Byte dentitionRight) {
        this.dentitionRight = dentitionRight;
    }

    /**
     * 获取前牙覆盖 0：浅，1：I，2：II，3：III
     *
     * @return anterior_overjet - 前牙覆盖 0：浅，1：I，2：II，3：III
     */
    public Byte getAnteriorOverjet() {
        return anteriorOverjet;
    }

    /**
     * 设置前牙覆盖 0：浅，1：I，2：II，3：III
     *
     * @param anteriorOverjet 前牙覆盖 0：浅，1：I，2：II，3：III
     */
    public void setAnteriorOverjet(Byte anteriorOverjet) {
        this.anteriorOverjet = anteriorOverjet;
    }

    /**
     * 获取前牙覆合  0：浅，1：I，2：II，3：III
     *
     * @return anterior_overbite - 前牙覆合  0：浅，1：I，2：II，3：III
     */
    public Byte getAnteriorOverbite() {
        return anteriorOverbite;
    }

    /**
     * 设置前牙覆合  0：浅，1：I，2：II，3：III
     *
     * @param anteriorOverbite 前牙覆合  0：浅，1：I，2：II，3：III
     */
    public void setAnteriorOverbite(Byte anteriorOverbite) {
        this.anteriorOverbite = anteriorOverbite;
    }

    /**
     * 获取反合具体情况（0-无，1-有）
     *
     * @return malocclusion - 反合具体情况（0-无，1-有）
     */
    public Boolean getMalocclusion() {
        return malocclusion;
    }

    /**
     * 设置反合具体情况（0-无，1-有）
     *
     * @param malocclusion 反合具体情况（0-无，1-有）
     */
    public void setMalocclusion(Boolean malocclusion) {
        this.malocclusion = malocclusion;
    }

    /**
     * 获取反合牙位
     *
     * @return malocclusion_tooth_bit - 反合牙位
     */
    public String getMalocclusionToothBit() {
        return malocclusionToothBit;
    }

    /**
     * 设置反合牙位
     *
     * @param malocclusionToothBit 反合牙位
     */
    public void setMalocclusionToothBit(String malocclusionToothBit) {
        this.malocclusionToothBit = malocclusionToothBit;
    }

    /**
     * 获取上颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度
     *
     * @return arch_maxilla_up - 上颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度
     */
    public Byte getArchMaxillaUp() {
        return archMaxillaUp;
    }

    /**
     * 设置上颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度
     *
     * @param archMaxillaUp 上颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度
     */
    public void setArchMaxillaUp(Byte archMaxillaUp) {
        this.archMaxillaUp = archMaxillaUp;
    }

    /**
     * 获取下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     *
     * @return arch_maxilla_up_level - 下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     */
    public Byte getArchMaxillaUpLevel() {
        return archMaxillaUpLevel;
    }

    /**
     * 设置下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     *
     * @param archMaxillaUpLevel 下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     */
    public void setArchMaxillaUpLevel(Byte archMaxillaUpLevel) {
        this.archMaxillaUpLevel = archMaxillaUpLevel;
    }

    /**
     * 获取颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度下
     *
     * @return arch_maxilla_down - 颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度下
     */
    public Byte getArchMaxillaDown() {
        return archMaxillaDown;
    }

    /**
     * 设置颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度下
     *
     * @param archMaxillaDown 颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度下
     */
    public void setArchMaxillaDown(Byte archMaxillaDown) {
        this.archMaxillaDown = archMaxillaDown;
    }

    /**
     * 获取下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     *
     * @return arch_maxilla_down_level - 下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     */
    public Byte getArchMaxillaDownLevel() {
        return archMaxillaDownLevel;
    }

    /**
     * 设置下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     *
     * @param archMaxillaDownLevel 下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     */
    public void setArchMaxillaDownLevel(Byte archMaxillaDownLevel) {
        this.archMaxillaDownLevel = archMaxillaDownLevel;
    }

    /**
     * 获取spee曲线 0：正常，1：中度，2：重度，3：反向
     *
     * @return spee - spee曲线 0：正常，1：中度，2：重度，3：反向
     */
    public Byte getSpee() {
        return spee;
    }

    /**
     * 设置spee曲线 0：正常，1：中度，2：重度，3：反向
     *
     * @param spee spee曲线 0：正常，1：中度，2：重度，3：反向
     */
    public void setSpee(Byte spee) {
        this.spee = spee;
    }

    /**
     * 获取牙列中线上颌偏移情况 0-左，1-右
     *
     * @return arch_middle_maxilla_up - 牙列中线上颌偏移情况 0-左，1-右
     */
    public Byte getArchMiddleMaxillaUp() {
        return archMiddleMaxillaUp;
    }

    /**
     * 设置牙列中线上颌偏移情况 0-左，1-右
     *
     * @param archMiddleMaxillaUp 牙列中线上颌偏移情况 0-左，1-右
     */
    public void setArchMiddleMaxillaUp(Byte archMiddleMaxillaUp) {
        this.archMiddleMaxillaUp = archMiddleMaxillaUp;
    }

    /**
     * 获取上颌偏移距离
     *
     * @return up_offset - 上颌偏移距离
     */
    public Byte getUpOffset() {
        return upOffset;
    }

    /**
     * 设置上颌偏移距离
     *
     * @param upOffset 上颌偏移距离
     */
    public void setUpOffset(Byte upOffset) {
        this.upOffset = upOffset;
    }

    /**
     * 获取牙列中线下颌偏移情况 0-左，1-右
     *
     * @return arch_middle_maxilla_down - 牙列中线下颌偏移情况 0-左，1-右
     */
    public Byte getArchMiddleMaxillaDown() {
        return archMiddleMaxillaDown;
    }

    /**
     * 设置牙列中线下颌偏移情况 0-左，1-右
     *
     * @param archMiddleMaxillaDown 牙列中线下颌偏移情况 0-左，1-右
     */
    public void setArchMiddleMaxillaDown(Byte archMiddleMaxillaDown) {
        this.archMiddleMaxillaDown = archMiddleMaxillaDown;
    }

    /**
     * 获取下颌偏移距离
     *
     * @return down_offset - 下颌偏移距离
     */
    public Byte getDownOffset() {
        return downOffset;
    }

    /**
     * 设置下颌偏移距离
     *
     * @param downOffset 下颌偏移距离
     */
    public void setDownOffset(Byte downOffset) {
        this.downOffset = downOffset;
    }

    /**
     * 获取口腔卫生 0：差，1：一般，2：好
     *
     * @return oral_hygiene - 口腔卫生 0：差，1：一般，2：好
     */
    public Byte getOralHygiene() {
        return oralHygiene;
    }

    /**
     * 设置口腔卫生 0：差，1：一般，2：好
     *
     * @param oralHygiene 口腔卫生 0：差，1：一般，2：好
     */
    public void setOralHygiene(Byte oralHygiene) {
        this.oralHygiene = oralHygiene;
    }

    /**
     * 获取舌头状态 0：正常，1：舌肌松弛，
     *
     * @return tongue_status - 舌头状态 0：正常，1：舌肌松弛，
     */
    public String getTongueStatus() {
        return tongueStatus;
    }

    /**
     * 设置舌头状态 0：正常，1：舌肌松弛，
     *
     * @param tongueStatus 舌头状态 0：正常，1：舌肌松弛，
     */
    public void setTongueStatus(String tongueStatus) {
        this.tongueStatus = tongueStatus;
    }

    /**
     * 获取舌头功能 0： 前伸，1：侧方吐舌
     *
     * @return tongue_function - 舌头功能 0： 前伸，1：侧方吐舌
     */
    public String getTongueFunction() {
        return tongueFunction;
    }

    /**
     * 设置舌头功能 0： 前伸，1：侧方吐舌
     *
     * @param tongueFunction 舌头功能 0： 前伸，1：侧方吐舌
     */
    public void setTongueFunction(String tongueFunction) {
        this.tongueFunction = tongueFunction;
    }

    /**
     * 获取牙龈状态 0：无退缩，1：轻微推送，2：广泛退缩
     *
     * @return gingiva_status - 牙龈状态 0：无退缩，1：轻微推送，2：广泛退缩
     */
    public Byte getGingivaStatus() {
        return gingivaStatus;
    }

    /**
     * 设置牙龈状态 0：无退缩，1：轻微推送，2：广泛退缩
     *
     * @param gingivaStatus 牙龈状态 0：无退缩，1：轻微推送，2：广泛退缩
     */
    public void setGingivaStatus(Byte gingivaStatus) {
        this.gingivaStatus = gingivaStatus;
    }

    /**
     * 获取唇系统状态 0：正常，1：异常
     *
     * @return labial_frenum_status - 唇系统状态 0：正常，1：异常
     */
    public Boolean getLabialFrenumStatus() {
        return labialFrenumStatus;
    }

    /**
     * 设置唇系统状态 0：正常，1：异常
     *
     * @param labialFrenumStatus 唇系统状态 0：正常，1：异常
     */
    public void setLabialFrenumStatus(Boolean labialFrenumStatus) {
        this.labialFrenumStatus = labialFrenumStatus;
    }

    /**
     * 获取舌系统状态 0：正常，1：异常
     *
     * @return tongue_frenum_status - 舌系统状态 0：正常，1：异常
     */
    public Boolean getTongueFrenumStatus() {
        return tongueFrenumStatus;
    }

    /**
     * 设置舌系统状态 0：正常，1：异常
     *
     * @param tongueFrenumStatus 舌系统状态 0：正常，1：异常
     */
    public void setTongueFrenumStatus(Boolean tongueFrenumStatus) {
        this.tongueFrenumStatus = tongueFrenumStatus;
    }

    /**
     * 获取习惯 0-无；1-吮吸；2-唇咬；3-口呼吸；4-偏侧咀嚼；5-其他；
     *
     * @return habit - 习惯 0-无；1-吮吸；2-唇咬；3-口呼吸；4-偏侧咀嚼；5-其他；
     */
    public String getHabit() {
        return habit;
    }

    /**
     * 设置习惯 0-无；1-吮吸；2-唇咬；3-口呼吸；4-偏侧咀嚼；5-其他；
     *
     * @param habit 习惯 0-无；1-吮吸；2-唇咬；3-口呼吸；4-偏侧咀嚼；5-其他；
     */
    public void setHabit(String habit) {
        this.habit = habit;
    }

    /**
     * 获取习惯记录
     *
     * @return habit_remark - 习惯记录
     */
    public String getHabitRemark() {
        return habitRemark;
    }

    /**
     * 设置习惯记录
     *
     * @param habitRemark 习惯记录
     */
    public void setHabitRemark(String habitRemark) {
        this.habitRemark = habitRemark;
    }

    /**
     * 获取扁桃体 0：I，1：II，2：III，3：未见
     *
     * @return tonsil - 扁桃体 0：I，1：II，2：III，3：未见
     */
    public Byte getTonsil() {
        return tonsil;
    }

    /**
     * 设置扁桃体 0：I，1：II，2：III，3：未见
     *
     * @param tonsil 扁桃体 0：I，1：II，2：III，3：未见
     */
    public void setTonsil(Byte tonsil) {
        this.tonsil = tonsil;
    }

    /**
     * 获取开口运动0：左偏，1：右偏，2：绞索，3：受限，4：绞痛，5：正常
     *
     * @return mouth_open - 开口运动0：左偏，1：右偏，2：绞索，3：受限，4：绞痛，5：正常
     */
    public String getMouthOpen() {
        return mouthOpen;
    }

    /**
     * 设置开口运动0：左偏，1：右偏，2：绞索，3：受限，4：绞痛，5：正常
     *
     * @param mouthOpen 开口运动0：左偏，1：右偏，2：绞索，3：受限，4：绞痛，5：正常
     */
    public void setMouthOpen(String mouthOpen) {
        this.mouthOpen = mouthOpen;
    }

    /**
     * 获取张口受限程度0：无，1：I，2：II，3：III
     *
     * @return mouth_open_limit - 张口受限程度0：无，1：I，2：II，3：III
     */
    public Byte getMouthOpenLimit() {
        return mouthOpenLimit;
    }

    /**
     * 设置张口受限程度0：无，1：I，2：II，3：III
     *
     * @param mouthOpenLimit 张口受限程度0：无，1：I，2：II，3：III
     */
    public void setMouthOpenLimit(Byte mouthOpenLimit) {
        this.mouthOpenLimit = mouthOpenLimit;
    }

    /**
     * 获取左颞下颌关节0：开口，1：闭口，
     *
     * @return tmj_left - 左颞下颌关节0：开口，1：闭口，
     */
    public Byte getTmjLeft() {
        return tmjLeft;
    }

    /**
     * 设置左颞下颌关节0：开口，1：闭口，
     *
     * @param tmjLeft 左颞下颌关节0：开口，1：闭口，
     */
    public void setTmjLeft(Byte tmjLeft) {
        this.tmjLeft = tmjLeft;
    }

    /**
     * 获取左颞下颌关节开闭程度 0：轻微，1：明显
     *
     * @return tmj_left_level - 左颞下颌关节开闭程度 0：轻微，1：明显
     */
    public Byte getTmjLeftLevel() {
        return tmjLeftLevel;
    }

    /**
     * 设置左颞下颌关节开闭程度 0：轻微，1：明显
     *
     * @param tmjLeftLevel 左颞下颌关节开闭程度 0：轻微，1：明显
     */
    public void setTmjLeftLevel(Byte tmjLeftLevel) {
        this.tmjLeftLevel = tmjLeftLevel;
    }

    /**
     * 获取左颞下颌关节 是否弹响 0 -否；1 -是
     *
     * @return left_is_clicking - 左颞下颌关节 是否弹响 0 -否；1 -是
     */
    public Boolean getLeftIsClicking() {
        return leftIsClicking;
    }

    /**
     * 设置左颞下颌关节 是否弹响 0 -否；1 -是
     *
     * @param leftIsClicking 左颞下颌关节 是否弹响 0 -否；1 -是
     */
    public void setLeftIsClicking(Boolean leftIsClicking) {
        this.leftIsClicking = leftIsClicking;
    }

    /**
     * 获取右颞下颌关节0：开口，1：闭口，2：轻微，3：明显，4：弹响
     *
     * @return tmj_right - 右颞下颌关节0：开口，1：闭口，2：轻微，3：明显，4：弹响
     */
    public Byte getTmjRight() {
        return tmjRight;
    }

    /**
     * 设置右颞下颌关节0：开口，1：闭口，2：轻微，3：明显，4：弹响
     *
     * @param tmjRight 右颞下颌关节0：开口，1：闭口，2：轻微，3：明显，4：弹响
     */
    public void setTmjRight(Byte tmjRight) {
        this.tmjRight = tmjRight;
    }

    /**
     * 获取右颞下颌关节开闭程度 0：轻微，1：明显
     *
     * @return tmj_right_level - 右颞下颌关节开闭程度 0：轻微，1：明显
     */
    public Byte getTmjRightLevel() {
        return tmjRightLevel;
    }

    /**
     * 设置右颞下颌关节开闭程度 0：轻微，1：明显
     *
     * @param tmjRightLevel 右颞下颌关节开闭程度 0：轻微，1：明显
     */
    public void setTmjRightLevel(Byte tmjRightLevel) {
        this.tmjRightLevel = tmjRightLevel;
    }

    /**
     * 获取右颞下颌关节 是否弹响 0 -否；1 -是
     *
     * @return right_is_clicking - 右颞下颌关节 是否弹响 0 -否；1 -是
     */
    public Boolean getRightIsClicking() {
        return rightIsClicking;
    }

    /**
     * 设置右颞下颌关节 是否弹响 0 -否；1 -是
     *
     * @param rightIsClicking 右颞下颌关节 是否弹响 0 -否；1 -是
     */
    public void setRightIsClicking(Boolean rightIsClicking) {
        this.rightIsClicking = rightIsClicking;
    }

    /**
     * 获取肌肉接触0：阴性,1:阳性
     *
     * @return muscle - 肌肉接触0：阴性,1:阳性
     */
    public Boolean getMuscle() {
        return muscle;
    }

    /**
     * 设置肌肉接触0：阴性,1:阳性
     *
     * @param muscle 肌肉接触0：阴性,1:阳性
     */
    public void setMuscle(Boolean muscle) {
        this.muscle = muscle;
    }

    /**
     * 获取0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     *
     * @return muscle_left - 0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     */
    public String getMuscleLeft() {
        return muscleLeft;
    }

    /**
     * 设置0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     *
     * @param muscleLeft 0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     */
    public void setMuscleLeft(String muscleLeft) {
        this.muscleLeft = muscleLeft;
    }

    /**
     * 获取0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     *
     * @return muscle_right - 0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     */
    public String getMuscleRight() {
        return muscleRight;
    }

    /**
     * 设置0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     *
     * @param muscleRight 0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     */
    public void setMuscleRight(String muscleRight) {
        this.muscleRight = muscleRight;
    }

    /**
     * 获取其他
     *
     * @return other - 其他
     */
    public String getOther() {
        return other;
    }

    /**
     * 设置其他
     *
     * @param other 其他
     */
    public void setOther(String other) {
        this.other = other;
    }

    /**
     * 获取创建人ID
     *
     * @return crt_id - 创建人ID
     */
    public Integer getCrtId() {
        return crtId;
    }

    /**
     * 设置创建人ID
     *
     * @param crtId 创建人ID
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
     * 获取修改人ID
     *
     * @return upd_id - 修改人ID
     */
    public Integer getUpdId() {
        return updId;
    }

    /**
     * 设置修改人ID
     *
     * @param updId 修改人ID
     */
    public void setUpdId(Integer updId) {
        this.updId = updId;
    }

    /**
     * 获取修改时间
     *
     * @return upd_time - 修改时间
     */
    public Date getUpdTime() {
        return updTime;
    }

    /**
     * 设置修改时间
     *
     * @param updTime 修改时间
     */
    public void setUpdTime(Date updTime) {
        this.updTime = updTime;
    }
}