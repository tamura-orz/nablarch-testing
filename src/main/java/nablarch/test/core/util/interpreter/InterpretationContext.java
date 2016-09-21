package nablarch.test.core.util.interpreter;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

import static nablarch.core.util.Builder.concat;

import nablarch.core.util.annotation.Published;

/**
 * 解釈コンテキストクラス。<br/>
 * 以下の要素を保持する。
 * <ul>
 * <li>解釈対象となる値</li>
 * <li>解釈クラス({@link TestDataInterpreter})を格納したキュー</li>
 * </ul>
 * 具体的な使用方法は、{@link TestDataInterpreter}を参照。
 *
 * @author T.Kawasaki
 * @see TestDataInterpreter
 */
@Published(tag = "architect")
public class InterpretationContext {

    /** 解釈対象の値 */
    private String value;

    /** 解釈クラスを格納するキュー */
    private Queue<TestDataInterpreter> interpreters;

    /**
     * コンストラクタ
     *
     * @param value        解釈対象となる値
     * @param interpreters 解釈クラス
     */
    public InterpretationContext(String value, TestDataInterpreter... interpreters) {
        this(value, Arrays.asList(interpreters));
    }

    /**
     * コンストラクタ
     *
     * @param value        解釈対象となる値
     * @param interpreters 解釈クラス
     */
    public InterpretationContext(String value, Collection<TestDataInterpreter> interpreters) {
        this.value = value;
        this.interpreters = new LinkedList<TestDataInterpreter>(interpreters);
    }

    /**
     * 解釈対象の値を取得する。
     *
     * @return 解釈対象の値
     */
    public String getValue() {
        return value;
    }

    /**
     * 解釈対象となる値を設定する。<br/>
     * {@link TestDataInterpreter}にて、解釈の過程で解釈対象となる値を変更したい場合は
     * 本メソッドを使用するとよい。
     *
     * @param newValue 新しい値
     */
    public void setValue(String newValue) {
        this.value = newValue;
    }

    /**
     * 次の解釈クラスを起動する。<br/>
     * 解釈クラスが存在しない場合、すなわちどの解釈クラスも解釈できなかった場合は、
     * 解釈対象の値がそのまま返却される。
     *
     * @return 次の解釈クラスの処理結果
     */
    public String invokeNext() {
        if (interpreters.isEmpty()) {
            return value;  // 最後まで処理したら元のデータをそのまま返却
        }
        TestDataInterpreter next = interpreters.remove();
        try {
            return next.interpret(this);
        } catch (InterpretationFailedException e) {
            throw e;
        } catch (RuntimeException e) {
            throw new InterpretationFailedException(next, value, e);
        }
    }

    /** 解釈失敗時の例外 */
    static final class InterpretationFailedException extends RuntimeException {

        /**
         * コンストラクタ
         *
         * @param interpreter 解釈に失敗したクラス
         * @param value       解釈に失敗した値
         * @param cause       原因となった例外
         */
        private InterpretationFailedException(TestDataInterpreter interpreter, String value, Throwable cause) {
            super(getMsg(interpreter, value), cause);
        }

        /**
         * メッセージを取得する。
         *
         * @param interpreter 解釈に失敗したクラス
         * @param value       解釈に失敗した値
         * @return メッセージ
         */
        private static String getMsg(TestDataInterpreter interpreter, String value) {
            return concat(
                    "interpretation failed. value=[", value, "] ",
                    "interpreter=[", interpreter.getClass().getName(), "]");
        }
    }
}
