package nablarch.test.core.log;

import org.junit.Test;

/**
 * @author T.Kawasaki
 */
public class NopLogWriterTest {

    private NopLogWriter target = new NopLogWriter();


    @Test
    public void testInitialize() throws Exception {
        target.initialize(null);
    }

    @Test
    public void testTerminate() throws Exception {
        target.terminate();
    }

    @Test
    public void testWrite() throws Exception {
        target.write(null);
    }
}
