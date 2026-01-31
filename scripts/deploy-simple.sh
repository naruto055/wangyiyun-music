#!/bin/bash

###############################################################################
# 网易云音乐项目 - 简化部署脚本（一体化部署方案）
# 说明：在应用目录下创建所有必需的子目录，无需配置环境变量
# 作者：naruto
# 创建时间：2026-01-31
###############################################################################

set -e  # 遇到错误立即退出

# ==================== 配置区 ====================

# 应用配置
APP_NAME="wangyiyun-music"
APP_VERSION="0.0.1-SNAPSHOT"
APP_JAR="${APP_NAME}-${APP_VERSION}.jar"

# 部署目录配置（所有文件都在这个目录下）
DEPLOY_DIR="/opt/service/${APP_NAME}"

# 服务配置
SERVER_PORT=8910
SPRING_PROFILE="prod"

# JVM 参数
JAVA_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# 应用用户（非 root 用户运行）
APP_USER="appuser"

# ==================== 颜色输出 ====================

RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

function log_info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

function log_warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

function log_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

function log_step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# ==================== 前置检查 ====================

function check_prerequisites() {
    log_step "检查系统环境..."

    # 检查是否为 root 用户
    if [[ $EUID -ne 0 ]]; then
        log_error "请使用 root 用户或 sudo 执行此脚本"
        exit 1
    fi

    # 检查 Java 版本
    if ! command -v java &> /dev/null; then
        log_error "未检测到 Java，请先安装 JDK 17+"
        exit 1
    fi

    JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | cut -d'.' -f1)
    if [[ $JAVA_VERSION -lt 17 ]]; then
        log_error "Java 版本过低，当前: ${JAVA_VERSION}，需要: 17+"
        exit 1
    fi
    log_info "✅ Java 版本检查通过: ${JAVA_VERSION}"

    # 检查 JAR 文件是否存在
    if [[ ! -f "target/${APP_JAR}" ]]; then
        log_error "未找到 JAR 文件: target/${APP_JAR}"
        log_info "请先执行: mvn clean package"
        exit 1
    fi
    log_info "✅ JAR 文件检查通过"
}

# ==================== 创建应用用户 ====================

function create_app_user() {
    log_step "创建应用用户 ${APP_USER}..."

    if id "${APP_USER}" &>/dev/null; then
        log_warn "用户 ${APP_USER} 已存在，跳过创建"
    else
        useradd -r -s /bin/false ${APP_USER}
        log_info "✅ 用户 ${APP_USER} 创建成功"
    fi
}

# ==================== 创建目录结构（一体化） ====================

function create_directories() {
    log_step "创建一体化目录结构..."

    # 创建部署根目录
    mkdir -p ${DEPLOY_DIR}

    # 在部署目录下创建所有子目录
    mkdir -p ${DEPLOY_DIR}/music-data/temp
    mkdir -p ${DEPLOY_DIR}/music-data/audio
    mkdir -p ${DEPLOY_DIR}/tools
    mkdir -p ${DEPLOY_DIR}/logs

    log_info "✅ 目录结构创建完成："
    log_info "   ${DEPLOY_DIR}/"
    log_info "   ├── wangyiyun-music.jar  （待部署）"
    log_info "   ├── music-data/"
    log_info "   │   ├── temp/            ← 临时音频文件"
    log_info "   │   └── audio/           ← 永久音频文件"
    log_info "   ├── tools/               ← yt-dlp 工具"
    log_info "   └── logs/                ← 应用日志"
}

# ==================== 安装 yt-dlp（到应用目录） ====================

function install_ytdlp() {
    log_step "安装 yt-dlp 工具到应用目录..."

    YTDLP_PATH="${DEPLOY_DIR}/tools/yt-dlp"

    if [[ -f ${YTDLP_PATH} ]]; then
        log_warn "yt-dlp 已存在，跳过安装"
    else
        wget https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -O ${YTDLP_PATH}
        chmod +x ${YTDLP_PATH}
        log_info "✅ yt-dlp 安装成功: ${YTDLP_PATH}"
    fi

    # 验证安装
    ${YTDLP_PATH} --version
}

