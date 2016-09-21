package nablarch.test;

import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * {@link FixedBusinessDateProvider}クラスのテストクラス
 * @author Miki Habu
 */
public class FixedBusinessDateProviderTest {

    private FixedBusinessDateProvider target = new FixedBusinessDateProvider();
    
    /**
     * {@link FixedBusinessDateProvider#getDate()}のテスト
     */
    @Test
    public void testGetDate() {
        // カバレッジ用にここで呼び出しておく。
        target.initialize();
        Map<String, String> fixedDate = new HashMap<String, String>();
        fixedDate.put("00", "20110101");
        fixedDate.put("01", "20110102");
        fixedDate.put("02", "20110103");
        target.setFixedDate(fixedDate);
        target.setDefaultSegment("00");
        assertEquals("20110101", target.getDate());
    }
    
    /**
     * {@link FixedBusinessDateProvider#getAllDate()}のテスト
     */
    @Test
    public void testGetAllDate(){
        Map<String, String> fixedDate = new HashMap<String, String>();
        fixedDate.put("00", "20110101");
        fixedDate.put("01", "20110102");
        fixedDate.put("02", "20110103");
        target.setFixedDate(fixedDate);

        assertEquals(fixedDate, target.getAllDate());        
    }
    
    /**
     * {@link FixedBusinessDateProvider#getAllDate()}のテスト<br>
     * 異常系
     */
    @Test
    public void testGetAllDateErr(){
        // 固定日付を初期化していないとき
        try {
            target.getAllDate();
            fail();
        } catch (IllegalStateException e) {
            assertEquals("fixed date was not initialized.", e.getMessage());
        }
    }

    /**
     * {@link FixedBusinessDateProvider#getDate(String)}のテスト
     */
    @Test
    public void testGetDateBySegment() {
        // 正常系
        Map<String, String> fixedDate = new HashMap<String, String>();
        fixedDate.put("00", "20110101");
        fixedDate.put("01", "20110102");
        fixedDate.put("02", "20110103");
        target.setFixedDate(fixedDate);

        assertEquals("20110103", target.getDate("02"));
    }

    /**
     * {@link FixedBusinessDateProvider#getDate(String)}のテスト<br>
     * 異常系
     */
    @Test
    public void testGetDateBySegmentErr() {
        // 固定日付を初期化していないとき
        try {
            target.getDate("03");
            fail("test1");
        } catch (IllegalStateException e) {
            assertEquals("fixed date was not initialized.", e.getMessage());
        }

        // 設定していない区分を与えたとき
        try {
            Map<String, String> fixedDate = new HashMap<String, String>();
            fixedDate.put("00", "20110101");
            fixedDate.put("01", "20110102");
            fixedDate.put("02", "20110103");
            target.setFixedDate(fixedDate);

            target.getDate("03");
            fail("test2");
        } catch (IllegalStateException e) {
            assertEquals("segment was not found. segment:03.", e.getMessage());
        }

        // 取得した文字列の長さが0だったとき
        try {
            Map<String, String> fixedDate = new HashMap<String, String>();
            fixedDate.put("00", "20110101");
            fixedDate.put("01", "20110102");
            fixedDate.put("02", "");
            target.setFixedDate(fixedDate);

            target.getDate("02");
            fail("test3");
        } catch (IllegalStateException e) {
            assertEquals("segment was not found. segment:02.", e.getMessage());
        }
    }

    /**
     * {@link FixedBusinessDateProvider#setDate(String,String)}のテスト<br>
     * エラーが発生することを確認
     */
    @Test
    public void testSetDate() {
        try {
            target.setDate("00", "20500101");
            fail();
        } catch (UnsupportedOperationException e) {
            assertEquals("fixed date can not change.", e.getMessage());
        }
    }

}
