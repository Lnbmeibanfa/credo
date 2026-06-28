## Why

设计系统（mini-design-system）已完成，但小程序入口页仍是 Taro 默认 Hello World。SCREEN 00 欢迎页是 MVP 用户流的第一屏，承担品牌定位与「开始立约」转化，需要尽快用已有组件库还原设计稿。

## What Changes

- 将 `pages/index` 实现为 SCREEN 00 欢迎页（品牌 header、hero slogan、引用、四象限卡片、CTA、footer）
- 启用自定义导航栏（`navigationStyle: custom`）
- 文案集中至 `constants/welcomeContent.ts`
- CTA「开始立约」跳转至 `pages/contract-create`（placeholder 页面，仅标题与占位）
- 在 `app.config.ts` 注册 contract-create 路由

## Non-goals

- 不对接后端 API 或 `services/` 层
- 不实现微信登录
- 不实现回访用户跳转逻辑（有契约则跳 today 主页）
- 不实现完整创建睡眠契约表单（留给 contract-create change）
- 不做 UI snapshot 测试
- 不涉及后端 credo-server

## Capabilities

### New Capabilities

- `mini-welcome-screen`: 欢迎页 UI 展示与 CTA 导航至创建契约 placeholder

### Modified Capabilities

（无现有 archived spec）

## Impact

- **Affected**: `apps/mini/credo/src/pages/index/`、`apps/mini/credo/src/pages/contract-create/`（新建 placeholder）、`apps/mini/credo/src/constants/welcomeContent.ts`、`apps/mini/credo/src/app.config.ts`
- **Depends on**: mini-design-system 组件库（PageShell、PageHeader、FeatureListCard 等）
- **Not affected**: 后端、数据库、其他业务页面
