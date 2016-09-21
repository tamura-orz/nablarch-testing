package nablarch.test.core.file;

import org.junit.Test;

/**
 * {@link DataFile}ノテストクラス。
 *
 * @author T.Kawasaki
 */
public class DataFileTest {

    /** 不正なディレクティブが指定された場合、例外が発生すること。 */
    @Test(expected = IllegalArgumentException.class)
    public void testConvertValueWithInvalidDirective() {
        DataFile target = new FixedLengthFile("hoge");
        target.setDirective("invalid-directive", "should be denied.");
    }


}
