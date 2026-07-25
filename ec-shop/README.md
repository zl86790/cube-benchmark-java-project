# EC-Shop: 电子商务系统 Bug 测试平台

一个使用 Spring Boot 3.2.5 + Java 21 + Gradle 构建的电子商务网站，**专门用于测试 AI Agent（如 Codex、Claude、Cursor 等）的系统 Bug 发现和 Debug 能力**。

项目中**预先植入了 20 个运行时 Bug + 15 个编译错误**，涵盖高/中/低三个严重级别（Bug）以及多种常见编译错误类型，涉及并发、安全、数据一致性、精度丢失、资源泄漏等典型企业级应用的常见缺陷。

> 说明：15 个编译错误中，CE-1 ~ CE-5 是有意植入的独立、单点错误；CE-6 ~ CE-15 是在核对源码时额外发现但此前未被记录的错误，全部集中在 `service/OrderService.java` 与 `controller/OrderController.java`、`repository/OrderRepository.java`、`service/DiscountService.java`、`service/PaymentService.java`、`model/Order.java`、`model/OrderItem.java` 之间的接口不一致（方法签名、参数个数、字段名、枚举值均对不上）。这些错误已通过实际执行 `gradle compileJava` 并在隔离副本中逐条修复至 `BUILD SUCCESSFUL` 验证无遗漏。

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
└── src/
    └── main/
        ├── java/com/ecshop/
        │   ├── EcShopApplication.java          (1)  应用入口
        │   ├── config/                          (3)  配置类
        │   │   ├── SecurityConfig.java
        │   │   ├── WebConfig.java
        │   │   └── CacheConfig.java
        │   ├── controller/                      (8)  REST 控制器
        │   │   ├── ProductController.java
        │   │   ├── CartController.java
        │   │   ├── OrderController.java
        │   │   ├── UserController.java
        │   │   ├── PaymentController.java
        │   │   ├── InventoryController.java
        │   │   ├── CategoryController.java
        │   │   └── AdminController.java
        │   ├── service/                         (12) 业务逻辑层
        │   │   ├── ProductService.java
        │   │   ├── CartService.java
        │   │   ├── OrderService.java
        │   │   ├── UserService.java
        │   │   ├── PaymentService.java
        │   │   ├── InventoryService.java
        │   │   ├── CategoryService.java
        │   │   ├── TaxService.java
        │   │   ├── DiscountService.java
        │   │   ├── ShippingService.java
        │   │   ├── NotificationService.java
        │   │   └── AuditService.java
        │   ├── repository/                      (7)  数据访问层
        │   │   ├── ProductRepository.java
        │   │   ├── CartRepository.java
        │   │   ├── OrderRepository.java
        │   │   ├── UserRepository.java
        │   │   ├── PaymentRepository.java
        │   │   ├── InventoryRepository.java
        │   │   └── CategoryRepository.java
        │   ├── model/                           (11) JPA 实体
        │   │   ├── Product.java
        │   │   ├── Category.java
        │   │   ├── User.java
        │   │   ├── Cart.java
        │   │   ├── CartItem.java
        │   │   ├── Order.java
        │   │   ├── OrderItem.java
        │   │   ├── Payment.java
        │   │   ├── Address.java
        │   │   ├── Discount.java
        │   │   └── Inventory.java
        │   ├── dto/                             (8)  数据传输对象
        │   │   ├── ProductDTO.java
        │   │   ├── CartDTO.java
        │   │   ├── CartItemDTO.java
        │   │   ├── OrderDTO.java
        │   │   ├── OrderItemDTO.java
        │   │   ├── UserDTO.java
        │   │   ├── PaymentDTO.java
        │   │   └── ApiResponse.java
        │   ├── exception/                       (4)  异常类
        │   │   ├── GlobalExceptionHandler.java
        │   │   ├── BusinessException.java
        │   │   ├── ProductNotFoundException.java
        │   │   └── InsufficientInventoryException.java
        │   └── util/                            (3)  工具类
        │       ├── PriceCalculator.java
        │       ├── ValidationUtils.java
        │       └── DateUtils.java
        └── resources/
            └── application.yml
```

**总计: 57 个 Java 源文件**

---

## 快速启动

```bash
cd ec-shop
gradle bootRun
```

应用启动后访问: `http://localhost:8080`

> **注意**: 项目当前包含 15 个编译错误（详见下方），`gradle compileJava` 会失败。如需正常运行项目，需要先修复这些编译错误。修复 CE-1~CE-5 后 `gradle compileJava` **仍然不会通过**，因为 OrderService.java 相关的 CE-6~CE-15 尚未修复。

---

## Bug 清单（共 20 个运行时 Bug + 15 个编译错误）

---

## ⚙️ 编译错误 (Compilation Errors) — 15 个

