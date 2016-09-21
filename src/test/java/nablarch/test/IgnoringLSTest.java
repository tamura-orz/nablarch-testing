package nablarch.test;

import nablarch.core.util.Builder;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertThat;

/**
 * {@link IgnoringLS}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class IgnoringLSTest {

    /** 改行コードのバリエーション */
    private final List<String> lineSeparators = Arrays.asList("\r", "\n", "\r\n");
    /** 文書テンプレート */
    private final List<String> template = Arrays.asList(
                "私はその人を常に先生と呼んでいた。",
                "だからここでもただ先生と書くだけで本名は打ち明けない。",
                "これは世間を憚かる遠慮というよりも、その方が私にとって自然だからである。",
                "私はその人の記憶を呼び起すごとに、すぐ「先生」といいたくなる。",
                "筆を執っても心持は同じ事である。",
                "よそよそしい頭文字などはとても使う気にならない");

    /**
     * {@link IgnoringLS#equals(String)}のテストケース。<br/>
     * 全改行コードの組み合わせにおいて、改行コードが異なっても等価とみなされること。
     */
    @Test
    public void testEquals()  {

        for (String expectedLS : lineSeparators) {
            String expected = Builder.join(template, expectedLS);
            for (String actualLS : lineSeparators) {
                String actual = Builder.join(template, actualLS);
                assertThat(expected, IgnoringLS.equals(actual));
            }
        }
    }

    /**
     * {@link IgnoringLS#equals(String)}のテストケース。<br/>
     * null同士が等価と判断されること。
     */
    @Test
    public void testEqualsNull() {
        assertThat(null, IgnoringLS.equals(null));
    }

    /**
     * {@link IgnoringLS#equals(String)}のテストケース。<br/>
     * 改行コード以外で等しくない場合、例外が発生すること。
     */
    @Test(expected = AssertionError.class)
    public void testEqualsFail() {
        assertThat("aaa\nbbb", IgnoringLS.equals("aa\nabb"));
    }

    /**
     * {@link IgnoringLS#equals(String)}のテストケース。<br/>
     * nullとnull以外を比較した場合に、例外が発生すること。
     */
    @Test(expected = AssertionError.class)
    public void testEqualsFailExpectedNull() {
        assertThat("aaa\nbbb", IgnoringLS.equals(null));
    }

    /**
     * {@link IgnoringLS#equals(String)}のテストケース。<br/>
     * nullとnull以外を比較した場合に、例外が発生すること。
     */
    @Test(expected = AssertionError.class)
    public void testEqualsFailactualNull() {
        assertThat(null, IgnoringLS.equals("hoge"));
    }


    /**
     * {@link IgnoringLS#contains(String)}のテストケース。<br/>
     * 全改行コードの組み合わせて、改行コードが異なっても
     * 期待値が実際の値に包含されていると判定されること。
     */
    @Test
    public void testContains() {
         final List<String> actualTemplate = Arrays.asList(
                "これは世間を憚かる遠慮というよりも、その方が私にとって自然だからである。",
                "私はその人の記憶を呼び起すごとに、すぐ「先生」といいたくなる。",
                "筆を執っても心持は同じ事である。");

        // 改行コードが異なっても等価とみなされること
        for (String expectedLS : lineSeparators) {
            String expected = Builder.join(template, expectedLS);
            for (String actualLS : lineSeparators) {
                String actual = Builder.join(actualTemplate, actualLS);
                assertThat(expected, IgnoringLS.contains(actual));
            }
        }
    }

    /**
     * {@link IgnoringLS#contains(String)}のテストケース。<br/>
     * 改行コード以外で包含関係がない場合、例外が発生すること。
     */
    @Test(expected = AssertionError.class)
    public void testContainsFail() {
        assertThat("aaa\nbbb", IgnoringLS.contains("aba"));
    }


    /**
     * {@link IgnoringLS#contains(String)}のテストケース。<br/>
     * nullとnull以外を比較した場合に、例外が発生すること。
     */
    @Test(expected = AssertionError.class)
    public void testContainsFailExpectedNull() {
        assertThat("aaa\nbbb", IgnoringLS.contains(null));
    }

    /**
     * {@link IgnoringLS#contains(String)}のテストケース。<br/>
     * nullとnull以外を比較した場合に、例外が発生すること。
     */
    @Test(expected = AssertionError.class)
    public void testContainsFailactualNull() {
        assertThat(null, IgnoringLS.contains("hoge"));
    }
}
