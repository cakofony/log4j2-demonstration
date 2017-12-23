package net.ckozak.demo;

import javassist.bytecode.ByteArray;
import org.apache.logging.log4j.util.SortedArrayStringMap;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;

public class DeserializationTests {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void testSafeDeserialization() throws Exception {
        // Our "safe" object stream should recognize and disallow the gadget
        byte[] data = serialize(Payloads.createPayload(getDefaultTestCmd()));
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             SafeObjectInputStream ois = new SafeObjectInputStream(bais)) {
            expectedException.expect(UnsupportedOperationException.class);
            ois.readObject();
        }
    }

    @Test
    public void testSafeDeserializationWithSortedStringMap() throws Exception {
        // When wrapped in SortedArrayStringMap, the gadget should still be detected and disallowed.
        // but... here's a calculator!
        SortedArrayStringMap map = new SortedArrayStringMap();
        map.putValue("key", Payloads.createPayload(getDefaultTestCmd()));
        byte[] data = serialize(map);
        try (ByteArrayInputStream bais = new ByteArrayInputStream(data);
             SafeObjectInputStream ois = new SafeObjectInputStream(bais)) {
            expectedException.expect(UnsupportedOperationException.class);
            ois.readObject();
        }
    }

    private byte[] serialize(Object object) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(object);
        }
        return baos.toByteArray();
    }

    // Various calculator applications
    private static String getDefaultTestCmd() {
        return getFirstExistingFile(
                "C:\\Windows\\System32\\calc.exe",
                "/Applications/Calculator.app/Contents/MacOS/Calculator",
                "/usr/bin/gnome-calculator",
                "/usr/bin/kcalc"
        );
    }

    private static String getFirstExistingFile(String ... files) {
        for (String path : files) {
            if (new File(path).exists()) {
                return path;
            }
        }
        throw new UnsupportedOperationException("no known test executable");
    }
}
