package nablarch.test.core.log;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Before;
import org.junit.Test;

import nablarch.core.log.basic.LogLevel;
import static nablarch.test.Assertion.fail;
import static org.junit.Assert.assertEquals;

/**
 * {@link LogVerifier}のテストケース
 *
 * @author T.Kawasaki
 */
public class LogVerifierTest {

    /** テスト対象 */
    private LogVerifier target = new LogVerifier();

    @Before
    public void clear() {
        LogVerifier.clear();
    }

    /** 期待するメッセージ全てが見つかるケース。 */
    @Test
    public void testAllExpectedMessageFound() {

        ExpectedLogMessage exp1 = new ExpectedLogMessage(LogLevel.ERROR, "えらー");
        ExpectedLogMessage exp2 = new ExpectedLogMessage(LogLevel.INFO, "いん", "ふぉ");
        LogVerifier.add(exp1);
        LogVerifier.add(exp2);


        target.write(new MockLogContext(LogLevel.ERROR, "えらー"));
        target.write(new MockLogContext(LogLevel.INFO, "いんふぉ"));

        LogVerifier.verify("");

        // カバレッジ用に最後に呼んでおく。
        target.terminate();
    }

    /**
     * 期待するメッセージのうち一部が見つからないケース。<br/>
     * 例外が発生し、見つからなかったログが例外メッセージに含まれること。
     */
    @Test
    public void testSomeMessageNotFound() {
        ExpectedLogMessage exp1 = new ExpectedLogMessage(LogLevel.ERROR, "えらー");
        ExpectedLogMessage exp2 = new ExpectedLogMessage(LogLevel.INFO, "いん", "ふぉ");
        LogVerifier.add(exp1);
        LogVerifier.add(exp2);

        target.write(new MockLogContext(LogLevel.ERROR, "えらー"));

        try {
            LogVerifier.verify("additional message.");
            fail("期待した例外が発生しませんでした。");
        } catch (AssertionError e) {
            assertEquals("additional message. ; " +
                    "following log message(s) expected. " +
                    "but not found. [Log level=[INFO] messages=[いん, ふぉ]]",
                    e.getMessage());
        }
    }

    /**
     * 期待するメッセージ全てが見つからないケース。<br/>
     * 例外が発生し、見つからなかったログが例外メッセージに含まれること。
     */
    @Test
    public void testAllMessageNotFound() {
        ExpectedLogMessage exp1 = new ExpectedLogMessage(LogLevel.ERROR, "えらー");
        ExpectedLogMessage exp2 = new ExpectedLogMessage(LogLevel.INFO, "いんふぉ");
        LogVerifier.add(exp1);
        LogVerifier.add(exp2);

        try {
            LogVerifier.verify("");
            fail("期待した例外が発生しませんでした。");
        } catch (AssertionError e) {
            assertEquals("following log message(s) expected. but not found. "
                    + "[Log level=[ERROR] messages=[えらー], "
                    + "Log level=[INFO] messages=[いんふぉ]]", e.getMessage());
        }
    }

    /**
     * 期待するログメッセージの設定時に、必須カラムが含まれていない場合、
     * 例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetExpectedLogMessagesFail() {
        LogVerifier.setExpectedLogMessages(
                Arrays.asList(Collections.<String, String>emptyMap()));
    }
}
