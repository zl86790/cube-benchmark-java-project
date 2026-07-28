# EC-Shop: 电子商务系统 Bug 测试平台

一个使用 Spring Boot 3.2.5 + Java 21 + Gradle 构建的电子商务网站，**专门用于测试 AI Agent（如 Codex、Claude、Cursor 等）的系统 Bug 发现和 Debug 能力**。

项目中预先植入了 **50 个编译错误 + 50 个业务逻辑 Bug**，均按 高 / 中 / 低 三档严重度分类，分布在全项目 200 个 Java 源文件（原始电商主流程的 57 个核心文件 + 18 个后追加的业务模块共 143 个文件）中，覆盖并发、安全（越权/IDOR、SQL 注入、缺失鉴权、敏感信息泄露）、数据一致性、精度丢失、资源泄漏、幂等性、事务边界等企业级应用的典型缺陷类型。

> 说明：本项目之前的版本包含一批更小规模的 15 编译错误 + 20 运行时 Bug（记录在旧版 README 中）。这些问题此前已被全部修复（编译错误被直接修正；20 个运行时 Bug 的代码位置和内容保持不变，被并入本次的 50 个 Bug 清单，编号为 Bug #1~#20）。本 README 已整体替换为当前代码库实际包含的 50+50 缺陷清单，与代码状态保持一致。

---

## 技术栈

| 技术 | 版本/说明 |
|------|----------|
| Java | 21 |
| Spring Boot | 3.2.5 |
| Spring Security | 6.x |
| Spring Data JPA | 3.x |
| H2 Database | 内存数据库 |
| Gradle | 8.x |
| Lombok | 最新 |

---

## 项目结构

```
ec-shop/
├── build.gradle
├── settings.gradle
├── README.md
└── src/main/
    ├── java/com/ecshop/
    │   ├── EcShopApplication.java
    │   ├── config/            配置类：SecurityConfig, WebConfig, CacheConfig
    │   ├── controller/        REST 控制器（电商主流程 8 个 + 18 个业务模块各 1~2 个）
    │   ├── service/           业务逻辑层（电商主流程 12 个 + 18 个业务模块各 1~2 个）
    │   ├── repository/        数据访问层（电商主流程 7 个 + 18 个业务模块各 1~3 个）
    │   ├── model/              JPA 实体（电商主流程 11 个 + 18 个业务模块各 1~3 个）
    │   ├── dto/                数据传输对象
    │   ├── exception/          异常类
    │   └── util/                工具类：PriceCalculator, ValidationUtils, DateUtils
    └── resources/application.yml
```

**电商主流程核心模块（57 个文件）**：商品 Product、分类 Category、用户 User、购物车 Cart、订单 Order、支付 Payment、库存 Inventory、折扣 Discount、地址 Address。

**后追加的 18 个业务模块（143 个文件）**：商品评价 Review、优惠券 Coupon、收藏夹 Wishlist、物流配送 Shipment、退货退款 Return/Refund、会员积分 Loyalty、站内通知 Notification、搜索 Search、报表统计 Report、仓库 Warehouse、供应商与采购单 Supplier/PurchaseOrder、秒杀促销 Promotion/FlashSale、品牌与标签 Brand/Tag、收货地址簿 AddressBook、礼品卡 GiftCard、商品问答 ProductQA、会员等级 Membership、审计日志 AuditLog。

**总计: 200 个 Java 源文件**

---

## 快速启动

```bash
cd ec-shop
gradle bootRun
```

> **注意**: 项目当前包含 50 个编译错误，`gradle compileJava` 会失败。由于 Lombok 注解处理器在遇到大量同时存在的解析失败（尤其是缺失 import 导致的字段类型无法解析）时会提前中止当前编译轮次，**单次 `gradle compileJava` 通常只会展示其中一部分错误**（约 15~20 个），修复这批之后重新编译才会看到下一批。这是刻意保留的特性，用于测试 Agent 能否在多轮编译反馈下持续推进，而不是"看到几个错误就以为改完了"。完整验证方法：反复执行「编译 → 修复当前报错 → 再编译」直到 `BUILD SUCCESSFUL`。

