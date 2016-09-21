package nablarch.test.core.log;

import nablarch.core.log.basic.LogContext;
import nablarch.core.log.basic.LogWriter;
import nablarch.core.log.basic.ObjectSettings;

/**
 * 何も出力しない{@link LogWriter}実装クラス。
 *
 * @author T.Kawasaki
 */
public class NopLogWriter implements LogWriter {

    /** {@inheritDoc} */
    public void initialize(ObjectSettings settings) {
    }

    /** {@inheritDoc} */
    public void terminate() {
    }

    /** {@inheritDoc} */
    public void write(LogContext context) {
    }
}
