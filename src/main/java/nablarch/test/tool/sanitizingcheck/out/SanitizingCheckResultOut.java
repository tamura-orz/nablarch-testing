package nablarch.test.tool.sanitizingcheck.out;

import nablarch.test.tool.sanitizingcheck.util.FileUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * サニタイジングチェック結果をXMLに出力する。
 *
 * @author Tomokazu Kagawa
 */
public final class SanitizingCheckResultOut {

    /**
     * デフォルトコンストラクタ
     */
    private SanitizingCheckResultOut() {
    }

    /**
     * サニタイジングチェック結果をXMLファイルに出力する。<br>
     * 引数のMap<String, List<String>>インスタンスは次のものとする。<br>
     * キー値にはJSP名、バリュー値には当該JSPにて検知したエラーメッセージのリストが格納されている。
     *
     * @param sanitizingCheckMessages サニタイジングチェック結果エラーメッセージ
     * @param xmlPath 出力先XMLファイル
     */
    public static void outToXml(Map<String, List<String>> sanitizingCheckMessages, String xmlPath) {

        Document dom = createDom(sanitizingCheckMessages);
        FileUtil.outToXml(dom, xmlPath);

    }

    /**
     * サニタイジングチェック結果のMap<String, List<String>>インスタンスより、
     * XML出力用のDocumentオブジェクトを作成する。
     *
     * @param sanitizingCheckMessages サニタイジングチェック結果エラーメッセージ
     * @return XML出力用のDocumentオブジェクト
     */
    private static Document createDom(Map<String, List<String>> sanitizingCheckMessages) {

        Document document = null;

        try {
            document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Element result = document.createElement("result");
        for (Entry<String, List<String>> entry : sanitizingCheckMessages.entrySet()) {
            String jspName = entry.getKey();
            Element item = document.createElement("item");
            Element path = document.createElement("path");
            Text jspPath = document.createTextNode(jspName);
            path.appendChild(jspPath);
            item.appendChild(path);
            Element errors = document.createElement("errors");
            for (String error : entry.getValue()) {
                Element errorDetail = document.createElement("error");
                Text errorMessage = document.createTextNode(error);
                errorDetail.appendChild(errorMessage);
                item.appendChild(errorDetail);
                errors.appendChild(errorDetail);
            }
            item.appendChild(errors);
            result.appendChild(item);
        }
        document.appendChild(result);
        return document;
    }
}
