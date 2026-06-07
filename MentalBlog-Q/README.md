# MentalBlog-Q（前端）

Vue 3 + TypeScript + Vite 的博客前端与管理后台（SPA）。

完整启动与数据库初始化请先看项目根目录 [README.md](../README.md)。

## 开发启动

```bash
npm install
npm run dev
```

- 默认地址：`http://localhost:5173`
- 后端代理：
  - `/api` → `http://localhost:8080`
  - `/uploads` → `http://localhost:8080`

代理配置见 [vite.config.ts](./vite.config.ts)。

## 构建

```bash
npm run build
```
