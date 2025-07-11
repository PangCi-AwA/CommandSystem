package org.command.system;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.bukkit.command.CommandSender;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class CommandMethod {
    private final Object object;
    private final Method method;
    private final String[] templates;
    @Nullable
    private final String alias;
    @Nullable
    private final String permission;

    public CommandMethod(Object object, Method method, String[] templates) {
        this.object = object;
        this.method = method;
        this.templates = templates;
        this.alias = null;
        this.permission = null;
    }

    public boolean isMatch(String alias, String[] args) {
        if (this.alias != null && !this.alias.equalsIgnoreCase(alias)) return false;
        if (args.length != templates.length) return false;
        for (int i = 0; i < this.templates.length; i++) {
            // 跳过参数类型
            if (this.templates[i].startsWith("<") && this.templates[i].endsWith(">")) continue;
            // 判断指令是否匹配
            if (!this.templates[i].equalsIgnoreCase(args[i])) return false;
        }
        return true;
    }

    public String matchTab(String alias, String[] args) {
        if (this.alias != null && !this.alias.equalsIgnoreCase(alias)) return null;
        if (args.length > templates.length) return null;
        for (int i = 0; i < args.length - 1; i++) {
            if (this.templates[i].startsWith("<") && this.templates[i].endsWith(">")) continue;
            if (!args[i].equalsIgnoreCase(this.templates[i])) return null;
        }
        return this.templates[args.length - 1];
    }

    public CommandArgs buildArgs(String[] args) {
        if (args.length != templates.length) throw new IllegalArgumentException("Parameters do not match template.");
        CommandArgs commandArgs = new CommandArgs();
        for (int i = 0; i < this.templates.length; i++) {
            if (!this.templates[i].startsWith("<") || !this.templates[i].endsWith(">")) continue;
            String[] split = templates[i].substring(1, templates[i].length() - 1).split(":");
            commandArgs.put(split[0], split[1], args[i]);
        }
        return commandArgs;
    }

    public void invokeExecute(CommandSender sender, CommandArgs args) throws Exception {
        List<Object> parameters = new ArrayList<>();
        for (Class<?> parameterType : this.method.getParameterTypes()) {
            if (parameterType == CommandSender.class) {
                parameters.add(sender);
            } else if (parameterType == CommandArgs.class) {
                parameters.add(args);
            } else {
                parameters.add(null);
            }
        }
        this.method.invoke(this.object, parameters.toArray());
    }
}
