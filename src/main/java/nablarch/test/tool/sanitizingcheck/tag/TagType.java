package nablarch.test.tool.sanitizingcheck.tag;

/**
 * 解析対象のタグタイプを表す列挙型。
 *
 * @author hisaaki sioiri
 */
public enum TagType {
    /** ディレクティブを表す */
    DIRECTIVE,
    /** HTMLコメント */
    HTML_COMMENT,
    /** EL式 */
    EL,
    /** タグリブ */
    TAGLIB,
    /** スクリプトレット */
    JSP_CORE
}
