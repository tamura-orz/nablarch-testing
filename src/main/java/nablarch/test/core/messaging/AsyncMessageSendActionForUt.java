package nablarch.test.core.messaging;

import nablarch.core.db.statement.SqlRow;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.messaging.action.AsyncMessageSendAction;

/**
 * リクエスト単体テスト用のメッセージ送信（応答なし）アクション。
 * <p/>
 * 本クラスは、バッチ起動引数で「errorCase=true」が指定された場合に、
 * 例外を送出し異常系のテストとして処理を行う。
 * <p/>
 * バッチ起動引数でerrorCaseが指定されていない場合や、errorCaseがfalseの場合には、
 * {@link AsyncMessageSendAction}に処理を委譲する。
 *
 * @author hisaaki sioiri
 */
public class AsyncMessageSendActionForUt extends AsyncMessageSendAction {

    /** 異常系のケースか否か */
    private boolean errorCase;

    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {
        if ("true".equals(command.getParam("errorCase"))) {
            errorCase = true;
        }
        super.initialize(command, context);
    }

    @Override
    public Result handle(SqlRow inputData, ExecutionContext ctx) {
        if (errorCase) {
            throw new RuntimeException("this is abnormal end case.");
        }
        return super.handle(inputData, ctx);
    }
}

