package nablarch.test.core.integration;

import nablarch.test.core.batch.BatchRequestTestSupport;
import nablarch.test.core.db.DbAccessTestSupport;
import nablarch.test.core.http.AbstractHttpRequestTestTemplate;
import nablarch.test.core.http.Advice;
import nablarch.test.core.messaging.MessagingRequestTestSupport;
import nablarch.test.event.TestEventDispatcher;
import org.junit.Before;

/**
 * 結合テストサポートクラス。
 *
 * @author T.Kawasaki
 */
public class IntegrationTestSupport extends TestEventDispatcher {

    /** テストクラス。 */
    private Class<?> testClass;

    /**
     * コンストラクタ
     *
     * @param testClass テストクラス
     */
    public IntegrationTestSupport(Class<?> testClass) {
        this.testClass = testClass;
    }

    /** コンストラクタ。 */
    protected IntegrationTestSupport() {
        this.testClass = getClass();
    }

    /** テストメソッド実行前にデータベースセットアップを実行する。 */
    @Before
    public void setUpDbBeforeTestMethod() {
        new DbAccessTestSupport(testClass).setUpDb("setUpDb");
    }

    /**
     * バッチ処理方式のテストを実行する。
     *
     * @param sheetName シート名
     */
    public void executeBatch(String sheetName) {
        new BatchRequestTestSupport(testClass).executeIntegrationTest(sheetName);
    }

    /**
     * メッセージ同期応答方式のテストを実行する。
     *
     * @param sheetName シート名
     */
    public void executeMessagingSync(String sheetName) {
        new MessagingRequestTestSupport(testClass).executeIntegrationTest(sheetName);
    }

    /**
     * オンライン処理方式のテストを実行する。
     *
     * @param sheetName シート名
     * @param baseUri   ベースURI
     */
    public void executeOnline(String sheetName, String baseUri) {
        createInstance(baseUri).execute(sheetName, false);
    }

    /**
     * オンライン処理方式のテストを実行する。
     *
     * @param sheetName シート名
     * @param baseUri   ベースURI
     * @param advice    コールバック
     */
    @SuppressWarnings("unchecked")
    public void executeOnline(String sheetName, String baseUri, Advice advice) {
        createInstance(baseUri).execute(sheetName, advice);
    }

    /**
     * インスタンスを生成する。
     *
     * @param baseUri ベースURI
     * @return インスタンス
     */
    protected AbstractHttpRequestTestTemplate createInstance(final String baseUri) {
        return new AbstractHttpRequestTestTemplate(testClass) {
            @Override
            protected String getBaseUri() {
                return baseUri;
            }
        };
    }
}
