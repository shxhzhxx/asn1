package com.shxhzhxx.asn1;

import android.util.Base64;
import android.util.Log;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.shxhzhxx.asn1.reflect.ASN1Decoder;
import com.shxhzhxx.asn1.reflect.ASN1Encoder;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.shxhzhxx.asn1.Constants.LOG_TAG;

@RunWith(AndroidJUnit4.class)
public class ASN1UnitTest {
    private static final int REPEAT = 10;
    private static final SecureRandom random = new SecureRandom();

    @Test
    public void go() {
        try {
            integerTest();
            octetStringTest();
            objectTest();
            listTest();
            setTest();
        } catch (Exception e) {
            Log.d(LOG_TAG, e.toString());
            Assert.fail();
        }
    }


    private void integerTest() throws Exception {
        Log.d(LOG_TAG, "int 测试");
        Assert.assertArrayEquals(new byte[]{0x02, 0x01, 0x01}, ASN1Encoder.encode(1));
        Assert.assertArrayEquals(new byte[]{0x02, 0x03, 0x01, (byte) 0x86, (byte) 0xa0}, ASN1Encoder.encode(100000));
        Integer num = -100;
        Assert.assertArrayEquals(new byte[]{0x02, 0x01, (byte) 0x9c}, ASN1Encoder.encode(num));

        for (int i = 0; i < REPEAT; i++) {
            int randomNumber = (int) (Math.random() * Integer.MAX_VALUE);
            Assert.assertEquals((int) ASN1Decoder.decode(ASN1Encoder.encode(randomNumber), int.class), randomNumber);
            Assert.assertEquals((int) ASN1Decoder.decode(ASN1Encoder.encode(randomNumber), Integer.class), randomNumber);

            Assert.assertEquals((int) ASN1Decoder.decode(ASN1Encoder.encode(-randomNumber), int.class), -randomNumber);
            Assert.assertEquals((int) ASN1Decoder.decode(ASN1Encoder.encode(-randomNumber), Integer.class), -randomNumber);
        }
    }

    private void octetStringTest() throws Exception {
        Log.d(LOG_TAG, "bytes 测试");
        Assert.assertArrayEquals(Base64.decode("BAtoZWxsbyB3b3JsZA==", Base64.DEFAULT), ASN1Encoder.encode("hello world".getBytes()));
        Assert.assertArrayEquals(Base64.decode("BAzkuK3mlofmtYvor5U=", Base64.DEFAULT), ASN1Encoder.encode("中文测试".getBytes()));
        Assert.assertArrayEquals(Base64.decode("BBwwMzc5NTY4MzIhIyYoKiNeLT0s77yM44CC44CK", Base64.DEFAULT), ASN1Encoder.encode("037956832!#&(*#^-=,，。《".getBytes()));

        for (int i = 0; i < REPEAT; i++) {
            byte[] randomBytes = random.generateSeed((int) (Math.random() * 1024 * 1024));
            Assert.assertArrayEquals(randomBytes, ASN1Decoder.decode(ASN1Encoder.encode(randomBytes), byte[].class));
        }
    }

    private void objectTest() throws Exception {
        Log.d(LOG_TAG, "object 测试");
        for (int i = 0; i < REPEAT; i++) {
            Model modelIn = randomModel();
            Model modelOut = ASN1Decoder.decode(ASN1Encoder.encode(modelIn), Model.class);
            Assert.assertEquals(modelIn, modelOut);
        }

        NestModel nestModelIn = randomNestModel();
        int n = (int) (Math.random() * 100);
        for (int i = 0; i < n; i++) {
            nestModelIn.nestList.add(randomNestModel());
        }
        NestModel nestModelOut = ASN1Decoder.decode(ASN1Encoder.encode(nestModelIn), NestModel.class);
        Assert.assertEquals(nestModelIn, nestModelOut);


        for (int i = 0; i < REPEAT; i++) {
            GenericModel<GenericModel<Model>> ggModelIn = new GenericModel<>();
            ggModelIn.val = (int) (Math.random() * Integer.MAX_VALUE);
            ggModelIn.model = new GenericModel<>();
            ggModelIn.model.model = randomModel();
            ggModelIn.model.val = (int) (Math.random() * Integer.MAX_VALUE);

            GenericModel<GenericModel<Model>> ggModelOut = ASN1Decoder.decode(ASN1Encoder.encode(ggModelIn), GenericModel.class, GenericModel.class, Model.class);
            Assert.assertEquals(ggModelIn, ggModelOut);
        }


        MultiGenericModel<Model, ModelB, Integer> mModelIn = new MultiGenericModel<>();
        mModelIn.cmd = (int) (Math.random() * Integer.MAX_VALUE);
        mModelIn.list = randomModelList(100);
        mModelIn.model = randomModel();
        mModelIn.nestList = new ArrayList<>();
        n = (int) (Math.random() * 100);
        for (int i = 0; i < n; i++) {
            GenericModel<ModelB> genericModel = new GenericModel<>();
            genericModel.val = (int) (Math.random() * Integer.MAX_VALUE);
            genericModel.model = new ModelB((int) (Math.random() * Integer.MAX_VALUE), (int) (Math.random() * Integer.MAX_VALUE), random.generateSeed((int) (Math.random() * 1024)));
            mModelIn.nestList.add(genericModel);
        }
        mModelIn.list2 = new ArrayList<>();
        n = (int) (Math.random() * 100);
        for (int i = 0; i < n; i++) {
            mModelIn.list2.add((int) (Math.random() * Integer.MAX_VALUE));
        }

        MultiGenericModel<Model, ModelB, Integer> mModelOut = ASN1Decoder.decode(ASN1Encoder.encode(mModelIn), MultiGenericModel.class, Model.class, ModelB.class, Integer.class);
        Assert.assertEquals(mModelIn, mModelOut);
    }