---

## Bug 清单总览（50 个编译错误 + 50 个运行时 Bug）

| 类别 | 高 (High) | 中 (Medium) | 低 (Low) | 合计 |
|------|-----------|-------------|----------|------|
| ⚙️ 编译错误 (CE) | 13 | 19 | 18 | **50** |
| 🐛 运行时 Bug | 17 | 16 | 17 | **50** |
| **合计** | **30** | **35** | **35** | **100** |

---

## ⚙️ 编译错误 (Compilation Errors) — 50 个

CE-1~CE-17 分布在电商主流程的核心文件中；CE-18~CE-50 分布在 18 个后追加业务模块中。类型覆盖：缺少 import、import/引用不存在的类、返回类型与返回值不匹配、调用不存在的方法（含拼写错误）、枚举常量被重命名、字段名/方法名漂移（实体与调用方不一致）、方法调用参数数量或类型错误。相当一部分 CE 是"定义端已修改、调用端未同步"的**跨文件链式错误**（表中标注"链"），需要 Agent 追踪调用链才能一次性定位根因，而不是只改报错的那一行。

### 总览表

| # | 严重度 | 文件 | 类型 | 链? |
|---|--------|------|------|-----|
| CE-1 | 🟢 Low | `config/WebConfig.java` | 缺少 import (`CorsRegistry`) | |
| CE-2 | 🟢 Low | `config/CacheConfig.java` | 返回类型与返回值不匹配 | |
| CE-3 | 🟡 Medium | `service/UserService.java` + `controller/UserController.java` | 参数数量不匹配 | 链 |
| CE-4 | 🟢 Low | `controller/CategoryController.java` | 方法名拼写错误 | |
| CE-5 | 🟢 Low | `controller/PaymentController.java` | 参数类型不匹配 (String→Long) | |
| CE-6 | 🟡 Medium | `service/UserService.java` | 返回类型与返回值不匹配 (Optional 未解包) | |
| CE-7 | 🟢 Low | `service/ShippingService.java` | 引用未声明的常量 | |
| CE-8 | 🟡 Medium | `service/AuditService.java` + `controller/AdminController.java` | 参数数量不匹配（3 处调用点） | 链 |
| CE-9 | 🔴 High | `repository/CategoryRepository.java` + `service/CategoryService.java` | 泛型 ID 类型不匹配 (Long→String) | 链 |
| CE-10 | 🔴 High | `repository/PaymentRepository.java` + `service/PaymentService.java` | 方法被重命名，调用方未同步 | 链 |
| CE-11 | 🔴 High | `model/Discount.java` + `service/DiscountService.java` | 枚举常量被重命名 | 链 |
| CE-12 | 🔴 High | `model/CartItem.java` + `service/CartService.java`/`OrderService.java` | 字段被重命名（6 处调用点） | 链 |
| CE-13 | 🟡 Medium | `dto/PaymentDTO.java` + `service/PaymentService.java` | 字段被重命名 | 链 |
| CE-14 | 🔴 High | `exception/InsufficientInventoryException.java` + `service/InventoryService.java` | 构造函数被删除 | 链 |
| CE-15 | 🟢 Low | `util/ValidationUtils.java` | 缺少 import (`Pattern`) | |
| CE-16 | 🟡 Medium | `util/DateUtils.java` | 调用不存在的方法 (`LocalDateTime.toInstant()`) | |
| CE-17 | 🟢 Low | `util/PriceCalculator.java` | 返回类型与返回值不匹配 | |
| CE-18 | 🟡 Medium | `service/ReviewService.java` | 参数数量不匹配（repository 方法） | |
| CE-19 | 🟢 Low | `controller/ReviewController.java` | 缺少 import (`List`) | |
| CE-20 | 🔴 High | `service/CouponService.java` + `controller/CouponController.java` | 返回类型与返回值不匹配 (Optional 未解包) | 链 |
| CE-21 | 🟡 Medium | `controller/CouponController.java` | 返回类型误用 (DTO 当实体用) | |
| CE-22 | 🟡 Medium | `service/WishlistService.java` | 调用不存在的方法（拼写/命名错误） | |
| CE-23 | 🔴 High | `service/ShipmentService.java` + `controller/ShipmentController.java` | 参数类型不匹配 (Long→String) | 链 |
| CE-24 | 🟢 Low | `model/TrackingEvent.java` | 字段被重命名 | |
| CE-25 | 🔴 High | `model/ReturnRequest.java` + `service/ReturnService.java` | 枚举常量被重命名（2 处调用点） | 链 |
| CE-26 | 🟡 Medium | `service/LoyaltyService.java` | 参数类型不匹配 (Long→int)，内部调用连锁报错 | |
| CE-27 | 🟢 Low | `model/PointsTransaction.java` | 缺少 import (`LocalDateTime`) | |
| CE-28 | 🟢 Low | `controller/NotificationController.java` | 方法名拼写错误 | |
| CE-29 | 🟡 Medium | `service/SearchService.java` | `Comparator.comparing()` 调用方式错误 | |
| CE-30 | 🟡 Medium | `service/ReportService.java` | 引用不存在的字段/方法 | |
| CE-31 | 🔴 High | `service/WarehouseService.java` + `controller/WarehouseController.java` | 参数数量不匹配 | 链 |
| CE-32 | 🟢 Low | `controller/WarehouseController.java` | 缺少 import (`List`) | |
| CE-33 | 🔴 High | `service/PurchaseOrderService.java` | 类型不匹配 (`Supplier` vs `List<Supplier>`) | |
| CE-34 | 🔴 High | `controller/PurchaseOrderController.java` + `service/PurchaseOrderService.java` | 参数数量不匹配 | 链 |
| CE-35 | 🟡 Medium | `model/PurchaseOrderItem.java` + `service/PurchaseOrderService.java` | 字段类型不匹配 (Integer→String) | 链 |
| CE-36 | 🟡 Medium | `service/PromotionService.java` + `controller/PromotionController.java` | 参数类型不匹配 (Long→String) | 链 |
| CE-37 | 🟢 Low | `model/FlashSaleItem.java` | 缺少 import (`BigDecimal`) | |
| CE-38 | 🟡 Medium | `service/TagService.java` | 返回类型与返回值不匹配 (Optional 未解包) | |
| CE-39 | 🟢 Low | `controller/BrandController.java` | 方法名拼写错误 | |
| CE-40 | 🟡 Medium | `service/UserAddressService.java` | 参数数量不匹配（repository 方法） | |
| CE-41 | 🔴 High | `service/GiftCardService.java` | 返回类型与返回值不匹配 (Optional 未解包) | |
| CE-42 | 🟡 Medium | `controller/GiftCardController.java` + `service/GiftCardService.java` | 参数数量不匹配 | 链 |
| CE-43 | 🟡 Medium | `service/ProductQAService.java` | 方法名拼写错误（repository 方法） | |
| CE-44 | 🟢 Low | `model/ProductAnswer.java` | 缺少 import (`LocalDateTime`) | |
| CE-45 | 🔴 High | `service/MembershipService.java` + `controller/MembershipController.java` | 返回类型不匹配 (`List` vs `Page`)，内部两处调用连锁报错 | 链 |
| CE-46 | 🟢 Low | `model/UserMembership.java` | 字段被重命名 | |
| CE-47 | 🟢 Low | `controller/AuditLogController.java` | 方法名拼写错误 | |
| CE-48 | 🟡 Medium | `model/PurchaseOrder.java` + `service/PurchaseOrderService.java` | 枚举常量被重命名 | 链 |
| CE-49 | 🟡 Medium | `model/CouponRedemption.java` + `service/CouponService.java` | 字段类型不匹配 (Long→String) | 链 |
| CE-50 | 🟢 Low | `model/GiftCardTransaction.java` | 字段被重命名（潜伏，暂无调用方直接触发） | |

