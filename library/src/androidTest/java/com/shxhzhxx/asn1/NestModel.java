package com.shxhzhxx.asn1;

import androidx.annotation.Nullable;

import com.shxhzhxx.asn1.reflect.ASN1Field;

import java.util.Arrays;
import java.util.List;

public class NestModel {
    @ASN1Field(order = 1)
    public int cmd;

    @ASN1Field(order = 2)
    public byte[] payload;

    @ASN1Field(order = 3)
    public Model model;

    @ASN1Field(order = 4)
    public List<Integer> ints;

    @ASN1Field(order = 5)
    public List<Model> list;

    @ASN1Field(order = 6)
    public List<NestModel> nestList;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        NestModel nestModel = (NestModel) obj;
        return hashCode() == nestModel.hashCode();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + cmd;
        result = prime * result + (payload == null ? 0 : Arrays.hashCode(payload));
        result = prime * result + (model == null ? 0 : model.hashCode());
        result = prime * result + (ints == null ? 0 : ints.hashCode());
        result = prime * result + (list == null ? 0 : list.hashCode());
        result = prime * result + (nestList == null ? 0 : nestList.hashCode());
        return result;
    }
}
