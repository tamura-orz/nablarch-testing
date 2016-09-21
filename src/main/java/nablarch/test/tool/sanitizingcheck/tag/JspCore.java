package nablarch.test.tool.sanitizingcheck.tag;

/**
 * JSPのコアタグを表すクラス。
 *
 * @author hisaaki sioiri
 */
public class JspCore extends Tag {

    /** コアタグの種類 */
    private final CoreTagType coreTagType;

    /**
     * コンストラクタ。
     *
     * @param name タグ名
     * @param lineNo 行番号
     * @param position タグの位置
     */
    public JspCore(String name, int lineNo, int position) {
        super(name, lineNo, position);
        coreTagType = CoreTagType.getInstance(name);
    }

    @Override
    public void parse(int lineNo, String line, int searchPosition) {
        int pos = line.indexOf(coreTagType.getCloseTag(), searchPosition);
        if (pos != -1) {
            setCloseTagPosition(pos + 1);
        }
    }

    @Override
    public TagType getType() {
        return TagType.JSP_CORE;
    }

    @Override
    protected String getMessage() {
        if (coreTagType == CoreTagType.COMMENT) {
            return "JSP Comment: <%-- xxx --%>";
        } else if (coreTagType == CoreTagType.DEFINITION) {
            return "JSP Declaration: <%! xxx %>";
        } else if (coreTagType == CoreTagType.EXPRESSION) {
            return "JSP Expression: <%= xxx %>";
        } else if (coreTagType == CoreTagType.SCRIPTLET) {
            return "JSP Scriptlet: <% xxx %>";
        }
        return super.getMessage();
    }

    /**
     * コアタグのタイプを取得する。
     *
     * @return コアタグのタイプ
     */
    public CoreTagType getCoreTagType() {
        return coreTagType;
    }

    /** コアタグの種類を表す列挙型。 */
    public enum CoreTagType {
        /** コメント */
        COMMENT("--%>"),
        /** スクリプトレット */
        SCRIPTLET("%>"),
        /** 宣言 */
        DEFINITION("%>"),
        /** 式 */
        EXPRESSION("%>");

        /** 閉じタグ */
        private final String closeTag;

        /**
         * コンストラクタ。
         *
         * @param closeTag 閉じタグを表す文字列。
         */
        CoreTagType(String closeTag) {
            this.closeTag = closeTag;
        }

        /**
         * 開始タグに対応したタイプを生成する。
         *
         * @param startTag 開始タグ
         * @return タイプ
         */
        static CoreTagType getInstance(String startTag) {
            if (startTag.equals("<%--")) {
                return COMMENT;
            } else if (startTag.equals("<%!")) {
                return DEFINITION;
            } else if (startTag.equals("<%=")) {
                return EXPRESSION;
            } else {
                return SCRIPTLET;
            }
        }

        /**
         * 閉じタグ。
         *
         * @return 閉じタグ
         */
        public String getCloseTag() {
            return closeTag;
        }
    }
}

