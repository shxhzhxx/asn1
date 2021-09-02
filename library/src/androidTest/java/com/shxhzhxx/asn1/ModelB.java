package com.shxhzhxx.asn1;

import androidx.annotation.Nullable;

import com.shxhzhxx.asn1.reflect.ASN1Field;

import java.util.Arrays;

public class ModelB {
    @ASN1Field(order = 1)
    public int id;

    @ASN1Field(order = 2)
    public int cmd;

    @ASN1Field(order = 3)
    public byte[] data;


    public ModelB() {

    }

    public ModelB(int id, int cmd, byte[] data) {
        this.id = id;
        this.cmd = cmd;
        this.data = data;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + id;
        result = prime * result + cmd;
        result = prime * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        ModelB model = (ModelB) obj;
        return id == model.id && cmd == model.cmd && Arrays.equals(data, model.data);
    }
}
