package nablarch.test.core.util.interpreter;

/**
 * null値を解釈する解釈クラス。<br/>
 * 要素の値が、半角「null」(大文字、小文字は区別しない)の場合は、null値に置き換える。
 * @author T.Kawasaki
 */
public class NullInterpreter implements TestDataInterpreter {

    /** nullを表す表記 */
    private static final String NULL_NOTATION = "null";

    /** {@inheritDoc} */
    public String interpret(InterpretationContext context) {
        if (NULL_NOTATION.equalsIgnoreCase(context.getValue())) {
            return null;  // nullを返却
        }
        return context.invokeNext();
    }
}
