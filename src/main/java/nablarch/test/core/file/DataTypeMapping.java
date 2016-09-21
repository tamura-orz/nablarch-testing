package nablarch.test.core.file;

import nablarch.core.util.annotation.Published;

/**
 * 外部インタフェース設計書のデータ型とフレームワークのデータ型を
 * 対応付けするクラス。<br/>
 *
 * @author T.Kawasaki
 */
@Published(tag = "architect")
public interface DataTypeMapping {

    /**
     * 外部インタフェース設計書のデータ型をフレームワークのデータ型シンボルへ変換する。
     *
     * @param expressionInDesign 設計書上のデータ型
     * @return フレームワークのデータ型シンボル
     */
    String convertToFrameworkExpression(String expressionInDesign);

}
