package nablarch.test.core.standalone;

import static nablarch.core.util.Builder.concat;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.launcher.Main;
import nablarch.test.NablarchTestUtils;
import nablarch.test.core.db.DbAccessTestSupport;
import nablarch.test.core.log.LogVerifier;
import nablarch.test.core.messaging.RequestTestingMessagingProvider.RequestTestingMessagingContext;
import nablarch.test.core.messaging.RequestTestingMessagingClient;
import nablarch.test.core.util.MapCollector;

/**
 * @author T.Kawasaki
 */
@Published
public class TestShot {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(TestShot.class);

    /** データベースサポートクラス */
    private final DbAccessTestSupport dbSupport;

    /** テストデータのシート名 */
    private final String sheetName;

    /** このショットが使用するテストデータ */
    private final Map<String, String> testData;

    /** 前準備、結果検証を行うクラス */
    private final TestShotAround around;

    /** 実際のステータスコード */
    private int actualStatusCode = Integer.MIN_VALUE;

    /** テストクラス */
    private Class<?> testClass;

    /**
     * コンストラクタ。
     *
     * @param sheetName シート名
     * @param testData  テストデータ
     * @param dbSupport DBサポートクラス
     * @param around    前準備、結果検証を行うクラス
     * @param testClass    テストクラス
     */
    public TestShot(String sheetName, Map<String, String> testData,
                    DbAccessTestSupport dbSupport, TestShotAround around, Class<?> testClass) {
        this.dbSupport = dbSupport;
        this.sheetName = sheetName;
        this.testData = testData;
        this.around = around;
        this.testClass = testClass;
    }

    /** テストショットを実行する。 */
    public final void executeTestShot() {

        // 必須項目の存在チェック
        NablarchTestUtils.assertContainsRequiredKeys(
                "sheet=[" + sheetName + "]", testData, getRequiredColumns());
        
        clearPreviousTestData();
        
        String caseDesc = concat("test case. no=[", getNo(), "] case=[", getCaseName(), "]");
        LOGGER.logDebug("START " + caseDesc);
        // 準備
        setUp();

        // 実行
        Main main = around.createMain();
        actualStatusCode = invokeTarget(main);
        // 結果検証
        assertAll();
        LOGGER.logDebug("END " + caseDesc);
    }

    /** テストで使用するデータのキャッシュをクリアする */
    protected void clearPreviousTestData() {

        // メッセージ同期送信で使用する要求電文のキャッシュをクリアする
        RequestTestingMessagingClient.clearSendingMessageCache();
        RequestTestingMessagingContext.clearSendingMessageCache();
        
        LogVerifier.clear();
        
    }

    /**
     * 必須カラム一覧を取得する。
     *
     * @return 必須カラム一覧
     */
    protected Set<String> getRequiredColumns() {
        return REQUIRED_COLUMNS;
    }

    /**
     * テスト対象を起動する。
     *
     * @param main 起動するメインクラス
     * @return ステータスコード
     */
    private int invokeTarget(Main main) {
        CommandLine cmd = createCommandLine();
        LOGGER.logDebug("Invoking test target. ");
        LOGGER.logDebug("\trequest path =[" + cmd.getRequestPath() + "]");
        LOGGER.logDebug("\tcommand line arguments=" + cmd.getArgs());
        LOGGER.logDebug("\tcommand line options=" + cmd.getParamMap());
        return main.handle(cmd, new ExecutionContext());
    }

    /** アサートを実行する。 */
    private void assertAll() {
        String msgOnFail = concat("no=[", getNo(), "] case=[", getCaseName(), "] ");
        // ステータスコード確認
        String statusMsg = around.compareStatus(actualStatusCode, this);
        msgOnFail += statusMsg;
        assertTables(msgOnFail);        // DB確認
        around.assertOutputData(msgOnFail, this);    // 出力ファイル確認
        assertMessages(); // メッセージ確認
        LogVerifier.verify(msgOnFail);  // ログ確認
        if (StringUtil.hasValue(statusMsg)) {
            throw new AssertionError(statusMsg);
        }
    }

    /** 準備を行う。 */
    private void setUp() {
        setUpTable();         // DB
        around.setUpInputData(this);     // 入力ファイル
        setUpExpectedLog();   // 出力ファイル
        setUpMessage();  // 要求電文（期待値）
    }

