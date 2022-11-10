## <h1 align = "center">客户患者绑定关系</h1>



### 简要描述：

*   ```  
    查询客户
    ```



### 请求URL：

* [http://x.com/api/user/password1](http://x.com/api/user/password1)



### 请求方式：

* POST



### json参数

```
{
    &quot;uid&quot;: 1,
    &quot;password&quot;:&quot;123456&quot;
  }
```



### json参数字段说明



| 参数名 | 必选 | 类型 | 说明 |
| --- |:---:| --- | --- |
| uid | 是 | number | 用户id |
| password | 是 | string | 要设置的新密码 |



### 返回示例

```
{
    &quot;error_code&quot;: 0,
    &quot;data&quot;: {
      &quot;uid&quot;: &quot;1&quot;,
      &quot;username&quot;: &quot;12154545&quot;,
      &quot;name&quot;: &quot;吴系挂&quot;,
      &quot;groupid&quot;: 2 ,
      &quot;reg_time&quot;: &quot;1936864169&quot;,
      &quot;last_login_time&quot;: &quot;0&quot;,
    }
  }
```



### 返回参数说明

| 参数名 | 类型 | 说明 |
| --- |:---:| --- |
| groupid | int | 用户组id，1：超级管理员；2：普通用户 |