以下 15 个编译错误共同导致项目当前**无法通过 `gradle compileJava`**。CE-1~CE-5 用于测试 AI Agent 从编译器错误信息反向定位源码问题的能力，覆盖了 Java 开发中最常见的编译错误类型；CE-6~CE-15 集中暴露 `OrderService.java` 与其调用方/被调用方之间的接口漂移（签名、参数个数、字段名、枚举值不一致），用于测试 Agent 能否在**多文件间追踪调用链**、一次性识别同一根源导致的连锁编译失败，而不是逐个 symbol 修复。

### 编译错误总览

| # | 严重度 | 文件 | 错误类型 | 说明 |
|---|--------|------|----------|------|
| CE-1 | ❌ 编译 | `service/OrderService.java` | 缺少 import | 删除了 `import com.ecshop.service.CartService;`，字段 `cartService` 无法解析 |
| CE-2 | ❌ 编译 | `service/InventoryService.java` | 返回类型不匹配 | `getInventory()` 签名返回 `String`，方法体返回 `Inventory` 对象 |
| CE-3 | ❌ 编译 | `service/NotificationService.java` | 方法不存在 | 四处调用 `getEmailAddress()`，User 的 Lombok getter 是 `getEmail()` |
| CE-4 | ❌ 编译 | `service/CartService.java` | 缺少 import | 删除了 `import java.math.BigDecimal;`，`toDTO()` 中 6 处引用全部报错 |
| CE-5 | ❌ 编译 | `controller/ProductController.java` | 参数数量错误 | `getProducts(page)` 只传 1 个参数，方法签名需要 2 个 `(page, size)` |
| CE-6 | ❌ 编译 | `service/OrderService.java` | import 了不存在的类 | `import com.ecshop.model.OrderStatus;`，实际是嵌套类 `Order.OrderStatus`，不存在顶层 `OrderStatus` 类 |
| CE-7 | ❌ 编译 | `service/OrderService.java` | import 了不存在的类 | `import com.ecshop.model.PaymentStatus;`，实际是嵌套类 `Payment.PaymentStatus`，且该 import 全文未被使用 |
| CE-8 | ❌ 编译 | `service/OrderService.java` | 枚举常量不存在 | `order.setStatus(OrderStatus.NEW)`，但 `Order.OrderStatus` 枚举只有 `PENDING/CONFIRMED/SHIPPED/DELIVERED/CANCELLED/REFUNDED`，没有 `NEW` |
| CE-9 | ❌ 编译 | `service/OrderService.java` + `model/OrderItem.java` | 方法不存在 | `orderItem.setProductName(...)`（第 63 行）与 `item.getProductName()`（第 167 行），但 `OrderItem` 实体根本没有 `productName` 字段 |
| CE-10 | ❌ 编译 | `service/OrderService.java` + `service/PaymentService.java` | 参数数量错误 | `paymentService.processPayment(savedOrder)` 只传 1 个参数，但方法签名是 `processPayment(Order order, Payment.PaymentMethod method, String creditCardNumber)`，需要 3 个 |
| CE-11 | ❌ 编译 | `service/OrderService.java` + `repository/OrderRepository.java` | 参数数量错误 | `orderRepository.findByUserId(userId)` 只传 1 个参数，但仓库方法签名是 `findByUserId(Long userId, Pageable pageable)`，需要 2 个 |
| CE-12 | ❌ 编译 | `service/OrderService.java` + `service/DiscountService.java` | 方法不存在 | `discountService.calculateDiscount(order)`，但 `DiscountService` 没有这个公开方法（只有 `applyDiscount(String, BigDecimal)` 和私有的 `calculateDiscount(Discount, BigDecimal)`） |
| CE-13 | ❌ 编译 | `service/OrderService.java` + `model/Order.java` / `dto/OrderDTO.java` | 方法不存在 | `order.setShippingAmount(...)`（第 145 行）与 `order.getShippingAmount()`（第 158 行），但 `Order` 实体和 `OrderDTO` 的字段都叫 `shippingFee`，没有 `shippingAmount` |
| CE-14 | ❌ 编译 | `controller/OrderController.java` + `service/OrderService.java` | 参数数量错误 | `orderService.createOrder(userId, addressId, discountCode, notes)` 传 4 个参数，但 `OrderService.createOrder()` 只接受 2 个 `(userId, addressId)` |
| CE-15 | ❌ 编译 | `controller/OrderController.java` + `service/OrderService.java` | 方法不存在 | `orderService.getUserOrders(userId, page, size)`，但 `OrderService` 中根本不存在名为 `getUserOrders` 的方法 |

### 编译错误覆盖类型

| 类型 | 数量 | 对应错误 |
|------|------|----------|
| 缺少 import | 2 | CE-1, CE-4 |
| import/引用了不存在的类 | 2 | CE-6, CE-7 |
| 返回类型与返回值不匹配 | 1 | CE-2 |
| 调用不存在的方法 | 4 | CE-3, CE-9, CE-12, CE-15 |
| 枚举常量不存在 | 1 | CE-8 |
| 字段名/方法名不匹配（实体与调用方漂移） | 1 | CE-13 |
| 方法调用参数数量错误 | 4 | CE-5, CE-10, CE-11, CE-14 |

---

