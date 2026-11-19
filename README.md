# 冰箱管家 Fridge Manager

Android 原生课程设计，选题 **B6 菜谱 / 冰箱管理**。仓库包含一个主 App 和一个可独立启动的数据看板伴生模块。

## 功能

### 主 App：`app`

- 食材库存新增、编辑、删除。
- 按名称、分类、数量和过期日期记录食材。
- 首页汇总食材种类、7 天内临期数量和已经过期数量。
- 通过 WorkManager 每天扫描库存，使用系统通知提醒临期或过期食材。
- Android 8.0 及以上支持通知渠道；Android 13 及以上动态申请通知权限。
- 根据现有库存推荐菜谱，按库存匹配度排序，并优先使用临期食材。
- 菜谱列表可进入详情页查看所需食材和制作步骤。
- 库存页具备加载态、空态、错误态和重试入口。

### 伴生模块：`dashboard`（P2 数据看板）

- 拥有独立应用图标，可独立安装和启动。
- 通过受签名权限保护的 `ContentProvider` 读取主 App 的 Room 数据。
- 使用自定义 `View` 绘制食材新鲜度环形图。
- 支持将库存导出为 UTF-8 CSV。
- 支持从看板清理主 App 中已经过期的食材。
- 主 App 未安装或数据接口不可用时提供错误提示、重试和打开主 App 入口。

## 技术栈

- Kotlin + Android XML 布局
- Room + KSP
- ViewModel + StateFlow + Kotlin Coroutines
- RecyclerView + ViewBinding
- WorkManager + NotificationCompat
- ContentProvider 跨 App 数据共享
- Canvas 自定义图表
- Storage Access Framework（CSV 导出）

项目没有后端和第三方业务 SDK，菜谱推荐完全在本地执行。

## 模块结构

```text
FridgeManager/
├─ app/                    主 App
│  ├─ database/            Room 实体、DAO、数据库
│  ├─ notification/        WorkManager 扫描、通知与定时调度
│  ├─ provider/            给伴生模块使用的数据接口
│  ├─ recipe/              本地菜谱和推荐算法
│  ├─ ui/                  RecyclerView Adapter
│  ├─ util/                日期与临期计算
│  └─ viewmodel/           库存 UI 状态
├─ dashboard/              P2 数据看板伴生模块
└─ docs/                   设计与演示文档
```

## 构建与运行

环境：Android Studio、JDK 17+、Android SDK 35。

```powershell
.\gradlew.bat :app:assembleDebug
.\gradlew.bat :dashboard:assembleDebug
```

运行顺序：

1. 安装并打开 `app`，添加几项不同过期日期的食材。
2. 在首页点击“立即检查”，允许通知权限后可验证系统提醒。
3. 点击“按现有食材推荐菜谱”，选择推荐项进入详情页。
4. 安装 `dashboard`，从桌面独立打开，或从主 App 点击“打开食材数据看板”。
5. 在看板中刷新、导出 CSV、清理过期库存。

Debug 构建会自动让两个模块使用同一调试签名，因此签名级数据权限可直接工作。发布版必须使用同一正式签名。

## 测试

```powershell
.\gradlew.bat :app:testDebugUnitTest :dashboard:testDebugUnitTest
```

测试覆盖：

- 过期日期解析与临期判断。
- 菜谱食材别名匹配和临期优先标记。
- 看板统计日期计算。
- CSV 表头、引号、逗号与公式注入转义。

## 关键实现说明

### 菜谱推荐

每道菜包含必需食材和可选食材。系统将库存名称与食材别名匹配，例如“西红柿”和“番茄”、“马铃薯”和“土豆”。

```text
得分 = 必需食材覆盖率
     + 可选食材匹配数 × 0.03
     + 使用临期食材奖励 0.12
```

最终按“主要食材是否齐全、得分、匹配数量”排序。

### 每日扫描

WorkManager 使用唯一周期任务，每天最多发送一次摘要通知。通知 ID 固定，新的扫描结果会更新已有通知。用户在 Android 13 及以上拒绝通知权限时，库存与推荐功能仍能正常使用。

### 跨 App 数据接口

主 App 暴露：

```text
content://com.fridge.fridgeapp.provider/food
```

读写分别由签名级权限 `READ_FOOD_DATA` 和 `WRITE_FOOD_DATA` 保护。伴生模块不直接打开主 App 的数据库文件，因此数据所有权和权限边界清晰。

## 已知限制

- WorkManager 是系统调度，不保证每天在完全相同的秒级时刻执行；课堂演示可使用“立即检查”。
- 菜谱库是课程演示用的本地内置集合，可继续在 `RecipeRepository.kt` 中扩展。
- 没有云同步，所有库存数据保存在设备本地。
