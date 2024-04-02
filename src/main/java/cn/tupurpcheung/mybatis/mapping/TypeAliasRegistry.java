package cn.tupurpcheung.mybatis.mapping;

import java.util.HashMap;
import java.util.Map;

public class TypeAliasRegistry<T> {

    private Map<String, Class<? extends T>> TYPE_ALIASES = new HashMap<>();

    public void registerAlias(String alias, Class<? extends T> value) {
        TYPE_ALIASES.put(alias.toLowerCase(), value);
    }

    public Class<? extends T> resolveAlias(String type) {
        return TYPE_ALIASES.getOrDefault(type.toLowerCase(), null);
    }
}
