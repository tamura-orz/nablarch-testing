package nablarch.test.core.http;

import java.io.File;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.core.util.annotation.Published;
import nablarch.fw.ExecutionContext;
import nablarch.test.tool.htmlcheck.Html4HtmlChecker;
import nablarch.test.tool.htmlcheck.HtmlChecker;

/**
 * HTTPリクエストテスト用の設定定義クラス。
 *
 * @author hisaaki sioiri
 */
public class HttpTestConfiguration {

    /**
     * HTMLファイル出力ディレクトリ
     */
    private String htmlDumpDir = "./tmp/html_dump";

    /**
     * webベースディレクトリ
     */
    private String webBaseDir = "../main/web";

    /**
     * クライアントサイドテスト用リソースの配置ディレクトリ
     */
    private String jsTestResourceDir = "../test/web";

    /**
     * アプリケーション用設定ファイル
     * (本番用ファイルとの差分）
     */
    private String xmlComponentFile = null;

    /**
     * ユーザIDセッションキー
     */
    private String userIdSessionKey = "user.id";

    /**
     * ApplicationExceptionが格納されるリクエストスコープのキー
     */
    private String exceptionRequestVarKey = ExecutionContext.THROWN_APPLICATION_EXCEPTION_KEY;

    /**
     * ダンプファイルの拡張子
     */
    private String dumpFileExtension = "html";

    /**
     * HTMLリソースの文字コード。
     * デフォルトはUTF-8。
     */
    private String htmlResourcesCharset = "UTF-8";

    /**
     * ダンプHTMLへの可変項目の出力可否。
     */
    private boolean dumpVariableItem = false;

    /** HTMLリソースとしてコピー対象から除外するディレクトリのリスト */
    private List<String> ignoreHtmlResourceDirectory;

    /** テンプディレクトリのパス */
    private String tempDirectory;

    /** アップロードファイルの一時ディレクトリ */
    private String uploadTmpDirectory = "./tmp";

    /**
     * ダンプHTMLへの可変項目の出力可否を取得する。
     * @return dumpVariableItem ダンプHTMLへの可変項目の出力可否
     */
    public boolean isDumpVariableItem() {
        return dumpVariableItem;
    }

    /**
     * ダンプHTMLへの可変項目の出力可否を設定する。
     * @param dumpVariableItem dumpVariableItem ダンプHTMLへの可変項目の出力可否
     */
    public void setDumpVariableItem(boolean dumpVariableItem) {
        this.dumpVariableItem = dumpVariableItem;
    }

    /**
     * HtmlCheckツール設定ファイルのパス
     */
    private String htmlCheckerConfig;

    /**
     * HtmlCheckツールを実行するか否か
     */
    private boolean isCheckHtml = true;

    /**
     * HTMLリソースの文字コードを設定する。
     * @return HTMLリソースの文字コード
     */
    @Published(tag = "architect")
    public String getHtmlResourcesCharset() {
        return htmlResourcesCharset;
    }

    /**
     * HTMLリソースの文字コードを設定する。
     * @param htmlResourcesCharset HTMLリソースの文字コード
     */
    public void setHtmlResourcesCharset(String htmlResourcesCharset) {
        this.htmlResourcesCharset = htmlResourcesCharset;
    }

    /**
     * HTTPヘッダー
     */
    private Map<String, String> httpHeader = new HashMap<String, String>() {
        {
            put("Content-Type", "application/x-www-form-urlencoded");
            put("Accept-Language", "ja JP");
        }
    };

    /**
     * システムで使用する共通のセッション情報(ログインユーザ名など)
     */
    private Map<String, Object> sessionInfo = new HashMap<String, Object>();

    /**
     * Webベース配下から、ダンプディレクトリへコピーされるHTMLリソースの拡張子。
     * ここでコピーされたファイルは、ダンプHTMLのURI属性（hrefやsrc）から参照される。
     */
    private List<String> htmlResourcesExtensionList = Arrays.asList("css", "js", "jpg");

    /**
     * HTMLリソースのコピー先ディレクトリ名。
     */
    private String htmlResourcesRoot = "htmlResources";

    /** バックアップ実施フラグ */
    private boolean backup = true;

    /** HTML チェッカー*/
    private HtmlChecker htmlChecker;

    /**
     * バックアップ実施フラグを取得する。
     * @return バックアップ実施フラグ
     */
    public boolean isBackup() {
        return backup;
    }

    /**
     * バックアップ実施フラグを設定する。
     * @param backup バックアップ実施フラグ
     */
    public void setBackup(boolean backup) {
        this.backup = backup;
    }

    /**
     * ダンプディレクトリへコピーされるHTMLリソースの拡張子を取得する。
     * @return ダンプディレクトリへコピーされるHTMLリソースの拡張子
     */
    @Published(tag = "architect")
    public List<String> getHtmlResourcesExtensionList() {
        return htmlResourcesExtensionList;
    }

