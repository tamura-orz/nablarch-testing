package nablarch.test.core.db;

import java.sql.Types;

/**
 * テスト用の{@link DefaultValues}実装クラス。<br/>
 *
 * @author Naoki Yamamoto
 */
public class MockDefaultValues extends BasicDefaultValues {
	/** {@inheritDoc} */
    @SuppressWarnings("fallthrough")
    @Override
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
        case Types.TIME:
        case Types.TIMESTAMP:
            return getDateValue();
            // BLOB
        case Types.BLOB:
            return getBinaryValue();
            //その他のバイナリカラムは一旦保留
        case Types.BINARY:
        case Types.LONGVARBINARY:
        case Types.VARBINARY:
            //throw new UnsupportedOperationException("not implemented yet. (binary)");
        	return getBinaryValue();
        case Types.BIT:
        case Types.BOOLEAN:
            return getBooleanValue();
        default:
        }
        return getUnknownValue(columnType, maxLength);
    }
    
    /** {@inheritDoc} */
    @Override
    protected String getUnknownValue(int columnType, int length) {
        switch (columnType) {
            case Types.LONGVARCHAR:
                return super.getCharValue(10);
            default:
                super.getUnknownValue(columnType, length);
        }
        return null;
    }
}
