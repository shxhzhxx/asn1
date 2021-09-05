package com.shxhzhxx.asn1.reflect;

import com.shxhzhxx.asn1.ASN1OutputStream;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class ASN1Encoder {

    public static byte[] encode(Object object) throws Exception {
        if (object == null) {
            return new ASN1OutputStream().writeNull().getEncoded();
        }
        if (object.getClass() == Integer.class) {
            return new ASN1OutputStream().writeInteger((Integer) object).getEncoded();
        }
        if (object.getClass() == byte[].class) {
            return new ASN1OutputStream().writeOctetString((byte[]) object).getEncoded();
        }
        if (List.class.isAssignableFrom(object.getClass())) {
            ASN1OutputStream os = new ASN1OutputStream();
            for (Object item : (List) object) {
                os.write(encode(item));
            }
            return new ASN1OutputStream().writeSequence(os.getEncoded()).getEncoded();
        }
        if (Set.class.isAssignableFrom(object.getClass())) {
            ASN1OutputStream os = new ASN1OutputStream();
            for (Object item : (Set) object) {
                os.write(encode(item));
            }
            return new ASN1OutputStream().writeSet(os.getEncoded()).getEncoded();
        }


        List<Field> fields = new ArrayList<>();
        for (Field field : object.getClass().getDeclaredFields()) {
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

        ASN1OutputStream asn1 = new ASN1OutputStream();

        for (Field field : fields) {
            asn1.write(encode(field.get(object)));
        }
        return new ASN1OutputStream().writeSequence(asn1.getEncoded()).getEncoded();
    }
}
