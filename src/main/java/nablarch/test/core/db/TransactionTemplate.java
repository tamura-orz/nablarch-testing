package nablarch.test.core.db;

import nablarch.core.db.connection.AppDbConnection;
import nablarch.core.db.connection.DbConnectionContext;
import nablarch.core.db.transaction.SimpleDbTransactionManager;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.SystemRepository;
import nablarch.core.transaction.TransactionContext;
import nablarch.core.util.annotation.Published;

import static nablarch.core.util.Builder.concat;

/**
 * トランザクション内で簡易的な処理を記述する為のテンプレートクラス。<br/>
 *
 * @author Tsuyoshi Kawasaki
 */
@Published
public abstract class TransactionTemplate {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(TransactionTemplate.class);

    /** SimpleDbTransactionManager */
    private final SimpleDbTransactionManager manager;

    /**
     * コンストラクタ<br/>
     * デフォルトのトランザクションを使用する。
     *
     * @see nablarch.core.transaction.TransactionContext
     */
    public TransactionTemplate() {
        this(TransactionContext.DEFAULT_TRANSACTION_CONTEXT_KEY);
    }

    /**
     * コンストラクタ<br/>
     *
     * @param managerKey SimpleDbTransactionManagerを取得するためのキー
     */
    public TransactionTemplate(String managerKey) {
        manager = (SimpleDbTransactionManager) SystemRepository.getObject(managerKey);
        if (manager == null) {
            throw new IllegalArgumentException(concat(
                    "can't get SimpleDbTransactionManager from SystemRepository. ",
                    "check configuration. key=[", managerKey, "]"));
        }
    }

    /**
     * コンストラクタ<br/>
     *
     * @param manager SimpleDbTransactionManagerインスタンス
     */
    public TransactionTemplate(SimpleDbTransactionManager manager) {
        if (manager == null) {
            throw new IllegalArgumentException("argument must not be null.");
        }
        this.manager = manager;
    }

    /**
     * トランザクション処理を実行する。<br/>
     * 実行時例外が発生した場合はロールバック、そうでない場合はコミットを行う。<br/>
     * いずれの場合もトランザクションは終了する。
     */
    public final void execute() {

        manager.beginTransaction();
        try {
            // コネクション取得
            String transactionName = manager.getDbTransactionName();
            AppDbConnection conn = DbConnectionContext.getConnection(transactionName);
            // トランザクション内での処理
            doInTransaction(conn);
            // コミット
            manager.commitTransaction();
        } catch (RuntimeException e) {
            rollbackQuietly(); // ロールバックを試行
            throw e;
        } catch (Exception e) {
            rollbackQuietly(); // ロールバックを試行
            throw new RuntimeException("exception occurred in transaction.", e);
        } finally {
            // トランザクション終了
            endTransactionQuietly();
        }
    }

    /**
     * 例外発生無しにロールバックする。
     */
    private void rollbackQuietly() {
        try {
            manager.rollbackTransaction();
        } catch (RuntimeException e) {
            LOGGER.logWarn("exception occurred in rollback.", e);
        }
    }

    /**
     * 例外発生無しにトランザクションを終了する。
     */
    private void endTransactionQuietly() {
        try {
            manager.endTransaction();
        } catch (RuntimeException e) {
            LOGGER.logWarn("exception occurred in endTransaction.", e);
        }
    }

    /**
     * トランザクション内ので処理を行う。<br/>
     * トランザクション内で実行する処理をサブクラスまたは無名クラスにて定義すること。
     *
     * @param conn コネクション
     * @throws Exception 例外
     */
    protected abstract void doInTransaction(AppDbConnection conn) throws Exception;

}
