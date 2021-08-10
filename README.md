# ali-webdav
本项目实现了阿里云盘的webdav协议，只需要简单的配置一下，就可以让阿里云盘变身为webdav协议的文件服务器。
基于此，你可以把阿里云盘挂载为Windows、Linux、Mac系统的磁盘，可以通过NAS系统做文件管理或文件同步，更多玩法等你挖掘


## 代码运行

```
# 代码下载 
git clone  https://github.com/maruifu/ali-webdav.git
# 代码打包 
mvn clean package -Dmaven.test.skip=true
# 运行
java -jar ali-webdav-0.1.0.jar  --aliyundrive.refresh-token="12ecb25e6ef23f0baf6bc3f9221022c7"  
# 也可以这样
java -jar ali-webdav-0.1.0.jar  --aliyundrive.refresh-token="12ecb25e6ef23f0baf6bc3f9221022c7"  --server.port="8888"  --aliyundrive.auth.enable="true"  --aliyundrive.auth.username="username"  --aliyundrive.auth.password="password"

# aliyundrive.refresh-token 是你的refreshToken 必填参数
# server.port 是你的端口默认是8080 选填
# aliyundrive.auth.enable 是否开启WebDav账户验证，默认开启true 选填
# aliyundrive.auth.username WebDav账户，默认admin 选填
# aliyundrive.auth.password WebDav密码，默认admin 选填

```

## 容器运行
```bash
# 运行
docker run -d --name=ali-webdav --restart=always -p 8888:8080  -e ALIYUNDRIVE_REFRESH_TOKEN="12ecb25e6ef23f0baf6bc3f9221022c7"xiaomageit/ali-webdav:0.1.0
# 也可以这样
docker run -d --name=ali-webdav --restart=always -p 8888:8080  -v /etc/localtime:/etc/localtime -v /Users/maruifu/work/temp/:/usr/local/java/docker/ -e TZ="Asia/Shanghai" -e ALIYUNDRIVE_REFRESH_TOKEN="12ecb25e6ef23f0baf6bc3f9221022c7" -e ALIYUNDRIVE_AUTH_PASSWORD="admin" -e JAVA_OPTS="-Xmx1g" xiaomageit/ali-webdav:0.1.0

# ALIYUNDRIVE_REFRESH_TOKEN 是你的refreshToken 必填参数
# /usr/local/java/docker/ 挂载卷自动维护了最新的refreshToken，建议挂载
# ALIYUNDRIVE_AUTH_ENABLE 是否开启WebDav账户验证，默认开启true 选填
# ALIYUNDRIVE_AUTH_USERNAME WebDav账户，默认admin 选填
# ALIYUNDRIVE_AUTH_PASSWORD WebDav密码，默认admin 选填
# JAVA_OPTS 可修改最大内存占用，比如 -e JAVA_OPTS="-Xmx512m" 表示最大内存限制为512m
```



# 新手教程
# 群晖

**[点击我查看教程](./tutorial/synology.md)**


## Windows10
TODO

## Linux
TODO

## Mac
TODO

# 客户端兼容性
| 客户端         | 下载 |    上传    |              备注              |
| :------------- | ---: | :--------: | :----------------------------: |
| 群辉Cloud Sync | 可用 |    可用    |      使用单向同步非常稳定      |
| Rclone         | 可用 |    可用    |       推荐，支持各个系统       |
| Mac原生        | 可用 |    可用    |                                |
| Windows原生    | 可用 | 有点小问题 | 不建议，适配有点问题，上传报错 |
| RaiDrive       | 可用 |    可用    |    Windows平台下建议用这个     |


# 浏览器获取refreshToken方式
1. 先通过浏览器（建议chrome）打开阿里云盘官网并登录：https://www.aliyundrive.com/drive/
2. 登录成功后，按F12打开开发者工具，点击Application，点击Local Storage，点击 Local Storage下的 [https://www.aliyundrive.com/](https://www.aliyundrive.com/)，点击右边的token，此时可以看到里面的数据，其中就有refresh_token，把其值复制出来即可。（格式为小写字母和数字，不要复制双引号。例子：ca6bf2175d73as2188efg81f87e55f11）
3. 第二步有点繁琐，大家结合下面的截图就看懂了
 ![image](https://xiaomage.myds.me:10001/images/2021/08/10/119246278-e6760880-bbb2-11eb-877c-aca16cf75d89.png)

# 功能说明
## 支持的功能
1. 查看文件夹、查看文件
2. 文件移动目录
3. 文件重命名
4. 文件下载
5. 文件删除
6. 文件上传（支持大文件自动分批上传）
7. 支持超大文件上传（官方限制30G）
8. 支持WebDav权限校验（默认账户密码：admin/admin）
9. 文件下载断点续传
10. Webdav下的流媒体播放等功能
## 暂不支持的功能
1. 移动文件到其他目录的同时，修改文件名。比如 /a.zip 移动到 /b/a1.zip，是不支持的
2. 文件上传断点续传
3. 部分客户端兼容性不好
## 已知问题
1. 没有做文件sha1校验，不保证上传文件的100%准确性（一般场景下，是没问题的）
2. 通过文件名和文件大小判断是否重复。也就是说如果一个文件即使发生了更新，但其大小没有任何改变，是不会自动上传的
3. 不支持文件名包含 `/` 字符  

