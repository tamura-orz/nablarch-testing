package nablarch.test.core.file;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.dataformat.InvalidDataFormatException;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.test.Assertion;
import nablarch.test.TestSupport;
import nablarch.test.core.reader.DataType;
import nablarch.test.core.reader.TestDataParser;

import java.util.Arrays;
import java.util.List;

import static nablarch.core.util.Builder.concat;

/**
 * テストで必要なファイル操作をサポートするクラス。<br/>
 *
 * @author T.Kawasaki
 */
@Published
public class FileSupport {

    /** テストサポートクラス(テストデータ読み込み用) */
    private final TestSupport support;

    /**
     * コンストラクタ
     *
     * @param testClass テスト対象クラス
     */
    public FileSupport(Class<?> testClass) {
        support = new TestSupport(testClass);
    }

    /**
     * コンストラクタ
     *
     * @param testSupport テストサポートクラス
     */
    public FileSupport(TestSupport testSupport) {
        this.support = testSupport;
    }

    /**
     * ファイルを準備する。
     *
     * @param sheetName 取得元シート名
     * @param groupId   グループID(オプション)
     * @throws IllegalStateException ファイルの情報が取得できていない場合
     */
    public void setUpFile(String sheetName, String... groupId) throws IllegalStateException {
        doSetUpFile(true, sheetName, groupId);
    }

    /**
     * ファイルを準備する。<br/>
     * 対象データが存在しない場合は何もしない。
     *
     * @param sheetName 取得元シート名
     * @param groupId   グループID(オプション)
     */
    public void setUpFileIfNecessary(String sheetName, String... groupId) {
        doSetUpFile(false, sheetName, groupId);
    }

    /**
     * ファイルを準備する。
     *
     * @param needsCheck チェックの要否
     * @param sheetName  シート名
     * @param groupId    グループID
     */
    private void doSetUpFile(boolean needsCheck, String sheetName, String... groupId) {
        List<DataFile> files = getSetupFile(sheetName, groupId);
        if (needsCheck) {
            checkDataExists(files, sheetName, groupId);
        }
        for (DataFile e : files) {
            e.write();
        }
    }

    /**
     * 準備用のファイルを取得する。
     *
     * @param sheetName 取得元シート名
     * @param groupId   グループID(オプション)
     * @return 準備用のファイル
     */
    private List<DataFile> getSetupFile(String sheetName, String... groupId) {
        String resourceName = support.getResourceName(sheetName);
        TestDataParser parser = support.getTestDataParser();
        String basePath = support.getPathOf(resourceName);
        return parser.getSetupFile(basePath, resourceName, groupId);
    }


    /**
     * ファイルの情報が取得できたことを確認する。<br/>
     *
     * @param files     取得したファイル
     * @param types     データ型
     * @param sheetName 取得元シート名
     * @param groupId   グループID
     * @throws IllegalStateException ファイルの情報が取得できていない場合
     */
    private void checkDataExists(
            List<? extends DataFile> files, String sheetName, String[] groupId, DataType... types)
            throws IllegalStateException {

        if (!files.isEmpty()) {
            return;  // OK
        }
        // ファイルの情報が取得できなかった場合
        String gid = StringUtil.isNullOrEmpty(groupId) ? "" : groupId[0];
        throw new IllegalStateException(concat(
                " data not found. ",
                " sheet=[", sheetName, "]",
                " group id=[", gid, "]",
                " dataType=", Arrays.asList(types)));

    }

    /**
     * ファイルの比較を行う。
     *
     * @param msgOnFail 比較失敗時のメッセージ
     * @param sheetName シート名
     * @param groupId   グループID(オプション)
     */
    public void assertFile(String msgOnFail, String sheetName, String... groupId) {
        String resourceName = support.getResourceName(sheetName);
        String path = support.getPathOf(resourceName);
        List<DataFile> expectedFiles = support.getTestDataParser().getExpectedFile(path, resourceName, groupId);
        checkDataExists(expectedFiles, sheetName, groupId);

        for (DataFile expected : expectedFiles) {
            try {
                assertFile(msgOnFail, expected);
            } catch (RuntimeException e) {
                throw new IllegalStateException(
                        "assertFile failed. sheet=[" + sheetName + "] "
                                + "groupId=[" + nullToEmpty(groupId) + "] "
                                + "see nested exception(s) for more detail. "
                                + msgOnFail,
                        e);
            }
        }
    }

    /**
     * ファイルの内容が想定通りであることを表明する。
     *
     * @param msgOnFail 比較失敗時のメッセージ
     * @param expected  期待するファイル
     */
    void assertFile(String msgOnFail, DataFile expected) {
        msgOnFail += " ; file=[" + expected.getPath() + "] ";
        List<DataRecord> expectedContents = expected.toDataRecords();
        List<DataRecord> actualContents = null;
        try {
            actualContents = expected.read();
        } catch (InvalidDataFormatException e) {
            if (isEmptyFile(expectedContents)) {
                // 空ファイルを想定している場合、その旨をメッセージに付加する
                msgOnFail = msgOnFail
                        + " ; empty file is expectedContent. "
                        + "but reading actual file was failed "
                        + "because of nested exception below.";
                throw new IllegalStateException(msgOnFail, e);
            }
            throw e;
        }
        Assertion.assertEquals(msgOnFail, expectedContents, actualContents);
    }

    /**
     * 空ファイルであるかどうか判定する。
     *
     * @param records 調査対象
     * @return 全データレコードが空の場合、真
     */
    boolean isEmptyFile(List<DataRecord> records) {
        for (DataRecord e : records) {
            if (!e.isEmpty()) {
                return false;
            }
        }
        return true;
    }

    /**
     * nullを空文字に変換する。
     *
     * @param vararg 可変長文字列
     * @return 引数がnullの場合は空文字、そうでない場合は0番目の要素
     */
    private static String nullToEmpty(String[] vararg) {
        return StringUtil.isNullOrEmpty(vararg) ? "" : vararg[0];
    }
}
