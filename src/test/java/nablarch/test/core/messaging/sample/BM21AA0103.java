package nablarch.test.core.messaging.sample;

import nablarch.core.dataformat.DataRecord;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
import nablarch.fw.messaging.MessageSender;
import nablarch.fw.messaging.SyncMessage;

/**
 * メッセージ同期送信テスト用バッチActionサンプル。
 * @author Masato Inoue
 */
public class BM21AA0103 extends FileBatchAction {
    @Override
    public String getDataFileName() {
        return "test3.dat";
    }
    @Override
    public String getFormatFileName() {
        return "test";
    }
    
    public Result doData(DataRecord record, ExecutionContext ctx) {
        record.put("count", 0);
        MessageSender.sendSync(new SyncMessage("RM21AA0101").addDataRecord(record));
        return new Result.Success();
    }
    
    
}
