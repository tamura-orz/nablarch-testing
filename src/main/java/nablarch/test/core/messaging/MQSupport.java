package nablarch.test.core.messaging;

import nablarch.core.util.Builder;
import nablarch.core.util.annotation.Published;
import nablarch.test.TestSupport;
import nablarch.test.core.messaging.MessagePool.Comparator;

/**
 * テストで必要なメッセージング操作をサポートするクラス。
 *
 * @author T.Kawasaki
 */
@Published
public class MQSupport {

    /** 受信キュー名 */
    private static final String RECEIVE_QUEUE_NAME = "TEST.REQUEST";

    /** 送信キュー名 */
    private static final String SEND_QUEUE_NAME = "TEST.RESPONSE";

    /** テストサポートクラス(テストデータ読み込み用) */
    private final TestSupport support;


    /**
     * コンストラクタ。
     *
     * @param testClass テストクラス
     */
    public MQSupport(Class<?> testClass) {
        support = new TestSupport(testClass);
    }

    /**
     * 要求電文の準備を行う。
     *
     * @param sheetName シート名
     * @return テストデータ（メッセージ）投入用クラスのインスタンス
     */
    public MessagePool.Putter prepareRequestMessage(String sheetName) {
        MessagePool pool = getSetUpMessages(sheetName);
        return pool.prepareForPut();
    }

    /**
     * 結果検証の準備を行う。
     *
     * @param sheetName シート名
     * @return 検証用クラスのインスタンス
     */
    public Comparator prepareForAssertion(String sheetName) {
        MessagePool pool = getExpectedMessages(sheetName);
        return pool.prepareForCompare();
    }

    /**
     * テストデータから期待するメッセージを取得する。
     *
     * @param sheetName シート名
     * @return 期待するメッセージを格納したデータ
     */
    private MessagePool getExpectedMessages(String sheetName) {
        return getMessages(sheetName, "expectedMessages");
    }

    /**
     * テストデータから準備用メッセージを取得する。
     *
     * @param sheetName シート名
     * @return 準備用メッセージを格納したデータ
     */
    private MessagePool getSetUpMessages(String sheetName) {
        return getMessages(sheetName, "setUpMessages");
    }

    /**
     * メッセージをテストデータから取得する。
     *
     * @param sheetName シート名
     * @param id        ID
     * @return メッセージを格納したデータ
     */
    private MessagePool getMessages(String sheetName, String id) {
        String resourceName = support.getResourceName(sheetName);
        String path = support.getPathOf(resourceName);
        MessagePool result = support.getTestDataParser().getMessage(path, resourceName, id);
        if (result == null) {
            throw new IllegalStateException(Builder.concat(
                    "no data found. path=[", path, "] ",
                    "resource=[", resourceName, "] id=[", id, "]"));
        }
        return result;
    }

}
