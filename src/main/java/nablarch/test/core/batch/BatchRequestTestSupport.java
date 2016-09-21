package nablarch.test.core.batch;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.fw.launcher.Main;
import nablarch.test.Assertion;
import nablarch.test.NablarchTestUtils;
import nablarch.test.core.file.FileSupport;
import nablarch.test.core.standalone.MainForRequestTesting;
import nablarch.test.core.standalone.StandaloneTestSupportTemplate;
import nablarch.test.core.standalone.TestShot;
import nablarch.test.core.standalone.TestShot.TestShotAround;

import java.util.Set;

import static nablarch.core.util.Builder.concat;

/**
 * バッチリクエスト単体テストサポートクラス。<br/>
 *
 * @author T.Kawasaki
 */
@Published
public class BatchRequestTestSupport extends StandaloneTestSupportTemplate {

    /** コンストラクタ。 */
    protected BatchRequestTestSupport() {
        super();
    }

    /**
     * コンストラクタ。
     *
     * @param testClass テストクラス
     */
    public BatchRequestTestSupport(Class<?> testClass) {
        super(testClass);
    }

    /** {@inheritDoc} */
    @Override
    protected TestShotAround createTestShotAround(Class<?> testClass) {
        return new BatchTestShotAround(testClass);
    }

    /**
     * バッチの前準備、結果検証を行うクラス。
     *
     * @author T.Kawasaki
     */
    private static class BatchTestShotAround implements TestShotAround {

        /** ロガー */
        private static final Logger LOGGER = LoggerManager.get(BatchTestShotAround.class);

        /** ファイルサポートクラス */
        private final FileSupport fileSupport;

        /**
         * コンストラクタ
         * @param testClass テストクラス
         */
        BatchTestShotAround(Class<?> testClass) {
            this.fileSupport = new FileSupport(testClass);
        }

        /**
         * {@inheritDoc}
         * 入力ファイルの準備を行う。
         */
        public void setUpInputData(TestShot testShot) {
            String gid = testShot.get(SETUP_FILE);
            if (StringUtil.isNullOrEmpty(gid)) {
                return;
            }
            if (gid.equals(TestShot.DEFAULT_GID)) {
                gid = null;
            }
            fileSupport.setUpFile(testShot.getSheetName(), gid);
        }

        /**
         * {@inheritDoc}
         * バッチが出力したファイルの検証を行う。
         */
        public void assertOutputData(String msgOnFail, TestShot testShot) {
            String gid = testShot.get(EXPECTED_FILE);
            if (StringUtil.isNullOrEmpty(gid)) {
                return;
            }
            if (gid.equals(TestShot.DEFAULT_GID)) {
                gid = null;  // グループID指定なし
            }
            try {
                fileSupport.assertFile(msgOnFail, testShot.getSheetName(), gid);
            } catch (RuntimeException e) {
                LOGGER.logDebug("comparing output file failed.", e);
                Assertion.fail(msgOnFail, "; comparing output file failed. [",
                               NablarchTestUtils.getMessages(e), "]");
            }
        }

        /** {@inheritDoc} */
        public boolean isColumnForTestFramework(String columnName) {
            return OPTIONAL_COLUMNS.contains(columnName);
        }

        /** {@inheritDoc} */
        public String compareStatus(int actual, TestShot testShot) {
            String expected = testShot.get(TestShot.EXPECTED_STATUS_CODE);
            return String.valueOf(actual).equals(expected)
                ? ""
                : concat("expected status code is [", expected, "].",
                         "but was [", actual, "]");
        }

        /** {@inheritDoc} */
        public Main createMain() {
            return new MainForRequestTesting();
        }

        /** 準備ファイルのグループID */
        private static final String SETUP_FILE = "setUpFile";

        /** 期待するファイルのグループIDを */
        private static final String EXPECTED_FILE = "expectedFile";

        /** 任意設定のカラム */
        private static final Set<String> OPTIONAL_COLUMNS =
                NablarchTestUtils.asSet(SETUP_FILE, EXPECTED_FILE);
    }
}
