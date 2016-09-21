package nablarch.test.core.db;

/**
 * JDK5でOracleを使用する場合の{@link DefaultValues}実装クラス。<br/>
 * NVARCHAR2、NCHAR、NCLOBが{@link java.sql.Types#OTHER}にマッピングする問題に対処する為に使用する。
 *
 * @author T.Kawasaki
 * @see Oracle15DbInfo
 */
public class Oracle15DefaultValues extends BasicDefaultValues {

    /** {@inheritDoc} */
    @Override
    protected String getUnknownValue(int columnType, int length) {
        switch (columnType) {
            case Oracle15DbInfo.NCHAR:
                return super.getCharValue(length);
            case Oracle15DbInfo.NVARCHAR:
                return super.getVarcharValue(length);
            case Oracle15DbInfo.NCLOB:
                return super.getClobValue(length);
            default:
                super.getUnknownValue(columnType, length);
        }
        return null;
    }
}
