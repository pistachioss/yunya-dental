## <h1 align = "center">客户患者绑定关系</h1>



### 简要描述：

*   <font size=4> 根据unionId查询客户患者关系</font>


### 请求URL：

* 测试 [http://test.ivy2.yunya365.com/api/patient/white/wechat/user/relate](http://test.ivy2.yunya365.com/api/patient/white/wechat/user/relate)
* 正式 [http://ivy2.yunya365.com/api/patient/white/wechat/user/relate](http://ivy2.yunya365.com/api/patient/white/wechat/user/relate)


### 请求方式：

* GET



### 参数

| 参数名 | 必选 | 类型      | 说明 |
| --- |:---:|---------| --- |
| unionId | 是 | string  | 微信开放平台的唯一身份标识 |


### 返回示例

```
{
    "status": 0,
    "msg": "success",
    "data": [
        {
            "patientName": "xy",
            "patientId": 105,
            "dictionaryName": "朋友",
            "dictionaryId": 127,
            "mobile": "17816726150",
            "bindDate": "2022-11-10 16:49"
        },
        {
            "patientName": "艾",
            "patientId": 235,
            "dictionaryName": "同事",
            "dictionaryId": 128,
            "mobile": "13306517203",
            "bindDate": "2022-11-10 17:43"
        }
    ],
    "audit": true
}
```



### 返回参数说明

| 参数名 |   类型    | 说明                    |
| --- |:-------:|-----------------------|
| status |   int   | 0成功，其余状态失败(根据该字段校验接口) |
| msg | String  | 成功success             |
| data | Object  | 数据，见data明细                |
| audit | Boolean |                       |

### data明细

| 参数名 |   类型    | 说明     |
| --- |:-------:|--------|
| patientName | String | 患者姓名   |
| patientId | Integer  | 患者id   |
| dictionaryName | String  | 关系名称   |
| dictionaryId |   Integer   | 关系字典ID |
| mobile | String  | 患者手机号  |
| bindDate | String  | 绑定时间(yyyy-MM-dd HH:mm:ss) |