    /** データベースの準備を行う。 */
    private void setUpTable() {
        String gid = testData.get(SETUP_TABLE);
        if (StringUtil.isNullOrEmpty(gid)) {
            return;  // 空欄の場合、何もしない
        }

        // デフォルトのセットアップ
        dbSupport.setUpDb(sheetName, null);
        if (!gid.equals(DEFAULT_GID)) {
            // グループID指定のセットアップ
            dbSupport.setUpDb(sheetName, gid);
        }
    }

    /** メッセージ同期送信で送信されたメッセージのアサートを行う */
    private void assertMessages() {
        RequestTestingMessagingClient.assertSendingMessage(testClass, sheetName, get("no"), get("expectedMessageByClient"));        
        RequestTestingMessagingContext.assertSendingMessage(testClass, sheetName, get("no"), get("expectedMessage"));        
    }

    /** 期待するログメッセージの準備を行う。 */
    private void setUpExpectedLog() {
        String id = testData.get(EXPECTED_LOG);
        if (StringUtil.isNullOrEmpty(id)) {
            return;  // 指定がない場合は何もしない。
        }

        List<Map<String, String>> expectedLogMessages
                = dbSupport.getListMap(sheetName, id);
        if (expectedLogMessages.isEmpty()) {
            throw new IllegalStateException("expected log data must be set. expected log id = [" + id + "]");
        }
        LogVerifier.setExpectedLogMessages(expectedLogMessages);
    }

    /** メッセージ同期送信のリクエスト単体テストを実行するための準備を行う */
    private void setUpMessage() {
        // メッセージ同期送信を行う場合に、MockMessagingContextに必要なテストケースの情報を格納する
        RequestTestingMessagingClient.initializeForRequestUnitTesting(testClass, sheetName, get("no"), 
                get("responseMessageByClient"), get("expectedMessageByClient"));
        RequestTestingMessagingContext.initializeForRequestUnitTesting(testClass, sheetName, get("no"), get("responseMessage"), get("expectedMessage"));
    }

    /**
     * DBのアサートを行う。
     *
     * @param msgOnFail 失敗時のメッセージ
     */
    private void assertTables(String msgOnFail) {

        String expectedTableGroupId = testData.get(EXPECTED_TABLE_GROUP_ID);
        if (StringUtil.isNullOrEmpty(expectedTableGroupId)) {
            return; // 指定がない場合はアサートしない。
        }

        // デフォルトのアサート（データがなければスキップ）
        dbSupport.assertTableEquals(msgOnFail, sheetName, false);

        if (!expectedTableGroupId.equals(DEFAULT_GID)) {
            // グループID指定のアサート
            dbSupport.assertTableEquals(msgOnFail, sheetName, expectedTableGroupId);
        }

    }

    /**
     * コマンドラインオプションを抽出する。
     *
     * @return コマンドラインオプション
     */
    private Map<String, String> extractOptions() {
        return new MapCollector<String, String, String>() {
            // オプションカラムでありかつ値がnullでないものを抽出
            @Override
            protected String evaluate(String key, String value) {
                return isCommandLineOptionColumn(key) && value != null
                        ? value
                        : skip();
            }
        }
        .collect(testData);
    }

    /**
     * コマンドラインオプションのカラムかどうか判定する。
     * <ul>
     * <li>コマンドライン引数(args[n])でない。</li>
     * <li>自動テストフレームワーク用のカラム(no, case等)でない。</li>
     * </ul>
     *
     * @param columnName カラム名
     * @return コマンドラインオプションのカラムであれば真
     */
    private boolean isCommandLineOptionColumn(String columnName) {
        return !(isCommandLineArgumentColumn(columnName)
                || COLUMNS_FOR_TFW.contains(columnName)
                || around.isColumnForTestFramework(columnName));
    }

    /**
     * コマンドライン引数のカラムであるか判定する。
     *
     * @param columnName カラム名
     * @return コマンドライン引数の場合、真
     */
    private boolean isCommandLineArgumentColumn(String columnName) {
        return columnName.matches("args\\[[0-9]+\\]");
    }

    /**
     * コマンドライン引数を抽出する。
     *
     * @return コマンドライン引数
     */
    private List<String> extractArguments() {
        List<String> args = new ArrayList<String>();
        for (int i = 0;; i++) {
            String key = concat("args[", i, "]");
            String value = testData.get(key);
            if (StringUtil.isNullOrEmpty(value)) {
                break;
            }
            args.add(value);
        }
        return args;
    }

