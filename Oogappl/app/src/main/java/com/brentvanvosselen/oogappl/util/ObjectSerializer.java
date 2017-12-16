package com.brentvanvosselen.oogappl.util;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectSerializer {
    public static <T extends Serializable> String serialize2(T obj) {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream;
        try {
            objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
            objectOutputStream.writeObject(obj);
            objectOutputStream.close();
            return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
        } catch (IOException e) {
            throw new Error(e);
        }
    }


    public static <T extends Serializable> T deserialize2(String data) {
        try {
            byte[] dataBytes = Base64.decode(data, Base64.DEFAULT);
            final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBytes);
            final ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

            @SuppressWarnings({"unchecked"})
            final T obj = (T) objectInputStream.readObject();

            objectInputStream.close();
            return obj;
        } catch (IOException e) {
            throw new Error(e);
        } catch (ClassNotFoundException e) {
            throw new Error(e);
        }
    }

}
