# 请求流程与数据流转说明

本文档用于说明当前项目中 JWT 用户注册、登录以及鉴权访问接口的完整请求流程。

## 1. 整体分层

当前项目按 MVC 思路进行整理，主要分为以下几层：

- Controller 层：负责接收 HTTP 请求、校验入参、返回响应
- Service 层：负责核心业务编排
- Repository 层：负责和数据库交互
- Entity/DTO 层：负责数据承载
- Security 层：负责 Spring Security 与 JWT 认证鉴权
- Exception 层：负责统一异常处理

对应目录如下：

- `controller`：接口入口
- `service`：业务逻辑
- `repository`：数据库访问
- `entity`：数据库实体
- `dto/request`：请求参数对象
- `dto/response`：响应参数对象
- `security`：JWT 与认证流程
- `config`：安全配置
- `exception`：统一异常处理

## 2. 注册流程

接口：

- `POST /api/auth/register`

请求进入系统后的流转过程如下：

### 2.1 前端发起请求

前端提交用户名、邮箱、密码，请求体会被映射到 `RegisterRequest`。

对应类：

- `src/main/java/com/demo/demo/dto/request/RegisterRequest.java`

### 2.2 Controller 接收请求

请求先进入 `AuthController` 的 `register` 方法。

对应类：

- `src/main/java/com/demo/demo/controller/AuthController.java`

这里主要做两件事：

- 通过 `@RequestBody` 接收 JSON 请求体
- 通过 `@Valid` 触发参数校验

如果参数不合法，例如用户名为空、密码长度不足，会直接抛出参数校验异常，不会继续进入业务层。

### 2.3 全局异常处理参数错误

如果 `RegisterRequest` 校验失败，异常会被 `GlobalExceptionHandler` 捕获。

对应类：

- `src/main/java/com/demo/demo/exception/GlobalExceptionHandler.java`

它会把异常统一包装成标准 JSON 响应，避免前端收到杂乱的报错格式。

### 2.4 Service 处理注册业务

参数通过后，`AuthController` 会调用 `AuthService.register()`。

对应类：

- `src/main/java/com/demo/demo/service/AuthService.java`

这个方法会完成以下业务逻辑：

1. 检查用户名是否已存在
2. 检查邮箱是否已存在
3. 创建 `User` 实体对象
4. 使用 `PasswordEncoder` 对密码进行加密
5. 调用 `UserRepository` 保存到数据库

### 2.5 Repository 操作数据库

`AuthService` 内部通过 `UserRepository` 查询和保存用户。

对应类：

- `src/main/java/com/demo/demo/repository/UserRepository.java`

Repository 层主要负责：

- `existsByUsername`：判断用户名是否存在
- `existsByEmail`：判断邮箱是否存在
- `save`：保存用户数据

### 2.6 Entity 自动维护时间字段

用户保存时，对应实体是 `User`。

对应类：

- `src/main/java/com/demo/demo/entity/User.java`

实体中通过：

- `@PrePersist` 自动初始化 `createdAt`
- `@PrePersist` 自动初始化 `updatedAt`
- `@PreUpdate` 自动更新 `updatedAt`

这意味着插入和更新时，时间字段会自动维护。

### 2.7 生成 JWT

用户保存成功后，`AuthService` 会调用 `JwtTokenProvider` 生成 token。

对应类：

- `src/main/java/com/demo/demo/security/JwtTokenProvider.java`

JWT 中当前主要保存：

- 用户名
- 签发时间
- 过期时间

### 2.8 返回响应

生成 JWT 后，Service 会把结果封装成 `AuthResponse`，Controller 再包装成 `ApiResponse` 返回给前端。

对应类：

- `src/main/java/com/demo/demo/dto/response/AuthResponse.java`
- `src/main/java/com/demo/demo/dto/response/ApiResponse.java`

最终注册流程可以概括为：

`RegisterRequest -> AuthController -> AuthService -> UserRepository -> User -> JwtTokenProvider -> AuthResponse -> ApiResponse`

## 3. 登录流程

接口：

- `POST /api/auth/login`

登录流程与注册类似，但核心区别是：登录不是创建用户，而是先做身份认证，再签发 JWT。

### 3.1 接收登录请求

前端提交用户名和密码，请求体被映射为 `LoginRequest`。

对应类：

- `src/main/java/com/demo/demo/dto/request/LoginRequest.java`

### 3.2 Controller 转交 Service

请求进入 `AuthController.login()` 后，会调用 `AuthService.login()`。

对应类：

- `src/main/java/com/demo/demo/controller/AuthController.java`
- `src/main/java/com/demo/demo/service/AuthService.java`

### 3.3 Spring Security 发起认证

