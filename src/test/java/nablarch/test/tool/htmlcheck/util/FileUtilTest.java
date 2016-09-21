package nablarch.test.tool.htmlcheck.util;

import java.io.Closeable;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import junit.framework.TestCase;

/**
 * FileUtilのテストクラス.
 *
 * @author Tomokazu Kagawa
 */
public class FileUtilTest extends TestCase {

    /**
     * readCsvメソッドのテスト.<br>
     * 該当CSVファイルにレコードがない場合
     *
     * @see FileUtil#readCsv(String, Charset)
     */
    @Test
    public final void testReadCsvNormalNoRecord() {

        // 期待データ準備
        List<String[]> expectedRes = new ArrayList<String[]>();

        // 実行
        List<String[]> actualRes = null;
        actualRes = FileUtil.readCsv("src/test/java/nablarch/test/tool/htmlcheck/util/FileUtil0Record.csv",
                Charset.forName("UTF-8"));

        // 比較
        assertEquals(expectedRes.size(), actualRes.size());
        for (int i = 0; i < expectedRes.size(); i++) {
            String[] expectedEveryRow = expectedRes.get(i);
            String[] actualEveryRow = actualRes.get(i);
            assertEquals(expectedEveryRow.length, actualEveryRow.length);
            for (int j = 0; j < expectedEveryRow.length; j++) {
                assertEquals(expectedEveryRow[j], actualEveryRow[j]);
            }
        }
    }
    
    /**
     * readCsvメソッドのテスト<br>
     * 該当CSVファイルのレコード数が1の場合
     *
     * @see FileUtil#readCsv(String, Charset)
     */
    @Test
    public void testReadCsvNormal1Record() {

        // 期待データ準備
        List<String[]> expectedRes = new ArrayList<String[]>();
        String[] row = new String[2];
        row[0] = "test";
        row[1] = "test2";
        expectedRes.add(row);

        // 実行
        List<String[]> actualRes = null;
        actualRes = FileUtil.readCsv("src/test/java/nablarch/test/tool/htmlcheck/util/FileUtil1Record.csv",
                Charset.forName("UTF-8"));

        // 比較
        assertEquals(expectedRes.size(), actualRes.size());
        for (int i = 0; i < expectedRes.size(); i++) {
            String[] expectedEveryRow = expectedRes.get(i);
            String[] actualEveryRow = actualRes.get(i);
            assertEquals(expectedEveryRow.length, actualEveryRow.length);
            for (int j = 0; j < expectedEveryRow.length; j++) {
                assertEquals(expectedEveryRow[j], actualEveryRow[j]);
            }
        }
    }

    /**
     * readCsvメソッドのテスト.<br>
     * 指定されたCSVファイルが存在しない場合
     *
     * @see FileUtil#readCsv(String, Charset)
     */
    @Test
    public final void testReadCsvAbnormalFileNotFound() {

        // 実行
        try {
            FileUtil.readCsv("src/test/java/nablarch/test/tool/htmlcheck"
                    + "/util/FileUtilNoFile.csv", Charset.forName("UTF-8"));
            fail();
        } catch (RuntimeException e) {
            assertEquals(
                    "can't read file [src/test/java/nablarch/test/tool/"
                    + "htmlcheck/util/FileUtilNoFile.csv]",
                    e.getMessage());
        }
    }

    /**
     * {@link FileUtil#closeQuietly(Closeable)}のテスト.<br>
     * クローズ時に例外が発生しても、握りつぶされること。

     */
    @Test
    public void testCloseQuietly() {
        FileUtil.closeQuietly(new Closeable() {
            @Override
            public void close() throws IOException {
                throw new RuntimeException("exception on close");
            }
        });
        // 例外が発生しなければテスト成功。
    }
}
