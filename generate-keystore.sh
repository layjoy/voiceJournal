#!/bin/bash

# 生成签名密钥脚本

echo "=========================================="
echo "声音日记 - 生成签名密钥"
echo "=========================================="

KEYSTORE_FILE="voicejournal.keystore"
KEY_ALIAS="voicejournal"

if [ -f "$KEYSTORE_FILE" ]; then
    echo "警告: $KEYSTORE_FILE 已存在"
    read -p "是否覆盖? (y/n): " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "取消操作"
        exit 0
    fi
    rm "$KEYSTORE_FILE"
fi

echo "开始生成密钥库..."
echo "请按提示输入信息"
echo ""

keytool -genkey -v \
    -keystore "$KEYSTORE_FILE" \
    -alias "$KEY_ALIAS" \
    -keyalg RSA \
    -keysize 2048 \
    -validity 10000

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "密钥库生成成功！"
    echo "=========================================="
    echo "文件位置: $KEYSTORE_FILE"
    echo "密钥别名: $KEY_ALIAS"
    echo ""
    echo "下一步:"
    echo "1. 妥善保管此密钥库文件"
    echo "2. 创建 keystore.properties 文件"
    echo "3. 不要将密钥库提交到Git"
    echo "=========================================="
else
    echo "密钥库生成失败"
    exit 1
fi
