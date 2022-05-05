package com.shxhzhxx.asn1.reflect;

import com.shxhzhxx.asn1.ASN1InputStream;
import com.shxhzhxx.asn1.Constants;

import java.lang.reflect.Field;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ASN1Decoder {
    public static <T> T decode(byte[] in, Class<T> cls) throws Exception {
        return decode(new ASN1InputStream(in), new TypeAdapter(cls));
    }

    public static <T> T decode(byte[] in, TypeToken<T> typeToken) throws Exception {
        return decode(new ASN1InputStream(in), typeToken.getAdapter());
    }

    @SuppressWarnings("unchecked")
    private static <T> T decode(ASN1InputStream in, TypeAdapter type) throws Exception {
        int tag = in.readTag();
        int len = in.readLength();

        Class cls = type.getTypeClass();

        Object object = null;
        switch (tag) {
            case Constants.TAG_INTEGER:
                object = new BigInteger(in.readContent(len)).intValue();
                break;
            case Constants.TAG_OCTET_STRING:
                object = in.readContent(len);
                break;
            case Constants.TAG_NULL:
                object = null;
                break;
            case Constants.TAG_SET:
                if (cls == Set.class) {
                    object = decodeCollection(in, len, type.getItemType(), new HashSet<>());
                    break;
                }
                break;
            case Constants.TAG_SEQUENCE:
                if (cls == List.class) {
                    object = decodeCollection(in, len, type.getItemType(), new ArrayList<>());
                    break;
                }

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
                    field.set(object, decode(is, type.getFieldType(field)));
                }
                break;
        }

        return (T) object;
    }

    private static Collection<Object> decodeCollection(ASN1InputStream in, int len, TypeAdapter itemType, Collection<Object> collection) throws Exception {
        byte[] remaining = in.readContent(len);
        while (remaining.length > 0) {
            ASN1InputStream is = new ASN1InputStream(remaining);
            collection.add(decode(is, itemType));
            remaining = is.remaining();
        }
        return collection;
    }

}
