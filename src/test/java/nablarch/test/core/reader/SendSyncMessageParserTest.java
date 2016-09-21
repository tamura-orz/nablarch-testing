package nablarch.test.core.reader;

import org.junit.Test;

/**
 * {@link SendSyncMessageParser}のテストクラス。
 */
public class SendSyncMessageParserTest {

    /** {@link SendSyncMessageParser#getFwHeader()}を呼び出した場合、例外が発生すること。*/
    @Test(expected = UnsupportedOperationException.class)
    public void testGetFwHeader() {
        SendSyncMessageParser target = new SendSyncMessageParser(null, null, null);
        target.getFwHeader();
    }


}