package nablarch.test.core.util.interpreter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import nablarch.core.util.BinaryUtil;

import org.junit.Assert;
import org.junit.Test;

public class BinaryFileInterpreterTest {

    /** テスト対象クラス */
    private BinaryFileInterpreter target = new BinaryFileInterpreter("src/test/java/nablarch/test/core/util/interpreter/");

    /**
     * ファイルが正常に読み込めること。
     */
    @Test
    public void testOk() {

        InterpretationContext ctx = new InterpretationContext(
                "${binaryFile:testdata.txt}", target);

        String expected = "あいうえお\r\nかきくけこ\r\nさしすせそ";

        assertThat("HexStringの比較", ctx.invokeNext(),
                is(BinaryUtil.convertToHexString(expected.getBytes())));
    }

    /**
     * ファイルパスを表す記法ではない場合。
     */
    @Test
    public void testNotApplicable() {
        InterpretationContext ctx = new InterpretationContext("${hoge}", target);
        assertEquals("${hoge}", ctx.invokeNext());
    }

    /**
     * ファイルが見つからない場合。
     */
    @Test
    public void testFileNotFound() {
        InterpretationContext ctx = new InterpretationContext("${binaryFile:hoge}", target);
        try {
            ctx.invokeNext();
            Assert.fail();
        } catch (RuntimeException e) {
            assertThat(
                    e.getMessage(),
                    is("interpretation failed. value=[${binaryFile:hoge}] interpreter=[nablarch.test.core.util.interpreter.BinaryFileInterpreter]"));
        }

    }
}
