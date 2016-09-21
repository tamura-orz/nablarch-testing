package nablarch.test.core.messaging.sample;

import nablarch.core.dataformat.DataRecord;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.FileBatchAction;
import nablarch.fw.messaging.MessageSender;
import nablarch.fw.messaging.SyncMessage;

/**
 * メッセージ同期送信テスト用バッチActionサンプル。
 * リクエストを一回だけ送信する。
 * @author Masato Inoue
 */
public class BM21AA0104 extends FileBatchAction {
    @Override
    public String getDataFileName() {
        return "test3.dat";
    }
    @Override
    public String getFormatFileName() {
        return "test";
    }
    
    public Result doData(DataRecord record, ExecutionContext ctx) {
        
        DataRecord dataRecord = new DataRecord();
        dataRecord.put("userInfoId", "00000000000000000112");
        dataRecord.put("userId", "000000001");
        dataRecord.put("loginId", "HOGEHOGE1");
        dataRecord.put("kanjiName", "漢字名１");
        dataRecord.put("kanaName", "カナメイ１");

        DataRecord dataRecord2 = new DataRecord();
        dataRecord2.put("userInfoId", "00000000000000000113");
        dataRecord2.put("userId", "000000002");
        dataRecord2.put("loginId", "HOGEHOGE2");
        dataRecord2.put("kanjiName", "漢字名２");
        dataRecord2.put("kanaName", "カナメイ２");
        
        MessageSender.sendSync(new SyncMessage("RM21AA0104_01").addDataRecord(dataRecord));
        MessageSender.sendSync(new SyncMessage("RM21AA0104_01").addDataRecord(dataRecord2));
        MessageSender.sendSync(new SyncMessage("RM21AA0104_02").addDataRecord(record));
        return new Result.Success();
    }
    
    
}
