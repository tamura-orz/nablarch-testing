package nablarch.core.db.statement;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * postgreSQLデータベース用のSELECT結果の項目値を変換するクラス。
 *
 * @author Naoki Yamamoto
 */
public class PostgreResultSetConvertor implements ResultSetConvertor {

    /** {@inheritDoc} */
    public Object convert(ResultSet rs, ResultSetMetaData rsmd, int columnIndex) throws SQLException {
    	return rs.getObject(columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    public boolean isConvertible(ResultSetMetaData rsmd, int columnIndex) throws SQLException {
    	return true;
    }
}
