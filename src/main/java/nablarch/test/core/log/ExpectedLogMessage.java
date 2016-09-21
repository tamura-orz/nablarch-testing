package nablarch.test.core.log;

import nablarch.core.log.basic.LogContext;
import nablarch.core.log.basic.LogLevel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static nablarch.core.util.Builder.concat;

/**
 * 期待するログメッセージを表すクラス。<br/>
 * ログが想定通り出力されることを確認する為に使用する。
 *
 * @author T.Kawasaki
 * @see LogVerifier
 */
public class ExpectedLogMessage {

    /** 期待するメッセージ */
    private final List<String> expectedMessages;

    /** 期待するログレベル */
    private final LogLevel expectedLogLevel;

    /**
     * コンストラクタ。<br/>
     *
     * @param logLevel 期待するログレベル
     * @param messages 期待するメッセージ
     */
    public ExpectedLogMessage(LogLevel logLevel, List<String> messages) {
        expectedLogLevel = logLevel;
        expectedMessages = messages;
    }

    /**
     * コンストラクタ。<br/>
     *
     * @param logLevel 期待するログレベル
     * @param messages 期待するメッセージ
     */
    ExpectedLogMessage(LogLevel logLevel, String... messages) {
        this(logLevel, Arrays.asList(messages));
    }

    /**
     * 実際のログとマッチするか判定する。<br/>
     * 以下の条件全てを満たす場合に真と判定する。
     * <ul>
     * <li>ログレベルが等しい。</li>
     * <li>実際のメッセージに、期待するメッセージが含まれている。</li>
     * </ul>
     *
     * @param actual 実際のログ
     * @return 判定結果
     */
    public boolean matches(LogContext actual) {

        // ログレベルが等しいか
        LogLevel actualLogLevel = actual.getLevel();
        if (!expectedLogLevel.equals(actualLogLevel)) {
            return false;
        }

        // ログの文言
        List<String> actualMessages = new ArrayList<String>();
        actualMessages.add(actual.getMessage());
        // 例外のエラーメッセージ
        extractMessages(actual.getError(), actualMessages);

        String allActualMessage = actualMessages.toString();
        return contains(allActualMessage);
    }

    /**
     * 例外またはエラーから、メッセージを抽出する。
     *
     * @param t           例外またはエラー
     * @param accumulator 結果を収集するリスト
     */
    private void extractMessages(Throwable t, List<String> accumulator) {
        if (t == null) {
            return;
        }
        accumulator.add(t.getMessage());
        Throwable next = t.getCause();
        if (next == null || next == t) {
            return;
        }
        extractMessages(next, accumulator); // tail recursion
    }

    /**
     * 期待するメッセージ全てが、実際のメッセージに含まれているか判定する。
     *
     * @param actual 実際のメッセージ
     * @return 期待するメッセージ全てが含まれていた場合に真
     */
    private boolean contains(String actual) {
        for (String e : expectedMessages) {
            if (!actual.contains(e)) {
                return false;
            }
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String toString() {
        return concat("Log level=[", expectedLogLevel, "] messages=", expectedMessages);
    }

}

