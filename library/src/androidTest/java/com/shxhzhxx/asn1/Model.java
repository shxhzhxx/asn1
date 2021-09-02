package com.shxhzhxx.asn1;

import androidx.annotation.Nullable;

import com.shxhzhxx.asn1.reflect.ASN1Field;

import java.util.Arrays;

public class Model {
    @ASN1Field(order = 1)
    public int id;

    @ASN1Field(order = 2)
    public byte[] data;

    public Model() {

    }

    public Model(int id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Model model = (Model) obj;
        return id == model.id && Arrays.equals(data, model.data);
    }
}