### 关键编译错误详解（节选高严重度项）

#### CE-9 — CategoryRepository 泛型 ID 类型不匹配
```java
// repository/CategoryRepository.java
public interface CategoryRepository extends JpaRepository<Category, String> { // ❌ 应为 Long
```
`Category.id` 是 `Long`，但仓库声明的泛型 ID 是 `String`，导致 `CategoryService` 里所有 `categoryRepository.findById(Long id)` 的调用都报参数类型不匹配。**修复**：把泛型改回 `Long`。

#### CE-10 — PaymentRepository.findByOrderId 被重命名
```java
// repository/PaymentRepository.java
Optional<Payment> findByOrder_Id(Long orderId); // 原名 findByOrderId，PaymentService 仍在调用旧名
```
**修复**：要么把方法名改回 `findByOrderId`，要么同步修改 `PaymentService.getPaymentByOrderId()` 里的调用。

#### CE-11 — Discount.DiscountType 枚举常量被重命名
```java
// model/Discount.java
public enum DiscountType { PERCENT, FIXED_AMOUNT }  // 原为 PERCENTAGE
// service/DiscountService.java 第 75 行仍引用 Discount.DiscountType.PERCENTAGE
```
**修复**：统一两处的常量名。

#### CE-12 — CartItem.unitPrice 被重命名为 price
```java
// model/CartItem.java
private BigDecimal price;  // 原字段名 unitPrice
```
`CartService.addItem()`、`CartService.toDTO()`（3 处）、`OrderService.createOrder()`（2 处）都仍在调用 `getUnitPrice()`/`setUnitPrice()`，一次字段重命名连锁触发两个文件共 6 处编译错误。**修复**：统一改回 `unitPrice`，或同步更新所有调用点为 `getPrice()`/`setPrice()`。

