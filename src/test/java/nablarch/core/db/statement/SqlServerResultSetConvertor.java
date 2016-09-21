package nablarch.core.db.statement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

/**
 * カバレッジをあげるために使用するResultSetConvertor実装クラス。
 *
 * @author hisaaki sioiri
 */
public class SqlServerResultSetConvertor implements ResultSetConvertor {

    public Object convert(ResultSet rs, ResultSetMetaData rsmd, int columnIndex) throws SQLException {
        switch (rsmd.getColumnType(columnIndex)) {
            case Types.BINARY:
            case Types.VARBINARY:
                if (rs.getObject(columnIndex) == null) {
                    return null;
                } else {
                    return rs.getBinaryStream(columnIndex);
                }
            default:
                return rs.getObject(columnIndex);
        }
    }

    public boolean isConvertible(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
        return true;
    }
}
