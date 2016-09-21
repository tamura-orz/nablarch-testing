package nablarch.test.tool.sanitizingcheck.tag;

/**
 * コメント。
 *
 * @author hisaaki sioiri
 */
public class SuppressJspCheck extends Tag {

    /**
     * コンストラクタ。
     *  @param lineNo 行番号
     * @param position タグの位置
     */
    public SuppressJspCheck(int lineNo, int position) {
        super("suppress tag", lineNo, position);
    }

    @Override
    public TagType getType() {
        return TagType.JSP_CORE;
    }
}
