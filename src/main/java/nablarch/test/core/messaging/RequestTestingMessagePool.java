package nablarch.test.core.messaging;

import java.util.ArrayList;
import java.util.Map;
import java.util.NoSuchElementException;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.dataformat.LayoutDefinition;
import nablarch.fw.messaging.MessagingException;
import nablarch.fw.messaging.SendingMessage;
import nablarch.test.core.file.FixedLengthFile;
import nablarch.test.core.reader.DataType;
import nablarch.test.core.reader.SendSyncMessageParser;

/**
 * リクエスト単体テストの際に、テストショット毎のメッセージを保持するクラス。
 *
 * @author Masato Inoue
 */
public class RequestTestingMessagePool extends MessagePool {
    
    /**
     * コンストラクタ
     * @param source   元のデータ
     * @param fwHeader フレームワーク制御ヘッダ
     */
    public RequestTestingMessagePool(FixedLengthFile source,
            Map<String, String> fwHeader) {
        super(source, fwHeader);
    }


    /** リクエストID */
    private String requestId;
    
    /**
     * リクエストIDを設定する
     * @param requestId リクエストID
     */
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    /** 
     * リクエストIDを取得する
     * @return リクエストID
     */
    public String getRequestId() {
        return requestId;
    }
    
    /**
     * 応答電文を生成する。
     * @param sheetName シート名
     * @param caseNo テストケース番号
     * @param responseMessageId 応答電文のID
     * @param requestId リクエストID 
     * @param dataType データタイプ
     * @return 応答電文
     * @throws NoSuchElementException メッセージがない場合
     */
    public byte[] createRequestTestingReceivedMessageBinary(String sheetName,
            Integer caseNo, String responseMessageId, DataType dataType, String requestId)
            throws NoSuchElementException {

        // メッセージのDataRecordを取得する
        if (!getIterator().hasNext()) {
            // 応答電文が見つからない場合、例外をスローする
            throw new RuntimeException(
                    String.format(
                            "response message was not found in sheet. sheet name=[%s], case no=[%s], "
                          + "message id=[%s], data type name=[%s], request id=[%s].",
                            sheetName, caseNo, responseMessageId,
                            dataType.getName(), requestId));
        }
        DataRecord messageRecord = getIterator().next();
        
        if (messageRecord.containsValue(SendSyncMessageParser.ErrorMode.TIMEOUT.getValue())) {
            // タイムアウトを返却するテストの場合、nullを返却する
            return null;
        } else if (messageRecord.containsValue(SendSyncMessageParser.ErrorMode.MSG_EXCEPTION.getValue())) {
            // MessagingExceptionをスローするテストの場合、nullを返却する
            throw new MessagingException("message exception was happened! this exception was thrown by mock.");
        }
        
        // テストデータ変換
        messageRecord = convertByFileType(messageRecord);
        // 対応するレイアウト定義を生成
        LayoutDefinition ld = createLayoutFromDataRecord(messageRecord);
        
        // SendingMessageを使用してメッセージをバイナリ化する
        SendingMessage sendingMessage = new SendingMessage();
        sendingMessage.setFormatter(getFormatter().setDefinition(ld))
        .addRecord(messageRecord);
        
        return sendingMessage.getBodyBytes();
    }

    /**
     * 応答電文をレコードをオブジェクトとして取得する。
     * @param sheetName シート名
     * @param caseNo テストケース番号
     * @param responseMessageId 応答電文のID
     * @param requestId リクエストID 
     * @param dataType データタイプ
     * @return 応答電文レコード
     * @throws NoSuchElementException メッセージがない場合
     */
    public DataRecord getRequestTestingReceivedMessage(String sheetName,
            Integer caseNo, String responseMessageId, DataType dataType, String requestId)
            throws NoSuchElementException {

        // メッセージのDataRecordを取得する
        if (!getIterator().hasNext()) {
            // 応答電文が見つからない場合、例外をスローする
            throw new RuntimeException(
                    String.format(
                            "response message was not found in sheet. sheet name=[%s], case no=[%s], "
                          + "message id=[%s], data type name=[%s], request id=[%s].",
                            sheetName, caseNo, responseMessageId,
                            dataType.getName(), requestId));
        }
        DataRecord messageRecord = getIterator().next();
        
        // テストデータ変換
        messageRecord = convertByFileType(messageRecord);

        return messageRecord;
    }
    
    /**
     * アサート用要求電文のリストを取得する。
     * @return アサート用メッセージのリスト
     */
    public ArrayList<DataRecord> getExpectedMessageList() {
        ArrayList<DataRecord> list = new ArrayList<DataRecord>();
        while (getIterator().hasNext()) {
            list.add(getIterator().next());
        }
        return list;
    }
    
}