### CE-1 — OrderService.java：缺少 CartService import

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java` |
| **行号** | 第 32 行（字段声明） |
| **类型** | 缺少 import |

**问题描述**:   
文件顶部的 import 区域删除了 `import com.ecshop.service.CartService;` 这一行，但字段声明：

```java
private final CartService cartService;  // ❌ cannot find symbol: class CartService
```

仍然存在。编译器会报 `cannot find symbol: class CartService`。

**触发编译**: `gradle compileJava` — 直接报错。

**修复**: 在 import 区域添加：
```java
import com.ecshop.service.CartService;
```

---

### CE-2 — InventoryService.java：返回类型声明错误

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/InventoryService.java` |
| **方法** | `getInventory()` ＋ `updateInventory()`（连锁） |
| **类型** | 返回类型不匹配 |

**问题描述**:   
`getInventory()` 方法签名声明返回 `String`，但方法体实际返回 `Inventory` 对象：

```java
public String getInventory(Long productId) {   // ❌ 声明返回 String
    return inventoryRepository.findByProductId(productId)  // 实际返回 Inventory
            .orElseThrow(...);
}
```

同时 `updateInventory()` 内部调用 `getInventory()` 后将返回值赋给 `Inventory` 类型的变量，并调用其 setter 方法，也会连锁报错：

```java
public Inventory updateInventory(...) {
    Inventory inventory = getInventory(productId);  // ❌ String 不能赋给 Inventory
    inventory.setAvailableQuantity(quantity);         // ❌ String 没有 setAvailableQuantity()
    ...
}
```

**修复**: 将 `getInventory()` 的返回类型改为 `Inventory`：
```java
public Inventory getInventory(Long productId) { ... }
```

---

### CE-3 — NotificationService.java：调用不存在的方法

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/NotificationService.java` |
| **行号** | 第 15、20、25、30 行（四处调用） |
| **类型** | 方法不存在 |

**问题描述**:   
全部四个方法中调用了 `order.getUser().getEmailAddress()`，但 `User` 类的 email 字段由 Lombok `@Data` 注解自动生成的是 `getEmail()`，`getEmailAddress()` 是拼写错误，该方法不存在：

```java
public void sendOrderConfirmation(Order order) {
    log.info("Sending order confirmation email to {} for order {}",
            order.getUser().getEmailAddress(),  // ❌ cannot find symbol: method getEmailAddress()
            order.getOrderNumber());
}
```

四个方法全部受影响：`sendOrderConfirmation`、`sendOrderShippedNotification`、`sendOrderCancelledNotification`、`sendPaymentFailedNotification`（均为单参数 `(Order order)`，方法内部通过 `order.getUser()` 取得 `User`）。

**修复**: 将所有 `getEmailAddress()` 替换为 `getEmail()`。

---

### CE-4 — CartService.java：缺少 BigDecimal import

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/CartService.java` |
| **方法** | `toDTO()` |
| **类型** | 缺少 import |

**问题描述**:   
文件顶部的 import 区域删除了 `import java.math.BigDecimal;`，但 `toDTO()` 方法中大量使用 `BigDecimal`，导致 6 处 `cannot find symbol` 错误：

```java
// 第 90-92 行
BigDecimal total = cart.getItems().stream()           // ❌ cannot find symbol
        .map(item -> item.getUnitPrice().multiply(BigDecimal.valueOf(...))) // ❌
        .reduce(BigDecimal.ZERO, BigDecimal::add);     // ❌ ❌
// 第 102 行
itemDTO.setSubtotal(item.getUnitPrice().multiply(BigDecimal.valueOf(...))); // ❌
```

**修复**: 在 import 区域添加：
```java
import java.math.BigDecimal;
```

---

### CE-5 — ProductController.java：方法调用参数数量错误

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `controller/ProductController.java` |
| **行号** | 第 31 行 |
| **类型** | 参数数量不匹配 |

**问题描述**:   
`getProducts()` 端点中调用 `productService.getProducts(page)` 只传了 1 个参数，但 `ProductService.getProducts(int page, int size)` 需要 2 个参数：

```java
@GetMapping
public ApiResponse<List<ProductDTO>> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size) {
    // ❌ method getProducts(int,int) not applicable for argument (int)
    Page<Product> productPage = productService.getProducts(page);
    ...
}
```

第二个参数 `size` 明明已经在方法参数中声明了，但在调用时被遗漏。

**修复**: 传入第二个参数：
```java
Page<Product> productPage = productService.getProducts(page, size);
```

---

### CE-6 — OrderService.java：import 了不存在的顶层类 OrderStatus

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java` |
| **行号** | 第 9 行 |
| **类型** | import 了不存在的类 |

**问题描述**:   
```java
import com.ecshop.model.OrderStatus;  // ❌ cannot find symbol: class OrderStatus
```
`OrderStatus` 并不是 `com.ecshop.model` 包下的顶层类，而是定义在 `Order` 实体内部的嵌套枚举 `Order.OrderStatus`（见 `model/Order.java` 第 40-42 行）。文件中后续 `OrderStatus.CANCELLED`、`OrderStatus.SHIPPED` 等引用都依赖这个错误的 import。

**修复**: 
```java
import com.ecshop.model.Order.OrderStatus;
```

---

### CE-7 — OrderService.java：import 了不存在的顶层类 PaymentStatus

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java` |
| **行号** | 第 10 行 |
| **类型** | import 了不存在的类 |

