package com.yunya.modules.system.biz;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.pagehelper.PageInfo;
import com.yunya.framework.common.model.PageQueryParams;
import com.yunya.framework.common.utils.HanyuPinyinHelper;
import com.yunya.models.system.Brand;
import com.yunya.models.system.DictionaryItem;
import com.yunya.modules.system.domain.query.OrganizationQueryForm;
import com.yunya.modules.system.vo.OrganizationInfoVO;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.Collator;
import java.util.*;

/**
 * 简介: 品牌业务层测试
 *
 * @author: chow
 * @date: 2020/7/12 14:58
 * @description:
 * @since: 1.0.0
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class BrandBizTest {

  @Autowired private BrandBiz brandBiz;
  @Autowired private OrganizationBiz organizationBiz;

  @Autowired private DictionaryItemBiz dictionaryItemBiz;

  /** 根据条件查询 */
  @Test
  public void testList() {
    Map<String, Object> map = new HashMap<>();
    map.put("whetherPage",true);
    PageQueryParams query = new PageQueryParams(map);

    query.setPageNum(1);
    query.setPageSize(3);
    PageInfo<Brand> info = brandBiz.selectByQuery(query);
    System.out.println(info);
  }

  @Test
  public void testOrgList() {
    String param = "{\"types\":[0,2],\"whetherPage\":false}";
    OrganizationQueryForm queryForm = JSONObject.parseObject(param, OrganizationQueryForm.class);
    PageInfo<OrganizationInfoVO> result = organizationBiz.findList(queryForm);
    System.out.println(JSONObject.toJSON(result));
  }

  @Test
  public void addState() {
    String param = "[\n" +
            "    {\"id\":\"AO\",\"en\":\"Angola\",\"cn\":\"安哥拉\"}\n" +
            "    ,{\"id\":\"AF\",\"en\":\"Afghanistan\",\"cn\":\"阿富汗\"}\n" +
            "    ,{\"id\":\"AL\",\"en\":\"Albania\",\"cn\":\"阿尔巴尼亚\"}\n" +
            "    ,{\"id\":\"DZ\",\"en\":\"Algeria\",\"cn\":\"阿尔及利亚\"}\n" +
            "    ,{\"id\":\"AD\",\"en\":\"Andorra\",\"cn\":\"安道尔共和国\"}\n" +
            "    ,{\"id\":\"AI\",\"en\":\"Anguilla\",\"cn\":\"安圭拉岛\"}\n" +
            "    ,{\"id\":\"AG\",\"en\":\"Antigua and Barbuda\",\"cn\":\"安提瓜和巴布达\"}\n" +
            "    ,{\"id\":\"AR\",\"en\":\"Argentina\",\"cn\":\"阿根廷\"}\n" +
            "    ,{\"id\":\"AM\",\"en\":\"Armenia\",\"cn\":\"亚美尼亚\"}\n" +
            "    ,{\"id\":\"AN\",\"en\":\"Ascension\",\"cn\":\"阿森松\"}\n" +
            "    ,{\"id\":\"AU\",\"en\":\"Australia\",\"cn\":\"澳大利亚\"}\n" +
            "    ,{\"id\":\"AT\",\"en\":\"Austria\",\"cn\":\"奥地利\"}\n" +
            "    ,{\"id\":\"AZ\",\"en\":\"Azerbaijan\",\"cn\":\"阿塞拜疆\"}\n" +
            "    ,{\"id\":\"BS\",\"en\":\"Bahamas\",\"cn\":\"巴哈马\"}\n" +
            "    ,{\"id\":\"BH\",\"en\":\"Bahrain\",\"cn\":\"巴林\"}\n" +
            "    ,{\"id\":\"BD\",\"en\":\"Bangladesh\",\"cn\":\"孟加拉国\"}\n" +
            "    ,{\"id\":\"BB\",\"en\":\"Barbados\",\"cn\":\"巴巴多斯\"}\n" +
            "    ,{\"id\":\"BY\",\"en\":\"Belarus\",\"cn\":\"白俄罗斯\"}\n" +
            "    ,{\"id\":\"BE\",\"en\":\"Belgium\",\"cn\":\"比利时\"}\n" +
            "    ,{\"id\":\"BZ\",\"en\":\"Belize\",\"cn\":\"伯利兹\"}\n" +
            "    ,{\"id\":\"BJ\",\"en\":\"Benin\",\"cn\":\"贝宁\"}\n" +
            "    ,{\"id\":\"BM\",\"en\":\"Bermuda Is\",\"cn\":\"百慕大群岛\"}\n" +
            "    ,{\"id\":\"BO\",\"en\":\"Bolivia\",\"cn\":\"玻利维亚\"}\n" +
            "    ,{\"id\":\"BW\",\"en\":\"Botswana\",\"cn\":\"博茨瓦纳\"}\n" +
            "    ,{\"id\":\"BR\",\"en\":\"Brazil\",\"cn\":\"巴西\"}\n" +
            "    ,{\"id\":\"BN\",\"en\":\"Brunei\",\"cn\":\"文莱\"}\n" +
            "    ,{\"id\":\"BG\",\"en\":\"Bulgaria\",\"cn\":\"保加利亚\"}\n" +
            "    ,{\"id\":\"BF\",\"en\":\"Burkina-faso\",\"cn\":\"布基纳法索\"}\n" +
            "    ,{\"id\":\"MM\",\"en\":\"Burma\",\"cn\":\"缅甸\"}\n" +
            "    ,{\"id\":\"BI\",\"en\":\"Burundi\",\"cn\":\"布隆迪\"}\n" +
            "    ,{\"id\":\"CM\",\"en\":\"Cameroon\",\"cn\":\"喀麦隆\"}\n" +
            "    ,{\"id\":\"CA\",\"en\":\"Canada\",\"cn\":\"加拿大\"}\n" +
            "    ,{\"id\":\"CI\",\"en\":\"Cayman Is.\",\"cn\":\"开曼群岛\"}\n" +
            "    ,{\"id\":\"CF\",\"en\":\"Central African Republic\",\"cn\":\"中非共和国\"}\n" +
            "    ,{\"id\":\"TD\",\"en\":\"Chad\",\"cn\":\"乍得\"}\n" +
            "    ,{\"id\":\"CL\",\"en\":\"Chile\",\"cn\":\"智利\"}\n" +
            "    ,{\"id\":\"CN\",\"en\":\"China\",\"cn\":\"中国\"}\n" +
            "    ,{\"id\":\"CO\",\"en\":\"Colombia\",\"cn\":\"哥伦比亚\"}\n" +
            "    ,{\"id\":\"CG\",\"en\":\"Congo\",\"cn\":\"刚果\"}\n" +
            "    ,{\"id\":\"CK\",\"en\":\"Cook Is.\",\"cn\":\"库克群岛\"}\n" +
            "    ,{\"id\":\"CR\",\"en\":\"Costa Rica\",\"cn\":\"哥斯达黎加\"}\n" +
            "    ,{\"id\":\"CU\",\"en\":\"Cuba\",\"cn\":\"古巴\"}\n" +
            "    ,{\"id\":\"CY\",\"en\":\"Cyprus\",\"cn\":\"塞浦路斯\"}\n" +
            "    ,{\"id\":\"CZ\",\"en\":\"Czech Republic\",\"cn\":\"捷克\"}\n" +
            "    ,{\"id\":\"DK\",\"en\":\"Denmark\",\"cn\":\"丹麦\"}\n" +
            "    ,{\"id\":\"DJ\",\"en\":\"Djibouti\",\"cn\":\"吉布提\"}\n" +
            "    ,{\"id\":\"DO\",\"en\":\"Dominica Rep.\",\"cn\":\"多米尼加共和国\"}\n" +
            "    ,{\"id\":\"EC\",\"en\":\"Ecuador\",\"cn\":\"厄瓜多尔\"}\n" +
            "    ,{\"id\":\"EG\",\"en\":\"Egypt\",\"cn\":\"埃及\"}\n" +
            "    ,{\"id\":\"SV\",\"en\":\"EI Salvador\",\"cn\":\"萨尔瓦多\"}\n" +
            "    ,{\"id\":\"EE\",\"en\":\"EI Estonia\",\"cn\":\"爱沙尼亚\"}\n" +
            "    ,{\"id\":\"ET\",\"en\":\"Ethiopia\",\"cn\":\"埃塞俄比亚\"}\n" +
            "    ,{\"id\":\"FJ\",\"en\":\"Fiji\",\"cn\":\"斐济\"}\n" +
            "    ,{\"id\":\"FI\",\"en\":\"Finland\",\"cn\":\"芬兰\"}\n" +
            "    ,{\"id\":\"FR\",\"en\":\"France\",\"cn\":\"法国\"}\n" +
            "    ,{\"id\":\"GF\",\"en\":\"French Guiana\",\"cn\":\"法属圭亚那\"}\n" +
            "    ,{\"id\":\"GA\",\"en\":\"Gabon\",\"cn\":\"加蓬\"}\n" +
            "    ,{\"id\":\"GM\",\"en\":\"Gambia\",\"cn\":\"冈比亚\"}\n" +
            "    ,{\"id\":\"GE\",\"en\":\"Georgia\",\"cn\":\"格鲁吉亚\"}\n" +
            "    ,{\"id\":\"DE\",\"en\":\"Germany\",\"cn\":\"德国\"}\n" +
            "    ,{\"id\":\"GH\",\"en\":\"Ghana\",\"cn\":\"加纳\"}\n" +
            "    ,{\"id\":\"GI\",\"en\":\"Gibraltar\",\"cn\":\"直布罗陀\"}\n" +
            "    ,{\"id\":\"GR\",\"en\":\"Greece\",\"cn\":\"希腊\"}\n" +
            "    ,{\"id\":\"GD\",\"en\":\"Grenada\",\"cn\":\"格林纳达\"}\n" +
            "    ,{\"id\":\"GU\",\"en\":\"Guam\",\"cn\":\"关岛\"}\n" +
            "    ,{\"id\":\"GT\",\"en\":\"Guatemala\",\"cn\":\"危地马拉\"}\n" +
            "    ,{\"id\":\"GN\",\"en\":\"Guinea\",\"cn\":\"几内亚\"}\n" +
            "    ,{\"id\":\"GY\",\"en\":\"Guyana\",\"cn\":\"圭亚那\"}\n" +
            "    ,{\"id\":\"HT\",\"en\":\"Haiti\",\"cn\":\"海地\"}\n" +
            "    ,{\"id\":\"HN\",\"en\":\"Honduras\",\"cn\":\"洪都拉斯\"}\n" +
            "    ,{\"id\":\"HK\",\"en\":\"Hongkong\",\"cn\":\"香港\"}\n" +
            "    ,{\"id\":\"HU\",\"en\":\"Hungary\",\"cn\":\"匈牙利\"}\n" +
            "    ,{\"id\":\"IS\",\"en\":\"Iceland\",\"cn\":\"冰岛\"}\n" +
            "    ,{\"id\":\"IN\",\"en\":\"India\",\"cn\":\"印度\"}\n" +
            "    ,{\"id\":\"ID\",\"en\":\"Indonesia\",\"cn\":\"印度尼西亚\"}\n" +
            "    ,{\"id\":\"IR\",\"en\":\"Iran\",\"cn\":\"伊朗\"}\n" +
            "    ,{\"id\":\"IQ\",\"en\":\"Iraq\",\"cn\":\"伊拉克\"}\n" +
            "    ,{\"id\":\"IE\",\"en\":\"Ireland\",\"cn\":\"爱尔兰\"}\n" +
            "    ,{\"id\":\"IL\",\"en\":\"Israel\",\"cn\":\"以色列\"}\n" +
            "    ,{\"id\":\"IT\",\"en\":\"Italy\",\"cn\":\"意大利\"}\n" +
            "    ,{\"id\":\"IC\",\"en\":\"Ivory Coast\",\"cn\":\"科特迪瓦\"}\n" +
            "    ,{\"id\":\"JM\",\"en\":\"Jamaica\",\"cn\":\"牙买加\"}\n" +
            "    ,{\"id\":\"JP\",\"en\":\"Japan\",\"cn\":\"日本\"}\n" +
            "    ,{\"id\":\"JO\",\"en\":\"Jordan\",\"cn\":\"约旦\"}\n" +
            "    ,{\"id\":\"KH\",\"en\":\"Kampuchea (Cambodia )\",\"cn\":\"柬埔寨\"}\n" +
            "    ,{\"id\":\"KZ\",\"en\":\"Kazakstan\",\"cn\":\"哈萨克斯坦\"}\n" +
            "    ,{\"id\":\"KE\",\"en\":\"Kenya\",\"cn\":\"肯尼亚\"}\n" +
            "    ,{\"id\":\"KR\",\"en\":\"Korea\",\"cn\":\"韩国\"}\n" +
            "    ,{\"id\":\"KW\",\"en\":\"Kuwait\",\"cn\":\"科威特\"}\n" +
            "    ,{\"id\":\"KG\",\"en\":\"Kyrgyzstan\",\"cn\":\"吉尔吉斯坦\"}\n" +
            "    ,{\"id\":\"LA\",\"en\":\"Laos\",\"cn\":\"老挝\"}\n" +
            "    ,{\"id\":\"LV\",\"en\":\"Latvia\",\"cn\":\"拉脱维亚\"}\n" +
            "    ,{\"id\":\"LB\",\"en\":\"Lebanon\",\"cn\":\"黎巴嫩\"}\n" +
            "    ,{\"id\":\"LS\",\"en\":\"Lesotho\",\"cn\":\"莱索托\"}\n" +
            "    ,{\"id\":\"LR\",\"en\":\"Liberia\",\"cn\":\"利比里亚\"}\n" +
            "    ,{\"id\":\"LY\",\"en\":\"Libya\",\"cn\":\"利比亚\"}\n" +
            "    ,{\"id\":\"LI\",\"en\":\"Liechtenstein\",\"cn\":\"列支敦士登\"}\n" +
            "    ,{\"id\":\"LT\",\"en\":\"Lithuania\",\"cn\":\"立陶宛\"}\n" +
            "    ,{\"id\":\"LU\",\"en\":\"Luxembourg\",\"cn\":\"卢森堡\"}\n" +
            "    ,{\"id\":\"MO\",\"en\":\"Macao\",\"cn\":\"澳门\"}\n" +
            "    ,{\"id\":\"MG\",\"en\":\"Madagascar\",\"cn\":\"马达加斯加\"}\n" +
            "    ,{\"id\":\"MW\",\"en\":\"Malawi\",\"cn\":\"马拉维\"}\n" +
            "    ,{\"id\":\"MY\",\"en\":\"Malaysia\",\"cn\":\"马来西亚\"}\n" +
            "    ,{\"id\":\"MV\",\"en\":\"Maldives\",\"cn\":\"马尔代夫\"}\n" +
            "    ,{\"id\":\"ML\",\"en\":\"Mali\",\"cn\":\"马里\"}\n" +
            "    ,{\"id\":\"MT\",\"en\":\"Malta\",\"cn\":\"马耳他\"}\n" +
            "    ,{\"id\":\"MI\",\"en\":\"Mariana Is\",\"cn\":\"马里亚那群岛\"}\n" +
            "    ,{\"id\":\"MQ\",\"en\":\"Martinique\",\"cn\":\"马提尼克\"}\n" +
            "    ,{\"id\":\"MU\",\"en\":\"Mauritius\",\"cn\":\"毛里求斯\"}\n" +
            "    ,{\"id\":\"MX\",\"en\":\"Mexico\",\"cn\":\"墨西哥\"}\n" +
            "    ,{\"id\":\"MD\",\"en\":\"Moldova Republic of\",\"cn\":\"摩尔多瓦\"}\n" +
            "    ,{\"id\":\"MC\",\"en\":\"Monaco\",\"cn\":\"摩纳哥\"}\n" +
            "    ,{\"id\":\"MN\",\"en\":\"Mongolia\",\"cn\":\"蒙古\"}\n" +
            "    ,{\"id\":\"MS\",\"en\":\"Montserrat Is\",\"cn\":\"蒙特塞拉特岛\"}\n" +
            "    ,{\"id\":\"MA\",\"en\":\"Morocco\",\"cn\":\"摩洛哥\"}\n" +
            "    ,{\"id\":\"MZ\",\"en\":\"Mozambique\",\"cn\":\"莫桑比克\"}\n" +
            "    ,{\"id\":\"NA\",\"en\":\"Namibia\",\"cn\":\"纳米比亚\"}\n" +
            "    ,{\"id\":\"NR\",\"en\":\"Nauru\",\"cn\":\"瑙鲁\"}\n" +
            "    ,{\"id\":\"NP\",\"en\":\"Nepal\",\"cn\":\"尼泊尔\"}\n" +
            "    ,{\"id\":\"NS\",\"en\":\"Netheriands Antilles\",\"cn\":\"荷属安的列斯\"}\n" +
            "    ,{\"id\":\"NL\",\"en\":\"Netherlands\",\"cn\":\"荷兰\"}\n" +
            "    ,{\"id\":\"NZ\",\"en\":\"New Zealand\",\"cn\":\"新西兰\"}\n" +
            "    ,{\"id\":\"NI\",\"en\":\"Nicaragua\",\"cn\":\"尼加拉瓜\"}\n" +
            "    ,{\"id\":\"NE\",\"en\":\"Niger\",\"cn\":\"尼日尔\"}\n" +
            "    ,{\"id\":\"NG\",\"en\":\"Nigeria\",\"cn\":\"尼日利亚\"}\n" +
            "    ,{\"id\":\"KP\",\"en\":\"North Korea\",\"cn\":\"朝鲜\"}\n" +
            "    ,{\"id\":\"NO\",\"en\":\"Norway\",\"cn\":\"挪威\"}\n" +
            "    ,{\"id\":\"OM\",\"en\":\"Oman\",\"cn\":\"阿曼\"}\n" +
            "    ,{\"id\":\"PK\",\"en\":\"Pakistan\",\"cn\":\"巴基斯坦\"}\n" +
            "    ,{\"id\":\"PA\",\"en\":\"Panama\",\"cn\":\"巴拿马\"}\n" +
            "    ,{\"id\":\"PG\",\"en\":\"Papua New Cuinea\",\"cn\":\"巴布亚新几内亚\"}\n" +
            "    ,{\"id\":\"PY\",\"en\":\"Paraguay\",\"cn\":\"巴拉圭\"}\n" +
            "    ,{\"id\":\"PE\",\"en\":\"Peru\",\"cn\":\"秘鲁\"}\n" +
            "    ,{\"id\":\"PH\",\"en\":\"Philippines\",\"cn\":\"菲律宾\"}\n" +
            "    ,{\"id\":\"PL\",\"en\":\"Poland\",\"cn\":\"波兰\"}\n" +
            "    ,{\"id\":\"PF\",\"en\":\"French Polynesia\",\"cn\":\"法属玻利尼西亚\"}\n" +
            "    ,{\"id\":\"PT\",\"en\":\"Portugal\",\"cn\":\"葡萄牙\"}\n" +
            "    ,{\"id\":\"PR\",\"en\":\"Puerto Rico\",\"cn\":\"波多黎各\"}\n" +
            "    ,{\"id\":\"QA\",\"en\":\"Qatar\",\"cn\":\"卡塔尔\"}\n" +
            "    ,{\"id\":\"RI\",\"en\":\"Reunion\",\"cn\":\"留尼旺\"}\n" +
            "    ,{\"id\":\"RO\",\"en\":\"Romania\",\"cn\":\"罗马尼亚\"}\n" +
            "    ,{\"id\":\"RU\",\"en\":\"Russia\",\"cn\":\"俄罗斯\"}\n" +
            "    ,{\"id\":\"LC\",\"en\":\"Saint Lueia\",\"cn\":\"圣卢西亚\"}\n" +
            "    ,{\"id\":\"VC\",\"en\":\"Saint Vincent\",\"cn\":\"圣文森特岛\"}\n" +
            "    ,{\"id\":\"SEN\",\"en\":\"Samoa Eastern\",\"cn\":\"东萨摩亚(美)\"}\n" +
            "    ,{\"id\":\"SW\",\"en\":\"Samoa Western\",\"cn\":\"西萨摩亚\"}\n" +
            "    ,{\"id\":\"SM\",\"en\":\"San Marino\",\"cn\":\"圣马力诺\"}\n" +
            "    ,{\"id\":\"ST\",\"en\":\"Sao Tome and Principe\",\"cn\":\"圣多美和普林西比\"}\n" +
            "    ,{\"id\":\"SA\",\"en\":\"Saudi Arabia\",\"cn\":\"沙特阿拉伯\"}\n" +
            "    ,{\"id\":\"SN\",\"en\":\"Senegal\",\"cn\":\"塞内加尔\"}\n" +
            "    ,{\"id\":\"SC\",\"en\":\"Seychelles\",\"cn\":\"塞舌尔\"}\n" +
            "    ,{\"id\":\"SL\",\"en\":\"Sierra Leone\",\"cn\":\"塞拉利昂\"}\n" +
            "    ,{\"id\":\"SG\",\"en\":\"Singapore\",\"cn\":\"新加坡\"}\n" +
            "    ,{\"id\":\"SK\",\"en\":\"Slovakia\",\"cn\":\"斯洛伐克\"}\n" +
            "    ,{\"id\":\"SI\",\"en\":\"Slovenia\",\"cn\":\"斯洛文尼亚\"}\n" +
            "    ,{\"id\":\"SB\",\"en\":\"Solomon Is\",\"cn\":\"所罗门群岛\"}\n" +
            "    ,{\"id\":\"SO\",\"en\":\"Somali\",\"cn\":\"索马里\"}\n" +
            "    ,{\"id\":\"ZA\",\"en\":\"South Africa\",\"cn\":\"南非\"}\n" +
            "    ,{\"id\":\"ES\",\"en\":\"Spain\",\"cn\":\"西班牙\"}\n" +
            "    ,{\"id\":\"LK\",\"en\":\"Sri Lanka\",\"cn\":\"斯里兰卡\"}\n" +
            "    ,{\"id\":\"VC\",\"en\":\"St.Vincent\",\"cn\":\"圣文森特\"}\n" +
            "    ,{\"id\":\"SD\",\"en\":\"Sudan\",\"cn\":\"苏丹\"}\n" +
            "    ,{\"id\":\"SR\",\"en\":\"Suriname\",\"cn\":\"苏里南\"}\n" +
            "    ,{\"id\":\"SZ\",\"en\":\"Swaziland\",\"cn\":\"斯威士兰\"}\n" +
            "    ,{\"id\":\"SE\",\"en\":\"Sweden\",\"cn\":\"瑞典\"}\n" +
            "    ,{\"id\":\"CH\",\"en\":\"Switzerland\",\"cn\":\"瑞士\"}\n" +
            "    ,{\"id\":\"SY\",\"en\":\"Syria\",\"cn\":\"叙利亚\"}\n" +
            "    ,{\"id\":\"TW\",\"en\":\"Taiwan\",\"cn\":\"中国台湾\"}\n" +
            "    ,{\"id\":\"TJ\",\"en\":\"Tajikstan\",\"cn\":\"塔吉克斯坦\"}\n" +
            "    ,{\"id\":\"TZ\",\"en\":\"Tanzania\",\"cn\":\"坦桑尼亚\"}\n" +
            "    ,{\"id\":\"TH\",\"en\":\"Thailand\",\"cn\":\"泰国\"}\n" +
            "    ,{\"id\":\"TG\",\"en\":\"Togo\",\"cn\":\"多哥\"}\n" +
            "    ,{\"id\":\"TO\",\"en\":\"Tonga\",\"cn\":\"汤加\"}\n" +
            "    ,{\"id\":\"TT\",\"en\":\"Trinidad and Tobago\",\"cn\":\"特立尼达和多巴哥\"}\n" +
            "    ,{\"id\":\"TN\",\"en\":\"Tunisia\",\"cn\":\"突尼斯\"}\n" +
            "    ,{\"id\":\"TR\",\"en\":\"Turkey\",\"cn\":\"土耳其\"}\n" +
            "    ,{\"id\":\"TM\",\"en\":\"Turkmenistan\",\"cn\":\"土库曼斯坦\"}\n" +
            "    ,{\"id\":\"UG\",\"en\":\"Uganda\",\"cn\":\"乌干达\"}\n" +
            "    ,{\"id\":\"UA\",\"en\":\"Ukraine\",\"cn\":\"乌克兰\"}\n" +
            "    ,{\"id\":\"AE\",\"en\":\"United Arab Emirates\",\"cn\":\"阿拉伯联合酋长国\"}\n" +
            "    ,{\"id\":\"GB\",\"en\":\"United Kiongdom\",\"cn\":\"英国\"}\n" +
            "    ,{\"id\":\"US\",\"en\":\"United States of America\",\"cn\":\"美国\"}\n" +
            "    ,{\"id\":\"UY\",\"en\":\"Uruguay\",\"cn\":\"乌拉圭\"}\n" +
            "    ,{\"id\":\"UZ\",\"en\":\"Uzbekistan\",\"cn\":\"乌兹别克斯坦\"}\n" +
            "    ,{\"id\":\"VE\",\"en\":\"Venezuela\",\"cn\":\"委内瑞拉\"}\n" +
            "    ,{\"id\":\"VN\",\"en\":\"Vietnam\",\"cn\":\"越南\"}\n" +
            "    ,{\"id\":\"YE\",\"en\":\"Yemen\",\"cn\":\"也门\"}\n" +
            "    ,{\"id\":\"ZW\",\"en\":\"Zimbabwe\",\"cn\":\"津巴布韦\"}\n" +
            "    ,{\"id\":\"ZR\",\"en\":\"Zaire\",\"cn\":\"扎伊尔\"}\n" +
            "    ,{\"id\":\"ZM\",\"en\":\"Zambia\",\"cn\":\"赞比亚\"}\n" +
            "    ,{\"id\":\"CRO\",\"en\":\"Croatia\",\"cn\":\"克罗地亚\"}\n" +
            "    ,{\"id\":\"MKD\",\"en\":\"Macedonia\",\"cn\":\"北马其顿\"}\n" +
            "    ,{\"id\":\"BIH\",\"en\":\"Bosna i Hercegovina\",\"cn\":\"波黑\"}\n" +
            "    ,{\"id\":\"SRB\",\"en\":\"Republika Srbija\",\"cn\":\"塞尔维亚\"}\n" +
            "    ,{\"id\":\"MTG\",\"en\":\"Montenegro\",\"cn\":\"黑山\"}\n" +
            "    ,{\"id\":\"GNQ\",\"en\":\"Equatorial Guinea\",\"cn\":\"赤道几内亚\"}\n" +
            "]";
    Integer userId = 1;
    String userName = "系统管理员";
    Date now = new Date(System.currentTimeMillis());
    List<JSONObject> arr = JSON.parseArray(param, JSONObject.class);
    Comparator<Object> comparator = Collator.getInstance(Locale.ENGLISH);
    arr.sort((v1,v2)->{
      String c1 = HanyuPinyinHelper.toHanyuPinyin(v1.getString("cn"));
      String c2 = HanyuPinyinHelper.toHanyuPinyin(v2.getString("cn"));
      return comparator.compare(c1, c2);
    });
    System.out.println(arr);
    for (JSONObject obj : arr) {
      DictionaryItem entity = new DictionaryItem();
      entity.setEnglishName(obj.getString("en"));
      entity.setName(obj.getString("cn"));
      entity.setDictionaryTypeId(30);
      entity.setInservice(true);
      entity.setCrtId(userId);
      entity.setCrtName(userName);
      entity.setCrtTime(now);
      entity.setUpdId(userId);
      entity.setUpdName(userName);
      entity.setUpdTime(now);
      dictionaryItemBiz.insertSelective(entity);
    }
  }
}