# ==================== 部署应用 ====================

function deploy_application() {
    log_step "部署应用..."

    # 停止旧服务（如果存在）
    if systemctl is-active --quiet ${APP_NAME}; then
        log_info "停止旧服务..."
        systemctl stop ${APP_NAME}
    fi

    # 备份旧 JAR（如果存在）
    if [[ -f ${DEPLOY_DIR}/${APP_JAR} ]]; then
        BACKUP_FILE="${DEPLOY_DIR}/${APP_JAR}.$(date +%Y%m%d_%H%M%S).backup"
        mv ${DEPLOY_DIR}/${APP_JAR} ${BACKUP_FILE}
        log_info "旧版本已备份: ${BACKUP_FILE}"
    fi

    # 复制新 JAR
    cp target/${APP_JAR} ${DEPLOY_DIR}/
    log_info "✅ 应用文件部署完成"
}

# ==================== 配置文件权限 ====================

function set_permissions() {
    log_step "设置文件权限..."

    # 设置整个部署目录的所有权
    chown -R ${APP_USER}:${APP_USER} ${DEPLOY_DIR}

    # 设置目录权限
    chmod 750 ${DEPLOY_DIR}
    chmod 770 ${DEPLOY_DIR}/music-data
    chmod 770 ${DEPLOY_DIR}/music-data/temp
    chmod 770 ${DEPLOY_DIR}/music-data/audio
    chmod 750 ${DEPLOY_DIR}/tools
    chmod 770 ${DEPLOY_DIR}/logs

    log_info "✅ 权限设置完成"
}

# ==================== 创建 systemd 服务 ====================

function create_systemd_service() {
    log_step "创建 systemd 服务..."

    SERVICE_FILE="/etc/systemd/system/${APP_NAME}.service"

    cat > ${SERVICE_FILE} << EOF
[Unit]
Description=WangYiYun Music Service
After=network.target mysql.service

[Service]
Type=simple
User=${APP_USER}
WorkingDirectory=${DEPLOY_DIR}

# JVM 参数
Environment="JAVA_OPTS=${JAVA_OPTS}"

# 启动命令
# 说明：
# - 工作目录设置为 ${DEPLOY_DIR}
# - 应用会自动使用 music-data/temp 和 music-data/audio 子目录
# - 无需配置环境变量，开箱即用
ExecStart=/usr/bin/java \$JAVA_OPTS \\
  -jar ${DEPLOY_DIR}/${APP_JAR} \\
  --spring.profiles.active=${SPRING_PROFILE} \\
  --server.port=${SERVER_PORT}

# 重启策略
Restart=on-failure
RestartSec=10

# 日志配置
StandardOutput=journal
StandardError=journal
SyslogIdentifier=${APP_NAME}

# 安全配置
NoNewPrivileges=true
PrivateTmp=true

[Install]
WantedBy=multi-user.target
EOF

    log_info "✅ systemd 服务文件创建成功: ${SERVICE_FILE}"
}

# ==================== 启动服务 ====================

function start_service() {
    log_step "启动应用服务..."

    # 重载 systemd 配置
    systemctl daemon-reload

    # 启动服务
    systemctl start ${APP_NAME}

    # 等待服务启动
    sleep 5

    # 检查服务状态
    if systemctl is-active --quiet ${APP_NAME}; then
        log_info "✅ 服务启动成功"
        systemctl status ${APP_NAME} --no-pager
    else
        log_error "❌ 服务启动失败"
        log_error "请查看日志: journalctl -u ${APP_NAME} -n 50"
        exit 1
    fi

    # 设置开机自启
    systemctl enable ${APP_NAME}
    log_info "✅ 已设置开机自启"
}

# ==================== 健康检查 ====================

