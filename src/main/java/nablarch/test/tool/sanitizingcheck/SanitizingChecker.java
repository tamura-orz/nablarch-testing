package nablarch.test.tool.sanitizingcheck;

import java.io.File;
import java.io.FileFilter;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nablarch.test.tool.sanitizingcheck.tag.Tag;
import nablarch.test.tool.sanitizingcheck.tag.TagType;

/**
 * サニタイジングチェックツール
 * 
 * @author Tomokazu Kagawa
 */
public class SanitizingChecker {

    /** ファイルの文字エンコーディング */
    private final Charset charset;


    /**
     * サニタイジングチェック時に生じたエラーメッセージを格納する。<br>
     * 解析対象の全てのJSPに関するエラーメッセージを保持する。<br>
     * キー値にはJSP名、バリュー値には当該JSPにて検知したエラーメッセージのリストが格納される。
     */
    private final Map<String, List<String>> errorMap = new LinkedHashMap<String, List<String>>();

    /**
     * サニタイジングチェックツールの設定ファイル<br>
     * 使用を許可するタグの一覧を保持する。
     */
    private final SanitizingConf conf;

    /**
     * サニタイジングチェックツールでチェック対象とするファイルの拡張子の一覧。
     */
    private final List<String> additionalExts;

    /** チェック対象外のファイル(ディレクトリ)リスト(正規表現) */
    private final List<Pattern> excludePatterns;

    /**
     * コンストラクタ。
     *
     * 設定ファイルをロードし設定する。
     * @param confPath 設定ファイルパス
     * @param charset 文字コード
     * @param additionalExts jsp以外にチェック対象とする拡張子のリスト
     * @param excludePatterns チェック対象外ファイル(ディレクトリ)のパターンリスト(正規表現)
     */
    public SanitizingChecker(String confPath, Charset charset, List<String> additionalExts, List<Pattern> excludePatterns) {
        this.charset = charset;
        conf = new SanitizingConf(confPath, charset);
        this.additionalExts = additionalExts;
        this.excludePatterns = excludePatterns == null ? Collections.<Pattern>emptyList() : excludePatterns;
    }

    /**
     * サニタイジングチェック実施する。<br>
     * 指定されたディレクトリ配下のJSP、または指定されたJSPを対象にサニタイジングチェックを行う。<br>
     * ディレクトリが指定された際には、配下のサブフォルダ内のJSPも全てチェックする。<br>
     * 返却されるMap<String, List<String>>のキー値にはJSP名、バリュー値には当該JSPにて検知したエラーメッセージのリストが格納される。
     * 
     *
     * @param jspDirPath チェック対象JSPファイル、またはチェック対象ディレクトリ
     * @return エラー一覧
     */
    public Map<String, List<String>> checkSanitizing(String jspDirPath) {

        File file = new File(jspDirPath);
        if (file.isFile()) {
            checkForJspFile(file);
        } else if (file.isDirectory()) {
            checkForDir(file);
        } else {
            throw new IllegalArgumentException("confirm that " + jspDirPath + " exists");
        }
        return errorMap;
    }

    /**
     * ディレクトリに対してサブフォルダも含めてサニタイジングチェックを行う。
     *
     * @param path チェック対象フォルダ
     *
     */
    private void checkForDir(File path) {

        File[] files = path.listFiles(new FileFilter() {
            public boolean accept(File pathname) {
                if (isExcludeFile(pathname)) {
                    return false;
                }
                if (pathname.isDirectory() || pathname.getName().endsWith(".jsp")) {
                    return true;
                }
                for (String ext : additionalExts) {
                    if (pathname.getName().endsWith(ext)) {
                        return true;
                    }
                }
                return false;
            }

        });

        for (File file : files) {
            if (file.isDirectory()) {
                checkForDir(file.getAbsoluteFile());
            } else {
                checkForJspFile(file);
            }
        }
    }

    /**
     * 各JSPファイルに対してサニタイジングチェックを行う。
     *
     * @param jspPath チェック対象JSPパス
     * 
     */
    private void checkForJspFile(File jspPath) {

        if (isExcludeFile(jspPath)) {
            return;
        }

        List<String> errors = new ArrayList<String>();

        JspParser parser = new JspParser(jspPath.getAbsolutePath(), charset);
        List<? extends Tag> parse = parser.parse();
        for (Tag tag : parse) {
            checkTag(tag, errors);
            // タグの属性もチェックする
            List<Tag.TagAttribute> attributes = tag.getAttributes();
            for (Tag.TagAttribute attribute : attributes) {
                // 属性値を解析しチェック対象のタグの場合には、再度チェックを行う。
                Tag attributeTag = parser.findTag(attribute.getValue(), attribute.getLineNo(), 0);
                while (attributeTag != null) {
                    int position = attributeTag.getPosition();
                    if (attributeTag.getType() != TagType.EL) {
                        // タグの属性はEL式以外の場合にチェックを行う。
                        attributeTag.setPosition(attribute.getPosition() + position - 1);
                        checkTag(attributeTag, errors);
                    }
                    // 属性値のタグの開始位置を属性の開始位置 + 属性値のタグが持つ開始位置に変更し、再度タグを属性値を解析する。
                    attributeTag = parser.findTag(attribute.getValue(), attribute.getLineNo(), position);
                }
            }
        }
        if (!errors.isEmpty()) {
            errorMap.put(jspPath.getAbsolutePath(), errors);
        }
    }

    /**
     * 除外対象のパスか否か
     * @param path チェック対象のパス
     * @return チェック除外対象のパスの場合はtrue
     */
    private boolean isExcludeFile(File path) {
        for (Pattern excludePattern : excludePatterns) {
            Matcher matcher = excludePattern.matcher(path.getAbsolutePath());
            if (matcher.find()) {
                return true;
            }
        }
        return false;
    }

    /**
     * タグが使用できるかチェックする。
     *
     * 使用できない場合には、エラーメッセージをエラー情報に追加する。
     * @param tag タグ
     * @param errors エラー情報
     */
    private void checkTag(Tag tag, List<String> errors) {
        if (!tag.isSuppressJspCheck() && conf.isForbidden(tag.getName())) {
            errors.add(tag.toString() + " is misplaced or is not permitted to use in this project.");
        }
    }
}

