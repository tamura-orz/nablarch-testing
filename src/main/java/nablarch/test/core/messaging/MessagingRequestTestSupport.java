package nablarch.test.core.messaging;


import static junit.framework.Assert.fail;
import static org.junit.Assert.assertEquals;

import java.util.NoSuchElementException;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.dataformat.DataRecordFormatter;
import nablarch.core.dataformat.FormatterFactory;
import nablarch.core.util.Builder;
import nablarch.core.util.FilePathSetting;
import nablarch.core.util.annotation.Published;
import nablarch.fw.launcher.Main;
import nablarch.fw.messaging.FwHeaderDefinition;
import nablarch.fw.messaging.MessagingContext;
import nablarch.fw.messaging.MessagingProvider;
import nablarch.fw.messaging.ReceivedMessage;
import nablarch.fw.messaging.SendingMessage;
import nablarch.fw.messaging.StandardFwHeaderDefinition;
import nablarch.fw.messaging.reader.StructuredFwHeaderDefinition;
import nablarch.test.RepositoryInitializer;
import nablarch.test.core.messaging.MessagePool.Comparator;
import nablarch.test.core.messaging.MessagePool.Putter;
import nablarch.test.core.repository.ConfigurationBrowser;
import nablarch.test.core.standalone.MainForRequestTesting;
import nablarch.test.core.standalone.StandaloneTestSupportTemplate;
import nablarch.test.core.standalone.TestShot;
import nablarch.test.core.standalone.TestShot.TestShotAround;

/**
 * メッセージ同期応答用のテストサポートクラス。<br/>
 * <h3>本クラスを使用する際の注意事項</h3>
 * <p>
 * 本クラスは、入力データをキューにPUTする用途で、main側のコンポーネント定義ファイルを読み込む。
 * その際、nablarch.fw.messaging.FwHeaderDefinition実装クラスは、
 * {@code "fwHeaderDefinition"}という名前で登録されていなければならない。
 * これ以外の名称を使用する場合は、{@link #getFwHeaderDefinitionName()}をオーバライドすることにより
 * 本クラスが使用するnablarch.fw.messaging.FwHeaderDefinitionコンポーネント名を
 * 変更することができる。
 *
 * </p>
 *
 * @author T.Kawasaki
 */
@Published
public class MessagingRequestTestSupport extends StandaloneTestSupportTemplate {

    /** メッセージ投入用クラス */
    protected Putter setUpMessages;          // SUPPRESS CHECKSTYLE サブクラスに対してカプセル化の必要がないため

    /** 結果検証用クラス */
    protected Comparator expectedMessages;   // SUPPRESS CHECKSTYLE サブクラスに対してカプセル化の必要がないため

    /**
     * コンストラクタ。<br/>
     * サブクラスから使用されることを想定している。
     */
    protected MessagingRequestTestSupport() {
        super();
    }

    /**
     * コンストラクタ。
     *
     * @param testClass テストクラス。
     */
    public MessagingRequestTestSupport(Class<?> testClass) {
        super(testClass);
    }

    /** {@inheritDoc} */
    @Override
    protected TestShotAround createTestShotAround(Class<?> testClass) {
        return new MessagingTestShotAround();
    }

    /** {@inheritDoc} */
    @Override
    protected void beforeExecute(String sheetName) {
        MQSupport support = new MQSupport(testClass);
        setUpMessages = support.prepareRequestMessage(sheetName);
        expectedMessages = support.prepareForAssertion(sheetName);
    }

    /** {@inheritDoc} */
    @Override
    protected void beforeExecuteTestShot(TestShot shot) {
        shot.putIfAbsent(TestShot.REQUEST_PATH, "test");
        shot.putIfAbsent(TestShot.USER_ID, "test");
    }

    /** {@inheritDoc} */
    @Override
    protected void afterExecuteTestShot(TestShot shot) {
        EmbeddedMessagingProvider.stopServer();
    }

    /**
     * {@link nablarch.fw.messaging.MessagingContext}を取得する。
     *
     * @param testShot テストショット
     * @return 生成したインスタンス
     */
    private MessagingContext getMessagingContextFrom(TestShot testShot) {
        String diConfig = testShot.getDiConfig();
        MessagingProvider messagingProvider
                = ConfigurationBrowser.require(diConfig, "messagingProvider", false);
        return messagingProvider.createContext();
    }

