package nablarch.test.core.standalone;

import java.util.List;
import java.util.Map;

import nablarch.core.util.Builder;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.test.core.db.DbAccessTestSupport;
import nablarch.test.core.standalone.TestShot.TestShotAround;
import nablarch.test.event.TestEventDispatcher;

/**
 * 独立型の処理方式のテスト実行をサポートするテンプレートクラス。<br/>
 * 独立型処理方式のテストに共通の処理を提供する。
 * 処理方式固有の処理はサブクラスにて実装する。
 *
 * @author T.Kawasaki
 */
@Published
public abstract class StandaloneTestSupportTemplate extends TestEventDispatcher {

    /** テストクラス共通データを定義しているシート名 */
    private static final String SETUP_TABLE_SHEET = "setUpDb";

    /** テストショット一覧を表すキー名 */
    private static final String TEST_SHOTS = "testShots";

    /** テストクラス */
    protected final Class<?> testClass;    // SUPPRESS CHECKSTYLE サブクラスに対してカプセル化不要のため

    /** データベースサポートクラス */
    private final DbAccessTestSupport dbSupport;

    /**
     * コンストラクタ。
     *
     * @param testClass テストクラス
     */
    public StandaloneTestSupportTemplate(Class<?> testClass) {
        this.testClass = testClass;
        dbSupport = new DbAccessTestSupport(testClass);
    }

    /** コンストラクタ。 */
    protected StandaloneTestSupportTemplate() {
        this.testClass = getClass();
        dbSupport = new DbAccessTestSupport(testClass);
    }

    /**
     * テストを実行する。
     *
     * @param sheetName シート名
     */
    public final void execute(String sheetName) {
        SetUpDb strategy = once();
        execute(sheetName, strategy);
    }

    /**
     * テストを実行する。
     *
     * @param sheetName        シート名
     * @param setUpPerTestShot ショット毎にDBセットアップを行うかどうか
     */
    public final void execute(String sheetName, boolean setUpPerTestShot) {
        SetUpDb strategy = setUpPerTestShot ? everyShot() : once();
        execute(sheetName, strategy);
    }

    /**
     * 結合テストを実行する。
     *
     * @param sheetName シート名
     */
    public final void executeIntegrationTest(String sheetName) {
        SetUpDb strategy = never();
        execute(sheetName, strategy);
    }

    /**
     * テストを実行する。
     *
     * @param sheetName シート名
     * @param setUpDb   DBセットアップ区分
     */
    private void execute(String sheetName, SetUpDb setUpDb) {
        if (StringUtil.isNullOrEmpty(sheetName)) {
            throw new IllegalArgumentException("sheetName must not be null or empty.");
        }
        // メソッド起動毎のDBセットアップ
        setUpDb.forMethod();
        beforeExecute(sheetName);
        try {
            executeTest(sheetName, setUpDb);
        } finally {
            afterExecute();
        }
    }

    /**
     * テストを実行する。
     *
     * @param sheetName シート名
     * @param setUpDb   ショット毎にDBセットアップを行うかどうか
     */
    private void executeTest(String sheetName, SetUpDb setUpDb) {

        // テストケース一覧を取得する
        List<Map<String, String>> testCases = getTestCases(sheetName, dbSupport);

        for (Map<String, String> testCase : testCases) {
            // ショット毎のDBセットアップ
            setUpDb.forShot();
            TestShot shot = createTestShot(sheetName, dbSupport, testCase);

            executeTestShot(shot);  // テストショット実行
        }
    }
    


    /**
     * テストケース一覧を取得する。
     *
     * @param sheetName シート名
     * @param dbSupport DBサポートクラス
     * @return テストケース一覧
     */
    private List<Map<String, String>> getTestCases(String sheetName, DbAccessTestSupport dbSupport) {

        List<Map<String, String>> testCases = dbSupport.getListMap(sheetName, TEST_SHOTS);
        if (testCases.isEmpty()) {
            // テストデータが見つからない場合は、テスト失敗
            throw new IllegalArgumentException(Builder.concat(
                    "no test shot found. sheet=[", sheetName, "]"));
        }
        return testCases;
    }

    /**
     * テストショットを生成する。
     *
     * @param sheetName シート名
     * @param dbSupport DBサポートクラス
     * @param testData  1テストショット分のテストデータ
     * @return テストショット
     */
    private TestShot createTestShot(String sheetName,
                                    DbAccessTestSupport dbSupport,
                                    Map<String, String> testData) {

        TestShotAround around = createTestShotAround(testClass);
        return new TestShot(sheetName, testData, dbSupport, around, testClass);
    }

    /**
     * テストショットを実行する。
     *
     * @param testShot 実行対象のテストショット
     */
    private void executeTestShot(TestShot testShot) {
        beforeExecuteTestShot(testShot);
        try {
            testShot.executeTestShot();
        } finally {
            afterExecuteTestShot(testShot);
        }
    }


    /**
     * テストを実行する。<br/>
     * テストに使用するシート名はメソッド名と同じとみなされる。
     * 本メソッドはサブクラスからのみ使用できる。
     */
    protected final void execute() {
        String sheetName = getMethodName();
        if (StringUtil.isNullOrEmpty(sheetName)) {
            throw new IllegalStateException("only subclass can call execute() method.");
        }
        execute(sheetName);
    }

    /**
     * テストショット実行前に必要な事前処理を行う。
     *
     * @param shot 実行直前のテストショット
     */
    protected void beforeExecuteTestShot(TestShot shot) {
    }

    /**
     * テストショット実行後に必要な事後処理を行う。
     *
     * @param shot 実行直後のテストショット
     */
    protected void afterExecuteTestShot(TestShot shot) {
    }

    /**
     * 前準備、結果検証を行うクラスのインスタンスを生成する。
     *
     * @param testClass テストクラス
     * @return 生成したインスタンス
     */
    protected abstract TestShotAround createTestShotAround(Class<?> testClass);

    /**
     * テスト実行前の処理を行う。<br/>
     * サブクラスでオーバライドする。
     *
     * @param sheetName シート名
     */
    protected void beforeExecute(String sheetName) {
    }

    /**
     * 全テストショット終了後の処理を行う。<br/>
     * サブクラスでオーバライドする。
     */
    protected void afterExecute() {
    }

    /** DBセットアップのStrategyクラス。 */
    private abstract class SetUpDb {

        /** メソッド毎のDBセットアップを実行する。 */
        abstract void forMethod();

        /** ショット毎のDBセットアップを実行する。 */
        abstract void forShot();

        /** DBセットアップを実行する。 */
        void setUp() {
            dbSupport.setUpDb(SETUP_TABLE_SHEET);
        }
    }

    /**
     * DBセットアップしない。
     *
     * @return インスタンス
     */
    private SetUpDb never() {
        return new SetUpDb() {
            @Override
            void forMethod() {
            }

            @Override
            void forShot() {
            }
        };
    }

    /**
     * メソッド毎にDBセットアップする。
     *
     * @return インスタンス
     */
    private SetUpDb once() {
        return new SetUpDb() {
            @Override
            void forMethod() {
                setUp();
            }

            @Override
            void forShot() {
            }
        };
    }


    /**
     * ショット毎にDBセットアップする。
     *
     * @return インスタンス
     */
    private SetUpDb everyShot() {
        return new SetUpDb() {
            @Override
            void forMethod() {
            }

            @Override
            void forShot() {
                setUp();
            }
        };
    }
}
