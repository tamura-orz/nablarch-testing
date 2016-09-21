package nablarch.test.core.batch;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.NoInputDataBatchAction;
import nablarch.fw.launcher.CommandLine;

/** バッチ引数を使用するバッチのサンプル。 */
public class SimpleBatchSample extends NoInputDataBatchAction {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(SimpleBatchSample.class);

    /**
     * コマンドのサイズ
     */
    private int cmdSize;

    @Override
    public Result handle(ExecutionContext ctx) {
        LOGGER.logInfo("command line args length = [" + cmdSize + "]");
        return new Result.Success();
    }

    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {
        super.initialize(command, context);
        cmdSize = command.getArgs().size();
    }
}
