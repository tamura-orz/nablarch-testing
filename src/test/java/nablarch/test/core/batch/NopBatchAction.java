package nablarch.test.core.batch;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.Result.Success;
import nablarch.fw.action.BatchAction;

import java.util.Arrays;
import java.util.Iterator;

/**
 * 何もしないバッチアクション（テスト用）
 * @author T.Kawasaki
 */
public class NopBatchAction extends BatchAction<String> {
    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(NopBatchAction.class);
    /** {@inheritDoc} */
    @Override
    public Result handle(String inputData, ExecutionContext ctx) {
        LOGGER.logDebug(inputData);
        return new Success();
    }
    /** {@inheritDoc} */
    @Override
    public DataReader<String> createReader(ExecutionContext ctx) {
        return new DataReader<String>() {
            Iterator<String> i = Arrays.asList("1", "2", "3").iterator();
            public String read(ExecutionContext ctx) {
                return i.next();
            }

            public boolean hasNext(ExecutionContext ctx) {
                return i.hasNext();
            }

            public void close(ExecutionContext ctx) {
            }
        };
    }
}
