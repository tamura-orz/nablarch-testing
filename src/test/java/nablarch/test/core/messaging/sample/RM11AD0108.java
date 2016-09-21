package nablarch.test.core.messaging.sample;


import java.util.HashMap;

import nablarch.fw.ExecutionContext;
import nablarch.fw.messaging.action.MessagingAction;
import nablarch.fw.messaging.RequestMessage;
import nablarch.fw.messaging.ResponseMessage;


/**
 * 外部システムからのHTTP送信電文を入力として、ユーザ情報テンポラリテーブルにレコードを登録する。
 * 期待する応答データより短いデータを返信する。
 *
 * @author TIS
 */
public class RM11AD0108 extends MessagingAction {

    // ------------ 正常系制御 --------------- //
    /**
     * データ部に格納された登録ユーザレコードの項目バリデーションを行った後、
     * データベースに登録する。
     *
     * バリデーションエラーとなった場合は、処理をロールバックしてエラー応答を送信する。
     *
     * @param request 要求電文オブジェクト
     * @param context 実行コンテキスト
     * @return 応答電文オブジェクト
     */
    @Override
    protected ResponseMessage onReceive(RequestMessage request,
                                        ExecutionContext context) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("st", "x");
        // 応答データ返却
        ResponseMessage responseMessage = request.reply();
        responseMessage.setStatusCodeHeader("200");
        responseMessage.addRecord(hashMap);
        return responseMessage;
    }
}
