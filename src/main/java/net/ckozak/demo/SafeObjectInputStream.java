package net.ckozak.demo;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectStreamClass;

/**
 * {@link SafeObjectInputStream} provides simple protection against deserialization of
 * commons-collections types and disallows proxy class deserialization.
 * <b>NOT</b> a solution for production cases, this implementation serves as a
 * simple demonstration.
 */
public class SafeObjectInputStream extends ObjectInputStream {

    public SafeObjectInputStream(InputStream in) throws IOException {
        super(in);
    }

    @Override
    protected Class<?> resolveProxyClass(String[] interfaces) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected Class<?> resolveClass(ObjectStreamClass desc)
            throws IOException, ClassNotFoundException {
        if (desc.getName().startsWith("org.apache.commons")) {
            throw new UnsupportedOperationException();
        }
        return super.resolveClass(desc);
    }
}
