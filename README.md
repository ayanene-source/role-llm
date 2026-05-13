# RoleLLM

RoleLLM 是一个前后端分离的 LLM 对话系统第一版。

- 前端：Vue 3 + Element Plus
- 后端：Spring Boot + Java
- 模型平台：DeepSeek OpenAI-compatible Chat API

第一版只实现基础聊天能力：不登录、不保存历史、不使用数据库、不做流式输出。

## 文档

- [接口文档](docs/API.md)
- [后端流程说明](docs/BACKEND_FLOW.md)
- [OpenAPI 文件](docs/openapi.yaml)：可导入 Apifox

## 后端启动

API Key 只能放在后端环境变量或 `application.yml`，不要写进前端代码。

PowerShell 示例：

```powershell
$env:LLM_BASE_URL="https://api.deepseek.com"
$env:LLM_API_KEY="你的 DeepSeek API Key"
$env:LLM_MODEL="deepseek-v4-flash"
$env:LLM_TEMPERATURE="0.7"
mvn spring-boot:run
```

后端默认地址：

```text
http://localhost:8080
```

## 前端启动

```powershell
cd frontend
npm install
npm run dev
```

前端默认地址：

```text
http://localhost:5173
```

Vite 会把 `/api` 代理到 `http://localhost:8080`。

## 当前接口

```http
POST /api/chat
Content-Type: application/json

{ "message": "你好" }
```

响应：

```json
{
  "reply": "...",
  "model": "...",
  "usage": {}
}
```

详细字段、错误码和 Apifox 导入方式见 [接口文档](docs/API.md)。