**问题描述**:   
```java
import com.ecshop.model.PaymentStatus;  // ❌ cannot find symbol: class PaymentStatus
```
与 CE-6 同类问题：`PaymentStatus` 实际是 `Payment` 实体内部的嵌套枚举 `Payment.PaymentStatus`（见 `model/Payment.java` 第 57 行）。更进一步，这个 import 在 `OrderService.java` 全文中**从未被使用**——修复方式不是改成 `import com.ecshop.model.Payment.PaymentStatus;`，而应该直接**删除**这行无用 import。

**修复**: 删除该行 import。

---

### CE-8 — OrderService.java：使用了不存在的枚举常量 OrderStatus.NEW

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java` |
| **方法** | `createOrder()` |
| **行号** | 第 55 行 |
| **类型** | 枚举常量不存在 |

**问题描述**:   
```java
order.setStatus(OrderStatus.NEW);  // ❌ cannot find symbol: variable NEW
```
`Order.OrderStatus` 枚举定义为：
```java
public enum OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED, REFUNDED
}
```
即使 CE-6 的 import 修复后，`NEW` 也不是该枚举的合法值。即修好 import 之后编译仍会在这一行报错。

**修复**: 根据业务语义改为 `OrderStatus.PENDING`（新建订单的初始状态）。

---

### CE-9 — OrderService.java + OrderItem.java：OrderItem 没有 productName 字段

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java`（调用方）、`model/OrderItem.java`（缺失字段） |
| **行号** | `OrderService.java` 第 63 行、第 167 行 |
| **类型** | 方法不存在 |

**问题描述**:   
```java
orderItem.setProductName(cartItem.getProduct().getName());  // ❌ cannot find symbol: method setProductName
...
itemDTO.setProductName(item.getProductName());              // ❌ cannot find symbol: method getProductName
```
`OrderItem` 实体（`model/OrderItem.java`）只有 `id`、`order`、`product`、`quantity`、`unitPrice`、`subtotal`、`status` 字段，没有 `productName`。`OrderItemDTO.setProductName()` 本身是存在的（DTO 里有这个字段），问题出在 `OrderItem` **实体**缺少对应字段。

**修复**: 要么在 `OrderItem` 实体中新增 `productName` 字段并生成对应 getter/setter，要么改为在 DTO 转换时从 `item.getProduct().getName()` 取值（并删除第 63 行对不存在字段的赋值）。

---

### CE-10 — OrderService.java + PaymentService.java：processPayment() 参数数量不匹配

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java`（调用方）、`service/PaymentService.java`（方法定义） |
| **行号** | `OrderService.java` 第 82 行 |
| **类型** | 方法调用参数数量错误 |

**问题描述**:   
```java
paymentService.processPayment(savedOrder);
// ❌ method processPayment(Order,Payment.PaymentMethod,String) cannot be applied to given types
```
`PaymentService.processPayment()` 的实际签名是 `processPayment(Order order, Payment.PaymentMethod method, String creditCardNumber)`，需要 3 个参数，调用处只传了 1 个。

**修复**: 补齐支付方式和卡号参数，例如：
```java
paymentService.processPayment(savedOrder, Payment.PaymentMethod.CREDIT_CARD, creditCardNumber);
```
（`creditCardNumber` 需要作为 `createOrder()` 的入参传入，当前方法签名里也没有这个参数，属于同一处需要一并设计的接口缺口。）

---

### CE-11 — OrderService.java + OrderRepository.java：findByUserId() 参数数量不匹配

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java`（调用方）、`repository/OrderRepository.java`（方法定义） |
| **方法** | `getOrdersByUserId()` |
| **行号** | `OrderService.java` 第 97 行 |
| **类型** | 方法调用参数数量错误 |

**问题描述**:   
```java
public List<Order> getOrdersByUserId(Long userId) {
    return orderRepository.findByUserId(userId);
    // ❌ method findByUserId(Long,Pageable) cannot be applied to given types
}
```
`OrderRepository` 中定义的是 `Page<Order> findByUserId(Long userId, Pageable pageable)`，需要分页参数，而这里只传了 `userId`，且返回类型也对不上（方法声明返回 `List<Order>`，仓库方法返回 `Page<Order>`）。

**修复**: 要么给 `getOrdersByUserId()` 增加分页参数并返回 `Page<Order>`，要么在 `OrderRepository` 中新增一个不分页的 `List<Order> findByUserId(Long userId)` 方法。

---

