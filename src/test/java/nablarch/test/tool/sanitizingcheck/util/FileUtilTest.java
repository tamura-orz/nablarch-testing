package nablarch.test.tool.sanitizingcheck.util;

import junit.framework.TestCase;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import nablarch.test.tool.sanitizingcheck.util.FileUtil;

/**
 * FileUtilのテストクラス
 * 
 * @author Tomokazu Kagawa
 * @see FileUtil
 */
public class FileUtilTest extends TestCase {

    /**
     * readFileメソッドのテスト
     * 
     * @see FileUtil#readFile(String)
     */
    public void testReadFile() {
        
        // ファイルが存在しない場合
        try {
            FileUtil.readFile("noSuchFile.txt");
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertEquals("can't read file [noSuchFile.txt]", e.getMessage());
        }

        // 何も記述されていない場合
        List<String> stringList = FileUtil
                .readFile("src/test/java/nablarch/test/tool/sanitizingcheck/util/testReadFileNoContent.txt");
        assertEquals(new ArrayList<String>(), stringList);

        // 1行の場合
        stringList = FileUtil
                .readFile("src/test/java/nablarch/test/tool/sanitizingcheck/util/testReadFile1Row.txt");
        List<String> expected = new ArrayList<String>();
        expected.add("test");
        assertListString(expected, stringList);

        // 2行の場合
        stringList = FileUtil
                .readFile("src/test/java/nablarch/test/tool/sanitizingcheck/util/testReadFile2Row.txt");
        List<String> expected2 = new ArrayList<String>();
        expected2.add("test");
        expected2.add("test2");
        assertEquals(expected2, stringList);
        
        // 3行以上の場合
        stringList = FileUtil
                .readFile("src/test/java/nablarch/test/tool/sanitizingcheck/util/testReadFile3Row.txt");
        List<String> expected3 = new ArrayList<String>();
        expected3.add("test");
        expected3.add("test2");
        expected3.add("test3");
        assertEquals(expected3, stringList);
        
    }
    
    /**
     * readFileToStringメソッドのテスト
     * 
     * @see FileUtil#readFileToString(File)
     */
    public void testReadFileToString() {
        
        // ファイルが存在しない場合
        try {
            FileUtil.readFileToString(new File("noSuchFile.txt"));
            fail();
        } catch (Exception e) {
            assertTrue(e instanceof IllegalArgumentException);
            assertEquals("can't read file [noSuchFile.txt]", e.getMessage());
        }
        
        // 何も記述されていない場合
        String fileString = FileUtil.readFileToString(new File(
                "src/test/java/nablarch/test/tool/sanitizingcheck/util/testReadFileNoContent.txt"));
        assertEquals("", fileString);

        // 1行の場合
        fileString = FileUtil.readFileToString(new File(
                "src/test/java/nablarch/test/tool/sanitizingcheck/util/testReadFile1Row.txt"));
        assertEquals("test", fileString);

        // 2行の場合
        fileString = FileUtil.readFileToString(new File(
                "src/test/java/nablarch/test/tool/sanitizingcheck/util/testReadFile2Row.txt"));
        assertEquals("test\ntest2", fileString);

        // 3行以上の場合
        fileString = FileUtil.readFileToString(new File(
                "src/test/java/nablarch/test/tool/sanitizingcheck/util/testReadFile3Row.txt"));
        assertEquals("test\n" + "test2\n" + "test3", fileString);

    }

    /**
     * List<String>の比較を行う
     * 
     * @param expected 期待するList<String>
     * @param actual 実際のList<String>
     */
    private void assertListString(List<String> expected, List<String> actual) {
        
        assertEquals(expected.size(), actual.size());
        for (int i = 0; i < expected.size(); i++) {
            assertEquals(expected.get(i), actual.get(i));
        }
    }

}
