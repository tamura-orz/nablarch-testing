package nablarch.test.core.db;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;
import org.junit.Test;

import nablarch.core.util.BinaryUtil;
import static nablarch.test.Assertion.fail;
import static org.junit.Assert.assertEquals;

/**
 * @author T.Kawasaki
 */
public class BasicDefaultValuesTest {

    private BasicDefaultValues target = new BasicDefaultValues();

    /** 数値型 */
    private static final int[] NUMBER_TYPES = {
            Types.BIGINT,
            Types.DECIMAL,
            Types.DOUBLE,
            Types.FLOAT,
            Types.NUMERIC,
            Types.INTEGER,
            Types.REAL,
            Types.SMALLINT,
            Types.TINYINT
    };

    /** 数値型のデフォルト値は0であること。 */
    @Test
    public void testGetValueOfNumber() {

        for (int t : NUMBER_TYPES) {
            assertEquals("0", target.get(t, 10));
        }
    }

    /** 設定されたデフォルト値が返却されること。 */
    @Test
    public void testSetNumberValue() {
        target.setNumberValue("9");
        for (int t : NUMBER_TYPES) {
            assertEquals("9", target.get(t, 1));
        }
    }

    /** カラム長に応じてデフォルト値が切り詰められること。 */
    @Test
    public void testGetLimitedValueOfNumber() {
        target.setNumberValue("1234567890");
        for (int t : NUMBER_TYPES) {
            assertEquals("12345678", target.get(t, 8));
        }
    }

    /** 日付型 */
    private static final int[] DATE_TYPES = {
            Types.DATE,
            Types.TIME,
            Types.TIMESTAMP
    };

    /** 日付型のデフォルト値はepocであること。 */
    @Test
    public void testGetValueOfDate() {
        assertEquals(0L, ((Date) target.get(Types.DATE, 1)).getTime());
        assertEquals(0L, ((Time) target.get(Types.TIME, 1)).getTime());
        assertEquals(0L, ((Timestamp) target.get(Types.TIMESTAMP, 1)).getTime());
    }

    /** 設定されたデフォルト値が返却されること。 */
    @Test
    public void testSetDateValue() {
        target.setDateValue("2010-11-22 01:23:45.123456789");

        assertEquals("2010-11-22", target.get(Types.DATE, 1)
                .toString());
        assertEquals("01:23:45", target.get(Types.TIME, 1).toString());
        assertEquals("2010-11-22 01:23:45.123456789", target.get(Types.TIMESTAMP, 1).toString());
    }

    /** 固定長文字型 */
    private static final int[] CHAR_TYPES = {
            Types.CHAR,
            DbInfo.NCHAR
    };

    /**
     * 固定長文字型のデフォルト値は半角スペースであること。
     *
     * 指定した長さの文字列が取得できること。
     */
    @Test
    public void testGetValueOfChar() {
        String tenSpaces = "          ";
        assertEquals(10, tenSpaces.length());
        for (int t : CHAR_TYPES) {
            assertEquals(tenSpaces, target.get(t, 10));
        }
    }

    /** 可変長文字型 */
    private static final int[] VARCHAR_TYPES = {
            Types.VARCHAR,
            DbInfo.NVARCHAR
    };

    /**
     * 可変長文字列のデフォルト値は半角スペースであること。
     *
     * 可変長文字列の場合は、文字列長1で取得できること。
     */
    @Test
    public void testGetValueOfVarchar() {
        String s = " ";
        assertEquals(1, s.length());
        for (int t : VARCHAR_TYPES) {
            assertEquals(s, target.get(t, 10));
        }
    }

    /** CLOB型 **/
    private static final int[] CLOB_TYPES = {
            Types.CLOB,
            Types.LONGVARCHAR,
            DbInfo.NCLOB
    };

    /**
     * CLOBのデフォルト値は半角スペースであること。
     *
     * CLOBの場合は、文字列長1で取得できること。
     */
    @Test
    public void testGetValueOfClob() {
        String s = " ";
        assertEquals(1, s.length());
        for (int t : CLOB_TYPES) {
            assertEquals(s, target.get(t, 10));
        }
    }

    /** binary型 **/
    private static final int[] BINARY_TYPES = {
            Types.BLOB,
            Types.BINARY,
            Types.LONGVARBINARY,
            Types.VARBINARY
    };

    /**
     * BLOBのデフォルト値は半角スペースであること。
     *
     * BLOBの場合は、0埋め10byteのHEX表現で取得できること。
     */
    @Test
    public void testGetValueOfBlob() {
        String b = BinaryUtil.convertToHexString(new byte[10]);
        assertEquals(20, b.length());
        for (int t : BINARY_TYPES) {
            assertEquals(b, target.get(t, 10));
        }
    }

    /** 設定されたデフォルト値が返却されること。 */
    @Test
    public void testSetCharValue() {
        target.setCharValue(".");
        assertEquals(".", target.get(Types.VARCHAR, 10));
        assertEquals("..........", target.get(Types.CHAR, 10));
    }

    /**
     * 2文字以上の文字列がデフォルト値として設定できないこと。
     */
    @Test(expected = IllegalArgumentException.class)
    public void setCharValueTooLong() {
        target.setCharValue("ab"); // must be a charcter.
    }

    /**
     * 1文字未満の文字列がデフォルト値として設定できないこと。
     */
    @Test(expected = IllegalArgumentException.class)
    public void setCharValueTooShort() {
        target.setCharValue(""); // must be a charcter.
    }

    /**
     * nullがデフォルト値として設定できないこと。
     */
    @Test(expected = IllegalArgumentException.class)
    public void setCharValueNull() {
        target.setCharValue(null); // must be a charcter.
    }

    /** Boolean型 **/
    private static final int[] BOOLEAN_TYPES = {
            Types.BIT,
            Types.BOOLEAN
    };

    /**
     * Booleanのデフォルト値はfalseであること。
     */
    @Test
    public void testGetValueOfBoolean() {
        for (int t : BOOLEAN_TYPES) {
            assertEquals(false, target.get(t, 10));
        }
    }


    /** その他の型 */
    private static final int[] UNSUPPORTED_TYPES = {
            Types.ARRAY,
            Types.DATALINK,
            Types.DISTINCT,
            Types.JAVA_OBJECT,
            Types.NULL,
            Types.OTHER,
            Types.REF,
            Types.STRUCT,
    };

    /** サポートされない型が渡されたとき例外が発生すること。 */
    @Test
    public void testGetUnsupported() {
        for (int t : UNSUPPORTED_TYPES) {
            try {
                target.get(t, 1);
            } catch (UnsupportedOperationException e) {
                continue;
            }
            fail("type [", t, "] must throw UnsupportedOperationException.");
        }
    }
}
