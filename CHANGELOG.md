# 变更日志

本项目遵循[Keep a Changelog](https://keepachangelog.com/zh-CN/1.0.0/)规范，版本号采用[Semantic Versioning](https://semver.org/lang/zh-CN/)。

## [未发布]
### 新增
- CI：新增 GitHub Actions（CI/CodeQL）与 Dependabot
- 开源配置：新增 Issue/PR 模板、CODEOWNERS、.editorconfig、.gitattributes
- Release：新增 Release Drafter 与手动发布工作流

### 改进
- 统一 POM 许可证为 MIT；`id-generator-core` 对齐编译插件版本
- 完善 README（徽章、模块矩阵、发布说明）与安全策略邮箱

### 修复
- 修正 Base16/Base32 实现与测试的边界行为
- 放宽 UUIDv7 单调性测试适配同毫秒场景

## [0.1.0] - 2024-05-20
### 新增
- 初始版本核心ID生成算法
- 基准测试框架集成

### 修复
- 分布式节点时钟同步问题
- 序列号溢出处理逻辑

### 改进
- 优化Snowflake算法位分配
- 增强ZooKeeper连接稳定性

## 版本历史
- [0.1.0]: https://github.com/ixiongdi/id-generator/releases/tag/v0.1.0

[保持更新日志]: https://keepachangelog.com/zh-CN/1.0.0/
[语义化版本]: https://semver.org/lang/zh-CN/