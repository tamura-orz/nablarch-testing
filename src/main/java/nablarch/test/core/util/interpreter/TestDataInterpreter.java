package nablarch.test.core.util.interpreter;

import nablarch.core.util.annotation.Published;

/**
 * テストデータ記法を解釈するインタフェース。<br/>
 * 様々なテストデータを簡略に表現する機能を持つ。
 * 本インタフェースを実装するクラスは、おおよそ以下のような流れで処理をするとよい。
 * <ul>
 * <li>解釈対象の値を、{@link InterpretationContext#getValue()}により取得する。</li>
 * <li>値を解釈できる場合は、解釈した結果を返却する。
 * <li>自身で解釈しない（できない）場合は、{@link InterpretationContext#invokeNext()}の値を返却する。</li>
 * <li>自身で解釈した値を更に、後続処理に渡したい場合は、{@link InterpretationContext#setValue(String)}
 * で解釈後の値を設定した後、{@link InterpretationContext#invokeNext()}の値を返却する。</li>
 * </ul>
 *
 * @author T.Kawasaki
 * @see InterpretationContext
 */
@Published(tag = "architect")
public interface TestDataInterpreter {

    /**
     * 解釈する。<br/>
     *
     * @param context 解釈コンテキスト
     * @return 解釈された値
     */
    String interpret(InterpretationContext context);

}
