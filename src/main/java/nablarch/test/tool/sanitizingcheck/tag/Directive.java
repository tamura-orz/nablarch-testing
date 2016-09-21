package nablarch.test.tool.sanitizingcheck.tag;

/**
 * ディレクティブを表すクラス。
 *
 * @author hisaaki sioiri
 */
public class Directive extends Tag {

    /** タグ名 */
    public static final String TAG_NAME = "<%@";

    /** ディレクティブ名 */
    private final String directiveName;


    /**
     * コンストラクタ。
     *
     * @param lineNo 行番号
     * @param position タグの開始ポジション
     * @param directiveName ディレクティブ名
     */
    public Directive(int lineNo, int position, String directiveName) {
        super(TAG_NAME, lineNo, position);
        this.directiveName = directiveName;
    }

    @Override
    public TagType getType() {
        return TagType.DIRECTIVE;
    }

    /**
     * ディレクティブ名を取得する。
     *
     * @return ディレクティブ名
     */
    public String getDirectiveName() {
        return directiveName;
    }

    @Override
    protected String getMessage() {
        return "JSP Directive: " + super.getMessage() + ' ' + directiveName + " %>";
    }

    @Override
    public String getName() {
        return super.getName() + ' ' + directiveName;
    }
}