    private NestModel randomNestModel() {
        NestModel nestModel = new NestModel();
        nestModel.model = randomModel();
        nestModel.cmd = (int) (Math.random() * Integer.MAX_VALUE);
        nestModel.payload = random.generateSeed((int) (Math.random() * 1024));
        nestModel.list = randomModelList(100);
        nestModel.nestList = new ArrayList<>();
        nestModel.ints = new ArrayList<>();
        int n = (int) (Math.random() * 1024);
        for (int i = 0; i < n; i++) {
            nestModel.ints.add((int) (Math.random() * Integer.MAX_VALUE));
        }
        return nestModel;
    }

    private void listTest() throws Exception {
        Log.d(LOG_TAG, "list 测试");
        int n = (int) (Math.random() * 1024 * 10);
        List<Integer> intListIn = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            intListIn.add((int) (Math.random() * Integer.MAX_VALUE));
        }
        List<Integer> intListOut = ASN1Decoder.decode(ASN1Encoder.encode(intListIn), List.class);
        Assert.assertEquals(intListIn.size(), intListOut.size());
        for (int i = 0; i < intListIn.size(); i++) {
            Assert.assertEquals(intListIn.get(i), intListOut.get(i));
        }

        n = (int) (Math.random() * 1024);
        List<byte[]> bytesListIn = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            bytesListIn.add(random.generateSeed((int) (Math.random() * 1024)));
        }
        List<byte[]> bytesListOut = ASN1Decoder.decode(ASN1Encoder.encode(bytesListIn), List.class);
        Assert.assertEquals(bytesListIn.size(), bytesListOut.size());
        for (int i = 0; i < bytesListIn.size(); i++) {
            Assert.assertArrayEquals(bytesListIn.get(i), bytesListOut.get(i));
        }


        List<Model> objectListIn = randomModelList(100);
        List<Model> objectListOut = ASN1Decoder.decode(ASN1Encoder.encode(objectListIn), List.class, Model.class);
        Assert.assertEquals(objectListIn.size(), objectListOut.size());
        for (int i = 0; i < objectListIn.size(); i++) {
            Assert.assertEquals(objectListIn.get(i), objectListOut.get(i));
        }


        n = (int) (Math.random() * 100);
        List<List<Model>> objectNestListIn = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            objectNestListIn.add(randomModelList(n));
        }
        List<List<Model>> objectNestListOut = ASN1Decoder.decode(ASN1Encoder.encode(objectNestListIn), List.class, List.class, Model.class);
        Assert.assertEquals(objectNestListIn.size(), objectNestListOut.size());
        for (int i = 0; i < objectNestListIn.size(); i++) {
            Assert.assertEquals(objectNestListIn.get(i).size(), objectNestListOut.get(i).size());
            for (int ii = 0; ii < objectNestListIn.get(i).size(); ii++) {
                Assert.assertEquals(objectNestListIn.get(i).get(ii), objectNestListOut.get(i).get(ii));
            }
        }
    }

    private void setTest() throws Exception {
        Log.d(LOG_TAG, "set 测试");
        Set<Integer> intSetIn = new HashSet<>();
        int n = (int) (Math.random() * 1024 * 10);
        for (int i = 0; i < n; i++) {
            intSetIn.add((int) (Math.random() * Integer.MAX_VALUE));
        }
        Set<Integer> intSetOut = ASN1Decoder.decode(ASN1Encoder.encode(intSetIn), Set.class);
        Assert.assertEquals(intSetIn.size(), intSetOut.size());
        for (Integer item : intSetIn) {
            Assert.assertTrue(intSetOut.contains(item));
        }


        n = (int) (Math.random() * 100);
        Set<Model> objectSetIn = new HashSet<>();
        for (int i = 0; i < n; i++) {
            objectSetIn.add(randomModel());
        }
        Set<Model> objectSetOut = ASN1Decoder.decode(ASN1Encoder.encode(objectSetIn), Set.class, Model.class);
        Assert.assertEquals(objectSetIn.size(), objectSetOut.size());
        for (Model item : objectSetIn) {
            Assert.assertTrue(objectSetOut.contains(item));
        }


        n = (int) (Math.random() * 100);
        Set<Set<Model>> objectNestSetIn = new HashSet<>();
        for (int i = 0; i < n; i++) {
            Set<Model> set = new HashSet<>();
            int m = (int) (Math.random() * 100);
            for (int ii = 0; ii < m; ii++) {
                set.add(randomModel());
            }
            objectNestSetIn.add(set);
        }
        Set<Set<Model>> objectNestSetOut = ASN1Decoder.decode(ASN1Encoder.encode(objectNestSetIn), Set.class, Set.class, Model.class);
        Assert.assertEquals(objectNestSetIn.size(), objectNestSetOut.size());
        for (Set<Model> item : objectNestSetIn) {
            Assert.assertTrue(objectNestSetOut.contains(item));
        }
    }

    private Model randomModel() {
        if (Math.random() > 0.8) return null;
        byte[] data = Math.random() > 0.8 ? null : random.generateSeed((int) (Math.random() * 1024));
        return new Model((int) (Math.random() * Integer.MAX_VALUE), data);
    }

    private ArrayList<Model> randomModelList(int n) {
        if (Math.random() > 0.8) return null;
        n = (int) (Math.random() * n);
        ArrayList<Model> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(randomModel());
        }
        return list;
    }
}
