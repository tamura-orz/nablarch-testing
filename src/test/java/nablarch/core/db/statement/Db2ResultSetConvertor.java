package nablarch.core.db.statement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
* カバレッジをあげるために使用するResultSetConvertor実装クラス。
*
* @author hisaaki sioiri
*/
public class Db2ResultSetConvertor implements ResultSetConvertor {
    public Object convert(ResultSet rs, ResultSetMetaData rsmd, int columnIndex) throws SQLException {
        switch (rsmd.getColumnType(columnIndex)) {
            case java.sql.Types.SMALLINT:
                if (rs.getObject(columnIndex) == null) {
                    return null;
                } else {
                    return rs.getShort(columnIndex);
                }
            case java.sql.Types.CLOB:
            	return rs.getString(columnIndex);
            default:
                return rs.getObject(columnIndex);
        }}

    public boolean isConvertible(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
        return true;
    }
}
