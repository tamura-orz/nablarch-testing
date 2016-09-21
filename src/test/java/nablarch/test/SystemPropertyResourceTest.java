package nablarch.test;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
/**
 * SystemPropertyResourceのテストケース。
 * 
 * @author Koichi Asano 
 *
 */
public class SystemPropertyResourceTest {

    @Test
    public void testBeforeAfter() throws Throwable {
        SystemPropertyResource resource = new SystemPropertyResource();
        
        System.setProperty("beforeValue", "before value");
        resource.before();
        
        System.setProperty("beforeValue", "xxxx");
        System.setProperty("val", "value");
        
        resource.after();
        
        assertEquals("上書きしたシステムプロパティは元に戻る。", "before value", System.getProperty("beforeValue"));
        assertNull("実行前になかったプロパティはなくなる。", System.getProperty("val"));
    }
}
