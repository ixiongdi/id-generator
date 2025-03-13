# 贡献指南

## 贡献流程
1. Fork仓库并创建特性分支（格式：`feature/描述性名称`）
2. 提交PR到develop分支
3. PR模板需包含：
   - 改动目的
   - 关联的Issue编号
   - 测试方法（包括单元测试/压力测试）
   - 代码影响范围说明

## 开发环境
- JDK 8+
- Maven 3.6.3+
- IntelliJ IDEA（推荐安装Checkstyle/SpotBugs插件）

```bash
git clone https://github.com/ixiongdi/id-generator.git
mvn clean install -DskipTests
```

## 代码规范
- 遵循Google Java Style（配置见.idea/checkstyle-idea.xml）
- 使用Checkstyle验证（已预置配置）
- SpotBugs检查0警告
- 方法复杂度不超过15

## 测试要求
- 核心模块测试覆盖率≥80%
- 压力测试报告需包含在PR描述中
- 使用JMH进行基准测试

## 问题追踪
- Bug报告需包含：
  - 环境信息（JDK/Maven版本）
  - 重现步骤
  - 期望与实际行为
  - 相关日志/堆栈

## 代码评审
- 至少需要2个核心维护者批准
- 合并前需通过CI流水线（包含）：
  - 代码风格检查
  - 单元测试
  - 模块级集成测试
  - 压力测试基准

## 文档要求
- 公共API必须包含JavaDoc
- 配置变更需更新对应README章节
- 重大设计改动需提交设计文档到docs/design目录