#### CE-14 — InsufficientInventoryException 的 String 构造函数被删除
```java
// exception/InsufficientInventoryException.java
// 只剩 InsufficientInventoryException(Long productId, int available, int requested)
```
`InventoryService.deductStock()` 仍在调用 `new InsufficientInventoryException("Insufficient stock for product " + ...)`（单参数字符串版本）。**修复**：恢复单参数构造函数，或改造调用点传入 `productId`/`available`/`requested` 三个参数。

#### CE-20 / CE-41 — Optional 未解包（Coupon / GiftCard 各一处，同类模式）
```java
// service/CouponService.java
public Coupon getCoupon(String code) {
    return couponRepository.findByCode(code); // ❌ 返回 Optional<Coupon>，方法签名是 Coupon
}
```
GiftCardService.getGiftCard() 与 UserService.getUserByUsername()、TagService.getOrCreateTag() 也是同一类缺陷（CE-6、CE-38），全部是"方法签名写的是解包后的类型，方法体却直接返回了 `Optional<T>`"。**修复**：补上 `.orElseThrow(...)`。

#### CE-45 — MembershipService.getTiers() 返回类型不匹配
```java
// service/MembershipService.java
public Page<MembershipTier> getTiers() {          // ❌ 声明返回 Page
    return membershipTierRepository.findAllByOrderByMinSpendAsc(); // 该方法返回 List
}
```
`evaluateTier()` 内部把 `getTiers()` 的结果赋给 `List<MembershipTier>`，`MembershipController` 也按 `List` 处理返回值，因此这一处类型声明错误连锁触发 3 处编译失败。**修复**：把返回类型改回 `List<MembershipTier>`。

---

## 🔴 运行时 Bug 清单 — 50 个