    /**
     * ダンプディレクトリへコピーされるHTMLリソースの拡張子を設定する。
     * @param htmlResourcesExtensionList ダンプディレクトリへコピーされるHTMLリソースの拡張子
     */
    public void setHtmlResourcesExtensionList(
            List<String> htmlResourcesExtensionList) {
        this.htmlResourcesExtensionList = htmlResourcesExtensionList;
    }

    /**
     * HTMLリソースのコピー先ディレクトリ名を取得する。
     * @return HTMLリソースのコピー先ディレクトリ名
     */
    @Published(tag = "architect")
    public String getHtmlResourcesRoot() {
        return htmlResourcesRoot;
    }

    /**
     * HTMLリソースのコピー先ディレクトリ名を設定する。
     * @param htmlResourcesRoot HTMLリソースのコピー先ディレクトリ名
     */
    public void setHtmlResourcesRoot(String htmlResourcesRoot) {
        this.htmlResourcesRoot = htmlResourcesRoot;
    }

    /**
     * HTML出力ディレクトリを取得する。
     *
     * @return HTML出力ディレクトリ
     */
    @Published(tag = "architect")
    public String getHtmlDumpDir() {
        return htmlDumpDir;
    }

    /**
     * HTML出力ディレクトリを設定する。
     *
     * @param htmlDumpDir HTML出力ディレクトリ
     */
    public void setHtmlDumpDir(String htmlDumpDir) {
        this.htmlDumpDir = htmlDumpDir;
    }

    /**
     * webベースディレクトリを取得する。
     *
     * @return webベースディレクトリ
     */
    @Published(tag = "architect")
    public String getWebBaseDir() {
        return webBaseDir;
    }

    /**
     * webベースディレクトリを設定する。
     * <p>
     * PJ共通のwebモジュールが存在する場合、このプロパティに
     * カンマ区切りで複数のwebベースディレクトリを指定できる。
     *
     * {@literal "path/to/app/,path/to/common/"}
     * 複数指定した場合、先頭から順にリソースが探索される。
     * </p>
     * @param webBaseDir webベースディレクトリ
     */
    public void setWebBaseDir(String webBaseDir) {
        this.webBaseDir = webBaseDir;
    }


    /**
     * クライアントスクリプトテスト用リソース配置ディレクトリパスを取得する。
     *
     * @return クライアントスクリプトテスト用リソース配置ディレクトリパス。
     */
    @Published(tag = "architect")
    public String getJsTestResourceDir() {
        return jsTestResourceDir;
    }

    /**
     * クライアントスクリプトテスト用リソース配置ディレクトリパスを設定する。
     *
     * @param jsTestResourceDir クライアントスクリプトテスト用リソース配置ディレクトリパス
     */
    public void setJsTestResourceDir(String jsTestResourceDir) {
        this.jsTestResourceDir = jsTestResourceDir;
    }    

    /**
     * ターゲットコード用の設定ファイル(XMLファイル)をリードするための、ルートXMLファイル名を取得する。
     *
     * @return XMLファイル名
     */
    @Published(tag = "architect")
    public String getXmlComponentFile() {
        return xmlComponentFile;
    }

    /**
     * ターゲットコード用の設定ファイル(XMLファイル)をリードするための、ルートXMLファイル名を設定する。
     *
     * @param xmlComponentFile XMLファイル名
     */
    public void setXmlComponentFile(String xmlComponentFile) {
        this.xmlComponentFile = xmlComponentFile;
    }

    /**
     * ユーザIDセッションキーを取得する。
     *
     * @return ユーザIDセッションキー
     */
    @Published(tag = "architect")
    public String getUserIdSessionKey() {
        return userIdSessionKey;
    }

    /**
     * ユーザIDセッションキーを設定する。
     *
     * @param userIdSessionKey ユーザIDセッションキーを設定する。
     */
    public void setUserIdSessionKey(String userIdSessionKey) {
        this.userIdSessionKey = userIdSessionKey;
    }

    /**
     * ApplicationExceptionが格納されるリクエストスコープのキーを取得する。
     *
     * @return ApplicationExceptionが格納されるリクエストスコープのキー
     */
    @Published(tag = "architect")
    public String getExceptionRequestVarKey() {
        return exceptionRequestVarKey;
    }

    /**
     * ApplicationExceptionが格納されるリクエストスコープのキーを設定する。
     *
     * @param exceptionRequestVarKey ApplicationExceptionが格納されるリクエストスコープのキー
     */
    public void setExceptionRequestVarKey(String exceptionRequestVarKey) {
        this.exceptionRequestVarKey = exceptionRequestVarKey;
    }

    /**
     * ダンプファイルの拡張子を取得する。
     *
     * @return ダンプファイルの拡張子
     */
    @Published(tag = "architect")
    public String getDumpFileExtension() {
        return dumpFileExtension;
    }

    /**
     * ダンプファイルの拡張子を設定する。
     *
     * @param dumpFileExtension ダンプファイルの拡張子
     */
    public void setDumpFileExtension(String dumpFileExtension) {
        this.dumpFileExtension = dumpFileExtension;
    }

