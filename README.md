# 麦穗 Wheat — 习惯养成记账 App

面向学生的 Android 记账应用。核心主题是「时间与坚持——默默耕耘的农场」：
**小麦的生长只取决于“坚持记账这个习惯”，与花钱多少无关。**
只要当天记过账，小麦就获得一天的阳光和水分，稳步成长。

设计原型见 `wheat_app.html`（HTML 高保真原型，含习惯卡、粮仓、统计三 Tab 的交互与文案）。

## 核心逻辑

- 连击（Streak）按去重后的记账天数计算，与金额无关（`util/HabitUtils.kt` 的 `computeHabit()`）。
- 阶段划分：
  - 种子 SEED：连击 0 天
  - 幼苗 SPROUT：连击 1–2 天
  - 成长 GROWING：连击 3–6 天
  - 抽穗 MATURE：连击 7–13 天
  - 丰收 HARVEST：连击 14 天以上
- 今天未记账时，连击按昨天结算，不断签鼓励。
- 金额只影响「秋收粮仓」的水位展示，不影响小麦生长。

## 功能

- **麦田首页**：习惯麦田卡（连击徽章、今日打卡状态、成长值进度条）、Canvas 小麦动效（麦粒数随阶段增加）、快捷记账入口、今日财务记录列表。
- **秋收粮仓**：垂直水位粮仓（随预算剩余升降）、每月总生活费 / 本月已消耗、日均消费参考、分类消耗去向统计、月度预算设置。
- **收支统计**：本月累计支出与节余比例、近 7 天支出曲线（手绘 Canvas，非 Chart.js）、习惯贴士。
- **学生中心**：连续打卡 / 累计打卡 / 本月记录笔数、耕耘信条、生活费设置（与粮仓同数据源）。
- **记账弹窗**：底部 `ModalBottomSheet`，金额 + 分类九宫格（餐饮美食 / 学习进修 / 交通出行 / 购物消费 / 休闲娱乐 / 其他）+ 备注；保存后回到首页并提示打卡成功。
- **导航**：`Scaffold` + 顶部 `TopAppBar`（标题、副标题、生活费设置入口）+ 底部 4 Tab（麦田首页 / 秋收粮仓 / 收支统计 / 学生中心）+ 中央上浮 FAB 记账入口。

## 技术栈

- Kotlin + Jetpack Compose Material 3（BOM 2024.12.01）
- Navigation Compose（多 Tab 单 Activity）
- Room（`wheat_database`：`expenses`、`user_settings`）
- Coroutines + Flow + StateFlow ViewModel
- Canvas 自定义绘制（小麦、粮仓水位、7 天曲线）
- AGP 9.3.2 / Kotlin 2.0.21 / compileSdk=targetSdk=35 / minSdk=26
- 包名：`com.iicemeta.wheat`

## 目录结构

```
app/src/main/java/com/iicemeta/wheat/
├── MainActivity.kt                 # Scaffold + TopBar + 底部导航 + 记账 BottomSheet
├── data/
│   ├── dao/                        # ExpenseDao、SettingsDao
│   ├── database/AppDatabase.kt
│   └── model/                      # Expense、UserSettings
├── ui/
│   ├── components/                 # WheatAnimation、AddRecordSheet、WheatTopBar、
│   │                               # WheatBottomNavBar、CategoryMeta
│   ├── navigation/                 # Screen、WheatNavGraph
│   ├── screens/
│   │   ├── home/HomeScreen.kt
│   │   ├── granary/GranaryScreen.kt
│   │   ├── stats/StatsScreen.kt
│   │   └── mine/MineScreen.kt
│   └── theme/                      # 麦色板、Theme、Type
├── util/HabitUtils.kt              # 连击与阶段计算（唯一生长规则）
└── viewmodel/                      # Home、Granary、Stats、Mine、AddRecord
app/src/test/java/com/iicemeta/wheat/HabitLogicTest.kt
wheat_app.html                       # HTML 设计原型
```

## 构建与运行

```bash
# Debug 构建
./gradlew :app:assembleDebug

# Release 构建（真机流畅度对比建议用 release 包，debug 包无优化会明显更卡）
./gradlew :app:assembleRelease

# 单元测试
./gradlew :app:testDebugUnitTest
```

`HabitLogicTest` 覆盖：空记录为种子期、仅今天记账进入幼苗期、连续 3 天进入成长期、断签只算尾部连续天数。

## CI/CD（GitHub Actions Alpha 发布）

- 工作流：`.github/workflows/alpha-release.yml`，手动触发（Actions → Alpha Release → Run workflow），无需打版本标签。
- 版本号规则：Alpha 自动为 `v0.<run_number>.0-alpha`（`versionCode = run_number`）；推送 `v*` 标签（如 `v1.0.0`）则发布同名正式 Release。
- 构建产物：`com.iicemeta.wheat.apk`，以 Pre-release 形式附在 GitHub Release 上。
- 签名：默认回退 debug 签名（可安装体验）；如需正式签名，在仓库 Secrets 配置 `KEYSTORE_BASE64`、`KEY_ALIAS`、`KEYSTORE_PASSWORD`、`KEY_PASSWORD` 后自动启用。
- 本地复刻 CI 传参（PowerShell 注意引号）：
  `./gradlew :app:assembleRelease "-PversionCode=999" "-PversionName=0.999.0-alpha"`。

## 设计规范（对齐 wheat_app.html）

- 底色奶油 `#FDFBF7` / 卡片底 `#F9F4E8`，品牌橙 `#E07A5F` 只用于主 CTA 与强调。
- 卡片圆角 18–24dp，Hairline 边框，无大阴影；底部导航用 1dp 顶部发丝线。
- FAB 上浮叠加在占位块上，阴影落在导航栏容器内，不被安全区裁剪。
- 各页面底部内容 padding 96dp，避免被导航栏 + FAB 遮挡。
- 文案只谈习惯打卡，不谈金额奖励。