### CE-12 — OrderService.java + DiscountService.java：calculateDiscount(Order) 方法不存在

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java`（调用方）、`service/DiscountService.java`（方法定义） |
| **方法** | `calculateOrderTotals()` |
| **行号** | `OrderService.java` 第 137 行 |
| **类型** | 方法不存在 |

**问题描述**:   
```java
BigDecimal discount = discountService.calculateDiscount(order);
// ❌ cannot find symbol: method calculateDiscount(Order)
```
`DiscountService` 里唯一的公开折扣入口是 `applyDiscount(String code, BigDecimal orderAmount)`；另有一个私有方法 `calculateDiscount(Discount discount, BigDecimal orderAmount)`，签名不同且不可见。调用方传入的是整个 `Order` 对象，两者都对不上。

**修复**: 改为调用 `discountService.applyDiscount(order.getDiscountCode(), subtotal)`（前提是 `Order` 需要有折扣码字段，当前也没有），或者在 `DiscountService` 中新增一个接受 `Order` 的公开重载方法。

---

### CE-13 — OrderService.java + Order.java / OrderDTO.java：shippingAmount 字段名不匹配

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `service/OrderService.java`（调用方）、`model/Order.java` + `dto/OrderDTO.java`（字段定义） |
| **行号** | `OrderService.java` 第 145 行、第 158 行 |
| **类型** | 方法不存在（字段名漂移） |

**问题描述**:   
```java
order.setShippingAmount(shipping);            // 第145行 ❌ cannot find symbol
...
dto.setShippingAmount(order.getShippingAmount());  // 第158行 ❌ 两处都找不到符号
```
`Order` 实体和 `OrderDTO` 中的字段都叫 `shippingFee`（对应 `getShippingFee()`/`setShippingFee()`），全项目没有任何地方定义过 `shippingAmount`。

**修复**: 统一改用 `getShippingFee()` / `setShippingFee()`。

---

### CE-14 — OrderController.java + OrderService.java：createOrder() 参数数量不匹配

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `controller/OrderController.java`（调用方）、`service/OrderService.java`（方法定义） |
| **行号** | `OrderController.java` 第 24 行 |
| **类型** | 方法调用参数数量错误 |

**问题描述**:   
```java
Order order = orderService.createOrder(userId, addressId, discountCode, notes);
// ❌ method createOrder(Long,Long) cannot be applied to given types
```
控制器按 4 个参数 `(userId, addressId, discountCode, notes)` 调用，但 `OrderService.createOrder()` 只接受 2 个参数 `(Long userId, Long addressId)`，完全不处理折扣码和备注。

**修复**: 给 `OrderService.createOrder()` 增加 `discountCode`、`notes` 两个参数，并在方法体内接入折扣逻辑（同时也是修复 CE-12 的关联点）。

---

### CE-15 — OrderController.java + OrderService.java：getUserOrders() 方法不存在

| 属性 | 值 |
|------|-----|
| **严重度** | ❌ 编译错误 |
| **文件** | `controller/OrderController.java`（调用方）、`service/OrderService.java`（缺失方法） |
| **行号** | `OrderController.java` 第 39 行 |
| **类型** | 方法不存在 |

**问题描述**:   
```java
Page<Order> orders = orderService.getUserOrders(userId, page, size);
// ❌ cannot find symbol: method getUserOrders(Long,int,int)
```
`OrderService` 中只有不分页的 `getOrdersByUserId(Long userId)`（本身也因 CE-11 无法编译），没有任何名为 `getUserOrders` 的分页方法。

**修复**: 在 `OrderService` 中新增：
```java
public Page<Order> getUserOrders(Long userId, int page, int size) {
    return orderRepository.findByUserId(userId, PageRequest.of(page, size));
}
```

---

## 🔴 运行时 Bug 清单

---

### 🔴 高严重度（High）- 7 个

这些 Bug 可直接导致系统崩溃、数据丢失、安全漏洞或资金损失。

---

#### Bug #1 - 库存扣减竞态条件 (Race Condition)

| 属性 | 值 |
|------|-----|
| **严重度** | 🔴 High |
| **文件** | `service/InventoryService.java` |
| **方法** | `deductStock()` |
| **类型** | 并发安全 |

**问题描述**:   
`deductStock()` 方法在执行库存检查和扣减之间没有加锁或使用悲观锁。当多个并发请求同时扣减同一商品的库存时，会出现 **check-then-act** 竞态条件：

```java
// Thread 1 和 Thread 2 同时读到 availableQuantity = 5
// 两个请求都请求 4 个，都通过了检查
// 最终 availableQuantity = 5 - 4 - 4 = -3，出现超卖
```

**正确做法**: 使用 `@Lock(LockModeType.PESSIMISTIC_WRITE)` 或数据库行级锁/乐观锁 `@Version`。

---

#### Bug #2 - SQL 注入漏洞

| 属性 | 值 |
|------|-----|
| **严重度** | 🔴 High |
| **文件** | `service/ProductService.java` |
| **方法** | `advancedSearch()` |
| **类型** | 安全漏洞 |

**问题描述**:   
`advancedSearch()` 方法使用字符串拼接构建原生 SQL 查询，攻击者可通过搜索参数注入恶意 SQL：

```java
// 输入: name = "'; DROP TABLE products; --"
StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1");
sql.append(" AND name LIKE '%").append(name).append("%'"); // 直接拼接!
```

通过 `GET /api/products/advanced-search?name='; DROP TABLE products; --` 即可执行任意 SQL。

