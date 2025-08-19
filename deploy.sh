#!/bin/bash

# 部署配置
APP_NAME="id-generator"
REMOTE_HOST="xifan.uno"
REMOTE_USER="root"
REMOTE_DIR="/opt/apps/${APP_NAME}"
JAR_NAME="id-generator-web.jar"
JAVA_OPTS="-Xms512m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError"

# 日志函数
log() {
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] $1"
}

# 错误处理函数
handle_error() {
    log "错误: $1"
    exit 1
}

# 健康检查函数
health_check() {
    local url=$1
    local max_retries=30
    local retry_interval=5

    for ((i=1; i<=$max_retries; i++)); do
        if curl -s "$url/actuator/health" | grep -q '"status":"UP"'; then
            log "服务健康检查通过"
            return 0
        fi
        log "等待服务启动... ($i/$max_retries)"
        sleep $retry_interval
    done

    handle_error "服务健康检查失败"
    return 1
}

# Maven构建函数
build_maven() {
    log "开始Maven构建"
    
    mvn clean package -DskipTests || handle_error "Maven构建失败"
    log "Maven构建完成"
}

# 创建服务文件
create_service_file() {
    cat > ${APP_NAME}.service << EOF
[Unit]
Description=${APP_NAME} service
After=network.target

[Service]
Type=simple
User=${REMOTE_USER}
WorkingDirectory=${REMOTE_DIR}
ExecStart=/usr/bin/java ${JAVA_OPTS} -jar ${REMOTE_DIR}/${JAR_NAME}
Restart=always

[Install]
WantedBy=multi-user.target
EOF
}

# 远程部署函数
deploy_remote() {
    log "开始远程部署到 ${REMOTE_HOST}"
    
    # 创建远程目录
    ssh ${REMOTE_USER}@${REMOTE_HOST} "mkdir -p ${REMOTE_DIR}/{logs,config}"
    
    # 复制JAR包和服务文件
    scp ./id-generator-web/target/${JAR_NAME} ${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/
    create_service_file
    scp ${APP_NAME}.service ${REMOTE_USER}@${REMOTE_HOST}:/etc/systemd/system/
    rm ${APP_NAME}.service
    
    # 重启服务
    ssh ${REMOTE_USER}@${REMOTE_HOST} "systemctl daemon-reload && \
                                      systemctl restart ${APP_NAME} && \
                                      systemctl enable ${APP_NAME}"
    
    # 健康检查
    health_check "http://${REMOTE_HOST}:8080"
}

# 主函数
main() {
    log "开始部署 ${APP_NAME}"
    
    build_maven
    deploy_remote
    
    log "部署完成!"
}

# 执行主函数
main