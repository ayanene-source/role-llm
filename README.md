# RoleLLM

RoleLLM 是一个前后端分离的 LLM 对话系统第一版。

- 前端：Vue 3 + Element Plus
- 后端：Spring Boot + Java
- 模型平台：DeepSeek OpenAI-compatible Chat API
- GPT-SoVITS服务

已经实现对话、基础上下文记忆保存以及语音生成功能。

## 文档

- [接口文档](docs/API.md)
- [后端流程说明](docs/BACKEND_FLOW.md)
- [OpenAPI 文件](docs/openapi.yaml)：可导入 Apifox

##  GPT-SoVITS启动
```powershell：
cd E:\GPT-SoVITS-v2pro-20250604\GPT-SoVITS-v2pro-20250604
runtime\python.exe -I api_v2.py -a 127.0.0.1 -p 9880 -c GPT_SoVITS/configs/tts_infer.yaml
```

## 后端启动

API Key 只能放在后端环境变量或 `application.yml`，不要写进前端代码。

PowerShell 示例：

```powershell
$env:LLM_API_KEY="你的 DeepSeek API Key"
mvn spring-boot:run "-Dspring-boot.run.profiles=local"
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
}
```

详细字段、错误码和 Apifox 导入方式见 [接口文档](docs/API.md)。
