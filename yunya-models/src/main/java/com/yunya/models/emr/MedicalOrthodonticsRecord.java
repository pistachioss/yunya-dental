package com.yunya.models.emr;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;
import javax.persistence.*;

@Table(name = "medical_orthodontics_record")
@Data
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
    @ApiModelProperty("患者ID")
    private Integer patientId;

    /**
     * 面部对称 0:基本对称,1:不对称
     */
    @Column(name = "facial_symmetry")
    @ApiModelProperty("面部对称 0:基本对称,1:不对称")
    private Byte facialSymmetry;

    /**
     * 露龈笑 0：无，1：轻度，2：严重
     */
    @Column(name = "gummy_smile")
    @ApiModelProperty("露龈笑 0：无，1：轻度，2：严重")
    private Byte gummySmile;

    /**
     * 颏部偏移 0：正常，1：左偏，2：右偏
     */
    @Column(name = "chin_deflection")
    @ApiModelProperty("颏部偏移 0：正常，1：左偏，2：右偏")
    private Byte chinDeflection;

    /**
     * 唇肌 0：正常，1：紧张，2：松弛
     */
    @Column(name = "lip_muscle")
    @ApiModelProperty("唇肌 0：正常，1：紧张，2：松弛")
    private Byte lipMuscle;

    /**
     * 侧貌 0：直面型，1：凸面型，2：凹面型
     */
    @ApiModelProperty("侧貌 0：直面型，1：凸面型，2：凹面型")
    private Byte profile;

    /**
     * 颏部外形 0：正常，1：不足，2：过度
     */
    @Column(name = "chin_shape")
    @ApiModelProperty("颏部外形 0：正常，1：不足，2：过度")
    private Byte chinShape;

    /**
     * 牙列
     */
    @ApiModelProperty("牙列")
    private String dentition;

    /**
     * 磨牙左侧程度 I，II，III
     */
    @Column(name = "dentition_left")
    @ApiModelProperty("磨牙左侧程度 I，II，III")
    private Byte dentitionLeft;

    /**
     * 磨牙右侧程度 I，II，III
     */
    @Column(name = "dentition_right")
    @ApiModelProperty("磨牙右侧程度 I，II，III")
    private Byte dentitionRight;

    /**
     * 前牙覆盖 0：浅，1：I，2：II，3：III
     */
    @Column(name = "anterior_overjet")
    @ApiModelProperty("前牙覆盖 0：浅，1：I，2：II，3：III")
    private Byte anteriorOverjet;

    /**
     * 前牙覆合  0：浅，1：I，2：II，3：III
     */
    @Column(name = "anterior_overbite")
    @ApiModelProperty("前牙覆合  0：浅，1：I，2：II，3：III")
    private Byte anteriorOverbite;

    /**
     * 反合具体情况（0-无，1-有）
     */
    @ApiModelProperty("反合具体情况（0-无，1-有）")
    private Byte malocclusion;

    /**
     * 反合牙位
     */
    @Column(name = "malocclusion_tooth_bit")
    @ApiModelProperty("反合牙位")
    private String malocclusionToothBit;

    /**
     * 上颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度
     */
    @ApiModelProperty("上颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度")
    @Column(name = "arch_maxilla_up")
    private Byte archMaxillaUp;

    /**
     * 下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     */
    @ApiModelProperty("下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下")
    @Column(name = "arch_maxilla_up_level")
    private Byte archMaxillaUpLevel;

    /**
     * 颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度下
     */
    @ApiModelProperty("颌牙弓长度 0：散隙，1：无拥挤，2：重度拥挤，3：中度，4：轻度下")
    @Column(name = "arch_maxilla_down")
    private Byte archMaxillaDown;

    /**
     * 下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下
     */
    @ApiModelProperty("下颌散隙/拥挤程度 0：重度拥挤，1：中度，2：轻度下")
    @Column(name = "arch_maxilla_down_level")
    private Byte archMaxillaDownLevel;

    /**
     * spee曲线 0：正常，1：中度，2：重度，3：反向
     */
    @ApiModelProperty("spee曲线 0：正常，1：中度，2：重度，3：反向")
    private Byte spee;

    /**
     * 牙列中线上颌偏移情况 0-左，1-右
     */
    @ApiModelProperty("牙列中线上颌偏移情况 0-左，1-右")
    @Column(name = "arch_middle_maxilla_up")
    private Byte archMiddleMaxillaUp;

    /**
     * 上颌偏移距离
     */
    @ApiModelProperty("上颌偏移距离")
    @Column(name = "up_offset")
    private Byte upOffset;

    /**
     * 牙列中线下颌偏移情况 0-左，1-右
     */
    @ApiModelProperty("牙列中线下颌偏移情况 0-左，1-右")
    @Column(name = "arch_middle_maxilla_down")
    private Byte archMiddleMaxillaDown;

    /**
     * 下颌偏移距离
     */
    @ApiModelProperty("颌偏移距离")
    @Column(name = "down_offset")
    private Byte downOffset;

    /**
     * 口腔卫生 0：差，1：一般，2：好
     */
    @ApiModelProperty("口腔卫生 0：差，1：一般，2：好")
    @Column(name = "oral_hygiene")
    private Byte oralHygiene;

    /**
     * 舌头状态 0：正常，1：舌肌松弛，2 前伸
     */
    @ApiModelProperty("舌头状态 0：正常，1：舌肌松弛，2 前伸")
    @Column(name = "tongue_status")
    private String tongueStatus;

    /**
     * 侧方吐舌 0：否 ，1：是
     */
    @ApiModelProperty("侧方吐舌 0：否 ，1：是")
    @Column(name = "tongue_function")
    private String tongueFunction;

    /**
     * 牙龈状态 0：无退缩，1：轻微推送，2：广泛退缩
     */
    @ApiModelProperty("牙龈状态 0：无退缩，1：轻微推送，2：广泛退缩")
    @Column(name = "gingiva_status")
    private Byte gingivaStatus;

    /**
     * 唇系统状态 0：正常，1：异常
     */
    @ApiModelProperty("唇系统状态 0：正常，1：异常")
    @Column(name = "labial_frenum_status")
    private Byte labialFrenumStatus;

    /**
     * 舌系统状态 0：正常，1：异常
     */
    @ApiModelProperty("舌系统状态 0：正常，1：异常")
    @Column(name = "tongue_frenum_status")
    private Byte tongueFrenumStatus;

    /**
     * 习惯 0-无；1-吮吸；2-唇咬；3-口呼吸；4-偏侧咀嚼；5-其他；
     */
    @ApiModelProperty("习惯 0-无；1-吮吸；2-唇咬；3-口呼吸；4-偏侧咀嚼；5-其他；")
    private String habit;

    /**
     * 习惯记录
     */
    @ApiModelProperty("习惯记录")
    @Column(name = "habit_remark")
    private String habitRemark;

    /**
     * 扁桃体 0：I，1：II，2：III，3：未见
     */
    @ApiModelProperty("扁桃体 0：I，1：II，2：III，3：未见")
    private Byte tonsil;

    /**
     * 开口运动0：左偏，1：右偏，2：绞索，3：受限，4：绞痛，5：正常
     */
    @ApiModelProperty("开口运动0：左偏，1：右偏，2：绞索，3：受限，4：绞痛，5：正常")
    @Column(name = "mouth_open")
    private String mouthOpen;

    /**
     * 张口受限程度0：无，1：I，2：II，3：III
     */
    @ApiModelProperty("张口受限程度0：无，1：I，2：II，3：III")
    @Column(name = "mouth_open_limit")
    private Byte mouthOpenLimit;

    /**
     * 左颞下颌关节0：开口，1：闭口，
     */
    @ApiModelProperty("左颞下颌关节0：开口，1：闭口，")
    @Column(name = "tmj_left")
    private Byte tmjLeft;

    /**
     * 左颞下颌关节开闭程度 0：轻微，1：明显
     */
    @ApiModelProperty("左颞下颌关节开闭程度 0：轻微，1：明显")
    @Column(name = "tmj_left_level")
    private Byte tmjLeftLevel;

    /**
     * 左颞下颌关节 是否弹响 0 -否；1 -是
     */
    @ApiModelProperty("左颞下颌关节 是否弹响 0 -否；1 -是")
    @Column(name = "left_is_clicking")
    private Byte leftIsClicking;

    /**
     * 右颞下颌关节0：开口，1：闭口，2：轻微，3：明显，4：弹响
     */
    @ApiModelProperty("右颞下颌关节0：开口，1：闭口，2：轻微，3：明显，4：弹响")
    @Column(name = "tmj_right")
    private Byte tmjRight;

    /**
     * 右颞下颌关节开闭程度 0：轻微，1：明显
     */
    @ApiModelProperty("颞下颌关节开闭程度 0：轻微，1：明显")
    @Column(name = "tmj_right_level")
    private Byte tmjRightLevel;

    /**
     * 右颞下颌关节 是否弹响 0 -否；1 -是
     */
    @ApiModelProperty("右颞下颌关节 是否弹响 0 -否；1 -是")
    @Column(name = "right_is_clicking")
    private Byte rightIsClicking;

    /**
     * 肌肉接触0：阴性,1:阳性
     */
    @ApiModelProperty("肌肉接触0：阴性,1:阳性")
    private Byte muscle;

    /**
     * 0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     */
    @ApiModelProperty("0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌")
    @Column(name = "muscle_left")
    private String muscleLeft;

    /**
     * 0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌
     */
    @ApiModelProperty("0：咬肌，1：颞肌，2：关节区，3：盘后区，4：胸锁乳突肌")
    @Column(name = "muscle_right")
    private String muscleRight;

    /**
     * 其他
     */
    @ApiModelProperty("其他")
    private String other;

    /**
     * 创建人ID
     */
    @ApiModelProperty("创建人ID")
    @Column(name = "crt_id")
    private Integer crtId;

    /**
     * 创建时间
     */
    @ApiModelProperty("创建时间")
    @Column(name = "crt_time")
    private Date crtTime;

    /**
     * 修改人ID
     */
    @ApiModelProperty("修改人ID")
    @Column(name = "upd_id")
    private Integer updId;

    /**
     * 修改时间
     */
    @ApiModelProperty("修改时间")
    @Column(name = "upd_time")
    private Date updTime;


}