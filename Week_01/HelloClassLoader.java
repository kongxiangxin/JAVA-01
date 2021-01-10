import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;

public class HelloClassLoader extends ClassLoader{
    public static void main(String[] args) throws Exception{
        HelloClassLoader t = new HelloClassLoader();
        Class<?> helloClz = t.findClass("Hello");
        Object hello = helloClz.newInstance();
        Method method = helloClz.getMethod("hello");
        method.invoke(hello);
    }

    @Override
    protected Class<?> findClass(String name) throws ClassNotFoundException {
        String resourceName = name + ".xlass";
        try {
            byte[] bytes = loadResource(resourceName);
            decode(bytes);
            return defineClass(name, bytes, 0, bytes.length);
        } catch (IOException e) {
            throw new ClassNotFoundException("xclass resource file not found", e);
        }
    }

    private byte[] loadResource(String resource) throws IOException {
        InputStream resourceAsStream = getClass().getResourceAsStream(resource);
        if(resourceAsStream == null){
            throw new IOException("resource " + resource + " not found");
        }
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        int nextValue;
        while ((nextValue = resourceAsStream.read()) != -1) {
            byteArrayOutputStream.write(nextValue);
        }
        resourceAsStream.close();

        return byteArrayOutputStream.toByteArray();
    }

    private void decode(byte[] bytes){
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte)(255 - bytes[i]);
        }
    }
}