---

#### Bug #3 - 金额精度丢失

| 属性 | 值 |
|------|-----|
| **严重度** | 🔴 High |
| **文件** | `util/PriceCalculator.java` |
| **方法** | `calculateDiscount()`, `calculateTax()` |
| **类型** | 数值精度 |

**问题描述**:   
价格计算使用了 Java 的 `double` 和 `float` 浮点类型，而非 `BigDecimal`。浮点数无法精确表示十进制小数（如 0.1），导致金额计算出现误差：

```java
// double price = 0.1 + 0.2; // 结果: 0.30000000000000004, 不是 0.3
public double calculateDiscount(double price, double discountPercent) { ... }
```

在电商系统中，这种精度误差会累积，导致财务报表不平、对账错误。

---

#### Bug #4 - 缺少权限检查

| 属性 | 值 |
|------|-----|
| **严重度** | 🔴 High |
| **文件** | `controller/AdminController.java` |
| **方法** | `deleteProduct()`, `getAllUsers()`, `featureProduct()` |
| **类型** | 安全漏洞 - 授权缺失 |

**问题描述**:   
`AdminController` 中的所有管理端点都没有 `@PreAuthorize("hasRole('ADMIN')")` 注解，且 Spring Security 配置中对 `/api/**` 全部 `permitAll()`。这意味着**任何未认证的用户都可以执行管理操作**，包括删除商品、查看所有用户、设置精选商品等。

---

#### Bug #5 - 订单总额计算忽略已取消商品

| 属性 | 值 |
|------|-----|
| **严重度** | 🔴 High |
| **文件** | `service/OrderService.java` |
| **方法** | `calculateOrderTotals()` |
| **类型** | 业务逻辑错误 |

**问题描述**:   
`calculateOrderTotals()` 在计算订单总额时，对**所有** `OrderItem` 求和（包括状态为 `CANCELLED` 或 `RETURNED` 的商品行）：

```java
BigDecimal subtotal = order.getItems().stream()
    .map(OrderItem::getSubtotal)
    .reduce(BigDecimal.ZERO, BigDecimal::add);
// 没有过滤: .filter(item -> item.getStatus() != ItemStatus.CANCELLED)
```

这导致用户即使取消了部分商品，仍被收取这些商品的费用。

---

#### Bug #6 - Session Fixation 防护被禁用

| 属性 | 值 |
|------|-----|
| **严重度** | 🔴 High |
| **文件** | `config/SecurityConfig.java` |
| **方法** | `securityFilterChain()` |
| **类型** | 安全漏洞 |

**问题描述**:   
Security 配置中明确禁用了 Session Fixation 防护：

```java
.sessionFixation(sessionFixation -> sessionFixation.none()) // 应该是 .migrateSession()
```

攻击者可以预先设置一个 Session ID，诱导受害者使用该 Session 登录，之后攻击者即可使用相同的 Session ID 劫持已认证的会话。

---

#### Bug #7 - 支付与订单跨事务不一致

| 属性 | 值 |
|------|-----|
| **严重度** | 🔴 High |
| **文件** | `service/OrderService.java` + `service/PaymentService.java` |
| **方法** | `createOrder()` + `processPayment()` |
| **类型** | 事务一致性 |

**问题描述**:   
`OrderService.createOrder()` 在 `@Transactional` 方法中保存订单并清空购物车后提交事务，但 `PaymentService.processPayment()` 是**在事务外**被调用的（或在不同事务中）。这导致：

1. 订单已存入数据库，但支付失败 → 订单存在却无支付记录
2. 支付成功提交了事务，但订单事务回滚 → 支付成功但订单丢失

应使用分布式事务（如 Saga 模式）或将支付纳入同一事务边界。

---

### 🟡 中严重度（Medium）- 7 个

这些问题可能导致数据错误、性能问题或有限的安全风险。

---

#### Bug #8 - 分页 off-by-one 错误

| 属性 | 值 |
|------|-----|
| **严重度** | 🟡 Medium |
| **文件** | `service/ProductService.java` |
| **方法** | `getProducts()` |
| **类型** | 逻辑错误 |

**问题描述**:   
`PageRequest.of(page, size)` 中的 `page` 参数是零基索引（0 = 第一页），但 API 用户通常期望 `page=1` 是第一页。当前实现将 `page=1` 直接传给 `PageRequest.of(1, size)`，返回的是**第二页**。

**修复**: 应使用 `PageRequest.of(page - 1, size)`（当 page >= 1 时）。

---

#### Bug #9 - 缓存未失效

| 属性 | 值 |
|------|-----|
| **严重度** | 🟡 Medium |
| **文件** | `service/ProductService.java` |
| **方法** | `updateProduct()` |
| **类型** | 缓存一致性 |

