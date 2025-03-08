# iDeepSeek

基于 DeepSeek API 开发的跨平台聊天应用，支持基础聊天和深度思考模式。

## 项目简介

iDeepSeek 是一个使用现代技术栈开发的跨平台聊天应用，通过接入 DeepSeek API 实现智能对话功能。项目采用 Kotlin Multiplatform (KMP) 开发，实现了 Android 和 iOS 平台的代码共享。

## 主要功能

- 基础聊天：支持与 AI 进行自然对话
- 深度思考：支持更深入的推理和分析
- 会话管理：支持创建、重命名、删除会话
- 历史记录：保存并管理历史对话内容
- Token 管理：支持 API Token 的配置和管理

## 技术特点

### 跨平台开发
- 使用 Kotlin Multiplatform (KMP) 实现核心业务逻辑
- 实现 Android 和 iOS 平台代码共享，提高开发效率
- UI层使用Compose编写

### 现代化架构
- 采用 MVVM 架构模式
- 使用 Kotlin 协程处理异步操作
- 遵循单向数据流设计

### 核心技术栈
- **UI 框架**：Jetpack Compose
- **网络请求**：Ktor Client
- **数据持久化**：SQLDelight
- **异步处理**：Kotlin Coroutines + Flow
- **JSON 处理**：Kotlinx Serialization

### 数据存储
- 使用 SQLDelight 实现跨平台数据库
- 支持会话和消息的本地存储
- 实现数据实时同步

## 项目结构

```
iDeepSeek/
├── composeApp/            # 主要应用模块
│   ├── android/          # Android 平台特定代码
│   └── ios/             # iOS 平台特定代码
├── shared/               # 共享模块
│   ├── api/             # API 相关代码
│   ├── db/              # 数据库相关代码
│   └── model/           # 数据模型
```

## 开发环境要求

- Android Studio Arctic Fox 或更高版本
- Kotlin 1.9.0 或更高版本
- Gradle 8.0 或更高版本
- Xcode 14.0 或更高版本（用于 iOS 开发）