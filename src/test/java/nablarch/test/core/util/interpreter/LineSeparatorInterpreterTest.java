package nablarch.test.core.util.interpreter;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * {@link LineSeparatorInterpreter}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class LineSeparatorInterpreterTest {

    /** テスト対象 */
    private LineSeparatorInterpreter target = new LineSeparatorInterpreter();

    /**
     * 前準備。
     * 文字列"\r"をCR(0x0D)に置き換える設定を行う。
     */
    @Before
    public void setUp() {
        target.setLineSeparator("CR");
        target.setMatchPattern("\\\\r");
    }

    /** "\r"はCRに置き換わること。 */
    @Test
    public void testConvertBackR() {
        test("\\r", "\r");
        test("aaa\\r\nbbb", "aaa\r\nbbb");
    }

    /** CRそのもの(0x0D)は置き換わらないこと。*/
    @Test
    public void testDoNotConvertCR() {
        test("a\rb", "a\rb");
    }

    /** "\r"が含まれない場合、元の文字列が返却されること。 */
    @Test
    public void testDoNotConvert() {
        test("", "");
        test(null, null);
        test("abc", "abc");
    }

    /**
     * テストを実行する。
     *
     * @param input    入力値
     * @param expected 期待する値
     */
    private void test(String input, String expected) {
        InterpretationContext ctx = new InterpretationContext(input, target);
        assertThat(ctx.invokeNext(),
                   is(expected));
    }
}