**问题描述**:   
`getProduct()` 使用了 `@Cacheable(value = "products", key = "#id")` 缓存，但 `updateProduct()` 没有使用 `@CachePut` 或 `@CacheEvict` 使缓存失效。更新商品后，后续 `getProduct()` 读取仍返回旧数据。

---

#### Bug #10 - NPE 风险（懒加载）

| 属性 | 值 |
|------|-----|
| **严重度** | 🟡 Medium |
| **文件** | `service/OrderService.java` |
| **方法** | `getOrder()` |
| **类型** | 空指针异常 |

**问题描述**:   
`getOrder()` 返回的 `Order` 对象中，`items` 字段可能因懒加载而未初始化。当 DTO 转换代码访问 `order.getItems()` 时，如果 Hibernate Session 已关闭，会抛出 `LazyInitializationException`。此外，`toDTO()` 方法中直接访问 `item.getProduct().getId()` 和 `item.getProduct().getName()` 在 Product 为 null 或懒加载未初始化时也会 NPE。

---

#### Bug #11 - 税率舍入模式错误

| 属性 | 值 |
|------|-----|
| **严重度** | 🟡 Medium |
| **文件** | `service/TaxService.java` |
| **方法** | `calculateTax()` |
| **类型** | 数值计算规范 |

**问题描述**:   
税额计算使用了 `RoundingMode.HALF_UP`（四舍五入），而金融行业标准是 `HALF_EVEN`（银行家舍入法）。在大量交易中，`HALF_UP` 会产生累积偏差（总是向上舍入 0.5），导致总税额系统性偏高。

---

#### Bug #12 - 邮箱验证正则过宽松

| 属性 | 值 |
|------|-----|
| **严重度** | 🟡 Medium |
| **文件** | `util/ValidationUtils.java` |
| **方法** | `isValidEmail()` |
| **类型** | 输入验证不足 |

**问题描述**:   
邮箱验证正则 `^[^@]+@[^@]+\\.[^@]+$` 过于宽松，接受以下无效邮箱：
- `a@b` - 无有效域名
- `user@domain` - 缺少 TLD
- `user@.com` - 域名部分为空
- `@domain.com` - 用户名为空（第一个 `+` 改为 `*` 才行）

正确的正则应为 `^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$`。

---

#### Bug #13 - Prototype 作用域误用

| 属性 | 值 |
|------|-----|
| **严重度** | 🟡 Medium |
| **文件** | `service/DiscountService.java` |
| **方法** | 类级别 `@Scope("prototype")` |
| **类型** | Spring Bean 作用域错误 |

**问题描述**:   
`DiscountService` 被标记为 `@Scope("prototype")`，但它被 `OrderService`（单例）通过构造函数注入。由于 Spring 默认在单例初始化时只注入一次依赖，`OrderService` 持有的 `DiscountService` 实例**始终是同一个**，prototype 作用域不生效。更糟的是，`discountAppliedCount` 字段本意是每请求独立的计数器，但实际上**所有请求共享同一实例**，计数器完全不准确。

---

#### Bug #14 - 时区处理错误

| 属性 | 值 |
|------|-----|
| **严重度** | 🟡 Medium |
| **文件** | `util/DateUtils.java` |
| **方法** | `convertToDate()`, `convertToLocalDateTime()`, `formatDateTime()` |
| **类型** | 国际化/时区问题 |

**问题描述**:   
所有日期转换都使用 `ZoneId.systemDefault()`（服务器本地时区）而非 UTC。当服务器部署在不同时区时，订单创建时间、支付时间等会不一致。正确做法是内部统一使用 UTC 存储，仅在展示层转换到用户时区。

---

### 🟢 低严重度（Low）- 6 个

这些是代码质量问题，不会直接导致功能错误但有维护隐患。

---

#### Bug #15 - 错误消息误导

| 属性 | 值 |
|------|-----|
| **严重度** | 🟢 Low |
| **文件** | `exception/ProductNotFoundException.java` |
| **方法** | 构造函数 |
| **类型** | 代码质量问题 |

**问题描述**:   
`ProductNotFoundException` 的异常消息写的是 "**User** not found"，显然是复制粘贴遗留的错误。当商品不存在时，日志和 API 响应都显示 "User not found"，严重误导调试和客户端错误处理。

---

#### Bug #16 - 缺少输入验证

| 属性 | 值 |
|------|-----|
| **严重度** | 🟢 Low |
| **文件** | `controller/ProductController.java` |
| **方法** | `createProduct()` |
| **类型** | 输入验证缺失 |

**问题描述**:   
创建商品的接口没有对 `name` 字段进行长度和空值校验。可以创建名称为空字符串或 10000 字符的商品，数据库层面可能抛出异常或存储无效数据。

---

#### Bug #17 - 资源泄漏（未关闭 FileWriter）

| 属性 | 值 |
|------|-----|
| **严重度** | 🟢 Low |
| **文件** | `service/AuditService.java` |
| **方法** | `writeAuditLog()` |
| **类型** | 资源泄漏 |

