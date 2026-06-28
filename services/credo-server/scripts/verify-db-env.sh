#!/usr/bin/env bash
# 打印 dev 环境数据库相关环境变量（密码脱敏）
# 用法: bash scripts/verify-db-env.sh

set -euo pipefail

mask_secret() {
  local value="${1:-}"
  if [[ -z "$value" ]]; then
    echo "(未设置)"
    return
  fi
  local len=${#value}
  if (( len <= 2 )); then
    echo "** (${len} chars)"
    return
  fi
  echo "${value:0:1}***${value: -1} (${len} chars)"
}

check_var() {
  local name="$1"
  local value="${!name:-}"
  if [[ -z "$value" ]]; then
    echo "  ✗ $name = (未设置)"
    return 1
  fi
  echo "  ✓ $name = 已设置"
  return 0
}

echo "=== Credo dev 数据库环境变量 ==="
echo

missing=0

# 主配置（application-dev.yml）
echo "[application-dev.yml]"
if ! check_var DB_URL; then missing=1; fi
echo "      DB_URL = ${DB_URL:-(未设置)}"
if ! check_var DB_USERNAME; then missing=1; fi
echo "      DB_USERNAME = ${DB_USERNAME:-(未设置)}"
if ! check_var DB_PASSWORD; then missing=1; fi
echo "      DB_PASSWORD = $(mask_secret "${DB_PASSWORD:-}")"
echo

# 从 JDBC URL 解析 host / port / database（便于核对阿里云地址）
if [[ -n "${DB_URL:-}" ]]; then
  echo "[JDBC URL 解析]"
  if [[ "$DB_URL" =~ jdbc:mysql://([^:/]+)(:([0-9]+))?/([^?]+) ]]; then
    echo "      host     = ${BASH_REMATCH[1]}"
    echo "      port     = ${BASH_REMATCH[3]:-3306}"
    echo "      database = ${BASH_REMATCH[4]}"
  else
    echo "      ⚠ 无法解析 DB_URL，请确认格式类似:"
    echo "        jdbc:mysql://rm-xxx.mysql.rds.aliyuncs.com:3306/credo?..."
  fi
  echo
fi

# 可选：其他相关变量
echo "[其他可选变量]"
echo "      JWT_SECRET           = $(mask_secret "${JWT_SECRET:-}")"
echo "      WECHAT_MINI_APP_ID   = ${WECHAT_MINI_APP_ID:-(未设置)}"
echo "      WECHAT_MINI_APP_SECRET = $(mask_secret "${WECHAT_MINI_APP_SECRET:-}")"
echo

if (( missing )); then
  echo "结果: ✗ 缺少必需的数据库环境变量 (DB_URL / DB_USERNAME / DB_PASSWORD)"
  echo
  echo "Git Bash 设置示例:"
  echo '  export DB_URL="jdbc:mysql://rm-xxx.mysql.rds.aliyuncs.com:3306/credo?useUnicode=true&characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false"'
  echo '  export DB_USERNAME="你的用户名"'
  echo '  export DB_PASSWORD="你的密码"'
  exit 1
fi

echo "结果: ✓ 三个必需变量均已设置"
echo
echo "提示: 若启动仍报 Communications link failure，请检查:"
echo "  1. 阿里云 RDS 白名单是否包含你当前公网 IP"
echo "  2. RDS 是否允许外网访问（若从本地连）"
echo "  3. DB_URL 中的 host/port/database 是否正确"
echo "  4. 当前终端是否就是启动 mvn 的那个终端（环境变量是否同源）"
