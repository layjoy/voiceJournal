# 讯飞语音SDK集成说明

## 下载SDK

1. 访问讯飞开放平台：https://www.xfyun.cn/
2. 登录后进入控制台
3. 下载"语音听写"SDK for Android
4. 解压后将以下文件复制到项目的 `app/libs/` 目录：
   - `Msc.jar` 或 `iflytek-msc-xxx.aar`
   - `arm64-v8a/libmsc.so`
   - `armeabi-v7a/libmsc.so`

## 目录结构

```
app/
├── libs/
│   ├── Msc.jar (或 iflytek-msc-xxx.aar)
│   └── jniLibs/
│       ├── arm64-v8a/
│       │   └── libmsc.so
│       └── armeabi-v7a/
│           └── libmsc.so
```

## 配置信息

已在代码中配置：
- APP_ID: 5ea2c189
- API_KEY: a36cfb9b3b6e9ddd87212d7b106a82cb
- API_SECRET: a21702133210bff60dccac53d7d1208a

## 权限说明

AndroidManifest.xml 已添加必要权限：
- RECORD_AUDIO: 录音权限
- INTERNET: 网络访问（云端识别）
- ACCESS_NETWORK_STATE: 网络状态
- WRITE_EXTERNAL_STORAGE: 存储权限

## 注意事项

1. 首次运行需要动态申请录音权限
2. 需要网络连接才能使用云端识别
3. 建议在真机上测试，模拟器可能无法正常录音
