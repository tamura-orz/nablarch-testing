package nablarch.test.core.http;

import nablarch.core.util.annotation.Published;

/**
 * リクエスト単体テストテンプレートの基本実装クラス。<br/>
 * リクエスト単体テストクラスを実装する際の型指定を簡略化するため、
 * 本クラスは{@link TestCaseInfo}の型を指定している。
 *
 * @author Tsuyoshi Kawasaki
 * @see AbstractHttpRequestTestTemplate
 * @see TestCaseInfo
 */
@Published
public abstract class BasicHttpRequestTestTemplate extends AbstractHttpRequestTestTemplate<TestCaseInfo> {

    /** コンストラクタ。 */
    protected BasicHttpRequestTestTemplate() {
        super();
    }

    /**
     * コンストラクタ。
     * @param testClass テストクラス
     */
    public BasicHttpRequestTestTemplate(Class<?> testClass) {
        super(testClass);
    }
}
