# 贡献指南

## 贡献流程
1. Fork 仓库并创建特性分支（格式：`feat/描述性名称`、`fix/问题编号-简述`）
2. 提交 PR 到 `master` 分支（采用 GitHub Flow）；保持单一职责、小步提交
3. PR 模板需包含：
   - 改动目的
   - 关联的Issue编号
   - 测试方法（包括单元测试/压力测试）
   - 代码影响范围说明

4. Commit 信息建议遵循 Conventional Commits，例如：
   - `feat(core): 支持 UUIDv7 单调时间戳`
   - `fix(base32): 修复连字符解码`

## 开发环境
- JDK 8+（用于 `id-generator-core`）
- JDK 25+（用于 Spring/Vert.x/Solon 相关模块）
- Maven 3.8+
- IntelliJ IDEA（推荐安装 Checkstyle/SpotBugs 插件）

```bash
git clone https://github.com/ixiongdi/id-generator.git
mvn clean install -DskipTests
```

## 代码规范
- 遵循 Google Java Style（如安装插件请按 IDE 配置）
- 保持 `public` API 有清晰 Javadoc
- 方法圈复杂度建议不超过 15
- 禁止引入未使用的依赖/导入

## 测试要求
- 核心模块测试覆盖率≥80%
- 必须通过 CI（单测/构建/CodeQL）
- 基准测试（JMH）建议附在性能相关 PR 的描述中

## 问题追踪
- Bug报告需包含：
  - 环境信息（JDK/Maven版本）
  - 重现步骤
  - 期望与实际行为
  - 相关日志/堆栈

## 代码评审
- 至少 1 名维护者批准（核心变更建议 2 名）
- 合并前需通过 CI 流水线

## 文档要求
- 公共 API 必须包含 Javadoc
- 配置/行为变更需更新对应 README/文档
- 重大设计需提交设计文档到 `docs/design/`

## 发布流程
- 采用语义化版本（SemVer）：`MAJOR.MINOR.PATCH`
- 每次合并 `master` 后，Release Drafter 会自动汇总变更
- 准备发布时：
  1. 维护者在 Releases 页选择 Draft Release，校对分类与说明
  2. 指定新版本号并发布 Release
  3. 中央仓发布按父 `pom.xml` 的插件配置执行（如需签名/凭据请在本地/CI Secret 配置）