package nablarch.test.core.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;

import nablarch.common.dao.DatabaseUtil;
import nablarch.core.util.annotation.Published;
import nablarch.core.util.map.CaseInsensitiveMap;

/**
 * 汎用の{@link DbInfo}実装クラス。
 *
 * @author T.Kawasaki
 */
@Published(tag = "architect")
public class GenericJdbcDbInfo implements DbInfo {

    /** 主キー情報 */
    private final Map<String, String[]> primaryKeyMap = new CaseInsensitiveMap<String[]>();

    /** カラム名 */
    private final Map<String, String[]> columnsMap = new CaseInsensitiveMap<String[]>();

    /** カラム桁数 */
    private final Map<String, Map<String, Integer>> lengthMap = new CaseInsensitiveMap<Map<String, Integer>>();

    /** カラムタイプ */
    private final Map<String, Map<String, Integer>> typeMap = new CaseInsensitiveMap<Map<String, Integer>>();

    /** ユニークインデックス */
    private final Map<String, String[]> uniqueIdxMap = new CaseInsensitiveMap<String[]>();

    /** スキーマ名 */
    private String schemaName;

    /** スキーマ名 */
    private String originalSchemaName;

    /** データソース */
    private DataSource dataSource;

    /** {@inheritDoc} */
    public String[] getPrimaryKeys(String table) {
        String[] primaryKeys = primaryKeyMap.get(table);
        if (primaryKeys == null) {
            try {
                primaryKeys = doGetPrimaryKeys(table);
            } catch (SQLException e) {
                throw new RuntimeException("can't get primary keys. table=" + table + "]", e);
            }
            primaryKeyMap.put(table, primaryKeys);
        }
        return primaryKeys;
    }


