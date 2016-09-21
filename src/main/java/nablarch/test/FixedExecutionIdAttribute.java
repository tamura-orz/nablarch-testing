package nablarch.test;

import nablarch.common.handler.threadcontext.ExecutionIdAttribute;
import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

/**
 * スレッドコンテキスト({@link nablarch.core.ThreadContext})に固定の実行時IDを保持する。
 *
 * @author hisaaki sioiri
 */
@Published(tag = "architect")
public class FixedExecutionIdAttribute extends ExecutionIdAttribute {

    /** 実行時ID。 */
    private String executionId;

    @Override
    public Object getValue(Object req, ExecutionContext ctx) {
        return executionId;
    }

    /**
     * 実行時IDを設定する。
     * @param executionId 実行時ID
     */
    public void setExecutionId(String executionId) {
        this.executionId = executionId;
    }
}
