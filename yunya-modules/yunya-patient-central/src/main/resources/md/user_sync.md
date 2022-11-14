## <h1 align = "center">客户同步接口</h1>



### 简要描述：

*   ```  
    企业微信客户同步至小程序、公众号业务（单次限制1000）
    ```
    
    

### 请求URL：

* 测试 [https://test.ivy2.yunya365.com/api/patient/white/wechat/user](http://test.ivy2.yunya365.com/api/patient/white/wechat/user)
* 正式 [https://ivy2.yunya365.com/api/patient/white/wechat/user](http://ivy2.yunya365.com/api/patient/white/wechat/user)
  

### 请求方式：

* POST

  

### 参数

```
{
    "openId": "o6yjV5Anq6ExBAbqFuz6TWvxwY6U",
    "unionId": "o9ls5v7jFIPFPA2Q419-94JKU_tI",
    "address": "杭州xxxxx",
    "birthday": "2022-11-10 09:10:29",
    "city": "杭州市",
    "country": "中国",
    "gender": 0,
    "headImgurl": "https://thirdwx.qlogo.cn/mmopen/vi_32/DYAIOgq83erXZbKKM9HwFyjL56st2g0O9xTibkk9OB8ofFNOMYnbkcRGM0q4Bn1NcGrhh688jmrzb2vqtIHqOvQ/132",
    "language": "zh_CN",
    "mobile": "17816726150",
    "nickName": "佚名",
    "province": "浙江省",
    "remark": "备注",
    "sourceType": 2,
    "sourceTypeName": "xxx企业微信"
}
```



### 参数字段说明



| 参数名 | 必选 | 类型      | 说明                                  |
|:---:|:---:|---------|-------------------------------------|
| openId | 是 | string  | 公众号/小程序：openid，企业微信：external_userid |
| unionId |  是 | string  | 微信开放平台的唯一身份标识                       |
| address |  否  | string  | 地址                                  |
| birthday |  否 | string  | 生日(yyyy-MM-dd HH:mm:ss)                                |
| city |  否 | string  | 用户所在城市                              |
| country |  否 | string  | 用户所在国家                              |
| gender |  否 | Integer | 用户性别 0-未知 1-男性 2-女性                 |
| headImgurl |  否 | string  | 用户头像                                |
| language |  否 | string  | 语言                                  |
| mobile |  否 | string  | 手机号                                 |
| nickName |  否 | string  | 用户昵称                                |
| province |  否 | string  | 用户所在省份                              |
| remark |  否 | string  | 备注                                  |
| sourceType |  是 | Integer  | 用户来源（0-公众号 1-小程序 2-企业微信）            |
| sourceTypeName |  是 | string  | 用户来源名称                              |


### 返回示例

```
{
    "status": 0,
    "msg": "success",
    "data": true,
    "audit": true
}
```



### 返回参数说明

| 参数名 |   类型    | 说明                   |
| --- |:-------:|----------------------|
| status |   int   | 0成功，其余状态失败(根据该字段校验接口) |
| msg | String  | 成功success            |
| data | Object  |                    |
| audit | Boolean |                      |




