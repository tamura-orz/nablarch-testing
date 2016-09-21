package nablarch.test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.Matcher;
import org.junit.ComparisonFailure;

import static org.junit.Assert.assertThat;

/**
 * 例外発生を検証するクラス。
 *
 * @author T.Kawasaki
 */
public abstract class Trap {

    /** テスト失敗時のメッセージ */
    private final String msgOnFail;

    /** コンストラクタ。 */
    protected Trap() {
        this("");
    }

    /**
     * コンストラクタ。
     *
     * @param msgOnFail テスト失敗時のメッセージ
     */
    protected Trap(String msgOnFail) {
        this.msgOnFail = msgOnFail;
    }

    /**
     * 指定した例外を捕捉する。<br/>
     * 実際に発生した例外クラスは、指定した例外クラスまたはそのサブクラスでなければならない。
     *
     * @param expectedExceptionType 捕捉対象の例外クラス
     * @param <T>                   例外の型
     * @return 追加操作
     * @throws org.junit.ComparisonFailure 実際に発生した例外が、指定した例外クラスまたはそのサブクラスでない場合
     */
    public final <T extends Throwable> ExceptionAssertion<T> capture(Class<T> expectedExceptionType) {
        ExceptionComparator assignable = new ExceptionComparator(expectedExceptionType) {
            @Override
            boolean isExpected(Throwable actual) {
                return expectedType.isAssignableFrom(actual.getClass());
            }
        };
        return captureUsing(assignable);
    }

    /**
     * 指定した例外を捕捉する。<br/>
     * 実際に発生した例外クラスが指定した例外クラスと同じでなければならない。
     *
     * @param expectedExceptionType 捕捉対象の例外クラス
     * @param <T>                   例外の型
     * @return 追加操作
     * @throws org.junit.ComparisonFailure 実際に発生した例外が、指定した例外クラスでない場合
     */
    public final <T extends Throwable> ExceptionAssertion<T> captureStrictly(Class<T> expectedExceptionType) {
        ExceptionComparator equals = new ExceptionComparator(expectedExceptionType) {
            @Override
            boolean isExpected(Throwable actual) {
                return expectedType.equals(actual.getClass());
            }
        };
        return captureUsing(equals);
    }

    /**
     * {@link ExceptionComparator}を使って例外の捕捉を行う。
     *
     * @param comparator 例外を比較するロジックを持つ比較クラス
     * @param <T>        例外の型
     * @return 追加操作
     */
    private <T extends Throwable> ExceptionAssertion<T> captureUsing(ExceptionComparator comparator) {
        try {
            shouldFail();
        } catch (Throwable t) {
            if (!comparator.isExpected(t)) {
                t.printStackTrace();
                Assertion.failComparing(msgOnFail + " ; expected exception not occurred.", comparator.expectedType, t);
            }
            @SuppressWarnings("unchecked")
            T actual = (T) t;
            return new ExceptionAssertion<T>(msgOnFail, actual);
        }
        Assertion.fail(msgOnFail,
                       " ; expected exception [", comparator.expectedType, "]",
                       " not occurred. ");
        return null;     // dummy
    }


    /** 例外を比較するクラス。 */
    private abstract class ExceptionComparator {
        /** 期待する例外クラス */
        final Class<? extends Throwable> expectedType;

        /**
         * コンストラクタ。
         *
         * @param expectedType 期待する例外クラス
         */
        ExceptionComparator(Class<? extends Throwable> expectedType) {
            this.expectedType = expectedType;
        }

        /**
         * 期待する例外クラスであるか判定する。
         *
         * @param actual 実際の例外
         * @return 期待する例外である場合、真
         */
        abstract boolean isExpected(Throwable actual);
    }

    /**
     * 例外が発生するはずの処理を実行する。
     *
     * @throws Exception 例外
     */
    protected abstract void shouldFail() throws Exception;

    /**
     * 例外に対する表明クラス。
     *
     * @param <E> 例外の型
     */
    public static class ExceptionAssertion<E extends Throwable> {

        /** 実際に発生した例外 */
        private final E actual;

        /** 比較失敗時のメッセージ */
        private final String msgOnFail;

        /**
         * コンストラクタ。
         *
         * @param actual    実際に発生した例外
         */
        public ExceptionAssertion(E actual) {
            this("", actual);
        }

        /**
         * コンストラクタ。
         *
         * @param msgOnFail 比較失敗時のメッセージ
         * @param actual    実際に発生した例外
         */
        public ExceptionAssertion(String msgOnFail, E actual) {
            this.msgOnFail = msgOnFail;
            this.actual = actual;
        }

