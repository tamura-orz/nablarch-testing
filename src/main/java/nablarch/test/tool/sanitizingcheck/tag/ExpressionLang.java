package nablarch.test.tool.sanitizingcheck.tag;

/**
 * EL式を表すクラス。
 *
 * @author hisaaki sioiri
 */
public class ExpressionLang extends Tag {

    /** タグ名 */
    private static final String TAG_NAME = "${";

    /**
     * コンストラクタ。
     *
     * @param lineNo 行番号
     * @param position タグの位置
     */
    public ExpressionLang(int lineNo, int position) {
        super(TAG_NAME, lineNo, position);
    }

    @Override
    public TagType getType() {
        return TagType.EL;
    }

    @Override
    protected String getMessage() {
        return "JSP EL Element: ${ xxx }";
    }

    /**
     * {@inheritDoc}
     *
     * EL式では、閉じタグまでの読み飛ばし処理を行う。
     *
     * @param lineNo 解析対象の行
     * @param line 解析対象の行
     * @param searchPosition 解析開始位置
     */
    @Override
    public void parse(int lineNo, String line, int searchPosition) {
        boolean literal = false;
        int currentPosition = searchPosition;
        while (currentPosition < line.length()) {
            char c = line.charAt(currentPosition++);
            switch (c) {
                case '}':
                    if (literal) {
                        // リテラル内の場合は、EL式の終了文字とはしない。
                        continue;
                    }
                    // 終了文字のため、ここで解析終了
                    setCloseTagPosition(currentPosition);
                    return;
                case '\\':
                    // エスケープ文字の場合は、次の文字を読み飛ばす。
                    currentPosition++;
                    break;
                case '"' :
                    // 「'」と同じ扱い
                case '\'':
                    // リテラルの場合は、リテラル内かのフラグを反転
                    literal = !literal;
                    break;
                default:
                    // 何もしない
            }
        }
    }
}
