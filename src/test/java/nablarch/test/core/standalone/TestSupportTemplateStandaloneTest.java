package nablarch.test.core.standalone;

import nablarch.test.core.standalone.TestShot.TestShotAround;
import org.junit.Test;

/**
 * @author T.Kawasaki
 */
public class TestSupportTemplateStandaloneTest {

    /**
     * サブクラス以外から{@link nablarch.test.core.batch.BatchRequestTestSupport#execute()} を起動した場合、
     * 例外が発生すること。
     */
    @Test(expected = IllegalStateException.class)
    public void testConstructorNotSubclass() {
        StandaloneTestSupportTemplate target = new StandaloneTestSupportTemplate() {
            @Override
            protected TestShotAround createTestShotAround(Class<?> testClass) {
                return null;
            }
        };
        target.execute();
    }
}
