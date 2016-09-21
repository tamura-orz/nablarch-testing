package nablarch.test;

import org.hamcrest.BaseMatcher;

import nablarch.core.util.StringUtil;

/**
 * @author T.Kawasaki
 */
public final class StringMatcher {

    /** プライベートコンストラクタ。 */
    private StringMatcher() {
    }

    /**
     * 期待する文字列から始まることを表明する{@link org.hamcrest.Matcher}実装を返却する。
     *
     * @param expected 期待値（null,空文字は許容されない）
     * @return {@link nablarch.test.StringMatcher.StartsWith}インスタンス
     */
    public static BaseMatcher<String> startsWith(String expected) {
        return new StartsWith(expected);
    }

    /**
     * 実際の文字列が、期待した文字列から始まることを表明するための{@link org.hamcrest.Matcher}実装クラス。<br/>
     * <pre><code>
     * assertThat("abc", startsWith("ab")); //--> success
     * assertThat("abc", startsWith("aa")); //--> fail
     * </code></pre>
     */
    static class StartsWith extends AbstractStringMatcher {

        /**
         * コンストラクタ
         *
         * @param expected 期待値（null,空文字は許容されない）
         */
        StartsWith(String expected) {
            super(expected);
            if (StringUtil.isNullOrEmpty(expected)) {
                throw new IllegalArgumentException("argument must not be null");
            }
        }

        /** {@inheritDoc} */
        @Override
        protected boolean doMatches(String actual) {
            return actual.startsWith(getExpected());
        }
    }

    /**
     * 期待する文字列で終了することを表明する{@link org.hamcrest.Matcher}実装を返却する。
     *
     * @param expected 期待値（null,空文字は許容されない）
     * @return {@link nablarch.test.StringMatcher.StartsWith}インスタンス
     */
    public static BaseMatcher<String> endsWith(String expected) {
        return new EndsWith(expected);
    }

    /**
     * 実際の文字列が、期待した文字列で終わることを表明するための{@link org.hamcrest.Matcher}実装クラス。<br/>
     * <pre><code>
     * assertThat("abc", endsWith("bc")); //--> success
     * assertThat("abc", endsWith("cc")); //--> fail
     * </code></pre>
     */
    static class EndsWith extends AbstractStringMatcher {

        /**
         * コンストラクタ
         *
         * @param expected 期待値
         */
        EndsWith(String expected) {
            super(expected);
            if (StringUtil.isNullOrEmpty(expected)) {
                throw new IllegalArgumentException("argument must not be null");
            }
        }

        /** {@inheritDoc} */
        @Override
        protected boolean doMatches(String actual) {
            return actual.endsWith(getExpected());
        }
    }


}
