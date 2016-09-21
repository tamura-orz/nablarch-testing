package nablarch.test.core.messaging;

import nablarch.core.util.annotation.Published;
import nablarch.fw.launcher.Main;
import nablarch.test.core.standalone.TestShot;

/**
 * メッセージ応答なし受信処理用のテストサポートクラス。
 *
 * @author hisaaki sioiri
 */
@Published
public class MessagingReceiveTestSupport extends MessagingRequestTestSupport {

    /**
     * コンストラクタ。
     * サブクラスから使用されることを想定している。
     */
    protected MessagingReceiveTestSupport() {
        super();
    }

    /**
     * コンストラクタ。
     *
     * @param testClass テストクラス。
     */
    public MessagingReceiveTestSupport(Class<?> testClass) {
        super(testClass);
    }

    /** {@inheritDoc} */
    @Override
    protected TestShot.TestShotAround createTestShotAround(Class<?> testClass) {
        return new MessagingTestShotAround(super.createTestShotAround(
                testClass));
    }

    /** {@inheritDoc} */
    @Override
    protected void beforeExecute(String sheetName) {
        MQSupport support = new MQSupport(testClass);
        setUpMessages = support.prepareRequestMessage(sheetName);
    }

    /**
     * メッセージテスト用のテストショット前準備・結果検証クラス。
     *
     * @author T.Kawasaki
     */
    static final class MessagingTestShotAround implements TestShot.TestShotAround {

        /** テストデータの準備、検証を行う実態 */
        private final TestShot.TestShotAround testShotAround;

        /**
         * {@inheritDoc}
         */
        public Main createMain() {
            return testShotAround.createMain();
        }


        /**
         * コンストラクタ
         * @param testShotAround ラップするクラス
         */
        MessagingTestShotAround(TestShot.TestShotAround testShotAround) {
            this.testShotAround = testShotAround;
        }

        /** {@inheritDoc} */
        public void setUpInputData(TestShot testShot) {
            testShotAround.setUpInputData(testShot);
        }

        /**
         * {@inheritDoc}
         * アウトプットデータの検証は行わない。
         */
        public void assertOutputData(String msgOnFail, TestShot testShot) {
            // nop
        }

        /** {@inheritDoc} */
        public boolean isColumnForTestFramework(String columnName) {
            return testShotAround.isColumnForTestFramework(columnName);
        }

        /** {@inheritDoc} */
        public String compareStatus(int actual, TestShot testShot) {
            return testShotAround.compareStatus(actual, testShot);
        }
    }

}
