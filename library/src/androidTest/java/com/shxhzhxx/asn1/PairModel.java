package com.shxhzhxx.asn1;

import androidx.annotation.Nullable;

import com.shxhzhxx.asn1.reflect.ASN1Field;

public class PairModel<F, S> {
    @ASN1Field(order = 1)
    public F first;

    @ASN1Field(order = 2)
    public S second;

    public PairModel() {
    }

    public PairModel(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (first == null ? 0 : first.hashCode());
        result = prime * result + (second == null ? 0 : second.hashCode());
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        PairModel model = (PairModel) obj;
        return hashCode() == model.hashCode();
    }
}
