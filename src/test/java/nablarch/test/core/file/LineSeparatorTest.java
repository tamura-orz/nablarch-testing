package nablarch.test.core.file;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@link LineSeparator}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class LineSeparatorTest {

    /** 各要素が、それぞれの改行コードを返却すること。 */
    @Test
    public void testToString() {
        assertThat(LineSeparator.CRLF.toString(), is("\r\n"));
        assertThat(LineSeparator.CR.toString(), is("\r"));
        assertThat(LineSeparator.LF.toString(), is("\n"));
        assertThat(LineSeparator.NONE.toString(), is(""));
    }

    /** 文字列表現に対応する改行コードが返却されること。 */
    @Test
    public void testEvaluate() {
        // 列挙型に対応する場合、その要素がもつ改行コードが返却されること。
        assertThat(LineSeparator.evaluate("CRLF"), is("\r\n"));
        assertThat(LineSeparator.evaluate("CR"), is("\r"));
        assertThat(LineSeparator.evaluate("LF"), is("\n"));
        assertThat(LineSeparator.evaluate("NONE"), is(""));
        // 列挙型に対応するものがない場合、その表現自身が改行コードとみなされること。
        assertThat(LineSeparator.evaluate(":"), is(":"));
    }
}
