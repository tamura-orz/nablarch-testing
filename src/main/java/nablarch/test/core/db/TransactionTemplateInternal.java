package nablarch.test.core.db;

import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.TransactionManagerConnection;

/**
 * フレームワーク内部で使用する{@link TransactionTemplate}サブクラス。
 * トランザクションを制御する場合に使用する。
 * 本クラスはアプリケーションには公開されていない。
 *
 * @author T.Kawasaki
 */
public abstract class TransactionTemplateInternal extends TransactionTemplate {

    /**
     * コンストラクタ。
     *
     * @param managerKey SimpleDbTransactionManagerを取得するためのキー
     */
    public TransactionTemplateInternal(String managerKey) {
        super(managerKey);
    }

    /** {@inheritDoc} */
    @Override
    protected void doInTransaction(AppDbConnection conn) throws Exception {
        TransactionManagerConnection trConn = (TransactionManagerConnection) conn;
        doInTransaction(trConn);
    }

    /**
     * トランザクション内ので処理を行う。<br/>
     * トランザクション内で実行する処理をサブクラスまたは無名クラスにて定義すること。
     *
     * @param conn コネクション
     * @throws Exception 例外
     */
    protected abstract void doInTransaction(TransactionManagerConnection conn) throws Exception;

}