在 `AuthService.login()` 中，系统会调用 `AuthenticationManager.authenticate(...)`。

这一步的作用是：

- 根据用户名查用户
- 校验密码是否正确
- 如果正确，则生成认证结果

### 3.4 加载数据库用户

认证过程中，Spring Security 会调用 `CustomUserDetailsService.loadUserByUsername()`。

对应类：

- `src/main/java/com/demo/demo/security/CustomUserDetailsService.java`

它会通过 `UserRepository` 从数据库读取用户数据，并转成 Spring Security 认识的 `UserDetails` 对象。

### 3.5 认证成功后签发 JWT

认证通过后，`AuthService.login()` 会调用 `JwtTokenProvider.generateToken()` 生成 JWT，然后组装返回结果。

最终登录流程可以概括为：

`LoginRequest -> AuthController -> AuthService -> AuthenticationManager -> CustomUserDetailsService -> UserRepository -> JwtTokenProvider -> AuthResponse`

## 4. JWT 鉴权访问流程

接口示例：

- `GET /api/users/me`

这个接口不是公开接口，必须携带 JWT 才能访问。

### 4.1 请求先进入安全过滤链

所有请求都会先经过 `SecurityConfig` 中配置的 Spring Security 过滤链。

对应类：

- `src/main/java/com/demo/demo/config/SecurityConfig.java`

这里定义了：

- `/api/auth/**` 可以匿名访问
- 其他接口默认需要认证
- 系统采用无状态会话 `STATELESS`
- 在用户名密码过滤器前插入 JWT 过滤器

### 4.2 JWT 过滤器解析 Token

请求到来后，`JwtAuthenticationFilter` 会优先执行。

对应类：

- `src/main/java/com/demo/demo/security/JwtAuthenticationFilter.java`

它会执行这些动作：

1. 从请求头 `Authorization` 中读取 `Bearer token`
2. 调用 `JwtTokenProvider.validateToken()` 校验 token
3. 从 token 中解析用户名
4. 调用 `CustomUserDetailsService` 查询数据库用户
5. 构造认证对象 `UsernamePasswordAuthenticationToken`
6. 把认证信息放入 `SecurityContextHolder`

### 4.3 Controller 获取当前登录用户对应资源

JWT 校验通过后，请求才会进入 `UserController.getCurrentUser()`。

对应类：

- `src/main/java/com/demo/demo/controller/UserController.java`

然后调用 `UserService.getCurrentUserProfile()`。

对应类：

- `src/main/java/com/demo/demo/service/UserService.java`

### 4.4 从安全上下文中获取当前用户

`UserService` 会从 `SecurityContextHolder` 里取出当前认证信息，再根据用户名去数据库查用户详情。

查到用户后，系统会把实体转换为 `UserProfileResponse` 返回给前端。

对应类：

- `src/main/java/com/demo/demo/dto/response/UserProfileResponse.java`

最终受保护接口流程可以概括为：

`HTTP请求 + Authorization Bearer Token -> SecurityFilterChain -> JwtAuthenticationFilter -> JwtTokenProvider -> SecurityContextHolder -> UserController -> UserService -> UserRepository -> UserProfileResponse -> ApiResponse`

## 5. 异常流转

当前项目中的异常流转是统一处理的。

对应类：

- `src/main/java/com/demo/demo/exception/GlobalExceptionHandler.java`

主要有三类：

### 5.1 业务异常

例如：

- 用户名已存在
- 邮箱已被注册
- 用户不存在

这些异常由 `BusinessException` 表示。

对应类：

- `src/main/java/com/demo/demo/exception/BusinessException.java`

### 5.2 参数校验异常

例如：

- 用户名为空
- 邮箱格式不正确
- 密码长度不符合要求

这些由 `@Valid` 和 DTO 上的校验注解触发。

### 5.3 未知系统异常

如果代码运行中出现未预料异常，会由全局异常处理器兜底返回统一错误格式。

## 6. 一句话总结

可以把整套代码理解为下面三条主链路：

1. 注册：`RegisterRequest -> AuthController -> AuthService -> UserRepository -> User -> JwtTokenProvider -> Response`
2. 登录：`LoginRequest -> AuthController -> AuthService -> AuthenticationManager -> CustomUserDetailsService -> UserRepository -> JwtTokenProvider -> Response`
3. 鉴权访问：`Request + JWT -> JwtAuthenticationFilter -> SecurityContextHolder -> UserController -> UserService -> UserRepository -> Response`

## 7. 后续可扩展方向

后面如果要继续升级，这套结构也比较容易扩展：

- 增加角色与权限表
- 增加刷新 token
- 增加退出登录的黑名单机制
- 增加用户信息修改接口
- 增加统一错误码与业务状态码
- 切换 H2 到 MySQL
