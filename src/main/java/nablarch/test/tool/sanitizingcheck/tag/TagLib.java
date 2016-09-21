package nablarch.test.tool.sanitizingcheck.tag;

/**
 * コメント。
 *
 * @author hisaaki sioiri
 */
public class TagLib extends Tag {

    /**
     * コンストラクタ。
     *
     * @param name タグ名
     * @param lineNo 行番号
     * @param position タグの開始位置
     */
    public TagLib(String name, int lineNo, int position) {
        super(name, lineNo, position);
    }

    @Override
    public TagType getType() {
        return TagType.TAGLIB;
    }

    @Override
    protected String getMessage() {
        return "Custom Tag: " + super.getMessage() + '>';
    }

}
