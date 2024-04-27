# 二手交易系统

## V1.0实现

- 纯Java实现，控制台交互
- 系统角色：管理员，买家，卖家
- 管理员功能：查看上架商品和成交商品
- 卖家功能：上架商品，查看订单
- 买家功能：根据名称购买商品，查看所有商品
- 有欢迎简单的欢迎界面，由欢迎界面可以切换为设定的三个角色。
- 商品类只有名称和卖家的联系方式。

## 问题

- 不会运用单例模式
- 方法的调用不熟悉（实例方法，静态方法）

### **单例对象的创建，适合全局单例使用，直接使用类名去拿对象的属性和方法**

```java
public class Seller {
    //创建一个静态的对象，并创建静态的方法对外提供单例对象
    private static Seller seller = new Seller();
    public static Seller getSingle() {
        return seller;
    }
}
```

### 将对象存储在集合中的理解

`private static List<Product> products = new ArrayList<>();`

```java
public void addProduct(Product product) {
    products.add(product);
}
```

**这个集合中的数据是怎么样存储的？**

在 Java 中，ArrayList 是一个动态数组，它可以存储任意类型的对象。在您提供的代码中，products 变量是一个 List 类型，它被初始化为一个新的 ArrayList 实例。这意味着 products 集合可以存储任何类型的对象，并且它的大小可以根据需要动态增长。

- **`products` 集合的本质**

`private static List<Product> products = new ArrayList<>();` 声明了一个`ArrayList`，专门用来存储 `Product` 类型的对象。

- **`products.add(product)` 具体作用**？

- **创建对象引用:** 当你执行 `products.add(product)`， `ArrayList` 并不会把整个 `product` 对象直接复制进去。它做的是创建一个引用（可以理解为一个指针或地址），指向你创建的 `product`对象在内存中的实际位置。
- **存储对象属性:** 由于 `ArrayList` 存储的是这个引用，所以任何对于 `Product` 对象属性（`productName` 和 `sellerContact`）的修改都会同时反映在 `products` 集合中的元素上，因为它们本质上指向的是同一个对象（指向你创建的 `product`对象在内存中的实际位置）。

- 结合理解：

```java
//全局共用一个商品列表所以直接定义为静态，直接使用类名调用，对外提供单例对象
    private static List<Product> products = new ArrayList<>();
    public static List<Product> getProductsSingle() {
        return products;
    }

// 卖家上架商品信息，将商品对象存储到集合中（即使不是静态方法，但我new了对象，可以直接seller.addProduct调用）
    public void addProduct(Product product) {
        products.add(product);
    }

//通过对外的单例对象方法，拿到商品集合（在其它类中直接调用对外的单例对象方法）
        List<Product> Products = Seller.getProductsSingle();
        for (Product product : Products) {
            System.out.println("商品名称: " + product.getProductName() + ", 卖家联系方式: " + product.getSellerContact());
        }
```

**1. 获取商品集合:**

`Seller.getProductsSingle()` 方法是一个**对外暴露的单例方法**，用于获取所有商品的集合。该方法通常会从数据库或其他数据源中获取数据，并返回一个 `List<Product>` 类型的值。

**2. 遍历商品集合:**

`for` 循环会遍历 `Products` 集合中的每个元素（每一个元素都是一个对象，可以访问属性和方法），并将其赋值给循环变量 `product`。

**3. 打印商品信息:**

在每次循环迭代中，代码会使用 `product` 对象的 `getProductName()` 和 `getSellerContact()` 方法获取商品名称和卖家联系方式，并将其打印输出。

### 为什么使用单例方法

```Java
public class Seller {
    //创建一个静态的对象，并创建静态的方法对外提供单例方法
    private static Seller seller = new Seller();
    public static Seller getSingle() {
        return seller;
    }
//全局共用一个商品列表所以直接定义为静态，对外提供单例方法
	private static List<Product> products = new ArrayList<>();
	public static List<Product> getProductsSingle() {
    return products;
	}
}
```
我在其他类为什么不可以直接用类名. products直接获取这个对象？
		反而还要类名. getProductsSingle()去返回来获取这个对象？

**原因如下：**

