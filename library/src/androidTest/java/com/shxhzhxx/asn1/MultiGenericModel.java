package com.shxhzhxx.asn1;

import androidx.annotation.Nullable;

import com.shxhzhxx.asn1.reflect.ASN1Field;

import java.util.List;

public class MultiGenericModel<T1, T2, T3> {
    @ASN1Field(order = 1)
    public int cmd;

    @ASN1Field(order = 2)
    public List<Model> list;

    @ASN1Field(order = 3)
    public T1 model;

    @ASN1Field(order = 4)
    public List<GenericModel<T2>> nestList;

    @ASN1Field(order = 5)
    public List<T3> list2;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cmd;
        result = prime * result + (list == null ? 0 : list.hashCode());
        result = prime * result + (model == null ? 0 : model.hashCode());
        result = prime * result + (nestList == null ? 0 : nestList.hashCode());
        result = prime * result + (list2 == null ? 0 : list2.hashCode());
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        MultiGenericModel model = (MultiGenericModel) obj;
        return hashCode() == model.hashCode();
    }
}
