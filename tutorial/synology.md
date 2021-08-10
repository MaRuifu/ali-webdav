# 群晖使用ali-webdav

> 首先你的群晖支持docker

## ali-webdav下载
在docker的注册表搜索 `ali-webdav ` 如图所示然后点击下载
![image-20210810170701212](https://xiaomage.myds.me:10001/images/2021/08/10/image-20210810170701212.png)

## ali-webdava安装

映像中选中ali-webdava 点击启动

![image-20210810171329122](https://xiaomage.myds.me:10001/images/2021/08/10/image-20210810171329122.png)

### 高级设置

#### 启用自动重新启动

![image-20210810171451154](https://xiaomage.myds.me:10001/images/2021/08/10/image-20210810171451154.png)

作用是开机启动不用每次重新启动

#### 挂载文件夹路径

`/usr/local/java/docker/`

![image-20210810171706996](https://xiaomage.myds.me:10001/images/2021/08/10/image-20210810171706996.png)

作用是1.如果token 失效可以直接替换文件夹中的token。2.可以直接查看日志

#### 设置端口

![image-20210810172028901](https://xiaomage.myds.me:10001/images/2021/08/10/image-20210810172028901.png)

作用 后面可以页面访问，防止端口变化 

#### 设置参数

![image-20210810172216014](https://xiaomage.myds.me:10001/images/2021/08/10/image-20210810172216014.png)

```bash
# ALIYUNDRIVE_REFRESH_TOKEN 是你的refreshToken 必填参数
# 下面三个参数可以不做修改
# ALIYUNDRIVE_AUTH_ENABLE 是否开启WebDav账户验证，默认开启true 选填
# ALIYUNDRIVE_AUTH_USERNAME WebDav账户，默认admin 选填
# ALIYUNDRIVE_AUTH_PASSWORD WebDav密码，默认admin 选填
```
#### 验证是否成功

![image-20210810173239005](https://xiaomage.myds.me:10001/images/2021/08/10/image-20210810173239005.png)


输入你的群晖IP 加上刚才设置的端口号 我的地址是 http://192.168.2.153:8088/

如果出现访问页面则代表成功，如果不是的话 看一下日志提示的什么错误。