Bug #1~#20 位于电商主流程核心文件中（并发、安全、精度、事务一致性等经典问题）；Bug #21~#50 分布在 18 个后追加业务模块中，新增了多处 **IDOR / 越权访问**（Notification、Review、UserAddress）、**竞态条件**（Coupon、Loyalty、Promotion、GiftCard，与 Bug #1 库存竞态同类）、**幂等性缺失**（Coupon 重复兑换）、**状态机校验缺失**（Shipment、PurchaseOrder 的非法状态跳转）等模式 —— 部分是同一类缺陷在不同模块的重复出现，用于测试 Agent 能否识别"这是同一种模式的第 N 次出现"而不是把每个都当成孤立问题。

### 🔴 高严重度（High）- 17 个

| # | 文件 | 方法 | 描述 |
|---|------|------|------|
| Bug #1 | `service/InventoryService.java` | `deductStock()` | 库存扣减竞态条件（check-then-act，无锁），并发下超卖 |
| Bug #2 | `service/ProductService.java` | `advancedSearch()` | 字符串拼接构建原生 SQL，SQL 注入漏洞 |
| Bug #3 | `util/PriceCalculator.java` | `calculateDiscount()`/`calculateTax()` | 用 `double`/`float` 做金额计算，精度丢失 |
| Bug #4 | `controller/AdminController.java` | 全部端点 | 缺少 `@PreAuthorize`，管理接口任何人可调用 |
| Bug #5 | `service/OrderService.java` | `calculateOrderTotals()` | 订单总额计算未过滤已取消商品行 |
| Bug #6 | `config/SecurityConfig.java` | `securityFilterChain()` | Session Fixation 防护被禁用 (`sessionFixation().none()`) |
| Bug #7 | `service/OrderService.java` + `PaymentService.java` | `createOrder()`/`processPayment()` | 订单与支付跨事务边界，数据可能不一致 |
| Bug #22 | `service/CouponService.java` | `redeem()` | usageCount 竞态条件，并发下可突破 usageLimit |
| Bug #23 | `service/CouponService.java` | `redeem()` | 未调用已有的 `countByCouponIdAndUserId`，单用户可无限次兑换同一优惠券 |
| Bug #27 | `service/ReturnService.java` | `processRefund()` | 退款金额无上限校验，可退任意金额 |
| Bug #28 | `service/LoyaltyService.java` | `redeemPoints()` | 积分余额竞态条件（同 Bug #1 模式），并发下余额可变负 |
| Bug #29 | `controller/NotificationController.java` | `markRead()` | IDOR：未校验通知归属者，任何人可标记他人通知已读 |
| Bug #34 | `service/PromotionService.java` | `purchaseFlashSaleItem()` | soldCount 竞态条件（同 Bug #1 模式），并发下秒杀商品可超卖 |
| Bug #40 | `controller/AuditLogController.java` | 全部端点 | 缺少鉴权，审计日志任何人可读（同 Bug #4 模式） |
| Bug #41 | `service/GiftCardService.java` | `redeem()` | 余额竞态条件（同 Bug #1/#28 模式），并发下余额可变负 |
| Bug #42 | `service/ReviewService.java` | `deleteReview()` | IDOR：未校验评价归属者，任何人可删除他人评价 |
| Bug #48 | `service/UserAddressService.java` | `deleteAddress()` | IDOR：未校验地址归属者，任何人可删除他人收货地址 |

### 🟡 中严重度（Medium）- 16 个