function health_check() {
    log_step "执行健康检查..."

    # 等待应用启动
    log_info "等待应用完全启动（30秒）..."
    sleep 30

    # 检查端口监听
    if netstat -tuln | grep -q ":${SERVER_PORT}"; then
        log_info "✅ 端口 ${SERVER_PORT} 监听正常"
    else
        log_error "❌ 端口 ${SERVER_PORT} 未监听"
        exit 1
    fi

    # 检查目录是否创建
    if [[ -d ${DEPLOY_DIR}/music-data/temp && -d ${DEPLOY_DIR}/music-data/audio ]]; then
        log_info "✅ 数据目录创建成功"
    else
        log_warn "⚠️ 数据目录可能未正确创建"
    fi

    # 检查 API 服务（可选）
    if command -v curl &> /dev/null; then
        log_info "检查 API 服务..."
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${SERVER_PORT}/swagger-ui/index.html || echo "000")
        if [[ ${HTTP_CODE} == "200" ]]; then
            log_info "✅ Swagger UI 访问正常"
        else
            log_warn "⚠️ Swagger UI 访问异常，HTTP Code: ${HTTP_CODE}"
            log_warn "   可能原因：应用仍在启动中，请稍后手动验证"
        fi
    else
        log_warn "⚠️ 未安装 curl，跳过 API 检查"
        log_info "   请手动访问：http://localhost:${SERVER_PORT}/swagger-ui/index.html"
    fi
}

# ==================== 显示部署信息 ====================

function show_deployment_info() {
    echo ""
    echo "========================================"
    echo "🎉 部署完成！（一体化部署方案）"
    echo "========================================"
    echo "应用名称: ${APP_NAME}"
    echo "版本: ${APP_VERSION}"
    echo "部署目录: ${DEPLOY_DIR}"
    echo ""
    echo "目录结构："
    echo "${DEPLOY_DIR}/"
    echo "├── ${APP_JAR}           ← 应用程序"
    echo "├── music-data/"
    echo "│   ├── temp/                       ← 临时音频文件"
    echo "│   └── audio/                      ← 永久音频文件"
    echo "├── tools/"
    echo "│   └── yt-dlp                      ← 视频解析工具"
    echo "└── logs/                           ← 应用日志"
    echo ""
    echo "服务端口: ${SERVER_PORT}"
    echo "运行用户: ${APP_USER}"
    echo ""
    echo "特点："
    echo "✅ 所有文件在一个目录，易于管理和备份"
    echo "✅ 无需配置环境变量，开箱即用"
    echo "✅ 迁移时只需整体打包 ${DEPLOY_DIR} 目录"
    echo ""
    echo "访问地址："
    echo "  - Swagger UI: http://localhost:${SERVER_PORT}/swagger-ui/index.html"
    echo "  - API Docs: http://localhost:${SERVER_PORT}/v3/api-docs"
    echo ""
    echo "常用命令："
    echo "  - 查看状态: systemctl status ${APP_NAME}"
    echo "  - 查看日志: journalctl -u ${APP_NAME} -f"
    echo "  - 停止服务: systemctl stop ${APP_NAME}"
    echo "  - 重启服务: systemctl restart ${APP_NAME}"
    echo ""
    echo "备份与迁移："
    echo "  - 备份数据: tar -czf backup-\$(date +%Y%m%d).tar.gz ${DEPLOY_DIR}"
    echo "  - 迁移部署: 复制整个 ${DEPLOY_DIR} 目录到新服务器即可"
    echo "========================================"
}

# ==================== 主流程 ====================

function main() {
    log_info "开始部署 ${APP_NAME}（一体化部署方案）..."
    log_info "所有文件将部署在: ${DEPLOY_DIR}"

    check_prerequisites
    create_app_user
    create_directories
    install_ytdlp
    deploy_application
    set_permissions
    create_systemd_service
    start_service
    health_check
    show_deployment_info

    log_info "🎉 部署流程全部完成！"
}

# 执行主流程
main
