package nablarch.test.core.messaging.sample;

import static nablarch.core.validation.ValidationUtil.validateAndConvertRequest;

import java.util.HashMap;
import java.util.Map;

import nablarch.core.message.ApplicationException;
import nablarch.core.validation.ValidationContext;
import nablarch.fw.ExecutionContext;
import nablarch.fw.messaging.MessageSender;
import nablarch.fw.messaging.SyncMessage;
import nablarch.fw.web.HttpRequest;
import nablarch.fw.web.HttpResponse;
import nablarch.fw.web.interceptor.OnError;

/**
 * メッセージ同期送信テスト用オンラインActionサンプル。
 * @author Masato Inoue
 */
public class W11AD01Action {

    public static boolean timeoutTest = false;
    
    /** メッセージ同期送信を行う */
    @OnError(type = ApplicationException.class, path = "forward://RW11AD0101")
    public HttpResponse doRW11AD0102(HttpRequest request, ExecutionContext context) {

        W11AD01Form form = validate(request);
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("title", form.getTitle());
        data.put("publisher", form.getPublisher());
        data.put("authors", form.getAuthors());

        SyncMessage responseMessage1 = MessageSender.sendSync(new SyncMessage("RM11AD0101").addDataRecord(data));
        context.setRequestScopedVar("responseRM11AD0101_1", responseMessage1);
        SyncMessage responseMessage2 = MessageSender.sendSync(new SyncMessage("RM11AD0101").addDataRecord(data));
        context.setRequestScopedVar("responseRM11AD0101_2", responseMessage2);        
        SyncMessage responseMessage3 = MessageSender.sendSync(new SyncMessage("RM11AD0102").addDataRecord(data));
        context.setRequestScopedVar("responseRM11AD0102", responseMessage3);

        if (timeoutTest) {
            // タイムアウトのテスト
            SyncMessage responseMessage4 = MessageSender
                    .sendSync(new SyncMessage("RM11AD0101").addDataRecord(data));
            context.setRequestScopedVar("responseRM11AD0101_3",
                    responseMessage4);
        }

        return new HttpResponse("/test.html");
    }

    /** バリデーション */
    private W11AD01Form validate(HttpRequest request) {
        ValidationContext<W11AD01Form> context = validateAndConvertRequest("book", W11AD01Form.class, request, "validateForSend");
        if (!context.isValid()) {
            throw new ApplicationException(context.getMessages());
        }
        return context.createObject();
    }

}
