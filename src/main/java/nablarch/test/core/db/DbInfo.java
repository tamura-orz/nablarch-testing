package nablarch.test.core.db;

import nablarch.core.util.annotation.Published;



/**
 * DBシステム情報取得インタフェース。
 *
 * @author Hisaaki Sioiri
 */
@Published(tag = "architect")
public interface DbInfo {

    /**
     * 主キーを取得する。
     *
     * @param tabName テーブル名
     * @return 指定されたテーブルの主キーのカラム名の配列
     */
    String[] getPrimaryKeys(String tabName);

    /**
     * カラム名を取得する。
     *
     * @param tabName テーブル名
     * @return 指定されたテーブルのカラム名の配列
     */
    String[] getColumns(String tabName);

    /**
     * カラムのデータ型を取得する。
     *
     * @param tabName テーブル名
     * @param columnName カラム名
     * @return java.sql.Types からの SQL 型
     */
    int getColumnType(String tabName, String columnName);

    /**
     * ユニークインデックスかどうか判定する。
     *
     * @param tabName テーブル名
     * @param colName カラム名
     * @return ユニークインデックスである場合は真を、その他の場合は偽を返却する。
     */
    boolean isUniqueIndex(String tabName, String colName);

    /**
     * 指定したカラムのサイズを取得する。<br>
     *
     * @param tabName テーブル名
     * @param colName カラム名
     * @return カラムサイズ
     */
    int getColumnLength(String tabName, String colName);

    /**
     * 自動計算列かどうか判定する。<br>
     *
     * @param tabName テーブル名
     * @param colName カラム名
     * @return 自動計算列である場合は真を、その他の場合は偽を返却する。
     */
    boolean isComputedColumn(String tabName, String colName);

    /**
     * 指定されたカラムのデータタイプが数値型かを判定する。
     *
     * @param tableName テーブル名
     * @param columnName カラム名
     * @return カラムタイプが数値の場合は、true
     */
    boolean isNumberTypeColumn(String tableName, String columnName);

    /**
     * 指定されたカラムのデータタイプが日付型かを判定する。<br>
     * {@link java.sql.Types}が以下のタイプの場合は、日付型<br>
     * <ul>
     * <li>java.sql.Types.DATE</li>
     * <li>java.sql.Types.TIME</li>
     * <li>java.sql.Types.TIMESTAMP</li>
     * </ul>
     *
     * @param tableName テーブル名
     * @param columnName カラム名
     * @return 日付型の場合は、true
     */
    boolean isDateTypeColumn(String tableName, String columnName);


    /**
     * 指定されたカラムのデータタイプがバイナリ型かを判定する。
     *
     * @param tableName テーブル名
     * @param columnName カラム名
     * @return カラムタイプがバイナリの場合は、true
     */
    boolean isBinaryTypeColumn(String tableName, String columnName);

    /**
     * 指定されたカラムのデータタイプがBoolean型かを判定する。
     *
     * @param tableName テーブル名
     * @param columnName カラム名
     * @return カラムタイプがBoolean型の場合は、true
     */
    boolean isBooleanTypeColumn(String tableName, String columnName);



    /**
     * NCHARを表す定数。<br/>
     * JDBC4.0から{@link java.sql.Types}に追加されているが、JDBC3.0ではサポートされていないため
     * ここで定義する。値は同じであるため互換性がある。
     */
    int NCHAR = -15;

    /**
     * NVARCHARを表す定数。<br/>
     * JDBC4.0から{@link java.sql.Types}に追加されているが、JDBC3.0ではサポートされていないため
     * ここで定義する。値は同じであるため互換性がある。
     */
    int NVARCHAR = -9;
    
    /**
     * NCLOBを表す定数。<br/>
     * JDBC4.0から{@link java.sql.Types}に追加されているが、JDBC3.0ではサポートされていないため
     * ここで定義する。値は同じであるため互換性がある。
     */
    int NCLOB = 2011;
}
