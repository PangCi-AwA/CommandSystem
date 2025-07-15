package org.command.system;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

@Getter
public class CommandRegistry implements CommandExecutor, TabExecutor {

    private final Set<CommandMethod> methods = new HashSet<>();
    private final static Map<String, Function<String, List<String>>> customTabMap = new HashMap<>();

    public static void addCustomTab(String type, Function<String, List<String>> function) {
        customTabMap.put(type, function);
    }

    @Setter
    private String permission = null;

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String l, @NotNull String[] args) {
        if (this.permission != null && !sender.hasPermission(this.permission)) return true;
        CommandMethod method = findMethod(l, args);
        if (method == null || !method.hasPermission(sender)) return true;
        CommandArgs commandArgs = method.buildArgs(args);
        try {
            method.invokeExecute(sender, commandArgs);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return true;
    }

    @Nullable
    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String l, @NotNull String[] args) {
        if (this.permission != null && !sender.hasPermission(this.permission)) return List.of();
        List<String> result = new ArrayList<>();
        for (CommandMethod method : this.methods) {
            if (!method.hasPermission(sender)) continue;
            String s = method.matchTab(l, args);
            if (s != null) {
                if (s.startsWith("<player:")) {
                    for (Player onlinePlayer : Bukkit.getOnlinePlayers()) result.add(onlinePlayer.getName());
                } else {
                    boolean compete = false;
                    for (Map.Entry<String, Function<String, List<String>>> entry : customTabMap.entrySet()) {
                        if (s.startsWith("<%s:".formatted(entry.getKey()))) {
                            List<String> apply = entry.getValue().apply(
                                    s.substring(1, s.length() - 1).split(":")[1]
                            );
                            if (!apply.isEmpty()) {
                                compete = true;
                                result.addAll(apply);
                            }
                        }
                    }
                    if (!compete) result.add(s);
                }
            }
        }
        return result;
    }

    @Nullable
    public CommandMethod findMethod(String alias, @NotNull String[] args) {
        for (CommandMethod method : this.methods) {
            if (method.isMatch(alias, args)) return method;
        }
        return null;
    }

    public void register(Object obj) {
        for (Method method : obj.getClass().getDeclaredMethods()) {
            if (!method.isAnnotationPresent(CommandNode.class)) return;
            String alias = null;
            if (method.isAnnotationPresent(CommandAlias.class)) {
                alias = method.getAnnotation(CommandAlias.class).value();
            }
            String permission = null;
            if (method.isAnnotationPresent(CommandPermission.class)) {
                permission = method.getAnnotation(CommandPermission.class).value();
            }
            String[] split = method.getAnnotation(CommandNode.class).value().split("\\.");
            methods.add(new CommandMethod(obj, method, split, alias, permission));
        }
    }
}
