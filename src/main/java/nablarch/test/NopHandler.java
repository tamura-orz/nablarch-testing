package nablarch.test;

import nablarch.core.repository.initialization.Initializable;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Handler;

/**
 * 何もしないハンドラ実装クラス。<br/>
 * 本番で動作するハンドラを、テスト実行時のみ無効化する用途に使用する。
 * コンポーネント設定ファイルにて、無効にしたいハンドラの実体を本クラスにすることで、
 * そのハンドラの動作を無効化できる。
 *
 * @author T.Kawasaki
 */
public class NopHandler implements Handler<Object, Object>, Initializable {

    /**
     * {@inheritDoc}
     * この実装では、単に後続のハンドラに処理を委譲する。
     */
    public Object handle(Object o, ExecutionContext context) {
        return context.handleNext(o);
    }

    /** {@inheritDoc} */
    public void initialize() {
        // Nop
    }
}