        public final ExceptionAssertion<E> whichMessage(Matcher<String> matcher) {
            assertThat(actual.getMessage(), matcher);
            return this;
        }

        /**
         * 発生した例外メッセージに指定した文言が含まれることを表明する。
         *
         * @param expected 期待した文言
         * @return 本インスタンス
         */
        public final ExceptionAssertion<E> whichMessageContains(String... expected) {
            String actualMessage = actual.getMessage();
            List<String> expectedWords = Arrays.asList(expected);
            for (String e : expectedWords) {
                if (!actualMessage.contains(e)) {
                    Assertion.failComparing(
                            msgOnFail + "; expected message not found. ",
                            expectedWords,
                            actualMessage);
                }
            }
            return this;
        }

        /**
         * ネストした例外に、期待した例外が含まれることを表明する。
         *
         * @param expected 期待した例外
         * @param <T>      期待する例外の型
         * @return 見つかった例外で新規に生成された本クラスのインスタンス
         */
        public final <T extends Throwable> ExceptionAssertion<T> whichContains(Class<T> expected) {
            List<Throwable> allCauses = new ArrayList<Throwable>();
            gatherNested(actual, allCauses);
            T t = find(expected, allCauses);
            if (t == null) {
                // 見つからない
                throw new ComparisonFailure(msgOnFail,
                                            expected.getSimpleName(),
                                            allCauses.toString());
            }
            return new ExceptionAssertion<T>(msgOnFail, t);
        }

        /**
         * 指定した例外から、ネストした例外を抽出する。
         *
         * @param t           元の例外
         * @param accumulator 蓄積する入れ物
         */
        private void gatherNested(Throwable t, List<Throwable> accumulator) {
            Throwable cause = t.getCause();
            if (cause == null) {
                return;
            }
            accumulator.add(cause);
            gatherNested(cause, accumulator);
        }

        /**
         * Listから指定した型の例外を検索する。
         *
         * @param expected 期待する例外クラス
         * @param causes   実際の例外
         * @param <T>      期待する例外の方
         * @return 発見した例外
         */
        @SuppressWarnings("unchecked")
        private <T extends Throwable> T find(final Class<T> expected, List<Throwable> causes) {
            for (Throwable e : causes) {
                if (e.getClass().equals(expected)) {
                    return (T) e;
                }
            }
            return null;
        }

        /**
         * 発生した例外メッセージが、指定した文言から開始することを表明する。
         *
         * @param expected 期待した文言
         * @return 本インスタンス
         */
        public final ExceptionAssertion<E> whichMessageStartsWith(String expected) {
            String actualMessage = actual.getMessage();
            if (!actualMessage.startsWith(expected)) {
                Assertion.failComparing(
                        msgOnFail + "; expected message not found. ", expected, actualMessage);
            }
            return this;
        }

        /**
         * 発生した例外メッセージが、指定した文言で終了することを表明する。
         *
         * @param expected 期待した文言
         * @return 本インスタンス
         */
        public final ExceptionAssertion<E> whichMessageEndsWith(String expected) {
            String actualMessage = actual.getMessage();
            if (!actualMessage.endsWith(expected)) {
                Assertion.failComparing(
                        msgOnFail + "; expected message not found. ", expected, actualMessage);
            }
            return this;
        }

        /**
         * 発生した例外メッセージが指定した文言通りであることを表明する。
         *
         * @param expectedMessage 期待する文言
         * @return 本インスタンス
         */
        public final ExceptionAssertion<E> whichMessageIs(String expectedMessage) {
            String actualMessage = actual.getMessage();
            Assertion.assertEquals(msgOnFail, expectedMessage, actualMessage);
            return this;
        }

        /**
         * 発生した例外のネストした例外の型が等しいことを表明する。
         *
         * @param expectedNested 期待したネストした例外クラス
         * @return 本インスタンス
         */
        public final ExceptionAssertion<E> whichCauseIs(Class<? extends Throwable> expectedNested) {
            Throwable cause = actual.getCause();
            Class<? extends Throwable> clazz = cause == null ? null : cause.getClass();
            Assertion.assertEquals(msgOnFail, expectedNested, clazz);
            return this;
        }

        /**
         * スタックトレースを出力する。
         *
         * @return 本インスタンス
         */
        public final ExceptionAssertion<E> printStackTrace() {
            System.err.println("----------  actual exception is ------------");
            actual.printStackTrace();
            System.err.println("--------------------------------------------");
            return this;
        }

        /**
         * 実際に発生した例外を取得する。
         *
         * @return 発生した例外
         */
        public final E getActual() {
            return actual;
        }
    }
}
