package com.shxhzhxx.asn1.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.GenericArrayType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class TypeAdapter {
    private final Class _class;
    private final TypeVariable[] _typeParameters;
    private final TypeAdapter[] _actualTypeArguments;

    TypeAdapter(Type type) {
        this(type, new HashMap<TypeVariable, TypeAdapter>());
    }

    private TypeAdapter(Type type, Map<TypeVariable, TypeAdapter> map) {
        if (type instanceof Class) {
            _class = (Class) type;
        } else if (type instanceof ParameterizedType) {
            _class = (Class) ((ParameterizedType) type).getRawType();
        } else if (type instanceof GenericArrayType) {
            Type componentType = ((GenericArrayType) type).getGenericComponentType();
            if (byte.class != componentType) {//只支持byte[]，其他类型数组需使用List或Set
                throw new RuntimeException("unsupported GenericArrayComponentType:" + componentType);
            }
            _class = byte[].class;
        } else {
            throw new RuntimeException("unknown Type:" + type);
        }
        _typeParameters = _class.getTypeParameters();
        _actualTypeArguments = new TypeAdapter[_typeParameters.length];

        if (_actualTypeArguments.length > 0) {
            Type[] actualTypeArguments;
            if (type instanceof ParameterizedType) {
                actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
            } else {
                actualTypeArguments = _typeParameters;
            }
            for (int i = 0; i < actualTypeArguments.length; i++) {
                Type actualTypeArgument = actualTypeArguments[i];
                if (actualTypeArgument instanceof TypeVariable) {
                    _actualTypeArguments[i] = map.get(actualTypeArgument);
                    if (null == _actualTypeArguments[i]) {
                        throw new RuntimeException("unknown TypeVariable:" + actualTypeArgument);
                    }
                } else {
                    _actualTypeArguments[i] = new TypeAdapter(actualTypeArgument, map);
                }
            }
        }
    }


    TypeAdapter getFieldType(Field field) {
        Type fieldType = field.getGenericType();
        if (fieldType instanceof TypeVariable) {
            return mapType((TypeVariable) fieldType);
        }
        Map<TypeVariable, TypeAdapter> map = new HashMap<>();
        getFieldTypeArgs(fieldType, map);
        return new TypeAdapter(fieldType, map);
    }

    private void getFieldTypeArgs(Type type, Map<TypeVariable, TypeAdapter> map) {
        if (type instanceof TypeVariable) {
            map.put((TypeVariable) type, mapType((TypeVariable) type));
        } else if (type instanceof ParameterizedType) {
            Type[] typeArguments = ((ParameterizedType) type).getActualTypeArguments();
            for (Type typeArgument : typeArguments) {
                getFieldTypeArgs(typeArgument, map);
            }
        }
    }

    TypeAdapter getItemType() {
        if (Collection.class.isAssignableFrom(_class) && _actualTypeArguments.length > 0) {
            return _actualTypeArguments[0];
        }
        throw new RuntimeException("invalid collection type");
    }

    Class getTypeClass() {
        return _class;
    }

    private TypeAdapter mapType(TypeVariable typeVariable) {
        for (int i = 0; i < _typeParameters.length; i++) {
            if (typeVariable.equals(_typeParameters[i])) {
                return _actualTypeArguments[i];
            }
        }
        throw new RuntimeException("unknown TypeVariable:" + typeVariable);
    }
}
