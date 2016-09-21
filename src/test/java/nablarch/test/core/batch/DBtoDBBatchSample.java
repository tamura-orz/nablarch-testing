package nablarch.test.core.batch;

import java.sql.Timestamp;

import nablarch.core.date.SystemTimeProvider;
import nablarch.core.db.statement.SqlPStatement;
import nablarch.core.db.statement.SqlRow;
import nablarch.core.db.support.DbAccessSupport;
import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.repository.SystemRepository;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.BatchAction;
import nablarch.fw.reader.DatabaseRecordReader;

/** DBtoDBのサンプル。 */
public class DBtoDBBatchSample extends BatchAction<SqlRow> {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(DBtoDBBatchSample.class);

    /** DBサポート */
    private DbAccessSupport dbSupport = new DbAccessSupport(getClass());

    @Override
    public Result handle(SqlRow inputData, ExecutionContext ctx) {

        String id = (String) inputData.get("id");
        final int count = inputData.getInteger("counter");

        LOGGER.logInfo("会員ID[" + id + "]を処理します。");

        final int incremented = count + 1;  // カウンタをインクリメント
        Timestamp currentTimestamp = getCurrentTimestamp();      // 現在のタイムスタンプ

        update(incremented, currentTimestamp, id);               // 更新

        // マスタデータを変更
        changeMasterData();

        // 成功
        return new Result.Success();
    }

    /**
     * 現在のタイムスタンプを取得する。
     *
     * @return 現在のタイムスタンプ
     */
    private Timestamp getCurrentTimestamp() {
        SystemTimeProvider provider = SystemRepository.get("systemTimeProvider");
        return provider.getTimestamp();
    }

    /**
     * 更新する。
     *
     * @param count     カウント
     * @param timestamp タイムスタンプ
     * @param id        ID
     */
    private void update(int count, Timestamp timestamp, String id) {
        SqlPStatement statement = dbSupport.getSqlPStatement("update");
        statement.setInt(1, count);
        statement.setTimestamp(2, timestamp);
        statement.setString(3, id);
        statement.executeUpdate();
    }

    private static int pkCol1 = 0;
    private static int pkCol2 = 10;    
    
    private void changeMasterData() {
        SqlPStatement statement = dbSupport.getSqlPStatement("insert");
        statement.setString(1, String.valueOf(pkCol1++));
        statement.setLong(2, Long.valueOf(pkCol2++));
        statement.executeUpdate();
    }

    /** {@inheritDoc} */
    @Override
    public DataReader<SqlRow> createReader(ExecutionContext ctx) {
        SqlPStatement statement = dbSupport.getSqlPStatement("select");
        return new DatabaseRecordReader().setStatement(statement);
    }


}
