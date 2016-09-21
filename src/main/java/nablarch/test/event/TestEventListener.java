package nablarch.test.event;

import nablarch.core.util.annotation.Published;

/**
 * テストイベントリスナーインタフェース。
 * テスト実行中の各種イベント通知を受け取る。
 *
 * @author T.Kawasaki
 * @see TestEventDispatcher
 */
@Published(tag = "architect")
public interface TestEventListener {

    /** 全テスト実行前に1度限り呼び出されるコールバック */
    void beforeTestSuite();

    /** テストクラス毎にテスト実行前に1回呼び出されるコールバック */
    void beforeTestClass();

    /** テストメソッド実行前に呼び出されるコールバック */
    void beforeTestMethod();

    /** テストメソッド実行後に呼び出されるコールバック */
    void afterTestMethod();

    /** テストクラス毎にテスト実行後に1回呼び出されるコールバック */
    void afterTestClass();

    /**
     * {@link TestEventListener}実装のテンプレートクラス。<br/>
     * リスナーを作成する際は、本クラスを継承し必要なメソッドのみ
     * オーバライドするとよい。
     *
     * @author T.Kawasaki
     */
    @Published(tag = "architect")
    public static class Template implements TestEventListener {

        /** {@inheritDoc} */
        public void beforeTestSuite() {
        }

        /** {@inheritDoc} */
        public void beforeTestClass() {
        }

        /** {@inheritDoc} */
        public void beforeTestMethod() {
        }

        /** {@inheritDoc} */
        public void afterTestMethod() {
        }

        /** {@inheritDoc} */
        public void afterTestClass() {
        }
    }
}
