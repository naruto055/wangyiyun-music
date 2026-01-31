#!/bin/bash

###############################################################################
# 网易云音乐项目 - 一体化部署脚本（标准方案）
#
# 说明：
#   - 所有文件（应用、数据、工具）在一个目录下
#   - 零配置，开箱即用（依赖 ${user.dir} 自动解析路径）
#   - 易备份、易迁移（整体打包即可）
#   - 与 deploy-simple.sh 功能一致，可互换使用
#
# 适用场景：
#   - 生产环境标准部署
#   - 中小规模应用（数据量 < 100GB）
#   - 需要稳定可靠的部署方案
#
# 部署目录：/opt/service/wangyiyun-music
#
# 作者：naruto
# 创建时间：2026-01-31
# 最后更新：2026-01-31
###############################################################################

set -e  # 遇到错误立即退出

# ==================== 配置区 ====================

# 应用配置
APP_NAME="wangyiyun-music"
APP_VERSION="0.0.1-SNAPSHOT"
APP_JAR="${APP_NAME}-${APP_VERSION}.jar"

# 部署目录配置（与 deploy-simple.sh 保持一致）
DEPLOY_DIR="/opt/service/${APP_NAME}"
LOGS_DIR="${DEPLOY_DIR}/logs"
TOOLS_DIR="${DEPLOY_DIR}/tools"

# 数据存储目录配置（一体化部署，所有文件在应用目录下）
DATA_ROOT="${DEPLOY_DIR}/music-data"
TEMP_DIR="${DATA_ROOT}/temp"
AUDIO_DIR="${DATA_ROOT}/audio"

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
    log_info "Java 版本检查通过: ${JAVA_VERSION}"

    # 检查 JAR 文件是否存在
    if [[ ! -f "target/${APP_JAR}" ]]; then
        log_error "未找到 JAR 文件: target/${APP_JAR}"
        log_info "请先执行: mvn clean package"
        exit 1
    fi
    log_info "JAR 文件检查通过"
}

# ==================== 创建应用用户 ====================

function create_app_user() {
    log_step "创建应用用户 ${APP_USER}..."

    if id "${APP_USER}" &>/dev/null; then
        log_warn "用户 ${APP_USER} 已存在，跳过创建"
    else
        useradd -r -s /bin/false ${APP_USER}
        log_info "用户 ${APP_USER} 创建成功"
    fi
}

# ==================== 创建目录结构 ====================

function create_directories() {
    log_step "创建目录结构..."

    # 应用部署目录
    mkdir -p ${DEPLOY_DIR}
    mkdir -p ${LOGS_DIR}
    mkdir -p ${TOOLS_DIR}

    # 数据存储目录
    mkdir -p ${TEMP_DIR}
    mkdir -p ${AUDIO_DIR}

    log_info "目录结构创建完成"
}

# ==================== 安装 yt-dlp ====================

function install_ytdlp() {
    log_step "安装 yt-dlp 工具到应用目录..."

    YTDLP_PATH="${DEPLOY_DIR}/tools/yt-dlp"

    if [[ -f ${YTDLP_PATH} ]]; then
        log_warn "yt-dlp 已存在，跳过安装"
    else
        wget https://github.com/yt-dlp/yt-dlp/releases/latest/download/yt-dlp -O ${YTDLP_PATH}
        chmod +x ${YTDLP_PATH}
        log_info "yt-dlp 安装成功: ${YTDLP_PATH}"
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
    log_info "应用文件复制完成"
}

# ==================== 配置文件权限 ====================

function set_permissions() {
    log_step "设置文件权限..."

    # 设置应用目录所有权
    chown -R ${APP_USER}:${APP_USER} ${DEPLOY_DIR}
    chown -R ${APP_USER}:${APP_USER} ${DATA_ROOT}

    # 设置目录权限
    chmod 750 ${DEPLOY_DIR}
    chmod 770 ${DATA_ROOT}
    chmod 770 ${TEMP_DIR}
    chmod 770 ${AUDIO_DIR}

    log_info "权限设置完成"
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

# JVM 参数配置（路径使用 \${user.dir} 自动解析，无需环境变量）
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

    log_info "systemd 服务文件创建成功: ${SERVICE_FILE}"
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
    log_info "已设置开机自启"
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

    # 检查 Swagger UI（可选）
    if command -v curl &> /dev/null; then
        HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${SERVER_PORT}/swagger-ui/index.html || echo "000")
        if [[ ${HTTP_CODE} == "200" ]]; then
            log_info "✅ Swagger UI 访问正常"
        else
            log_warn "⚠️ Swagger UI 访问异常，HTTP Code: ${HTTP_CODE}"
        fi
    fi
}

# ==================== 显示部署信息 ====================

function show_deployment_info() {
    echo ""
    echo "========================================"
    echo "🎉 部署完成！"
    echo "========================================"
    echo "应用名称: ${APP_NAME}"
    echo "版本: ${APP_VERSION}"
    echo "部署目录: ${DEPLOY_DIR}"
    echo "数据目录: ${DATA_ROOT}"
    echo "服务端口: ${SERVER_PORT}"
    echo "运行用户: ${APP_USER}"
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
    echo "========================================"
}

# ==================== 主流程 ====================

function main() {
    log_info "开始部署 ${APP_NAME}..."

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

    log_info "部署流程全部完成！"
}

# 执行主流程
main
