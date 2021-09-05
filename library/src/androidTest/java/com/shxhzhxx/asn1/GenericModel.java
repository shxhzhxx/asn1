package com.shxhzhxx.asn1;

import androidx.annotation.Nullable;

import com.shxhzhxx.asn1.reflect.ASN1Field;

public class GenericModel<T> {
    @ASN1Field(order = 1)
    public int val;

    @ASN1Field(order = 2)
    public T model;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + val;
        result = prime * result + (model == null ? 0 : model.hashCode());
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        GenericModel genericModel = (GenericModel) obj;
        return hashCode() == genericModel.hashCode();
    }
}