- **封装性**: `products` 是一个私有变量，这意味着它只能在 `Seller` 类内部访问其他类要访问就必须对外提供方法（类似于私有属性对外提供相应的getter和setter方法来访问一样）。

### get和set方法的使用

- 纯粹是给private修饰的变量和方法和对象对外提供访问的作用。
- 都可以用idea的自动生成搞定

## V2.0实现

- 使用mysql, java-jdbc驱动连接
- 同样实现1.0的功能，所有的数据存起来
- 用控制台交互

### 实现思路

#### 系统欢迎界面

界面设计：

- 选择注册还是登录
- 注册提供三个角色，注册成功后返回登录界面
- 登录根据登录信息判断角色呈现不同的功能界面

#### 用navicat创建登录界面涉及的用户表

1. 在navicat中创建连接，连接mysql,输入创建mysql时的密码会创建默认数据库：**information_schema** 数据库存储 MySQL 系统表，用于存储数据库的元数据，例如表、列、索引等信息。如果您不使用任何工具来管理 MySQL 数据库，则可以删除此数据库。**mysql** 数据库存储 MySQL 系统数据库，包含一些用于管理 MySQL 服务器的数据库对象，例如存储用户权限、存储过程、触发器等信息。如果您不使用任何 MySQL 工具或应用程序，则可以删除此数据库。**performance_schema** 数据库存储性能分析数据，用于收集和分析 MySQL 服务器的性能指标。如果您不使用 Performance Schema 工具，则可以删除此数据库。**sys** 数据库包含用于存储 Performance Schema 数据的表。如果您不使用 Performance Schema 工具，则可以删除此数据库。

2. 创建项目的数据库后设计用户表（图形化导出sql代码）

```SQL
CREATE TABLE `goodtradedb`.`Untitled`  (
  `id` int NOT NULL AUTO_INCREMENT COMMENT '用户id',
  `username` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户名',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '用户密码',
  `role` tinyint(1) NOT NULL COMMENT '用户角色',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `idx_username`(`username` ASC) USING BTREE COMMENT '使用户名唯一',
  CONSTRAINT `role` CHECK (`role` in (1,2,3))
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
```

3. 使用TRUNCATE TABLE语句删除表中的所有数据，将AUTO_INCREMENT计数器重置为1：(表上有外键约束会无法执行)

```
TRUNCATE TABLE 表名;
```

如果不想撤销外键约束，又想清空表的计数器：

- **禁用外键约束**：在执行 TRUNCATE TABLE 命令之前，先禁用外键约束，然后再启用。

```sql
-- 禁用外键约束
SET foreign_key_checks = 0;

-- 执行 TRUNCATE TABLE 命令
TRUNCATE TABLE products;

-- 启用外键约束
SET foreign_key_checks = 1;
```

#### 构建maven项目(不要带特殊字符英文和数字就行，点不行，类加载器找不到)

- 创建项目时，勾选maven会得到基本项目结构
- 在项目的设置中修改maven位置为本地maven位置即可

- jdbc依赖

```xml
<dependencies>
        <!-- jdbc的驱动 -->
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <version>8.0.28</version>
        </dependency>
    </dependencies>
```

#### 欢迎界面中的注册功能（登录是一样的逻辑）和主菜单完成

#### 卖家界面设计

1. 设计一个商品表（Products）：

- 商品ID（ProductID）：主键，唯一标识每个商品。
- 商品名称（ProductName）：商品的名称。
- 商品描述（Description）：商品的描述或详情。
- 商品价格（Price）：商品的价格。
- 库存数量（StockQuantity）：商品的库存数量。
- 卖家ID（SellerID）：外键，与users表关联，表示该商品属于哪个卖家。
- SellerUserName:卖家用户名
- SellerContact：卖家联系方式

```sql
CREATE TABLE `goodtradedb`.`Untitled`  (
  `ProductID` int NOT NULL AUTO_INCREMENT COMMENT '商品id',
  `ProductName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `Description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `Price` decimal(10, 2) NOT NULL,
  `StockQuantity` int NOT NULL,
  `SellerID` int NOT NULL,
  `SellerUserName` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  `SellerContact` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL,
  PRIMARY KEY (`ProductID`) USING BTREE,
  INDEX `fkSellerId`(`SellerID` ASC) USING BTREE,
  CONSTRAINT `fkSellerId` FOREIGN KEY (`SellerID`) REFERENCES `goodtradedb`.`users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
