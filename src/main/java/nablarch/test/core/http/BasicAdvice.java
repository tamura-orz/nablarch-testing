package nablarch.test.core.http;

import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;

/**
 * リクエスト単体テストコールバックの基本実装クラス。<br/>
 * リクエスト単体テストクラスを実装する際の型指定を簡略化するため、
 * 本クラスは{@link TestCaseInfo}の型を指定している。
 * 
 * @author Tsuyoshi Kawasaki
 * @see AbstractHttpRequestTestTemplate
 * @see TestCaseInfo
 * @see Advice
 */
@Published
public class BasicAdvice implements Advice<TestCaseInfo> {

    /** {@inheritDoc} */
    public void beforeExecute(TestCaseInfo testCaseInfo, ExecutionContext context) {
        // NOP
    }

    /** {@inheritDoc} */
    public void afterExecute(TestCaseInfo testCaseInfo, ExecutionContext context) {
        // NOP
    }

}
