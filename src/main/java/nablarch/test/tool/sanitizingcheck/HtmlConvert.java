package nablarch.test.tool.sanitizingcheck;

import java.io.File;
import java.io.FileOutputStream;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * HTMLチェックツール解析結果XMLをHTMLに変換する。
 * 
 * @author Tomokazu Kagawa
 */
public final class HtmlConvert {

    /**
     * コンストラクタ
     */
    private HtmlConvert() {
    }

    /**
     * HTMLチェックツール解析結果XMLをHTMLに変換する。
     * 
     * @param args 引数（チェック結果XMLパス、XSLTファイルパス、出力HTMLファイルパス）
     */
    public static void main(String[] args) {

        validate(args);

        String checkResXmlPath = args[0];
        String xsltFilePath = args[1];
        String outHtmlPath = args[2];

        try {
            TransformerFactory tFactory = TransformerFactory.newInstance();
            StreamSource xsltSrc = new StreamSource(xsltFilePath);
            Transformer transformer = tFactory.newTransformer(xsltSrc);

            StreamSource checkResXml = new StreamSource(checkResXmlPath);
            StreamResult resultHtml = new StreamResult(new FileOutputStream(outHtmlPath));
            transformer.transform(checkResXml, resultHtml);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 引数のバリデーションを行う<br>
     * 
     * @param args 引数に与えられた引数
     */
    public static void validate(String[] args) {

        if (args.length != 3) {
            throw new IllegalArgumentException("enter paths of xml, xslt and html.");
        }

        String checkResXmlPath = args[0];
        String xsltFilePath = args[1];

        File xsltFile = new File(xsltFilePath);
        File checkResXmlFile = new File(checkResXmlPath);

        if (!xsltFile.isFile()) {
            throw new IllegalArgumentException("Xslt file doesn't exist.");
        }

        if (!checkResXmlFile.isFile()) {
            throw new IllegalArgumentException("Check result file doesn't exist.");
        }
    }
}
