package com.yunya.report.ultimate.enums;

/**
 * @author: chenlin
 * @date: 2023/5/15 17:17
 * @description: 治疗项目类别枚举值
 * @since: 1.0.0
 */
public enum TreatmentTariffEnum {

    PEDIATRIC_DENTISTRY("儿牙", new String[]{"T01001", "T04001", "T04016", "T04017", "T04019", "T04023", "T04025", "Z000107", "Z000118", "Z000288", "Z000147", "Z000148", "Z000149", "Z000150", "Z000191", "Z000201", "Z000202", "Z000203", "Z000204", "Z000205", "Z000206", "Z000207", "Z000208", "Z000209", "Z000210", "Z000211", "Z000212", "Z000213", "Z000214", "Z000215", "Z000216", "Z000217", "Z000218", "Z000219", "Z000220", "Z000221", "Z000222", "Z000223", "Z000224", "Z000225", "T04026", "T04027", "T04028"}),
    EXTRACTION_LOOSE_TEETH("拔松动牙", new String[]{"T06009"}),
    EXTRACTION_WIDISOM_TEETH("拔智齿", new String[]{"Z000052", "Z000167", "Z000168"}),
    PERIODONTAL_TREATMENT("牙周治疗", new String[]{"T05031", "Z000078", "Z000226", "Z000227", "Z000228", "Z000229", "Z000230", "Z000231", "Z000232", "Z000257", "Z000258"}),
    RESIN_FILLING("树脂充填", new String[]{"Z000191", "T02003", "T02004", "T02005", "T02006"}),
    PORCELAIN_RESTORATION("瓷修复", new String[]{"T07011", "T07012", "T07013", "T07017", "T07018", "T07021", "T07023", "T07024", "T07026", "T07027", "T07029", "T07030", "T07031", "T07034", "T08001", "T08002", "T08003", "T08004", "T08005", "T08006", "T08007", "T08008", "T08014", "T08015", "Z000087", "Z000106", "Z000115", "Z000116", "Z000267", "Z000269", "Z000270", "Z000236", "Z000237", "T07046"}),
    WHITENING("美白", new String[]{"T10003", "Z000132", "Z000133", "Z000134", "Z000175"}),
    FIXED_ORTHODONTIC("固定矫正", new String[]{"T11002", "T11003", "T11005", "T11006", "T11007", "T11008", "Z000066", "Z000067", "Z000068", "T11012", "T11013", "T11055", "T11056", "T11074", "T11075", "T11076", "T11077", "T11078", "T11079", "T11083", "T11084", "T11090", "T11091"}),
    INVISIBLE_ORTHODONTIC("隐形矫正", new String[]{"Z000058", "Z000059", "Z000060", "Z000064", "Z000108", "Z000109", "T11032", "T11033", "T11034", "Z000150", "T11063", "T11064", "T11080", "T11081", "T11082", "T11094", "T11095", "T11096", "T11100", "T11101"}),
    LINGUAL_ORTHODONTIC("舌侧矫正", new String[]{"Z000278", "Z000289", "T11035", "T11036", "T11088", "T11089", "T11097", "T11098"}),
    DENTAL_IMPLANT("种植", new String[]{"T09004", "Z000104", "Z000105", "Z000280", "Z000178", "Z000180", "T09022", "T09023", "T09024", "T09032", "T09033", "T09034", "T09035", "T09038", "T09039"}),
    ;

    /** 项目类别名称 */
    private String name;

    /** 项目编号类别 */
    private String[] itemNums;

    TreatmentTariffEnum(String name, String[] itemNums) {
       this.name = name;
       this.itemNums = itemNums;
    }

    public String[] getItemNums() {
        return itemNums;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