    /**
     * FW制御ヘッダのフォーマッタを取得する。<br/>
     * デフォルトの{@link StandardFwHeaderDefinition}の動作では、
     * {@link FilePathSetting}、{@link FormatterFactory}が{@link nablarch.core.repository.SystemRepository}から
     * ルックアップされるが、この時点での{@link nablarch.core.repository.SystemRepository}は
     * テスト側の設定ファイルで初期化された状態であり、これらのインスタンスは正しく取得できない。
     * ターゲット側の状態を作り出すために、事前にターゲット側の設定から取得済したインスタンスを明示的に指定して、
     * フォーマット定義を取得する。
     *
     * @param testShot テストショット
     * @return フォーマッタ
     */
    private DataRecordFormatter getHeaderFormatter(TestShot testShot) {
        String diConfig = testShot.getDiConfig();
        FilePathSetting filePathSetting
                = ConfigurationBrowser.require(diConfig, "filePathSetting", true);
        FormatterFactory formatterFactory
                = ConfigurationBrowser.require(diConfig, "formatterFactory", true);

        DataRecordFormatter dataRecordFormatter = null;
        FwHeaderDefinition fwHeaderDefinition = ConfigurationBrowser.require(diConfig, getFwHeaderDefinitionName(), true);
        if (!(fwHeaderDefinition instanceof StructuredFwHeaderDefinition)) {
            //「構造化データのフレームワーク制御ヘッダ」以外を用いている場合は、要求電文の1レコード目にフレームワーク制御ヘッダを付与するためにフォーマッタを取得する。
            //(後続処理で使用)
            dataRecordFormatter = ((StandardFwHeaderDefinition) fwHeaderDefinition).getFormatter(filePathSetting, formatterFactory);
        }

        return dataRecordFormatter;
    }

    /**
     * nablarch.fw.messaging.FwHeaderDefinition実装クラスを
     * システムリポジトリから取得するための名前を取得する。
     * 本メソッドは、{@code "fwHeaderDefinition"}を返却する。
     *
     * @return {@code "fwHeaderDefinition"}
     */
    protected String getFwHeaderDefinitionName() {
        return "fwHeaderDefinition";
    }

    /**
     * メッセージテスト用のテストショット前準備・結果検証クラス。
     *
     * @author T.Kawasaki
     */
    final class MessagingTestShotAround implements TestShotAround {

        /** コンストラクタ */
        private MessagingTestShotAround() {
        }

        /** FWヘッダのフォーマッタ */
        private DataRecordFormatter fwHeadFormatter;

        /**
         * {@inheritDoc}
         * 入力メッセージの準備を行う。
         */
        public void setUpInputData(TestShot testShot) {
            fwHeadFormatter = getHeaderFormatter(testShot);
            SendingMessage msg;
            try {
                msg = setUpMessages.createSendingMessage(fwHeadFormatter);
            } catch (NoSuchElementException e) {
                throw new IllegalStateException(Builder.concat(
                        "setUpMessage is lacking. ",
                        "no=[", testShot.getNo(), "] ",
                        "shot=[", testShot.getCaseName(), "] "
                        , e));
            }
            // 送信
            msg.setDestination("TEST.REQUEST")
               .setReplyTo("TEST.RESPONSE");
            MessagingContext context = getMessagingContextFrom(testShot);
            context.send(msg);
        }

        /**
         * {@inheritDoc}
         * 応答電文の内容を検証する。
         */
        public void assertOutputData(String msgOnFail, TestShot testShot) {
            MessagingContext context = getMessagingContextFrom(testShot);
            ReceivedMessage responseMessage = context.receiveSync("TEST.RESPONSE", 10000);
            if (responseMessage == null) {
                fail(msgOnFail + " no response message in the queue.");
            }
            if (fwHeadFormatter != null) {
                //fwHeadFormatterがnull以外の場合は、「構造化データのフレームワーク制御ヘッダ」以外を使用していることを表す。
                //この場合は、1レコード目にフレームワーク制御ヘッダが設定されているため、フレームワーク制御ヘッダのステータスコードを取り出し、assertを行う。
                DataRecord fwHeader = responseMessage.setFormatter(fwHeadFormatter).readRecord();
                String statusCode = fwHeader.getString("statusCode");
                assertEquals(msgOnFail + " unexpected status code in response message. header=" + fwHeader,
                             testShot.get("expectedStatusCode"), statusCode);
            }
            try {
                expectedMessages.compareBody(msgOnFail, responseMessage);
            } catch (NoSuchElementException e) {
                throw new IllegalStateException(Builder.concat(
                        "expectedMessage is lacking. ",
                        "no=[", testShot.getNo(), "] ",
                        "shot=[", testShot.getCaseName(), "] "
                        , e));
            }
        }

        /** {@inheritDoc} */
        public boolean isColumnForTestFramework(String columnName) {
            return false; // 特に専用のカラムはない
        }

        /**
         * {@inheritDoc}
         * メインメソッドのステータスコードは確認しない。
         */
        public String compareStatus(int actual, TestShot testShot) {
            return "";
        }

        /** {@inheritDoc} */
        public Main createMain() {
            return new MainForRequestTesting() {
                /**
                 * {@inheritDoc}
                 * リポジトリの再作成を行う。
                 */
                @Override
                protected void setUpSystemRepository(String configFilePath) {
                    // キャッシュを使用せずにリポジトリ初期化
                    RepositoryInitializer.recreateRepository(configFilePath);
                }
            };
        }
    }
}