    /**
     * HtmlCheckツール設定ファイルのパスを取得する。
     *
     * @return HtmlCheckツール設定ファイル
     */
    @Published(tag = "architect")
    public String getHtmlCheckerConfig() {
        return htmlCheckerConfig;
    }

    /**
     * HtmlCheckツール設定ファイルのパスを設定する。
     *
     * @param htmlCheckerConfig HtmlCheckツール設定ファイルのパス
     */
    public void setHtmlCheckerConfig(String htmlCheckerConfig) {
        this.htmlCheckerConfig = htmlCheckerConfig;
        this.htmlChecker = new Html4HtmlChecker(htmlCheckerConfig);
    }

    /**
     * HTTPHeaderを取得する。
     * @return HTTPHeader
     */
    @Published(tag = "architect")
    public Map<String, String> getHttpHeader() {
        return httpHeader;
    }

    /**
     * HTTPHeaderを設定する。
     * @param httpHeader HTTPHeader
     */
    public void setHttpHeader(Map<String, String> httpHeader) {
        this.httpHeader = httpHeader;
    }

    /**
     * セッション情報を取得する。
     * @return セッション情報
     */
    @Published(tag = "architect")
    public Map<String, Object> getSessionInfo() {
        return sessionInfo;
    }

    /**
     * セッション情報を設定する。
     * @param sessionInfo セッション情報
     */
    public void setSessionInfo(Map<String, Object> sessionInfo) {
        this.sessionInfo = sessionInfo;
    }


    /**
     * HTMLチェック実行するか否かの設定を設定する。
     * @param isCheckHtml HTMLチェック実行するか否かの設定
     */
    public void setCheckHtml(boolean isCheckHtml) {
        this.isCheckHtml = isCheckHtml;
    }

    /**
     * HTMLチェック実行するか否かの設定を取得する。
     * @return HTMLチェック実行する場合はtrue、実行しない場合はfalse
     */
    @Published(tag = "architect")
    public boolean isCheckHtml() {
        return isCheckHtml;
    }

    /**
     * HTMLリソースディレクトリを取得する。
     * @return HTMLリソースディレクトリ
     */
    @Published(tag = "architect")
    public File getHtmlResourcesDir() {
        return new File(htmlDumpDir, htmlResourcesRoot);
    }

    /**
     * HTMLリソースとして除外するディレクトリを取得する。
     *
     * @return HTMLリソースディレクトリとして除外するディレクトリのリスト
     */
    @Published(tag = "architect")
    public List<String> getIgnoreHtmlResourceDirectory() {
        return ignoreHtmlResourceDirectory;
    }

    /**
     * HTMLリソースとして除外するディレクトリのリストを設定する。
     * <p/>
     * {@link #setHtmlResourcesRoot(String)}で設定したディレクトリ配下から、リソースとして除外するディレクトリを設定する。
     * たとえば、HTMLリソースがバージョン管理されている場合、バージョン管理用のディレクトリ(.svnや.git)などを設定することにより、
     * 不要なファイルがコピーされることを回避でききる。
     *
     * @param ignoreHtmlResourceDirectory HTMLリソースディレクトリとして除外するディレクトリのリスト
     */
    public void setIgnoreHtmlResourceDirectory(List<String> ignoreHtmlResourceDirectory) {
        this.ignoreHtmlResourceDirectory = ignoreHtmlResourceDirectory;
    }

    /**
     * テンプディレクトリのパスを取得する。
     *
     * @return テンプディレクトリのパス
     */
    @Published(tag = "architect")
    public String getTempDirectory() {
        return tempDirectory;
    }

    /**
     * テンプディレクトリのパスを設定する。
     * <p/>
     * JSPのコンパイル後のクラスが格納されるディレクトリ。
     * 本設定を省略して場合のデフォルトテンプディレクトリは、jettyのデフォルト動作となる。
     *
     * @param tempDirectory テンプディレクトリのパス
     */
    public void setTempDirectory(String tempDirectory) {
        this.tempDirectory = tempDirectory;
    }

    /**
     * アップロードファイルの一時ディレクトリを取得する。
     *
     * 本ディレクトリは、テスト時にアップロードファイルを一時的に配置するディレクトリとなる。
     *
     * @return アップロードファイルの一時ディレクトリ
     */
    @Published(tag = "architect")
    public String getUploadTmpDirectory() {
        return uploadTmpDirectory;
    }

    /**
     * アップロードファイルの一時ディレクトリを設定する。
     * @param uploadTmpDirectory アップロードファイルの一時ディレクトリ
     */
    public void setUploadTmpDirectory(String uploadTmpDirectory) {
        this.uploadTmpDirectory = uploadTmpDirectory;
    }

    /**
     * HTMLチェッカーを取得する。
     * 
     * @return HTMLチェッカー
     */
    @Published(tag = "architect")
    public HtmlChecker getHtmlChecker() {
        return htmlChecker;
    }

    /**
     * HTMLチェッカーを設定する。
     * 
     * @param htmlChecker HTMLチェッカー
     */
    public void setHtmlChecker(HtmlChecker htmlChecker) {
        this.htmlChecker = htmlChecker;
    }
}

