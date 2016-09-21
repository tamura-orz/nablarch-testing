package nablarch.test.core.messaging.receive.form;

import nablarch.core.dataformat.DataRecord;
import nablarch.fw.messaging.RequestMessage;

/**
 * テスト用のフォーム
 *
 * @author hisaaki sioiri
 */
public class RM11AC0001Form {

    private String messageId;

    private String name;

    public RM11AC0001Form(String messageId, RequestMessage message) {

        this.messageId = messageId;
        DataRecord name = message.getRecordOf("name");
        this.name = name.getString("name");
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
