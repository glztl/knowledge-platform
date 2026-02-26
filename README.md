# 📚 Knowledge Platform

> 全栈知识管理平台 - 前后端一体化

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
[![Vue.js](https://img.shields.io/badge/Vue-3.0-green.svg)](https://vuejs.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.0-blue.svg)](https://spring.io/projects/spring-boot)

## 🚀 技术栈

### 后端（backend/）
- **框架**: Spring Boot 3.0
- **语言**: Java 17
- **数据库**: MySQL 8.0
- **安全**: Spring Security + JWT
- **存储**: MinIO（对象存储）
- **构建**: Maven

### 前端（frontend/）
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **语言**: TypeScript
- **UI 组件库**: Element Plus
- **状态管理**: Pinia
- **路由**: Vue Router 4
- **HTTP 客户端**: Axios

## 📦 功能模块

- ✅ 用户认证（JWT）
- ✅ 文档管理（CRUD）
- ✅ 分类管理（树形结构）
- ✅ 标签管理
- ✅ 文件上传（MinIO）
- ✅ 文档分享（密码/有效期）
- ✅ 版本历史（回滚）

## 🛠️ 快速开始

### 环境要求

- JDK 17+
- Node.js 18+
- MySQL 8.0+
- Docker（可选，用于 MinIO）

### 后端启动

```bash
cd backend
mvn spring-boot:run
