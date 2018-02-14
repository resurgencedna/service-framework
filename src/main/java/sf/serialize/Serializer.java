package sf.serialize;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Serializer {

    public static byte[] serialize(Object obj) {
        byte[] bytes = null;
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(obj);
            oos.flush();
            bytes = bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    public static Object deserialize(byte[] bytes) {
        return Serializer.deserialize(bytes, 0, bytes.length);
    }

    public static Object deserialize(byte[] bytes, int offset, int length) {
        if (bytes == null) {
            return null;
        }
        try {
            ByteArrayInputStream bis = new ByteArrayInputStream(bytes, offset, length);
            ObjectInputStream ois = new ObjectInputStream(bis);
            Object obj = ois.readObject();
            bis.close();
            ois.close();
            return obj;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

}