```

#### 买家界面的订单表设计

1. OrderID：订单ID，作为主键，唯一标识每个订单。
2. ProductID：商品ID，外键，与商品表关联，表示订单中包含的商品。
3. UserID：用户ID，外键，与用户表关联，表示下订单的用户。
4. Quantity：商品数量，表示订单中每种商品的购买数量。
5. OrderDate：订单日期，表示订单创建的日期时间。

- 通过商品id下单，通过传进来得username获取userid
- 用户输入商品购买数量即可
- 无需手动输入订单日期，创建订单时将被设置为当前时间，并且在更新订单时也会自动更新为当前时间。

```sql
CREATE TABLE `goodtradedb`.`Untitled`  (
  `OrderID` int NOT NULL AUTO_INCREMENT,
  `ProductID` int NOT NULL,
  `UserID` int NOT NULL,
  `Quantity` int NOT NULL,
  `OrderDate` datetime NOT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT '自动更新时间？',
  PRIMARY KEY (`OrderID`) USING BTREE,
  INDEX `fk_ProductID`(`ProductID` ASC) USING BTREE,
  INDEX `fk_UserID`(`UserID` ASC) USING BTREE,
  CONSTRAINT `fk_ProductID` FOREIGN KEY (`ProductID`) REFERENCES `goodtradedb`.`products` (`ProductID`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_UserID` FOREIGN KEY (`UserID`) REFERENCES `goodtradedb`.`users` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci ROW_FORMAT = Dynamic;
```

#### 避免数据冲突

**在涉及多个表的数据修改操作时，尤其是涉及到事务的一致性时，可以共用一个数据库连接，并将所有相关的数据库操作放在同一个 try-with-resources 语句中。这样可以确保要么所有操作都成功，要么所有操作都失败并回滚，从而保持数据的一致性。**

```java
// 将商品ID（谁的商品）、购买数量和用户ID（谁买的）保存到订单表中
        try(Connection connection = DriverManager.getConnection(Register.url, Register.DBusername, Register.DBpassword)) {
            if (userID != -1) {
                //查到用户id的情况下，共用一个数据库连接，方便回滚事务
                if (placeOrder(productID, userID, quantity, connection) && updateStockQuantity(productID, quantity, connection)) {
                    // 启动事务
                    connection.setAutoCommit(false);
                    System.out.println("订单已成功下单！");
                    System.out.println("商品名称:" + productName);
                    System.out.println("购买数量:" + quantity);
                } else {
                    // 如果任何一个方法执行失败，则回滚事务（避免数据冲突和不一致）
                    connection.rollback();
                    //可能数据库连接有问题可以重新下单一次
                    System.out.println("订单下单失败！请重新下单一次！");
                    return;
                }
            } else {
                System.out.println("用户名不存在！将结束系统，请重启程序");
                System.exit(0); // 退出系统
            }
        }catch (SQLException e) {
            // 处理 SQL 异常
            e.printStackTrace();
            System.out.println("发生了 SQL 异常：" + e.getMessage());
            System.out.println("将结束系统，请重启程序");
            System.exit(0); // 退出系统
        }
```

#### 表的关联性

- 如果买家和卖家都在同一个 `users` 表中，并且你希望通过 `users` 表中的 `id` 字段来约束 `orders` 表中的订单，那么你可以像你之前设定的一样，将两个外键约束都指向 `users` 表中的 `id` 字段。这样，在删除 `users` 表中的特定用户时，相关的订单也会被删除，从而实现了相同的效果。
- 因为失去了买家或则卖家的订单毫无意义

#### bug

上架bug

- 连续上架相同名称的商品，同一个卖家的商品，商品表没有合并

显示商品bug

- 商品库存为0的商品还是会被显示，但是不可以下单（后续要是可以补货的话，那就完美了）。

## V3实现（springboot3重构项目）

### 重构思路

#### 三类用户的区分设计

- 将**三类用户存储在同一张用户表中**，并**通过一个标识字段来区分它们的类型**。管理员可以通过**后台手动创建**用户并设置相应的标识字段值为1，买家和卖家则可以通过前端注册界面注册，并设置不同的标识字段值（例如2和3）来区分它们的类型。

#### 项目层次设计

表现层设计：一个表对应一个controller

### 数据库表的设置

- 用户表（id(自增)，username（唯一），password（加密的），role_id(用户权限)，nickname(注册后完善)，email（注册后完善）充当联系方式，user_pic(用阿里云存)，create_time,update_time）

- 商品表（product_id(自增)，product_name,product_desc,product_price,stock_quantity,seller_username(唯一键),seller_email，create_time，update_time）

- 订单表（order_id(增),product_id,product_name,purchase_quantity,seller_username(用户表唯一键),seller_email,buyer_username(用户表唯一键),buyer_email,create_time，update_time）



### ProductController

#### 难点：怎么验证该用户是否有权限访问这个接口？

#### 解决方案：

- 我原本打算把在拦截器中获取到的数据存到ThreadLocal中用来当用户访问接口时核验ThreadLocal中的roleId来决定该用户是否有权限访问改接口。但是我不知道怎么实现。难道在每一个接口前都把ThreadLocal中的roleId拿出来核对一下是否满足权限？ 比如有一些接口我只想给roleId为2的用户访问。
- 你的思路是正确的，可以使用拦截器将用户的权限信息存储在 `ThreadLocal` 中，然后在接口方法执行前进行权限验证。但是不需要在每个接口前都手动核对 `ThreadLocal` 中的 `roleId`。可以创建一个权限验证的注解，然后在需要进行权限验证的接口方法上添加该注解，在拦截器中解析注解并进行权限验证。（待验证。。。）

### OderController

#### 难点：

1. 想订单表与用户表，商品表建立关联，实现级联行为。当对应表字段修改时，为了保证数据的一致性，关联表的字段也要更新。因为是三类用户一张表，由于订单的设计，关联用户表的字段时，会发生冲突。即卖家，卖家email关联时，两个字段关联一张表的同一个字段。除非卖家和买家各用一张表保存。

2. 添加订单时，补充属性需要根据前端传入的商品id去查询商品信息和卖家信息，根据拦截器中的用户名查询数据库拿到买家信息。这里涉及两次数据库操作，可以不可以简化？添加订单之后还需要去products表中减去下单的数量还需要进行一次数据库操作。整个步骤下来需要四次数据库操作，有不有更好的解决方案？

3. 关于添加订单时，库存和购买数量的校验，我应该在参数传入时就校验购买数量与库存数量的校验吗？还是在业务层中利用id查询商品时才校验？

4. 添加订单时的一致性问题，假设库存已经更新了，但是后面的程序报错了，需要利用**数据库事务**来回滚，保证不会产生错误数据。（不知道事务从哪里开始，从哪里结束？）

   - 我要对这个方法使用数据库事务，我应该在serviceImpl中使用还是在controller中使用？

   - 在这种情况下，你应该在 Service 层中使用数据库事务。通常，控制器（Controller）负责接收请求、验证输入、调用适当的服务方法，并返回响应。而服务层（Service）则负责处理业务逻辑，包括事务管理、数据处理等。因此，在 Service 层中使用数据库事务更符合业务逻辑的划分。

     在 Spring 中，你可以使用 `@Transactional` 注解来管理事务。在 Service 类的方法上添加该注解，Spring 将会在调用该方法时自动启动事务，并在方法执行完成后根据执行情况提交或回滚事务。

5. 假设多个人同时进行下订单操作，商品库存更新时会不会出问题？比如同时减去了库存，而不是有序的减去库存。使用 Java 中的同步机制来确保在同一时间只有一个线程可以访问共享资源。**这可以通过 `synchronized` 关键字或 `ReentrantLock` 类来实现。** (用了数据库事务，同步机制就不需要了？)

6. 进行表的修改时为了数据的一致性也同样需要修改有关联的表，比如订单的购买数量减少2个那么商品表的库存量就要加2个。
