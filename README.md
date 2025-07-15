# 💬 CommandSystem - 快速构建 Bukkit 指令系统

一个基于注解的 Spigot / Bukkit 插件指令注册框架，支持自动参数解析、权限控制、Tab 补全和自定义类型。

---

## ✅ 功能特点

* 🚀 注解式注册方法，无需实现 CommandExecutor
* 🧠 自动参数类型识别与转换
* 🧩 支持自定义参数类型与 Tab 补全逻辑
* 🔐 自动权限判断
* 🧪 极简结构，轻松扩展与维护

---

## 🔧 快速开始

### 🚀 配置Gradle

```groovy
// 添加 JitPack 仓库
repositories {
    maven { url 'https://jitpack.io' }
}

// 添加依赖
dependencies {
    implementation 'com.github.PangCi-AwA:CommandSystem:1.0.1'
}
```

---

### 🛠 注册命令处理器

```java
public class PluginMain extends JavaPlugin {
    @Override
    public void onEnable() {
        CommandRegistry registry = new CommandRegistry();
        getCommand("demo").setExecutor(registry);
        getCommand("demo").setTabCompleter(registry);

        // 注册指令方法
        registry.register(this);
    }

    // /demo send <player> msg <message>
    @CommandNode("send.<player:target>.msg.<str:msg>")
    public void sendMsg(CommandSender sender, CommandArgs args) {
        Player target = args.getPlayer("target");
        String msg = args.getString("msg");
        if (target != null) target.sendMessage(msg);
    }
}
```

---

## 🔐 权限控制

```java

@CommandPermission("demo.send.msg")
@CommandNode("send.<player:target>.msg.<str:msg>")
public void sendMsg(CommandSender sender, CommandArgs args) {
    // 无权限将不会调用
}
```

---

## 🅰️ 指令别名支持

```java

@CommandAlias("demo2") // 只有 /demo2 send ... 会调用此方法
@CommandNode("send.<player:target>.msg.<str:msg>")
public void sendMsg(CommandSender sender, CommandArgs args) {
    // ...
}
```

---

## 📦 参数类型

| 模板              | 说明                 |
|-----------------|--------------------|
| `<player:name>` | 自动获取 Bukkit 在线玩家对象 |
| `<str:name>`    | 普通字符串              |
| `<int:name>`    | 整数类型               |
| `<double:name>` | 浮点数类型              |
| `<bool:name>`   | 布尔类型（true/false）   |

---

## 🔧 自定义类型与 Tab 补全

### 🛠 添加自定义参数反序列化

```java
CommandArgs.addCustomDeserialize("list-str",s ->List.

of(s.split(",")));

// /demo test a,b,c
@CommandNode("test.<list-str:items>")
public void handleList(CommandArgs args) {
    List<String> items = args.getT("items", List.class);
    // items = [a, b, c]
}
```

---

### ⚙️ 添加自定义 Tab 补全

```java
CommandRegistry.addCustomTab("config-file-name",prefix ->{
        // 示例：返回插件目录下所有配置文件名
        return List.

of("main.yml","settings.yml","data.yml");
});

// /demo config <config-file-name:file>
@CommandNode("config.<config-file-name:file>")
public void handleConfig(CommandArgs args) {
    String fileName = args.getString("file");
    // ...
}
```

---

## ✅ 实用工具

### 获取参数示例

```java
String msg = args.getString("msg");
Integer amount = args.getInt("amount");
Boolean enabled = args.getBool("toggle");
Player target = args.getPlayer("target");
```

---