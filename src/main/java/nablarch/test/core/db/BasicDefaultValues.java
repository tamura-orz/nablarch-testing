package nablarch.test.core.db;

import nablarch.core.util.BinaryUtil;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;

import nablarch.test.NablarchTestUtils;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

/**
 * DefaultValuesの基本実装クラス。<br/>
 * 明示的にデフォルト値が設定された場合は、その値を返却する。 そうでない場合は以下の値をデフォルト値として返却する。
 * 
 * <pre>
 * +----------+--------------------+
 * | カラム   | デフォルト値       |
 * +==========+====================+
 * | 数値型   | 0                  |
 * +----------+--------------------+
 * | 文字列型 | " " (半角スペース) |
 * +----------+--------------------+
 * | 日付型   | システム日時       |
 * +----------+--------------------+
 * | 論理型   | false              |
 * +----------+--------------------+
 * </pre>
 * 
 * @author T.Kawasaki
 */
@Published(tag = "architect")
public class BasicDefaultValues implements DefaultValues {

    /** 数値型のデフォルト値 */
    private String numberValue = "0";

    /** 日付型のデフォルト値 */
    private Timestamp dateValue = null;

    /** 文字列型のデフォルト値 */
    private String charValue = " ";

    /** バイナリ型のデフォルト値 */
    private String binaryValue = BinaryUtil.convertToHexString(new byte[10]);

    /** {@inheritDoc} */
    @SuppressWarnings("fallthrough")
    public Object get(int columnType, int maxLength) {
        switch (columnType) {
        // 文字列
        case Types.CHAR:
        case DbInfo.NCHAR:
            return getCharValue(maxLength);
        case Types.VARCHAR:
        case DbInfo.NVARCHAR:
            return getVarcharValue(maxLength);
        case Types.CLOB:
        case Types.LONGVARCHAR:
        case DbInfo.NCLOB:
            return getClobValue(maxLength);
            // 数値
        case Types.DECIMAL:
        case Types.DOUBLE:
        case Types.BIGINT:
        case Types.FLOAT:
        case Types.INTEGER:
        case Types.NUMERIC:
        case Types.SMALLINT:
        case Types.TINYINT:
        case Types.REAL:
            return getNumberValue(maxLength);
            // 日付
        case Types.DATE:
            return new Date(getDateValue().getTime());
        case Types.TIME:
            return new Time(getDateValue().getTime());
        case Types.TIMESTAMP:
            return getDateValue();
            // BLOB
        case Types.BLOB:
        case Types.BINARY:
        case Types.LONGVARBINARY:
        case Types.VARBINARY:
            return getBinaryValue();
            // Boolean
        case Types.BIT:
        case Types.BOOLEAN:
            return getBooleanValue();
        default:
        }
        return getUnknownValue(columnType, maxLength);
    }

    /**
     * 文字列型のデフォルト値を設定する。
     * 
     * @param charValue 文字列型のデフォルト値
     */
    public void setCharValue(String charValue) {
        if (charValue == null || charValue.length() != 1) {
            throw new IllegalArgumentException("charValue must be a character. but was ["
                    + charValue + "]");
        }
        this.charValue = charValue;
    }

    /**
     * 日付型のデフォルト値を設定する。
     * 
     * @param dateValue 日付型のデフォルト値（JDBC タイムスタンプエスケープ形式）
     * @see Timestamp#valueOf(String)
     */
    public void setDateValue(String dateValue) {
        this.dateValue = Timestamp.valueOf(dateValue);
    }

    /**
     * 数値型のデフォルト値を設定する。
     * 
     * @param numberValue 数値型のデフォルト値
     */
    public void setNumberValue(String numberValue) {
        this.numberValue = numberValue;
    }

    /**
     * 日付型のデフォルト値を取得する。<br/>
     * デフォルト値が明示的に設定されている場合はその値を、
     * そうでない場合は、epoch（1970-01-01 00:00:00.0）をデフォルト値として返却する。
     * 
     * @return 日付型のデフォルト値
     */
    protected Timestamp getDateValue() {
        return (dateValue == null) ? new Timestamp(0L) : dateValue;
    }

    /**
     * 可変長文字列型のデフォルト値を取得する。<br/>
     * デフォルト値をそのまま返却する。
     * 
     * @param length 本メソッドでは使用しない（サブクラスを考慮して付与）
     * @return 文字列型のデフォルト値
     */
    protected String getVarcharValue(int length) {
        return charValue;
    }

    /**
     * 固定長文字列型のデフォルト値を取得する。<br/>
     * デフォルト値を指定されたカラム長まで増幅して返却する。
     * 
     * @param length カラム長
     * @return 文字列型のデフォルト値
     */
    protected String getCharValue(int length) {
        return StringUtil.repeat(charValue, length);
    }

    /**
     * 数値型のデフォルト値を取得する。<br/>
     * デフォルト値が明示的に設定されている場合はその値を、 
     * 指定されたカラム長まで切り詰めて返却する。 
     * そうでない場合は、0をデフォルト値として返却する。
     * 
     * @param length カラム長
     * @return 数値型のデフォルト値
     */
    protected String getNumberValue(int length) {
        return NablarchTestUtils.limit(numberValue, length);
    }

    /**
     * Clob型のデフォルト値を取得する。<br/>
     * デフォルト値をそのまま返却する。
     * 
     * @param length 本メソッドでは使用しない（サブクラスを考慮して付与）
     * @return 文字列型のデフォルト値
     */
    protected String getClobValue(int length) {
        return charValue;
    }

    /**
     * バイナリ型のデフォルト値を取得する。<br/>
     * 指定した長さのbyte配列を生成し、返却する。
     * 
     * @return バイナリ型のデフォルト値
     */
    protected Object getBinaryValue() {
        return binaryValue;
    }

    /**
     * Boolean型のデフォルト値を取得する。<br/>
     *
     * @return Boolean型のデフォルト値
     */
    protected Boolean getBooleanValue() {
        return false;
    }

    /**
     * 不明な型の場合のデフォルト値を取得する。<br/>
     * 本実装では例外を送出する。
     * 
     * @param columnType カラム型
     * @param length カラム長
     * @return 値は返却されない
     * @throws UnsupportedOperationException 必ず送出される
     */
    protected String getUnknownValue(int columnType, int length)
        throws UnsupportedOperationException {
        throw new UnsupportedOperationException("can't generate value of [" + columnType + "]");
    }

}
