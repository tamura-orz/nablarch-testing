package nablarch.test;

import org.junit.Test;

import static nablarch.test.StringMatcher.*;
import static org.junit.Assert.*;

/**
 * {@link nablarch.test.StartsWithTest}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class StartsWithTest {

    /**
     * 実際の値がstartsWithメソッドの引数（期待値）から始まる場合、真が返却されること。
     */
    @Test
    public void testStartsWith() {
        String actualString = "abc";
        String[] shouldMatch = {"a", "ab", "abc"};
        for (String e : shouldMatch) {
            assertTrue(e, startsWith(e).matches(actualString));
        }

        String[] shouldNotMatch = {"abcd", "aabc", "abd"};
        for (String e : shouldNotMatch) {
            assertFalse(e, startsWith(e).matches(actualString));
        }
    }

    /** 引数が空文字の場合、例外が発生すること */
    @Test(expected = IllegalArgumentException.class)
    public void testStartsWithEmptyString() {
        startsWith("");
    }

    /** 引数が<@code>null</@code>の場合、例外が発生すること */
    @Test(expected = IllegalArgumentException.class)
    public void testStartsWithNull() {
        startsWith(null);
    }

    /**
     * 実際の値がendsWithメソッドの引数（期待値）で終わる場合、真が返却されること。
     */
    @Test
    public void testEndsWith() {
        String actualString = "abc";
        String[] shouldMatch = {"c", "bc", "abc"};
        for (String e : shouldMatch) {
            assertTrue(e, endsWith(e).matches(actualString));
        }

        String[] shouldNotMatch = {"0abc", "abcc", "abd"};
        for (String e : shouldNotMatch) {
            assertFalse(e, endsWith(e).matches(actualString));
        }
    }

    /** 引数が空文字の場合、例外が発生すること */
    @Test(expected = IllegalArgumentException.class)
    public void testEndsWithEmptyString() {
        endsWith("");
    }

    /** 引数が<@code>null</@code>の場合、例外が発生すること */
    @Test(expected = IllegalArgumentException.class)
    public void testEndsWithNull() {
        endsWith(null);
    }

}
