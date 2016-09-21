package nablarch.test.tool.sanitizingcheck;

import java.nio.charset.Charset;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nablarch.test.tool.sanitizingcheck.util.FileUtil;

/**
 * サニタイジングツール設定情報を保有するクラス
 * 
 */
public class SanitizingConf {

    /**
     * Actionタグの開始パターン
     */
    private static final Pattern ACTION_TAG_PATTERN = Pattern.compile("<\\S+:");

    /**
     * 許可されるタグのリストを設定する。
     */
    private final Set<String> allowedTagList = new HashSet<String>();

    /**
     * コンストラクタ<br>
     * 設定ファイルを読み込む。
     * 
     * @param confFilePath 設定ファイルパス
     * @param charset 文字コード
     */
    public SanitizingConf(String confFilePath, Charset charset) {
        if (confFilePath == null || confFilePath.isEmpty()) {
            throw new IllegalArgumentException("enter configuration path.");
        }
        loadConfig(confFilePath, charset);
    }

    /**
     * 設定ファイルをロードする。
     * 
     * @param confFilePath 設定ファイルパス
     * @param charset 文字コード
     */
    private void loadConfig(String confFilePath, Charset charset) {

        List<String> textList = FileUtil.readFile(confFilePath, charset);
        for (String text : textList) {
            if (!text.startsWith("--") && !text.isEmpty()) {
                allowedTagList.add(text.trim());
            }
        }
    }

    /**
     * タグが使用禁止であることを判定する。
     * 
     * @param startTagName チェック対象タグ名
     * @return 使用禁止である場合はtrue、それ以外はfalseを返却する。
     */
    public boolean isForbidden(String startTagName) {

        Matcher matcher = ACTION_TAG_PATTERN.matcher(startTagName);
        if (!matcher.find()) {
            return !allowedTagList.contains(startTagName);
        }

        for (String s : allowedTagList) {
            if (startTagName.startsWith(s)) {
                return false;
            }
        }
        return true;
    }
}