**问题描述**:   
`writeAuditLog()` 每次调用都创建 `new FileWriter()` 但从不关闭。在高并发场景下，文件描述符会逐渐耗尽，最终导致 `IOException: Too many open files`。应使用 try-with-resources：

```java
try (FileWriter writer = new FileWriter(AUDIT_LOG_FILE, true)) {
    writer.write(logEntry);
}
```

---

#### Bug #18 - 敏感数据日志泄露

| 属性 | 值 |
|------|-----|
| **严重度** | 🟢 Low |
| **文件** | `service/PaymentService.java` |
| **方法** | `processPayment()` |
| **类型** | 安全 - 日志泄露 |

**问题描述**:   
支付处理时在 **INFO** 级别日志中记录完整的信用卡号：

```java
log.info("Processing payment for order {}, amount: {}, card: {}",
    order.getOrderNumber(), order.getTotalAmount(), creditCardNumber);
```

信用卡号是 PCI-DSS 合规要求的敏感数据，不应记录在任何日志中。即使需要日志，也应在 DEBUG 级别且脱敏（如 `****1234`）。

---

#### Bug #19 - 硬编码配置值

| 属性 | 值 |
|------|-----|
| **严重度** | 🟢 Low |
| **文件** | `service/ShippingService.java` |
| **方法** | `calculateShippingFee()` |
| **类型** | 配置管理 |

**问题描述**:   
运费和免运费阈值硬编码在代码中（`BASE_SHIPPING_FEE = 15.00`），虽然 `application.yml` 中已配置了 `ecshop.shipping.base-fee` 和 `ecshop.shipping.free-shipping-threshold`，但 `ShippingService` 没有通过 `@Value` 读取，修改运费需要改代码重新部署。

---

#### Bug #20 - 重复 SKU 未校验

| 属性 | 值 |
|------|-----|
| **严重度** | 🟢 Low |
| **文件** | `service/ProductService.java` |
| **方法** | `createProduct()` |
| **类型** | 数据完整性 |

**问题描述**:   
创建商品时没有调用 `productRepository.existsBySku()` 检查 SKU 是否已存在。虽然数据库有 `UNIQUE` 约束（`@Column(unique = true)`），但这会导致数据库抛出 `DataIntegrityViolationException` 而非友好的业务异常，且异常消息对用户不友好。

---

## 对 AI Agent 的评估维度

测试 AI Agent 时应关注以下能力：

### 1. 发现能力
- Agent 是否能够通过代码审查发现所有 20 个运行时 Bug 和 15 个编译错误？
- 是否能理解业务逻辑而非仅检查语法错误？
- 是否能够在多文件之间追踪调用链发现问题？
- 能否从编译器错误信息反向定位到源码中的具体问题？

### 2. 分析能力
- 是否能正确判断每个 Bug 的严重度级别？
- 是否能推理出 Bug 的触发条件和影响范围？
- 对并发、安全等复杂 Bug 的分析深度如何？
- 对编译错误的类型分类是否准确？

### 3. 修复能力
- 修复方案是否正确且完整？
- 修复某个 Bug 时是否引入了新的 Bug？
- 是否考虑了修复对上下游代码的影响？
- 编译错误的修复是否一步到位（而非逐文件逐个修复）？

### 4. 沟通能力
- 是否能用清晰的语言解释 Bug 原因和修复方案？
- 是否能按照严重度优先级排序修复建议？
- 是否能区分编译错误和运行时 Bug 的不同评估标准？

---

## API 端点概览

| 方法 | 端点 | 说明 |
|------|------|------|
| GET | `/api/products?page=0&size=20` | 商品列表（⚠️ Bug #8） |
| GET | `/api/products/{id}` | 商品详情（⚠️ Bug #9） |
| GET | `/api/products/search?keyword=xxx` | 商品搜索 |
| GET | `/api/products/advanced-search?name=...&category=...` | 高级搜索（⚠️ Bug #2） |
| POST | `/api/products` | 创建商品（⚠️ Bug #16, #20） |
| PUT | `/api/products/{id}` | 更新商品（⚠️ Bug #9） |
| GET | `/api/cart/{userId}` | 购物车 |
| POST | `/api/cart/{userId}/items?productId=&quantity=` | 添加商品 |
| POST | `/api/orders?userId=&addressId=&discountCode=` | 创建订单（⚠️ Bug #1, #5, #7） |
| GET | `/api/orders/{id}` | 订单详情（⚠️ Bug #10） |
| POST | `/api/orders/{id}/cancel` | 取消订单 |
| POST | `/api/users/register` | 用户注册（⚠️ Bug #12） |
| DELETE | `/api/admin/products/{id}` | 删除商品（⚠️ Bug #4） |
| POST | `/api/admin/products/{id}/feature` | 精选商品（⚠️ Bug #4） |
| GET | `/api/inventory/product/{productId}` | 库存查询 |

---

## License

本项目仅用于教育和 AI Agent 测试目的。
