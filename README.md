# ASN.1



## Dependency

**Step 1.** Add the JitPack repository to your build file 

Add it in your root build.gradle at the end of repositories:

```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

**Step 2.** Add the dependency<br>

```
[![](https://jitpack.io/v/shxhzhxx/asn1.svg)](https://jitpack.io/#shxhzhxx/asn1)
```

```
	dependencies {
	        implementation 'com.github.shxhzhxx:asn1:v1.0'
	}
```





## Usage


```java
public class Student {
    @ASN1Field(order = 1)
    int age;

    @ASN1Field(order = 2)
    byte[] name;
}

Student student = new Student();
student.age = 11;
student.name = "hello world".getBytes();

byte[] asn1 = ASN1Encoder.encode(student);

Student decoded = ASN1Decoder.decode(asn1, Student.class);

```


```
[更多用法示例](https://github.com/shxhzhxx/asn1/blob/master/library/src/androidTest/java/com/shxhzhxx/asn1/ASN1UnitTest.java)
```

