package nablarch.test.tool.sanitizingcheck.tag;

/**
 * HTMLコメントを表すクラス。
 *
 * @author hisaaki sioiri
 */
public class HtmlComment extends Tag {

    /** HTMLコメントタグ */
    public static final String TAG_NAME = "<!--";

    /**
     * コンストラクタ。
     *
     * @param lineNo 行番号
     * @param position タグ名
     */
    public HtmlComment(int lineNo, int position) {
        super(TAG_NAME, lineNo, position);
    }

    @Override
    public TagType getType() {
        return TagType.HTML_COMMENT;
    }

    /**
     * {@inheritDoc}
     *
     * HTMLコメントでは、タグ内の解析処理は行わない。
     * また、タグの閉じ位置も自身のタグの位置とする。
     *
     * @param lineNo 解析対象の行
     * @param line 解析対象の行
     * @param searchPosition 解析開始位置
     */
    @Override
    public void parse(int lineNo, String line, int searchPosition) {
        setCloseTagPosition(searchPosition);
    }

    @Override
    protected String getMessage() {
        return "HTML Comment: <!-- xxx -->";
    }
}

