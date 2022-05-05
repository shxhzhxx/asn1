package com.shxhzhxx.asn1.reflect;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeToken<T> {
    private final Type _type;

    protected TypeToken() {
        Type superClass = getClass().getGenericSuperclass();

        if (!(superClass instanceof ParameterizedType)) {
            _type = Object.class;
            return;
        }

        Type[] actualTypeArguments = ((ParameterizedType) superClass).getActualTypeArguments();
        if (actualTypeArguments.length == 0) {
            _type = Object.class;
            return;
        }

        _type = actualTypeArguments[0];
    }

    public final Type getType() {
        return _type;
    }

    public final TypeAdapter getAdapter() {
        return new TypeAdapter(_type);
    }
}
