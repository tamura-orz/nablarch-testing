package nablarch.test.tool.sanitizingcheck;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nablarch.test.tool.htmlcheck.util.FileUtil;
import nablarch.test.tool.sanitizingcheck.tag.Directive;
import nablarch.test.tool.sanitizingcheck.tag.ExpressionLang;
import nablarch.test.tool.sanitizingcheck.tag.HtmlComment;
import nablarch.test.tool.sanitizingcheck.tag.JspCore;
import nablarch.test.tool.sanitizingcheck.tag.SuppressJspCheck;
import nablarch.test.tool.sanitizingcheck.tag.Tag;
import nablarch.test.tool.sanitizingcheck.tag.TagLib;

/**
 * JSPファイルを解析する。
 *
 * @author hisaaki sioiri
 */
public class JspParser {

    /** チェック対象外を示す文字列 */
    private static final String SUPPRESS_JSP_CHECK_KEYWORD = "suppress jsp check";

    /** 解析対象のタグパターン */
    private static final Pattern TAG_PATTERN = Pattern.compile(
            "((<%@)\\s*(\\w+)"              // ディレクティブ
            + "|(<!--\\s*([^\\s]*))"        // HTMLコメント
            + "|(<[\\w]+:[^/>\\s]+)"        // taglib
            + "|(<%(?:--(?:[ ]*(" + SUPPRESS_JSP_CHECK_KEYWORD + ").*--%>)?|!|=)?)"            // コアタグ
            + "|(\\$\\{))"                  // EL式
    );

    /**
     * HTMLコメントだが、使用を許可するコメント内容。
     *
     * これは、クライアント開発ツールを使用した場合、使用しなければならないHTMLコメントとなるため、
     * チェック対象から除外する。
     */
    private static final String[] EXCLUDE_HTML_COMMENT = {"<%/*", "*/%>", "[if", "<![endif]"};

    /** ファイル名 */
    private final String fileName;

    /** ファイルの文字コード */
    private final Charset charset;

    /**
     * コンストラクタ。
     *
     * @param fileName チェック対象のJSPファイル名
     * @param charset ファイルのエンコーディング
     */
    public JspParser(String fileName, Charset charset) {
        this.fileName = fileName;
        this.charset = charset;
    }

    /**
     * JSPファイルを解析する。
     *
     * @return 解析した結果の
     */
    public List<? extends Tag> parse() {

        // JSPを読み込み
        BufferedReader reader = null;
        try {
            reader = FileUtil.open(fileName, charset);

            List<Tag> tags = new ArrayList<Tag>();

            Tag lastTag = null;
            String lastLine = null;
            int lineNo = 0;
            int suppressLineNo = 0;
            while (true) {
                if (lastTag == null || !lastTag.isClosed()) {
                    // 最後のタグが存在していない場合または閉じられていない場合は、
                    // 次の行の解析処理を行う。
                    lastLine = reader.readLine();
                    lineNo++;
                    if (lastLine == null) {
                        break;
                    }
                    if (lastTag == null) {
                        lastTag = findTag(lastLine, lineNo, 0);
                    } else {
                        lastTag.parse(lineNo, lastLine, 0);
                    }
                } else {
                    // 上記以外の場合は、現在行に対してタグを検索する。
                    lastTag = findTag(lastLine, lineNo, lastTag.getCloseTagPosition());
                }

                if (lastTag == null) {
                    continue;
                }
                if (lastTag instanceof SuppressJspCheck) {
                    suppressLineNo = lastTag.getLineNo() + 1;
                    lastTag = null;
                } else if (lastTag.isClosed()) {
                    if (lastTag.getLineNo() == suppressLineNo) {
                        lastTag.setSuppressJspCheck(true);
                    }
                    tags.add(lastTag);
                }
            }
            return tags;
        } catch (IOException e) {
            throw new IllegalStateException("jsp file read error. file name = " + fileName, e);
        } finally {
            FileUtil.closeQuietly(reader);
        }
    }

    /**
     * 解析対象のタグを読み込む。
     * <p/>
     * これ以上解析対象のタグが存在しない場合は、nullを返却する。
     *
     * @param line 解析対象の行
     * @param lineNo 行番号
     * @param searchPosition 検索開始ポジション
     * @return 読み込んだタグデータ
     */
    public Tag findTag(String line, int lineNo, int searchPosition) {
        Matcher matcher = TAG_PATTERN.matcher(line);
        if (!matcher.find(searchPosition)) {
            return null;
        }
        int tagPosition = matcher.start() + 1;
        String matchStr = matcher.group();
        Tag tag;
        if (matchStr.startsWith(Directive.TAG_NAME)) {
            // ディレクティブ
            tag = new Directive(lineNo, tagPosition, matcher.group(3));
        } else if (matchStr.startsWith("<!--")) {
            // HTMLコメント
            // HTMLコメントは閉じタグの存在有無のチェックはしない。（開始タグだけて閉じていると判断する。)
            if (isExcludeHtmlComment(matcher.group(5))) {
                return null;
            }
            tag = new HtmlComment(lineNo, tagPosition);
        } else if (matchStr.startsWith("<%")) {
            // JSPのコアタグの場合
            if (SUPPRESS_JSP_CHECK_KEYWORD.equals(matcher.group(8))) {
                return new SuppressJspCheck(lineNo, tagPosition);
            } else {
                tag = new JspCore(matcher.group(1), lineNo, tagPosition);
            }
        } else if (matchStr.startsWith("${")) {
            // EL式の場合
            tag = new ExpressionLang(lineNo, tagPosition);
        } else {
            // 上記以外はタグリブと判断する。
            tag = new TagLib(matcher.group(1), lineNo, tagPosition);
        }
        tag.parse(lineNo, line, tagPosition);
        return tag;
    }

    /**
     * チェック対象のhtmlコメントかどうかを判定すする。
     *
     * {@link #EXCLUDE_HTML_COMMENT}で定義されてい値で開始されるhtmlコメントの場合には、
     * 使用しても問題無いと判定する。
     *
     * @param commentBody コメントのボディ部
     * @return チェック対象外のHTMLコメントの場合true
     */
    private boolean isExcludeHtmlComment(String commentBody) {
        for (String excludeComment : EXCLUDE_HTML_COMMENT) {
            if (commentBody.startsWith(excludeComment)) {
                return true;
            }
        }
        return false;
    }
}