| # | 文件 | 方法 | 描述 |
|---|------|------|------|
| Bug #8 | `service/ProductService.java` | `getProducts()` | 分页 off-by-one，`page=1` 实际返回第二页 |
| Bug #9 | `service/ProductService.java` | `updateProduct()` | `@Cacheable` 缓存更新后未失效，读到脏数据 |
| Bug #10 | `service/OrderService.java` | `getOrder()` | `orElse(null)` 无空值检查，NPE 风险 |
| Bug #11 | `service/TaxService.java` | `calculateTax()` | 税额舍入用 `HALF_UP` 而非金融标准 `HALF_EVEN` |
| Bug #12 | `util/ValidationUtils.java` | `isValidEmail()` | 邮箱正则过于宽松，`a@b`、`user@domain` 都能通过 |
| Bug #13 | `service/DiscountService.java` | 类级别 `@Scope("prototype")` | prototype 被单例注入，作用域失效，计数器在请求间共享 |
| Bug #14 | `util/DateUtils.java` | `convertToDate()` 等 | 使用服务器本地时区而非 UTC，跨时区部署时间不一致 |
| Bug #21 | `service/ReviewService.java` | `markHelpful()` | helpfulCount 读-改-写无锁，并发点赞可能丢失计数 |
| Bug #25 | `service/ShipmentService.java` | `addTrackingEvent()` | 无终态检查，已 DELIVERED 的运单会被物流事件打回 IN_TRANSIT |
| Bug #26 | `service/ShipmentService.java` | `markDelivered()` | 无当前状态检查，可从 PREPARING 直接跳到 DELIVERED |
| Bug #30 | `service/SearchService.java` | `search()` | `categoryId` 与关键词同时传入时被 if/else-if 结构静默忽略 |
| Bug #31 | `service/ReportService.java` | `getSalesReport()` | 平均订单额分子分母统计口径不一致（分母含已取消订单，分子不含） |
| Bug #32 | `service/WarehouseService.java` | `setStock()` | 未校验库存数量非负，可被设置为负数 |
| Bug #33 | `service/PurchaseOrderService.java` | `submit()` | 无当前状态检查，已 RECEIVED/CANCELLED 的采购单可被打回 SUBMITTED |
| Bug #39 | `service/MembershipService.java` | `evaluateTier()` | 全项目无任何地方调用此方法，会员等级永远不会自动升级 |
| Bug #50 | `controller/CouponController.java` | `redeemCoupon()` | 无幂等性保护，重复提交会重复兑换优惠券 |

### 🟢 低严重度（Low）- 17 个

| # | 文件 | 方法 | 描述 |
|---|------|------|------|
| Bug #15 | `exception/ProductNotFoundException.java` | 构造函数 | 异常消息误写成 "User not found"（复制粘贴遗留） |
| Bug #16 | `controller/ProductController.java` | `createProduct()` | 未校验商品名称长度/非空 |
| Bug #17 | `service/AuditService.java` | `writeAuditLog()` | `FileWriter` 从不关闭，资源泄漏 |
| Bug #18 | `service/PaymentService.java` | `processPayment()` | INFO 级别日志记录完整信用卡号 |
| Bug #19 | `service/ShippingService.java` | `calculateShippingFee()` | 运费/免运费阈值硬编码，未读取 `application.yml` 配置 |
| Bug #20 | `service/ProductService.java` | `createProduct()` | 未校验 SKU 是否重复，直接触发数据库唯一约束异常 |
| Bug #24 | `service/WishlistService.java` | `addItem()` | 未校验商品是否已下架/停用 |
| Bug #35 | `service/TagService.java` | `tagProduct()` | 未校验重复标签，同一商品可被打上重复的 ProductTag |
| Bug #36 | `service/UserAddressService.java` | `addAddress()` | 用户的第一个地址若未显式指定 `isDefault=true`，不会自动成为默认地址 |
| Bug #37 | `service/GiftCardService.java` | `issueGiftCard()` | 未校验金额为正数，可发行零/负余额礼品卡 |
| Bug #38 | `service/ProductQAService.java` | `askQuestion()` | 未校验问题内容非空 |
| Bug #43 | `service/BrandService.java` | `createBrand()` | 未预检查品牌名重复，直接抛出底层 `DataIntegrityViolationException` |
| Bug #44 | `service/SearchService.java` | `search()` | `sortBy` 传入未知值时静默忽略，无默认排序兜底 |
| Bug #45 | `service/PurchaseOrderService.java` | `receive()` | 未校验采购单是否有商品行，空采购单也能被"收货" |
| Bug #46 | `service/PromotionService.java` | `addFlashSaleItem()` | 未校验秒杀价低于原价 |
| Bug #47 | `service/MembershipService.java` | `evaluateTier()` | 多个等级 `minSpend` 相同时，`reduce()` 的选取结果依赖迭代顺序，不确定 |
| Bug #49 | `service/SupplierService.java` | `createSupplier()` | 未校验联系邮箱格式 |