    /**
     * 主キーを取得する。
     *
     * @param table テーブル名
     * @return 主キーカラム名
     * @throws SQLException 予期しない例外
     */
    private String[] doGetPrimaryKeys(String table) throws SQLException {
        Map<Integer, String> primaryKeys = new TreeMap<Integer, String>();
        
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            table = DatabaseUtil.convertIdentifiers(metaData, table);
            resultSet = metaData.getPrimaryKeys(null, getSchema(metaData), table);
            while (resultSet.next()) {
                primaryKeys.put(
                        resultSet.getInt("KEY_SEQ"),
                        resultSet.getString("COLUMN_NAME"));
            }

        } finally {
            resultSet.close();
            connection.close();
        }
        return primaryKeys.values().toArray(new String[primaryKeys.size()]);
    }


    /** {@inheritDoc} */
    public String[] getColumns(String table) {
        String[] columns = columnsMap.get(table);
        if (columns == null) {
            try {
                columns = doGetColumns(table);
            } catch (SQLException e) {
                throw new RuntimeException("can't get columns. table=" + table + "]", e);
            }
            columnsMap.put(table, columns);
        }
        return columns;
    }

    /**
     * カラム一覧を取得する。
     *
     * @param table テーブル名
     * @return カラム一覧
     * @throws SQLException 予期しない例外
     */
    private String[] doGetColumns(String table) throws SQLException {
        Map<Integer, String> columns = new TreeMap<Integer, String>();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            table = DatabaseUtil.convertIdentifiers(metaData, table);
            resultSet = metaData.getColumns(null, getSchema(metaData), table, null);
            while (resultSet.next()) {
                columns.put(
                        resultSet.getInt("ORDINAL_POSITION"),
                        resultSet.getString("COLUMN_NAME"));
            }
        } finally {
            resultSet.close();
            connection.close();
        }
        return columns.values().toArray(new String[columns.size()]);
    }


    /** {@inheritDoc} */
    public int getColumnType(String table, String column) {
        if (typeMap.get(table) == null) {
            loadMetaData(table);
        }

        Map<String, Integer> columnTypes = typeMap.get(table);
        Integer type = columnTypes.get(column);
        if (type == null) {
            throw new IllegalArgumentException(
                    "can't get column type. table=[" + table + "]. column=[" + column + "]");
        }
        return type;
    }

    /** {@inheritDoc} */
    public boolean isUniqueIndex(String table, String column) {
        String[] indices = getUniqueIndices(table);
        for (String index : indices) {
            if (index.equalsIgnoreCase(column)) {
                return true;
            }
        }
        return false;
    }

    /**
     * ユニークインデックス（主キー以外）を一覧を取得する。
     *
     * @param table テーブル名
     * @return ユニークインデックス一覧
     */
    private String[] getUniqueIndices(String table) {
        String[] uniqueIndices = uniqueIdxMap.get(table);
        if (uniqueIndices == null) {
            try {
                uniqueIndices = doGetUniqueIndices(table);
            } catch (SQLException e) {
                throw new RuntimeException("can't get columns. table=[" + table + "]", e);
            }
            uniqueIdxMap.put(table, uniqueIndices);
        }
        return uniqueIndices;
    }

    /**
     * ユニークインデックス（主キー以外）を取得する。
     *
     * @param table テーブル
     * @return ユニークインデックス一覧
     * @throws SQLException 予期しない例外
     */
    private String[] doGetUniqueIndices(String table) throws SQLException {
        List<String> indexedColumns = new ArrayList<String>();
        Connection connection = null;
        ResultSet resultSet = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getIndexInfo(
                    null,
                    getSchema(metaData),
                    DatabaseUtil.convertIdentifiers(metaData, table),
                    true,
                    false);
            while (resultSet.next()) {
                String indexedColumn = resultSet.getString("COLUMN_NAME");
                // 主キーでないなら追加
                if (indexedColumn != null && !isPrimaryKey(table, indexedColumn)) {
                    indexedColumns.add(indexedColumn);
                }
            }
        } finally {
            resultSet.close();
            connection.close();
        }
        return indexedColumns.toArray(new String[indexedColumns.size()]);
    }

    /**
     * 主キーカラムかどうか判定する。
     *
     * @param tableName テーブル名
     * @param column    カラム名
     * @return 判定結果
     */
    private boolean isPrimaryKey(String tableName, String column) {
        String[] pks = getPrimaryKeys(tableName);
        for (String pk : pks) {
            if (column.equalsIgnoreCase(pk)) {
                return true;
            }
        }
        return false;
    }

    /** {@inheritDoc} */
    public int getColumnLength(String table, String column) {
        if (lengthMap.get(table) == null) {
            this.loadMetaData(table);
        }
        return lengthMap.get(table).get(column);
    }


    /** {@inheritDoc} */
    public boolean isComputedColumn(String tabName, String colName) {
        return false;
    }


    /** {@inheritDoc} */
    public final boolean isNumberTypeColumn(String tableName, String columnName) {
        int columnType = getColumnType(tableName, columnName);
        return isNumberTypeColumn(columnType);
    }

    /**
     * 数値型かどうか判定する。
     *
     * @param columnType カラム型
     * @return 判定結果
     */
    @SuppressWarnings("fallthrough")
    protected boolean isNumberTypeColumn(int columnType) {
        switch (columnType) {
            case Types.DECIMAL:
            case Types.DOUBLE:
            case Types.BIGINT:
            case Types.FLOAT:
            case Types.INTEGER:
            case Types.NUMERIC:
            case Types.SMALLINT:
            case Types.TINYINT:
            case Types.REAL:
                return true;
            default:
                return false;
        }
    }

    /** {@inheritDoc} */
    public final boolean isDateTypeColumn(String tableName, String columnName) {
        int columnType = getColumnType(tableName, columnName);
        return isDateTypeColumn(columnType);
    }

    /**
     * 日付型かどうか判定する。
     *
     * @param columnType カラム型
     * @return 判定結果
     */
    @SuppressWarnings("fallthrough")
    protected boolean isDateTypeColumn(int columnType) {
        switch (columnType) {
            case Types.DATE:
            case Types.TIME:
            case Types.TIMESTAMP:
                return true;
            default:
                return false;
        }
    }


    /** {@inheritDoc} */
    public final boolean isBinaryTypeColumn(String tableName, String columnName) {
        int columnType = getColumnType(tableName, columnName);
        return isBinaryTypeColumn(columnType);
    }

    /**
     * バイナリ型であるかどうか判定する。
     *
     * @param columnType カラム型
     * @return 判定結果
     */
    @SuppressWarnings("fallthrough")
    protected boolean isBinaryTypeColumn(int columnType) {
        switch (columnType) {
            case Types.BINARY:
            case Types.BLOB:
            case Types.LONGVARBINARY:
            case Types.VARBINARY:
                return true;
            default:
                return false;
        }
    }

    /** {@inheritDoc} */
    public final boolean isBooleanTypeColumn(String tableName, String columnName) {
        int columnType = getColumnType(tableName, columnName);
        return isBooleanTypeColumn(columnType);
    }

    /**
     * Boolean型であるかどうか判定する。
     *
     * @param columnType カラム型
     * @return 判定結果
     */
    @SuppressWarnings("fallthrough")
    protected boolean isBooleanTypeColumn(int columnType) {
        switch (columnType) {
            case Types.BIT:
            case Types.BOOLEAN:
                return true;
            default:
                return false;
        }
    }


    /**
     * COLNAME,TYPENAME,LENGTHをそれぞれインスタンス変数にセットする。<br>
     * DatabaseMetaDataからカラムの情報を取得する。
     *
     * @param tableName テーブル名
     */
    private void loadMetaData(String tableName) {

        Map<String, Integer> columnTypes = new CaseInsensitiveMap<Integer>();    // カラム型
        Map<String, Integer> columnLengths = new CaseInsensitiveMap<Integer>();  // カラム長
        Map<Integer, String> columnNames = new TreeMap<Integer, String>();
        ResultSet resultSet = null;
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();
            resultSet = metaData.getColumns(
                    null,
                    getSchema(metaData),
                    DatabaseUtil.convertIdentifiers(metaData, tableName),
                    null);
            while (resultSet.next()) {
                String name = resultSet.getString("COLUMN_NAME");
                Integer type = getColumnType(resultSet);
                Integer length = resultSet.getInt("COLUMN_SIZE");
                final int position = resultSet.getInt("ORDINAL_POSITION");
                columnNames.put(position, name);
                columnTypes.put(name, type);
                columnLengths.put(name, length);
            }
            columnsMap.put(tableName, columnNames.values().toArray(new String[columnNames.size()]));
            typeMap.put(tableName, columnTypes);
            lengthMap.put(tableName, columnLengths);
        } catch (SQLException e) {
            throw new RuntimeException("can't get metadata. table =" + tableName + "]", e);
        } finally {
            closeQuietly(resultSet);
            closeQuietly(connection);
        }
    }

    /**
     * カラム型を取得する。
     *
     * @param resultSetOfGetColumns {@link DatabaseMetaData#getColumns(String, String, String, String)}で
     *                              取得した{@link ResultSet}
     * @return カラム型
     * @throws SQLException 予期しない例外
     */
    protected int getColumnType(ResultSet resultSetOfGetColumns) throws SQLException {
        return resultSetOfGetColumns.getInt("DATA_TYPE");
    }


    /**
     * データソースを設定する。
     *
     * @param dataSource データソース
     * @throws SQLException 予期しない例外
     */
    public void setDataSource(DataSource dataSource) throws SQLException {
        this.dataSource = dataSource;
    }

    /**
     * スキーマを設定する。
     *
     * @param schema スキーマ
     */
    public void setSchema(String schema) {
        this.originalSchemaName = schema;
    }

    /**
     * スキーマを取得する。
     *
     * @return スキーマ
     */
    private String getSchema(DatabaseMetaData metaData) {
        if (schemaName == null) {
            schemaName = DatabaseUtil.convertIdentifiers(metaData, originalSchemaName);
        }
        return schemaName;
    }

    /**
     * ResultSetをクローズする。例外は発生させない。
     * @param rs クローズ対象
     */
    private void closeQuietly(ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException ignored) { // SUPPRESS CHECKSTYLE
                // NOP
            }
        }
    }
    
    /**
     * Connectionをクローズする。例外は発生させない。
     * @param conn クローズ対象
     */
    private void closeQuietly(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException ignored) { // SUPPRESS CHECKSTYLE
                // NOP
            }
        }
    }
}
