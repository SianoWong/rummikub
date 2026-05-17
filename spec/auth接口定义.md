# 登录注册接口定义

本文档描述前端对接登录注册模块所需的接口、请求格式、响应格式和登录态规则。

## 基础约定

接口前缀：

```text
/api/auth
```

除注册接口外，请求和响应均使用 JSON：

```http
Content-Type: application/json
```

统一响应格式：

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

成功时：

```text
code = 0
message = success
```

失败时：

```json
{
  "code": 10009,
  "message": "用户名或密码错误",
  "data": null
}
```

## 登录态规则

登录和注册成功后，后端会返回 `token`。

前端需要保存该 token，并在需要登录态的接口中通过 Header 传递：

```http
X-Auth-Token: {token}
```

当前不使用 JWT，token 是后端生成的随机字符串。后端会在 Redis 中保存登录态：

```text
login:token:{token}
```

默认有效期：

```text
604800 秒，即 7 天
```

## 用户对象

登录态中的用户对象格式：

```json
{
  "userId": 1,
  "username": "alice",
  "nickname": "Alice",
  "status": 1
}
```

字段说明：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `userId` | number | 用户 ID |
| `username` | string | 登录用户名 |
| `nickname` | string | 昵称 |
| `status` | number | 用户状态，`1` 表示正常 |

## 注册

```http
POST /api/auth/register
```

请求类型：

```http
Content-Type: multipart/form-data
```

表单字段：

```text
username=alice
password=123456
nickname=Alice
avatarImage=<file>
```

字段规则：

| 字段 | 必填 | 规则 |
| --- | --- | --- |
| `username` | 是 | 字母、数字、下划线，长度 3 到 20 |
| `password` | 是 | 长度 6 到 64 |
| `nickname` | 否 | 最长 20；为空时默认使用 `username` |
| `avatarImage` | 否 | 头像图片文件 |

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "2d47e0d23f9b4d0cb3c9f4f2b6e0a123",
    "expiresInSeconds": 604800,
    "user": {
      "userId": 1,
      "username": "alice",
      "nickname": "Alice",
      "status": 1
    }
  }
}
```

说明：

- 注册成功后等同于登录成功，前端可以直接保存 `data.token`。
- 如果 `nickname` 为空，后端会自动使用 `username` 作为昵称。
- 如果传入 `avatarImage`，后端会保存到 `src/main/resource/avatar/{userId}` 目录下。
- 同一用户再次保存头像时，会清理该用户头像目录中的旧文件。

## 登录

```http
POST /api/auth/login
```

请求体：

```json
{
  "username": "alice",
  "password": "123456"
}
```

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "token": "2d47e0d23f9b4d0cb3c9f4f2b6e0a123",
    "expiresInSeconds": 604800,
    "user": {
      "userId": 1,
      "username": "alice",
      "nickname": "Alice",
      "status": 1
    }
  }
}
```

说明：

- 登录成功后，前端保存 `data.token`。
- 后续需要登录态的接口统一带 `X-Auth-Token`。

## 获取当前用户

```http
GET /api/auth/me
```

请求 Header：

```http
X-Auth-Token: {token}
```

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "userId": 1,
    "username": "alice",
    "nickname": "Alice",
    "status": 1
  }
}
```

未登录或 token 失效：

```json
{
  "code": 10012,
  "message": "未登录或登录已过期",
  "data": null
}
```

## 登出

```http
POST /api/auth/logout
```

请求 Header：

```http
X-Auth-Token: {token}
```

成功响应：

```json
{
  "code": 0,
  "message": "success",
  "data": true
}
```

说明：

- 登出会删除 Redis 中的 token 登录态。
- 如果 token 为空或已经失效，当前接口仍返回成功，前端可以直接清理本地 token。

## 获取用户头像

```http
GET /api/auth/avatar/{userId}
```

路径参数：

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| `userId` | number | 用户 ID |

成功响应：

```text
直接返回头像文件内容
```

头像不存在时：

```text
HTTP 200，响应 body 为空
```

说明：

- 该接口需要登录态，必须传 `X-Auth-Token`。
- 后端会读取 `src/main/resource/avatar/{userId}` 目录下的头像文件。
- 前端可以直接把该地址作为图片地址使用。

## 错误码

| code | message |
| --- | --- |
| `10001` | 注册参数不能为空 |
| `10002` | 用户名不能为空 |
| `10003` | 密码不能为空 |
| `10004` | 用户名只能包含字母、数字和下划线，长度3到20位 |
| `10005` | 密码长度必须在6到64位之间 |
| `10006` | 昵称不能超过20个字符 |
| `10007` | 头像保存失败 |
| `10008` | 用户名已存在 |
| `10009` | 用户名或密码错误 |
| `10010` | 用户状态不可用 |
| `10011` | 登录态创建失败 |
| `10012` | 未登录或登录已过期 |
| `50000` | 服务器内部错误 |

## 前端建议

- 注册/登录成功后，将 `data.token` 保存到前端状态和本地存储。
- 调用登录态接口时统一附加 `X-Auth-Token`。
- 收到 `code = 10012` 时，清理本地 token 并跳转到登录页。
- 不要在前端解析 token，token 不包含业务信息。
