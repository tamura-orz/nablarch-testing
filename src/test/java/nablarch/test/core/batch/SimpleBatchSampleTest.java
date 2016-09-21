package nablarch.test.core.batch;


import org.junit.Rule;
import org.junit.Test;

import test.support.SystemRepositoryResource;

/** {@link SimpleBatchSample}のテストクラス。 */
public class SimpleBatchSampleTest extends BatchRequestTestSupport {
	
    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource("unit-test.xml");


    /** テストを実行する。 */
    @Test
    public void testExecute() {
        execute("testExecute");
    }

}
