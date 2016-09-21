package nablarch.test.tool.htmlcheck;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.Builder;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;
import nablarch.test.tool.htmlcheck.util.FileUtil;

/**
 * 規約上許可されていないタグ/属性情報を保存するクラス。
 * 
 * @author Tomokazu Kagawa
 */
@Published(tag = "architect")
public class HtmlForbiddenNodeConf {

    /** ロガー */
    private static final Logger LOGGER = LoggerManager.get(HtmlForbiddenNodeConf.class);
    
    /**
     * 規約上許可されていないタグ/属性情報
     * Mapのキー値にはタグ名、バリューにはタグの要素のうち、許可しない要素が設定される。<br>
     * バリュー値がnullの場合はタグ自体を許可しないことを意味する。
     */
    private Map<String, Set<String>> forbidden = new HashMap<String, Set<String>>();

    /**
     * コンストラクタ
     * 
     * @param confFilePath 設定ファイルパス
     */
    public HtmlForbiddenNodeConf(String confFilePath) {

        if (confFilePath == null) {
            throw new IllegalArgumentException("argument confFilePath must not be null.");
        }

        loadConfig(confFilePath);
    }

    /**
     * 設定ファイルを読み込み、規約上許可しないタグ・属性情報を設定する。<br>
     * 読み込みに際して、設定ファイルがwell-formedであることをチェックする。<br>
     * well-formedでない設定ファイルとは次のいずれかに該当する設定ファイルをいう。
     * <ul>
     * <li>属性と対となるタグ名が記載されていないレコードが存在する。</li>
     * <li>カンマを記載していないレコードが存在する。</li>
     * </ul>
     * 
     * @param confFilePath 設定ファイルパス
     */
    private void loadConfig(String confFilePath) {

        List<String[]> forbiddenNodeList = FileUtil.readCsv(confFilePath);
        for (int i = 0; i < forbiddenNodeList.size(); i++) {
            String[] row = forbiddenNodeList.get(i);
            if (row.length != 2) {
                throw new IllegalArgumentException(Builder.concat(
                        "each line must have exactly two elements.",
                        "config file = [", confFilePath , "] ",
                        "line = [", (i + 1), "]"));
            }
            String tag = row[0];
            String attr = row[1];
            if (tag.length() == 0) {
                throw new IllegalArgumentException(Builder.concat(
                        "tag name (1st column) must not be empty.",
                        "config file = [", confFilePath , "] ",
                        "line = [", (i + 1), "]"));
            }
            addRule(tag, attr);
        }
        
        if (LOGGER.isTraceEnabled()) {
            LOGGER.logTrace(forbidden.toString());
        }
    }

    /**
     * 規約上許可しないタグ・属性情報を設定する。
     * 
     * @param tag タグ名
     * @param attr 属性名
     */
    private void addRule(String tag, String attr) {
        tag = tag.trim().toLowerCase();
        Set<String> attributes = forbidden.get(tag);
        if (attributes == null) {
            attributes = new HashSet<String>();
        }
        // 属性が指定されている場合
        if (!StringUtil.isNullOrEmpty(attr)) {
            attr = attr.trim();
            if (attr.length() != 0) {
                attributes.add(attr.toLowerCase());
            }
        }
        forbidden.put(tag, attributes);
    }

    /**
     * 指定したタグが規約上許可しないタグリストに含まれているかをチェックする。<br>
     * 許可しないタグ情報として属性情報が設定されているかいないかは考慮しない。<br>
     * 
     * @param tagName チェック対象タグ名
     * @return 指定したタグが規約上許可しないタグリストに含まれる場合はtrue、 そうでない場合はfalseを返却する。
     */
    public boolean contains(String tagName) {

        return forbidden.keySet().contains(tagName);
    }

    /**
     * 指定されたタグと属性の組が不許可であることをチェックする。
     * 
     * @param tagName チェック対象タグ名
     * @param attrName チェック対象属性名
     * @return 指定されたタグと属性の組が不許可である場合はtrue、 使用を許可されている場合はfalse
     */
    public boolean isForbiddenAttr(String tagName, String attrName) {
        return contains(tagName) && forbidden.get(tagName).contains(attrName);
    }

    /**
     * 指定したタグが使用を許可しないタグであるかをチェックする。
     * 
     * @param tagName チェック対象タグ名
     * @return 指定したタグが許可しないタグである場合はtrue、 そうでない場合はfalseを返却する。
     */
    public boolean isForbiddenTag(String tagName) {
        return contains(tagName) && forbidden.get(tagName).isEmpty();
    }
}
