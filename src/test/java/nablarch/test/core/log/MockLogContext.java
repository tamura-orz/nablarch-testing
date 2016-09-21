package nablarch.test.core.log;

import nablarch.core.log.basic.LogContext;
import nablarch.core.log.basic.LogLevel;

/**
 * テスト用ログコンテキスト継承クラス
 *
 * @author T.Kawasaki
 */
public class MockLogContext extends LogContext {
    /**
     * コンストラクタ。
     *
     * @param level      {@link nablarch.core.log.basic.LogLevel}
     * @param message    メッセージ
     * @param error      エラー情報(nullでも可)
     * @param options    オプション情報(nullでも可)
     */
    public MockLogContext(LogLevel level, String message, Throwable error, Object... options) {
        super("test", level, message, error, options);
    }

    /**
     * コンストラクタ。
     *
     * @param level      {@link nablarch.core.log.basic.LogLevel}
     * @param message    メッセージ
     */
    public MockLogContext(LogLevel level, String message) {
        this(level, message, null);
    }

}
