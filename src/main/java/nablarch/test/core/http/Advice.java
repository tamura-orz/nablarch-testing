package nablarch.test.core.http;

import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

/**
 * リクエスト単体テストのテスト実行前後に起動されるコールバックインタフェース。
 * リクエスト単体テストで、テスト実行前後に特別な準備処理、結果確認処理が必要な場合は
 * 本クラスのサブクラスにてその処理を定義する。
 *
 * @param <INF> テストケース情報の型
 * @author T.Kawasaki
 */
@Published
public interface Advice<INF> {

    /**
     * テスト実行直前（サブミット前）に起動されるコールバックメソッド
     *
     * @param testCaseInfo テストケース情報
     * @param context      ExecutionContext
     */
    void beforeExecute(INF testCaseInfo, ExecutionContext context);

    /**
     * テスト実行直後（サブミット後）に起動されるコールバックメソッド
     *
     * @param testCaseInfo テストケース情報
     * @param context      ExecutionContext
     */
    void afterExecute(INF testCaseInfo, ExecutionContext context);

}
