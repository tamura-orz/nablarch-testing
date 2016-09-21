package nablarch.test.core.db;

import nablarch.core.util.annotation.Published;

/**
 * データベースデフォルト値を表すインタフェース。
 * {@link TableData}にてカラムが省略された場合、
 * 本インタフェースの実装クラスからデフォルト値が取得される。
 *
 * @author T.Kawasaki
 */
@Published(tag = "architect")
public interface DefaultValues {

    /**
     * データ型に応じたデフォルト値を取得する。
     *
     * @param columnType java.sql.Types からの SQL 型
     * @param maxLength 最大桁
     * @return デフォルト値
     */
    Object get(int columnType, int maxLength);


}
