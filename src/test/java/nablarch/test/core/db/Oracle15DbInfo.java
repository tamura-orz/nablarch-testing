package nablarch.test.core.db;


import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * JDK5でOracleを使用する場合の{@link DbInfo}実装クラス。
 * <p/>
 * NVARCHAR2、NCHAR、NCLOBが{@link Types#OTHER}にマッピングする問題に対処する為に使用する。
 * JDK6の場合はこの問題は発生しないので、{@link GenericJdbcDbInfo}を使用するとよい。
 *
 * @author T.Kawasaki
 * @see GenericJdbcDbInfo
 */
public class Oracle15DbInfo extends GenericJdbcDbInfo {

    /**
     * {@inheritDoc}
     * <p/>
     * 取得したカラム型が{@link Types#OTHER}の場合、以下の処理を行う。
     * <ul>
     * <li>カラム型名を取得する。</li>
     * <li>カラム型名がNVARCHAR2の場合、{@link #NVARCHAR}を返却する。</li>
     * <li>カラム型名がNCHARの場合、{@link #NCHAR}を返却する。</li>
     * <li>カラム型名がNCLOBの場合、{@link #NCLOB}を返却する。</li>
     * </ul>
     */
    @Override
    protected int getColumnType(ResultSet resultSetOfGetColumns) throws SQLException {
        int type = super.getColumnType(resultSetOfGetColumns);
        if (type == Types.OTHER) {
            String typeName = resultSetOfGetColumns.getString("TYPE_NAME");
            if (typeName.equals("NVARCHAR2")) {
                type = NVARCHAR;
            } else if (typeName.equals("NCHAR")) {
                type = NCHAR;
            } else if (typeName.equals("NCLOB")) {
                type = NCLOB;
            }
        }
        return type;
    }
}
