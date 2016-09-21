package nablarch.test.core.db;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.common.dao.DatabaseUtil;


/**
 * {@code jp.co.tis.gsp.tools.db.EntityDependencyParser}から移植。
 *
 * @author T.Kawasaki
 */
public class EntityDependencyParser {

    /** キー＝テーブル名：値＝{@link Table}*/
    private Map<String, Table> tableMap = new HashMap<String, Table>();

    /**
     * 解析を行う。
     * <p/>
     * 解析結果は、{@link #getTableList()}で取得できる。
     * @param conn コネクション
     * @param schema スキーマ名
     */
    public void parse(Connection conn, String schema) {
        try {
            DatabaseMetaData metaData = conn.getMetaData();
            schema = DatabaseUtil.convertIdentifiers(metaData, schema);
            String[] types = {"TABLE"};
            ResultSet rs = metaData.getTables(null, schema, null, types);
            while (rs.next()) {
                String tableName = rs.getString("TABLE_NAME");
                parseReference(metaData, schema, tableName);
            }
            rs.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    /**
     * 参照関係を解析する。
     *
     * @param metaData データベースメタデータ
     * @param schema スキーマ名
     * @param tableName テーブル名
     * @throws SQLException 予期しない例外
     */
    void parseReference(DatabaseMetaData metaData, String schema, String tableName)
            throws SQLException {
        schema = DatabaseUtil.convertIdentifiers(metaData, schema);
        tableName =  DatabaseUtil.convertIdentifiers(metaData, tableName);
        ResultSet rs = metaData.getImportedKeys(null, schema, tableName);
        while (rs.next()) {
            String child = rs.getString("FKTABLE_NAME");
            String parent = rs.getString("PKTABLE_NAME");
            associate(parent, child);
        }
        rs.close();
    }

    /**
     * 親子テーブルの関連付けを行う。
     *
     * @param parent 親テーブル
     * @param child 子テーブル
     */
    void associate(String parent, String child) {
        Table childTable = tableMap.get(child);
        if (childTable == null) {
            childTable = new Table(child);
            tableMap.put(child, childTable);
        }
        Table parentTable = tableMap.get(parent);
        if (parentTable == null) {
            parentTable = new Table(parent);
            tableMap.put(parent, parentTable);
        }
        parentTable.addChild(childTable);
    }

    /**
     * 親子関係をカウントアップする。
     *
     * @param table 処理対象テーブル
     * @param tableReferenceCounts テーブル参照回数
     * @param level 階層
     */
    private void countUp(Table table, Map<String, Integer> tableReferenceCounts, int level) {
        for (Table childTable : table.children) {
            boolean isSelfReference = table.name.equals(childTable.name);
            if (!isSelfReference) {
                countUp(childTable, tableReferenceCounts, level + 1);
            }
        }

        Integer currentLevel = tableReferenceCounts.get(table.name);
        if (level > currentLevel) {
            tableReferenceCounts.put(table.name, level);
        }
    }

    /**
     * ソート済みテーブル一覧を取得する。
     *
     * @return ソート済みテーブル一覧
     */
    public List<String> getTableList() {
        List<Table> rootTables = new ArrayList<Table>();
        List<String> tableList = new ArrayList<String>();
        final Map<String, Integer> tableReferenceCounts = new HashMap<String, Integer>();
        for (Table table : tableMap.values()) {
            if (table.isRoot()) {
                rootTables.add(table);
            }
            tableReferenceCounts.put(table.name, 0);
            tableList.add(table.name);
        }
        if (!tableList.isEmpty() && rootTables.isEmpty()) {
            throw new IllegalStateException("ルートとなるテーブルが見つかりません。循環参照になっていると思います！");
        }
        for (Table table : rootTables) {
            countUp(table, tableReferenceCounts, 0);
        }
        Collections.sort(tableList, new Comparator<String>() {
            public int compare(String t1, String t2) {
                return tableReferenceCounts.get(t1) - tableReferenceCounts.get(t2);
            }
        });
        return tableList;
    }

    /** テーブルの依存関係を表すクラス。 */
    static class Table {
        /** テーブル名 */
        private final String name;

        /** 親テーブル */
        private final List<Table> parents = new ArrayList<Table>();

        /** 子テーブル*/
        private final List<Table> children = new ArrayList<Table>();

        /**
         * コンストラクタ。
         *
         * @param name テーブル名
         */
        Table(String name) {
            this.name = name;
        }

        /**
         * 子テーブルを追加する。
         *
         * @param child 子テーブル
         */
        void addChild(Table child) {
            children.add(child);
            child.parents.add(this);
        }

        /**
         * このテーブルがルートであるかどうか判定する。
         * @return ルートである場合、真
         */
        boolean isRoot() {
            switch (parents.size()) {
                case 0:          // 親がいないのでルート
                    return true;
                case 1:          // 唯一の親が自分（自己参照）の場合、ルート
                    return parents.get(0).equals(this);
                default:         // 親がいるのでルートでない
                    return false;
            }
        }

    }
}
