package cn.tupurpcheung.mybatis.mapping;

import java.util.HashMap;
import java.util.Map;

public class TypeAliasRegistry {

    private Map<String, Class<?>> TYPE_ALIASES = new HashMap<>();

    public void registerAlias(String alias, Class<?> value) {
        TYPE_ALIASES.put(alias.toLowerCase(), value);
    }

    public Class<?> resolveAlias(String type) {
        return TYPE_ALIASES.getOrDefault(type.toLowerCase(), null);
    }

    public Class<?> resolveAlias(String type,Class<?> defaultValue) {
        return TYPE_ALIASES.getOrDefault(type.toLowerCase(), defaultValue);
    }
}
