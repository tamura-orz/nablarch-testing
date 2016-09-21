package nablarch.test.core.log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.log.basic.LogContext;
import nablarch.core.log.basic.LogLevel;
import nablarch.core.log.basic.LogWriter;
import nablarch.core.log.basic.ObjectSettings;
import nablarch.core.util.Builder;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.test.Assertion;
import nablarch.test.NablarchTestUtils;
import static nablarch.core.util.Builder.concat;

/**
 * ログ出力結果を検証するためのログ出力クラス。<br/>
 * <p/>
 * <ul>
 * <li>テスト実行前に期待するログメッセージ(ExpectedLogMessage)一覧を設定する。
 * （{@link #setExpectedLogMessages(List)} ）</li>
 * <li></li>テスト実行中、本クラスはLogWriterの実装クラスとしてログ出力を受け取る。</li>
 * <li>検証メソッド（{@link #verify(String)}）実行時点で、期待するログメッセージ一覧のうち、
 * 実際にはログ出力されなかったものがあれば、例外が発生する。</li>
 * </ul>
 *
 * @author T.Kawasaki
 */
public class LogVerifier implements LogWriter {

    /** ログレベルを取得するためのキー */
    private static final String LOG_LEVEL_KEY = "logLevel";

    /** メッセージを取得するためのキー */
    private static final String MESSAGE_PREFIX_KEY = "message";

    /** 必須のキー一覧 */
    private static final Set<String> REQUIRED_KEYS =
            NablarchTestUtils.asSet(LOG_LEVEL_KEY, MESSAGE_PREFIX_KEY + "1");

    /** 期待するログメッセージ一覧 */
    private static List<ExpectedLogMessage> expectedLogMessages
            = Collections.synchronizedList(new ArrayList<ExpectedLogMessage>());

    /**
     * 期待するログメッセージを設定する。
     *
     * @param expected 期待するログメッセージ
     */
    @Published
    public static void setExpectedLogMessages(List<Map<String, String>> expected) {
        expectedLogMessages.clear();
        for (Map<String, String> e : expected) {
            add(createExpectedLogMessage(e));
        }
    }

    /**
     * 期待するログメッセージを生成する。
     *
     * @param src 元となるデータ
     * @return 期待するログメッセージ
     */
    private static ExpectedLogMessage createExpectedLogMessage(Map<String, String> src) {

        // 必須項目の存在チェック
        if (!src.keySet().containsAll(REQUIRED_KEYS)) {
            throw new IllegalArgumentException(concat(
                    "argument must contain required keys.",
                    " required=", REQUIRED_KEYS,
                    " argument=", src));
        }

        // 期待するログレベル
        LogLevel logLevel = LogLevel.valueOf(src.get(LOG_LEVEL_KEY));

        // 期待するメッセージ
        List<String> msgs = new ArrayList<String>();
        // message1, message2, message3... と続ける
        for (int suffix = 1;; suffix++) {
            String msg = src.get(MESSAGE_PREFIX_KEY + suffix);
            if (StringUtil.isNullOrEmpty(msg)) {
                break;   // 無くなったら終了
            }
            msgs.add(msg);
        }
        return new ExpectedLogMessage(logLevel, msgs);
    }

    /**
     * 期待するログメッセージを追加する。
     *
     * @param expected 期待するログメッセージ
     */
    static void add(ExpectedLogMessage expected) {
        logDebug("adding expected log message. [", expected, "]");
        expectedLogMessages.add(expected);
    }

    /**
     * 期待したログが全て出力されたかどうか検証する。<br/>
     *
     * @param msgOnFail 検証失敗時のメッセージ
     * @throws AssertionError 期待するログメッセージのうち、出力されなかったものが存在する場合
     */
    @Published
    public static void verify(String msgOnFail) throws AssertionError {
        if (expectedLogMessages.isEmpty()) {
            logDebug("verifying log successful.");
            return;    // 全てのログが出現済み
        }

        // 検証失敗
        String separator = StringUtil.isNullOrEmpty(msgOnFail) ? "" : " ; ";
        Assertion.fail(msgOnFail,
                separator,
                "following log message(s) expected. but not found. ",
                expectedLogMessages);
    }

    /** 期待するログメッセージをクリアする。 */
    public static void clear() {
        expectedLogMessages.clear();
    }

    /**
     * {@inheritDoc}
     * 本クラスでは何もしない。
     */
    public void initialize(ObjectSettings settings) {
        // NOP
    }

    /**
     * {@inheritDoc}
     * 本クラスでは何もしない。
     */
    public void terminate() {
        // NOP
    }

    /**
     * {@inheritDoc}
     * <p/>
     * 期待するログメッセージと実際のログメッセージを突き合わせる。
     * マッチした場合は、期待するログ一覧から削除される。
     */
    public void write(LogContext actual) {
        if (expectedLogMessages.isEmpty()) {
            return;
        }

        synchronized (expectedLogMessages) {
            Iterator<ExpectedLogMessage> iterator = expectedLogMessages.iterator();
            while (iterator.hasNext()) {
                ExpectedLogMessage expected = iterator.next();
                if (expected.matches(actual)) {
                    logDebug("expected log is detected.[", expected, "]");
                    iterator.remove(); // 出現したら削除
                    return;
                }
            }
        }
    }

    /**
     * ロガー。<br/>
     * 静的初期化子で初期化すると、ロガー初期化中にロガーを初期化してしまうので
     * 怠惰な初期化を行う。
     */
    private static Logger logger = null;

    /** ロックオブジェクト */
    private static final Object MUTEX = new Object();

    /**
     * デバッグログを出力する。<br/>
     *
     * @param msg メッセージ
     */
    private static void logDebug(Object... msg) {
        if (logger != null) {
            logger.logDebug(Builder.concat(msg));
            return;
        }
        synchronized (MUTEX) {
            logger = LoggerManager.get(LogVerifier.class);
        }
        logDebug(msg);
    }
}
