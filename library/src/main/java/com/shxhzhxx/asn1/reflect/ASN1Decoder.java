package com.shxhzhxx.asn1.reflect;

import com.shxhzhxx.asn1.ASN1InputStream;
import com.shxhzhxx.asn1.Constants;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ASN1Decoder {
    public static <T> T decode(ASN1InputStream in, Class<T> cls, LinkedList<Type> typeParameters) throws Exception {

        int tag = in.readTag();
        int len = in.readLength();

        Object object = null;

        switch (tag) {
            case Constants.TAG_INTEGER:
                if (cls == null || cls == Object.class || cls == Integer.class || cls == int.class) {
                    object = new BigInteger(in.readContent(len)).intValue();
                }
                break;
            case Constants.TAG_OCTET_STRING:
                if (cls == null || cls == Object.class || cls == byte[].class) {
                    object = in.readContent(len);
                }
                break;
            case Constants.TAG_NULL:
                object = null;
                break;
            case Constants.TAG_SET:
                if (cls == null || cls == Object.class || cls == Set.class) {
                    Set<Object> set = new HashSet<>();
                    Class itemClass = retrieveClass(typeParameters);
                    LinkedList<Type> typeParametersCopy = new LinkedList<>(typeParameters);

                    byte[] remaining = in.readContent(len);
                    while (remaining.length > 0) {
                        ASN1InputStream is = new ASN1InputStream(remaining);
                        if (set.isEmpty()) {
                            set.add(decode(is, itemClass, typeParameters));
                        } else {
                            set.add(decode(is, itemClass, new LinkedList<>(typeParametersCopy)));
                        }
                        remaining = is.remaining();
                    }
                    object = set;
                }
                break;
            case Constants.TAG_SEQUENCE:
                if (cls == null || cls == Object.class || cls == List.class) {
                    List<Object> list = new ArrayList<>();
                    Class itemClass = retrieveClass(typeParameters);
                    LinkedList<Type> typeParametersCopy = new LinkedList<>(typeParameters);

                    byte[] remaining = in.readContent(len);
                    while (remaining.length > 0) {
                        ASN1InputStream is = new ASN1InputStream(remaining);
                        if (list.isEmpty()) {
                            list.add(decode(is, itemClass, typeParameters));
                        } else {
                            list.add(decode(is, itemClass, new LinkedList<>(typeParametersCopy)));
                        }
                        remaining = is.remaining();
                    }
                    object = list;
                } else {
                    List<Field> fields = new ArrayList<>();
                    for (Field field : cls.getDeclaredFields()) {
                        if (field.isAnnotationPresent(ASN1Field.class)) {
                            fields.add(field);
                            field.setAccessible(true);
                        }
                    }
                    Collections.sort(fields, new Comparator<Field>() {
                        @Override
                        public int compare(Field o1, Field o2) {
                            return o1.getAnnotation(ASN1Field.class).order() - o2.getAnnotation(ASN1Field.class).order();
                        }
                    });

                    ASN1InputStream is = new ASN1InputStream(in.readContent(len));
                    object = cls.newInstance();

                    for (Field field : fields) {
                        typeParameters.addFirst(field.getGenericType());
                        Class fieldClass = retrieveClass(typeParameters);

                        LinkedList<Type> fieldTypeParameters = new LinkedList<>();
                        int typeCnt = fieldClass.getTypeParameters().length;
                        while (typeCnt > 0) {
                            typeCnt--;
                            Class fieldTypeItem = retrieveClass(typeParameters);
                            typeCnt += fieldTypeItem.getTypeParameters().length;
                            fieldTypeParameters.add(fieldTypeItem);
                        }
                        field.set(object, decode(is, fieldClass, fieldTypeParameters));
                    }
                }
                break;
        }

        return (T) object;
    }

    public static <T> T decode(byte[] in, Class<T> cls, Type... typeParameter) throws Exception {
        return decode(new ASN1InputStream(in), cls, new LinkedList<>(Arrays.asList(typeParameter)));
    }

    private static Class retrieveClass(LinkedList<Type> typeParameters) {
        while (!typeParameters.isEmpty()) {
            Type type = typeParameters.removeFirst();
            if (type instanceof Class) {
                return (Class) type;
            } else if (type instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                typeParameters.addAll(0, Arrays.asList(actualTypeArguments));
                return (Class) ((ParameterizedType) type).getRawType();
            }
        }
        return Object.class;
    }

}
