#!/bin/bash

# 声音日记 - 构建脚本

echo "=========================================="
echo "声音日记 - 开始构建"
echo "=========================================="

# 清理之前的构建
echo "清理之前的构建..."
./gradlew clean

# 运行单元测试
echo "运行单元测试..."
./gradlew test

if [ $? -ne 0 ]; then
    echo "单元测试失败，停止构建"
    exit 1
fi

# 构建Debug版本
echo "构建Debug APK..."
./gradlew assembleDebug

if [ $? -ne 0 ]; then
    echo "Debug构建失败"
    exit 1
fi

# 构建Release版本
echo "构建Release APK..."
./gradlew assembleRelease

if [ $? -ne 0 ]; then
    echo "Release构建失败"
    exit 1
fi

echo "=========================================="
echo "构建完成！"
echo "=========================================="
echo "Debug APK: app/build/outputs/apk/debug/app-debug.apk"
echo "Release APK: app/build/outputs/apk/release/app-release.apk"
echo "=========================================="
