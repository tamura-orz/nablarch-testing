package nablarch.test.core.db;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import nablarch.core.db.connection.TransactionManagerConnection;
import nablarch.core.repository.SystemRepository;

/**
 * {@link TableData}をソートするクラス。
 * テーブルの依存関係（FK）に則ってソートを行う。
 *
 * コンポーネント定義に{@literal nablarch.suppress-table-sort}というキーで
 * 真偽値{@code true}が設定されていた場合、ソートが行われず処理速度が改善される。
 * （DBにFKが設定されていない場合にのみ使用すること。）
 *
 * @author T.Kawasaki
 * @see EntityDependencyParser
 */
class TableDataSorter {

    /**
     * スキーマ名のリポジトリキー。
     */
    private static final String NABLARCH_DB_SCHEMA_REPOSITORY_KEY = "nablarch.db.schema";

    /** ソートをスキップするかどうか判定するためのキー */
    private static final String SUPPRESS_TABLE_SORT_KEY = "nablarch.suppress-table-sort";

    /**
     * テーブルの依存関係（FK）に則ってソートを行い、その結果を返却する（非破壊的メソッド）。
     * 親テーブルほど先頭に位置する。
     *
     * @param unordered ソート前のリスト
     * @param tranConn DBのメタ情報を取得するためのコネクション
     * @return ソート後のリスト
     */
    static List<TableData> sort(List<TableData> unordered, TransactionManagerConnection tranConn) {
        String schemaName = SystemRepository.getString(NABLARCH_DB_SCHEMA_REPOSITORY_KEY);
        if (schemaName == null) {
            throw new RuntimeException("schema name not specified.\n"
                    + "please set \"" + NABLARCH_DB_SCHEMA_REPOSITORY_KEY 
                    + "\" value in SystemRepository");
        }
        TableDataSorter sorter = new TableDataSorter(tranConn.getConnection(), schemaName);
        return sorter.sortTableDataByFK(unordered);
    }

    /**
     * テーブルの依存関係（FK）に則ってソートを行い、その結果を返却する（非破壊的メソッド）。
     * 親テーブルほど先頭に位置する。
     *
     * @param unordered ソート前のリスト
     * @param tranConn DBのメタ情報を取得するためのコネクション
     * @return ソート後のリスト
     */
    static List<TableData> reversed(List<TableData> unordered, TransactionManagerConnection tranConn) {
        List<TableData> sorted = sort(unordered, tranConn);
        Collections.reverse(sorted);
        return sorted;
    }

    /** コネクション */
    private final Connection conn;

    /** スキーマ名 */
    private final String schema;

    /** ソート済みのテーブル名一覧 */
    private List<String> sortedTables;

    /** 初回起動フラグ */
    private boolean isFirstCall = true;

    /**
     * コンストラクタ。
     * @param conn コネクション
     * @param schema スキーマ名
     */
    TableDataSorter(Connection conn, String schema) {
        this.conn = conn;
        this.schema = schema;
    }

    private static boolean isSortSuppressed() {
        return SystemRepository.getBoolean(SUPPRESS_TABLE_SORT_KEY);
    }

    /**
     * テーブルの依存関係（FK）に則ってソートを行い、その結果を返却する（非破壊的メソッド）。
     * 親テーブルほど先頭に位置する。
     *
     * @param unordered ソート前のリスト
     * @return ソート後のリスト
     */
    List<TableData> sortTableDataByFK(List<TableData> unordered) {
        // 依存関係を考慮し読み込むファイル順をソートする
        return getSorted(unordered, new TableDataComparator());
    }

    /**
     * テーブルの依存関係（FK）に則ってソートを行い、その結果を返却する（非破壊的メソッド）。
     * 親テーブルほど先頭に位置する。
     *
     * @param unordered ソート前のリスト
     * @return ソート後のリスト
     */
    List<String> sortTableNamesByFK(List<String> unordered) {
        return getSorted(unordered, new TableNameComparator());
    }

    /**
     * テーブルの依存関係（FK）に則ってソートを行い、その結果を返却する（非破壊的メソッド）。
     * 親テーブルほど先頭に位置する。
     *
     * @param unordered ソート前のリスト
     * @param comparator 比較に使用する{@link Comparator}
     * @param <T> 比較対象となるオブジェクトの形
     * @return ソート後のリスト
     */
    private <T> List<T> getSorted(List<T> unordered, Comparator<T> comparator) {
        // ソートが抑制されている場合は、元のリストをそのまま返却する。
        // ただし、TableDataSorter.reversedメソッドで、ここで返したリストを変更するので、
        // 新しいインスタンスを生成して返す。
        if (isSortSuppressed()) {
            return new ArrayList<T>(unordered);
        }
        // 依存関係を考慮し読み込むファイル順をソートする
        List<T> tables = new ArrayList<T>(unordered);
        Collections.sort(tables, comparator);
        return tables;
    }

    /**
     * ソート済みテーブル一覧を取得する。
     *
     * @return ソート済みテーブル一覧
     */
    private List<String> getSortedTableList() {
        EntityDependencyParser parser = new EntityDependencyParser();
        parser.parse(conn, schema);
        return parser.getTableList();
    }

    /**
     * ソート済みテーブル一覧上の位置（インデックス）を取得する。
     * 親テーブルほど小さいインデックスが返却される。
     *
     * @param tableName テーブル名
     * @return インデックス
     */
    private int getIndex(String tableName) {
        initData();
        for (int i = 0; i < sortedTables.size(); i++) {
            if (tableName.equalsIgnoreCase(sortedTables.get(i))) {
                return i;
            }
        }
        return 0;
    }

    /**
     * {@link TableData#getTableName()}とソート済みテーブル一覧を使用して
     * 比較を行う{@link Comparator}実装クラス。
     */
    private class TableDataComparator implements Comparator<TableData> {
        /** {@inheritDoc} */
        @Override
        public int compare(TableData t1, TableData t2) {
            return getIndex(t1.getTableName()) - getIndex(t2.getTableName());
        }
    }

    /**
     * テーブル名とソート済みテーブル一覧を使用して
     * 比較を行う{@link Comparator}実装クラス。
     */
    private class TableNameComparator implements Comparator<String> {
        /** {@inheritDoc} */
        @Override
        public int compare(String o1, String o2) {
            return getIndex(o1) - getIndex(o2);
        }
    }

    /** データの初期化を行う。*/
    private synchronized void initData() {
        if (isFirstCall) {
            this.sortedTables = getSortedTableList();
            isFirstCall = false;
        }            
    }
}
