package com.shxhzhxx.asn1;

import android.util.Base64;
import android.util.Log;
import android.util.Pair;

import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.shxhzhxx.asn1.reflect.ASN1Decoder;
import com.shxhzhxx.asn1.reflect.ASN1Encoder;
import com.shxhzhxx.asn1.reflect.TypeToken;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RunWith(AndroidJUnit4.class)
public class ASN1UnitTest {
    public static final String LOG_TAG = "ASN1";
    private static final int REPEAT = 100;
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
            Assert.assertEquals((int) ASN1Decoder.decode(ASN1Encoder.encode(randomNumber), new TypeToken<Integer>() {
            }), randomNumber);
            Assert.assertEquals((int) ASN1Decoder.decode(ASN1Encoder.encode(randomNumber), int.class), randomNumber);
            Assert.assertEquals((int) ASN1Decoder.decode(ASN1Encoder.encode(randomNumber), Integer.class), randomNumber);

            Assert.assertEquals((int) ASN1Decoder.decode(ASN1Encoder.encode(-randomNumber), new TypeToken<Integer>() {
            }), -randomNumber);
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
            Assert.assertArrayEquals(randomBytes, ASN1Decoder.decode(ASN1Encoder.encode(randomBytes), new TypeToken<byte[]>() {
            }));
            Assert.assertArrayEquals(randomBytes, ASN1Decoder.decode(ASN1Encoder.encode(randomBytes), byte[].class));
        }
    }

    private void objectTest() throws Exception {
        Log.d(LOG_TAG, "object 测试");
        for (int i = 0; i < REPEAT; i++) {
            Model modelIn = randomModel();
            Model modelOut = ASN1Decoder.decode(ASN1Encoder.encode(modelIn), Model.class);
            Assert.assertEquals(modelIn, modelOut);

            Model modelOut1 = ASN1Decoder.decode(ASN1Encoder.encode(modelIn), new TypeToken<Model>() {
            });
            Assert.assertEquals(modelIn, modelOut1);
        }

        NestModel nestModelIn = randomNestModel();
        int n = (int) (Math.random() * 100);
        for (int i = 0; i < n; i++) {
            nestModelIn.nestList.add(randomNestModel());
        }
        NestModel nestModelOut = ASN1Decoder.decode(ASN1Encoder.encode(nestModelIn), NestModel.class);
        Assert.assertEquals(nestModelIn, nestModelOut);
        NestModel nestModelOut1 = ASN1Decoder.decode(ASN1Encoder.encode(nestModelIn), new TypeToken<NestModel>() {
        });
        Assert.assertEquals(nestModelIn, nestModelOut1);


        for (int i = 0; i < REPEAT; i++) {
            GenericModel<GenericModel<Model>> ggModelIn = new GenericModel<>();
            ggModelIn.val = (int) (Math.random() * Integer.MAX_VALUE);
            if (Math.random() > 0.5) {
                ggModelIn.model = new GenericModel<>();
                ggModelIn.model.model = randomModel();
                ggModelIn.model.val = (int) (Math.random() * Integer.MAX_VALUE);
            }

            GenericModel<GenericModel<Model>> ggModelOut = ASN1Decoder.decode(ASN1Encoder.encode(ggModelIn), new TypeToken<GenericModel<GenericModel<Model>>>() {
            });
            Assert.assertEquals(ggModelIn, ggModelOut);
        }


        for (int x = 0; x < REPEAT; x++) {
            MultiGenericModel<Model, ModelB, Integer> mModelIn = new MultiGenericModel<>();
            mModelIn.cmd = (int) (Math.random() * Integer.MAX_VALUE);
            mModelIn.list = randomModelList(100);
            mModelIn.model = randomModel();
            mModelIn.nestList = new ArrayList<>();
            n = (int) (Math.random() * 100);
            for (int i = 0; i < n; i++) {
                GenericModel<ModelB> genericModel = new GenericModel<>();
                genericModel.val = (int) (Math.random() * Integer.MAX_VALUE);
                genericModel.model = randomModelB();
                mModelIn.nestList.add(genericModel);
            }
            mModelIn.list2 = new ArrayList<>();
            n = (int) (Math.random() * 100);
            for (int i = 0; i < n; i++) {
                mModelIn.list2.add((int) (Math.random() * Integer.MAX_VALUE));
            }
            mModelIn.list3 = new ArrayList<>();
            n = (int) (Math.random() * 100);
            for (int i = 0; i < n; i++) {
                mModelIn.list3.add(new PairModel<>(randomModelB(), (int) (Math.random() * 1024)));
            }

            MultiGenericModel<Model, ModelB, Integer> mModelOut = ASN1Decoder.decode(ASN1Encoder.encode(mModelIn), new TypeToken<MultiGenericModel<Model, ModelB, Integer>>() {
            });
            Assert.assertEquals(mModelIn, mModelOut);
        }

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
        List<Integer> intListOut = ASN1Decoder.decode(ASN1Encoder.encode(intListIn), new TypeToken<List<Integer>>() {
        });
        Assert.assertEquals(intListIn.size(), intListOut.size());
        for (int i = 0; i < intListIn.size(); i++) {
            Assert.assertEquals(intListIn.get(i), intListOut.get(i));
        }

        n = (int) (Math.random() * 1024);
        List<byte[]> bytesListIn = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            bytesListIn.add(random.generateSeed((int) (Math.random() * 1024)));
        }
        List<byte[]> bytesListOut = ASN1Decoder.decode(ASN1Encoder.encode(bytesListIn), new TypeToken<List<byte[]>>() {
        });
        Assert.assertEquals(bytesListIn.size(), bytesListOut.size());
        for (int i = 0; i < bytesListIn.size(); i++) {
            Assert.assertArrayEquals(bytesListIn.get(i), bytesListOut.get(i));
        }


        List<Model> objectListIn = randomModelList(100);
        List<Model> objectListOut = ASN1Decoder.decode(ASN1Encoder.encode(objectListIn), new TypeToken<List<Model>>() {
        });
        if (objectListIn == null) {
            Assert.assertNull(objectListOut);
        } else {
            Assert.assertEquals(objectListIn.size(), objectListOut.size());
            for (int i = 0; i < objectListIn.size(); i++) {
                Assert.assertEquals(objectListIn.get(i), objectListOut.get(i));
            }
        }


        n = (int) (Math.random() * 100);
        List<List<Model>> objectNestListIn = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            objectNestListIn.add(randomModelList(n));
        }
        List<List<Model>> objectNestListOut = ASN1Decoder.decode(ASN1Encoder.encode(objectNestListIn), new TypeToken<List<List<Model>>>() {
        });
        Assert.assertEquals(objectNestListIn.size(), objectNestListOut.size());
        for (int i = 0; i < objectNestListIn.size(); i++) {
            if (objectNestListIn.get(i) == null) {
                Assert.assertNull(objectNestListOut.get(i));
            } else {
                Assert.assertEquals(objectNestListIn.get(i).size(), objectNestListOut.get(i).size());
                for (int ii = 0; ii < objectNestListIn.get(i).size(); ii++) {
                    Assert.assertEquals(objectNestListIn.get(i).get(ii), objectNestListOut.get(i).get(ii));
                }
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
        Set<Integer> intSetOut = ASN1Decoder.decode(ASN1Encoder.encode(intSetIn), new TypeToken<Set<Integer>>() {
        });
        Assert.assertEquals(intSetIn.size(), intSetOut.size());
        for (Integer item : intSetIn) {
            Assert.assertTrue(intSetOut.contains(item));
        }


        n = (int) (Math.random() * 100);
        Set<Model> objectSetIn = new HashSet<>();
        for (int i = 0; i < n; i++) {
            objectSetIn.add(randomModel());
        }
        Set<Model> objectSetOut = ASN1Decoder.decode(ASN1Encoder.encode(objectSetIn), new TypeToken<Set<Model>>() {
        });
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
        Set<Set<Model>> objectNestSetOut = ASN1Decoder.decode(ASN1Encoder.encode(objectNestSetIn), new TypeToken<Set<Set<Model>>>() {
        });
        Assert.assertEquals(objectNestSetIn.size(), objectNestSetOut.size());
        for (Set<Model> item : objectNestSetIn) {
            Assert.assertTrue(objectNestSetOut.contains(item));
        }
    }

    private ModelB randomModelB(){
        if (Math.random() > 0.7) return null;
        return new ModelB((int) (Math.random() * Integer.MAX_VALUE), (int) (Math.random() * Integer.MAX_VALUE), random.generateSeed((int) (Math.random() * 1024)));
    }

    private Model randomModel() {
        if (Math.random() > 0.7) return null;
        byte[] data = Math.random() > 0.5 ? null : random.generateSeed((int) (Math.random() * 1024));
        return new Model((int) (Math.random() * Integer.MAX_VALUE), data);
    }

    private ArrayList<Model> randomModelList(int n) {
        if (Math.random() > 0.7) return null;
        n = (int) (Math.random() * n);
        ArrayList<Model> list = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            list.add(randomModel());
        }
        return list;
    }
}
