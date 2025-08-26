# 部署配置
$APP_NAME = "id-generator"
$REMOTE_HOST = "xifan.uno"
$REMOTE_USER = "root"
$REMOTE_DIR = "/opt/apps/$APP_NAME"
$JAVA_OPTS = "-Xms512m -Xmx512m -XX:+HeapDumpOnOutOfMemoryError"
$JAR_NAME = ""

# 日志函数
function Write-Log {
    param([string]$Message)
    Write-Host "[$(Get-Date -Format 'yyyy-MM-dd HH:mm:ss')] $Message"
}

# 错误处理函数
function Handle-Error {
    param([string]$Message)
    Write-Log "错误: $Message"
    exit 1
}

# 健康检查函数
function Test-Health {
    param([string]$Url)
    $maxRetries = 30
    $retryInterval = 5

    for ($i = 1; $i -le $maxRetries; $i++) {
        try {
            $response = Invoke-RestMethod -Uri "$Url/actuator/health" -Method Get
            if ($response.status -eq "UP") {
                Write-Log "服务健康检查通过"
                return $true
            }
        } catch {
            Write-Log "等待服务启动... ($i/$maxRetries)"
        }
        Start-Sleep -Seconds $retryInterval
    }

    Handle-Error "服务健康检查失败"
    return $false
}

# Maven构建函数
function Build-Maven {
    Write-Log "开始Maven构建"

    try {
        mvn clean package -DskipTests
        if ($LASTEXITCODE -ne 0) { throw "Maven构建失败" }

        # 获取实际的JAR包名称
        $jarPattern = "./id-generator-web/target/id-generator-web-*.jar"
        $jarFile = Get-ChildItem -Path $jarPattern | Sort-Object LastWriteTime -Descending | Select-Object -First 1
        if (-not $jarFile) {
            throw "找不到构建后的JAR包"
        }

        $script:JAR_NAME = $jarFile.Name
        Write-Log "Maven构建完成，JAR包名称: $JAR_NAME"
    } catch {
        Handle-Error $_.Exception.Message
    }
}

# 创建服务文件
function New-ServiceFile {
    $serviceContent = @"
[Unit]
Description=$APP_NAME service
After=network.target

[Service]
Type=simple
User=$REMOTE_USER
WorkingDirectory=$REMOTE_DIR
ExecStart=/usr/bin/java $JAVA_OPTS -jar $REMOTE_DIR/$JAR_NAME
Restart=always

[Install]
WantedBy=multi-user.target
"@

    $serviceContent | Set-Content -Path "$APP_NAME.service" -Encoding UTF8
}

# 远程部署函数
function Deploy-Remote {
    Write-Log "开始远程部署到 $REMOTE_HOST"

    # 创建远程目录
    ssh "${REMOTE_USER}@${REMOTE_HOST}" "mkdir -p ${REMOTE_DIR}/{logs,config}"

    # 复制JAR包和服务文件
    scp "./id-generator-web/target/$JAR_NAME" "${REMOTE_USER}@${REMOTE_HOST}:${REMOTE_DIR}/"
    New-ServiceFile
    scp "$APP_NAME.service" "${REMOTE_USER}@${REMOTE_HOST}:/etc/systemd/system/"
    Remove-Item "$APP_NAME.service"

    # 重启服务
    ssh "${REMOTE_USER}@${REMOTE_HOST}" "systemctl daemon-reload && systemctl restart $APP_NAME && systemctl enable $APP_NAME"

    # 健康检查
    Test-Health "http://${REMOTE_HOST}:8080"
}

# 主函数
function Main {
    Write-Log "开始部署 $APP_NAME"

    Build-Maven
    Deploy-Remote

    Write-Log "部署完成!"
}

# 执行主函数
try {
    Main
} catch {
    Handle-Error $_.Exception.Message
}
