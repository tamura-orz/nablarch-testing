package nablarch.test.core.messaging.sample;

import nablarch.core.dataformat.DataRecord;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
import nablarch.fw.messaging.MessageSender;
import nablarch.fw.messaging.SyncMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * メッセージ同期送信テスト用バッチActionサンプル。
 * @author Masato Inoue
 */
public class BM21AA0101 extends FileBatchAction {
    @Override
    public String getDataFileName() {
        return "test.dat";
    }
    @Override
    public String getFormatFileName() {
        return "test";
    }
    
    public Result doData(DataRecord record, ExecutionContext ctx) {
        Integer count = countUp(ctx);
        Map<String, Object> header = new HashMap<String, Object>();
        header.put("testCount", count); // ヘッダにカウントアップした値を設定

        String sendingMessageFormat = null;
        switch (record.getRecordNumber()) {
            case 1:       // fallthrough
            case 2:
                // リクエストID[RM21AA0101]で、1回目のメッセージ同期送信を行う
                sendingMessageFormat = "RM21AA0101";
                break;
            case 3:
                // リクエストID[RM21AA010102]で、1回目のメッセージ同期送信を行う
                sendingMessageFormat = "RM21AA010102";
                record.put("count", count);
        }
        SyncMessage message = new SyncMessage(sendingMessageFormat).setHeaderRecord(header)
                                                                   .addDataRecord(record);
        MessageSender.sendSync(message);
        return new Result.Success();
    }
    
    private Integer countUp(ExecutionContext ctx) {
        String key = "count";
        Integer previous = ctx.getSessionScopedVar(key);
        previous = (previous == null) ? 0 : previous;
        Integer next = previous + 1;
        ctx.setSessionScopedVar(key, next); // セッションにカウントアップした値を設定
        return next;
    }

    @Override
    protected void terminate(Result result, ExecutionContext ctx) {
        // ３回呼ばれなかった場合は、例外をスローする
        if((Integer)ctx.getSessionScopedVar("count") != 3) {
            throw new RuntimeException(String.format("count must be 3, but was [%s].", ctx.getSessionScopedVar("count")));
        }
    }
    
}