    /**
     * コマンドラインオブジェクトを作成する。
     *
     * @return コマンドラインオブジェクト
     */
    private CommandLine createCommandLine() {
        List<String> args = extractArguments();
        Map<String, String> opts = extractOptions();
        return new CommandLine(opts, args);
    }



    /**
     * ケース番号を取得する。
     *
     * @return ケース番号
     */
    public String getNo() {
        return get(NO);
    }

    /**
     * テストケース名称を取得する。
     *
     * @return テストケース名称
     */
    public String getCaseName() {
        return get(TITLE);
    }

    /**
     * テストデータを取得する。
     *
     * @param key カラム名
     * @return 値
     */
    public String get(String key) {
        return testData.get(key);
    }

    /**
     * コンポーネント設定ファイルを取得する。
     *
     * @return コンポーネント設定ファイル
     */
    public String getDiConfig() {
        return get(DI_CONFIG);
    }

    /**
     * 値が設定されていない場合、上書きする。
     * @param key キー
     * @param value 値
     */
    public void putIfAbsent(String key, String value) {
        if (!testData.containsKey(key)) {
            testData.put(key, value);
        }
    }

    /**
     * シート名を取得する。
     *
     * @return シート名
     */
    public final String getSheetName() {
        return sheetName;
    }

    /** テストショット番号 */
    public static final String NO = "no";

    /** テストショットの説明 */
    public static final String TITLE = "description";

    /** 期待するステータスコード */
    public static final String EXPECTED_STATUS_CODE = "expectedStatusCode";

    /** セットアップ対象テーブルのグループID */
    public static final String SETUP_TABLE = "setUpTable";

    /** コンポーネント設定ファイル */
    public static final String DI_CONFIG = "diConfig";

    /** リクエストパス */
    public static final String REQUEST_PATH = "requestPath";

    /** ユーザID */
    public static final String USER_ID = "userId";

    /** DB期待値のグループID */
    public static final String EXPECTED_TABLE_GROUP_ID = "expectedTable";

    /** 要求電文の期待値を定義しているカラム名 */
    public static final String EXPECTED_MESSAGE_COLUMN = "expectedMessage";

    /** 応答電文を定義しているカラム名 */
    public static final String RESPONSE_MESSAGE_COLUMN = "responseMessage";
    

    /** 期待するログ */
    public static final String EXPECTED_LOG = "expectedLog";

    /** デフォルトのグループID */
    public static final String DEFAULT_GID = "default";

    /** 必須カラム */
    public static final Set<String> REQUIRED_COLUMNS =
            NablarchTestUtils.asSet(
                    NO, TITLE, EXPECTED_STATUS_CODE, DI_CONFIG, REQUEST_PATH, USER_ID);

    /** 自動テストフレームワーク用のカラム */
    private static final Set<String> COLUMNS_FOR_TFW =
            NablarchTestUtils.asSet(NO, TITLE, EXPECTED_STATUS_CODE);




    /**
     * テストの前準備、結果検証を行うインタフェース。<br/>
     * 処理方式固有の前準備、結果検証ロジックを実装すること。
     *
     * @author T.Kawasaki
     */
    public interface TestShotAround {

        /**
         * 入力データを準備する。
         *
         * @param testShot テストショット
         */
        void setUpInputData(TestShot testShot);

        /**
         * 出力結果を検証する。
         *
         * @param msgOnFail アサート失敗時のメッセージ
         * @param testShot  テストショット
         */
        void assertOutputData(String msgOnFail, TestShot testShot);

        /**
         * テストフレームワークで使用するカラムかどうか判定する。<br/>
         * テストフレームワークで使用しないカラムであれば、コマンドラインオプションと見なされる。
         *
         * @param columnName カラム名
         * @return 判定結果
         */
        boolean isColumnForTestFramework(String columnName);

        /**
         * ステータスコードの比較を行う。
         *
         * @param actual 実際のステータスコード
         * @param testShot テストショット
         * @return ステータスコードの比較成功時は空文字、そうでない場合はエラーメッセージ
         */
        String compareStatus(int actual, TestShot testShot);

        /**
         * メインクラスを生成する。
         *
         * @return メインクラス
         */
        Main createMain();
    }

}