### 高严重度 Bug 详解（节选新增项）

#### Bug #22/#23 — CouponService.redeem() 双重缺陷
```java
// service/CouponService.java
coupon.setUsageCount(coupon.getUsageCount() + 1);  // Bug #22: 读-改-写无锁，并发可突破 usageLimit
couponRepository.save(coupon);
// Bug #23: CouponRedemptionRepository.countByCouponIdAndUserId() 已定义却从未被调用，
// 导致同一用户可以对同一张优惠券反复调用 redeem() 无限次
```

#### Bug #29/#42/#48 — 三处重复出现的 IDOR 模式
```java
// controller/NotificationController.java
public ApiResponse<Notification> markRead(@PathVariable Long id) {
    Notification notification = notificationRepository.findById(id)
            .orElseThrow(...);   // ❌ 没有检查 notification.getUser() 是否等于当前登录用户
    notification.setIsRead(true);
    ...
}
```
`ReviewService.deleteReview()`（Bug #42）与 `UserAddressService.deleteAddress()`（Bug #48）是完全相同的缺陷模式：只按主键查询并操作，从不校验资源归属者身份。三处出现在三个不同模块，是同一类"看似分散、实则同源"的安全缺陷，适合测试 Agent 能否识别跨文件的重复模式。

#### Bug #28/#34/#41 — 与 Bug #1 同构的竞态条件家族
`InventoryService.deductStock()`（Bug #1）、`LoyaltyService.redeemPoints()`（Bug #28）、`PromotionService.purchaseFlashSaleItem()`（Bug #34）、`GiftCardService.redeem()`（Bug #41）、`CouponService.redeem()`（Bug #22）全部是"先读余额/库存/次数 → 判断是否足够 → 再写回"的三段式操作，中间没有任何锁（悲观锁、乐观锁 `@Version`、数据库行级锁均未使用），在并发请求下都会出现超卖/透支。这是本项目中出现频率最高的单一缺陷模式（5 次独立实例）。

---

## 对 AI Agent 的评估维度

### 1. 发现能力
- Agent 能否发现全部 50 个编译错误 + 50 个运行时 Bug？
- 能否理解业务逻辑而非仅检查语法错误？
- 能否在跨文件（甚至跨模块）之间追踪调用链？
- 能否从编译器的单轮报错中意识到"修复后还会有更多"，主动进行多轮编译验证？
- 能否识别同一缺陷模式在不同模块中的重复出现（如 5 处竞态条件、3 处 IDOR）？

### 2. 分析能力
- 能否正确判断每个 Bug/CE 的严重度级别？
- 能否推理出触发条件和影响范围（尤其是并发类和安全类问题）？
- 对编译错误的类型分类是否准确（缺少 import / 类型不匹配 / 方法不存在 / 参数错误）？

### 3. 修复能力
- 修复方案是否正确且完整？
- 修复某个 Bug 时是否引入了新的 Bug？
- 是否考虑了修复对上下游代码的影响（尤其是链式编译错误）？
- 编译错误的修复是否一次性覆盖了所有连锁调用点？

### 4. 沟通能力
- 是否能用清晰的语言解释 Bug 原因和修复方案？
- 是否能按严重度优先级排序修复建议？
- 是否能区分编译错误和运行时 Bug 的不同评估标准？

---

## License

本项目仅用于教育和 AI Agent 测试目的。
