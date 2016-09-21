package nablarch.test.tool.sanitizingcheck.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;


/**
 * ファイル関連ユーティリティクラス。
 *
 * @author Tomokazu Kagawa
 */
public final class FileUtil {

    /**
     * デフォルトコンストラクタ
     */
    private FileUtil() {
    }

    /**
     * 読み込み時のデフォルト文字コード指定
     */
    private static final Charset DEFAULT_ENCODING = Charset.forName("UTF-8");

    /**
     * xml出力時のデフォルトインデックス数
     */
    private static final int DEFAULT_INDEX_AMOUNT = 2;

    /**
     * テキストファイルを読み込む。<br>
     * 文字コードはデフォルト(UTF-8)を使用する。<br>
     * 返却値はList<String[]>であり、CSVファイルの各行がListの1レコードに対応する。
     *
     * @param filePath 読み込み対象ファイルパス
     * @return 読み込む結果
     */
    public static List<String> readFile(String filePath) {
        return readFile(filePath, DEFAULT_ENCODING);
    }

    /**
     * テキストファイルを読み込む。<br>
     * 返却値はList<String>であり、テキストファイルの各行がListの1レコードに対応する。
     *
     * @param filePath 読み込み対象CSVファイル
     * @param charset 読み込み対象CSVファイルの文字コード
     * @return 読み込む結果
     */
    public static List<String> readFile(String filePath, Charset charset) {

        BufferedReader reader = null;
        try {

            reader = open(filePath, charset);
            List<String> text = new ArrayList<String>();
            String line;
            while ((line = reader.readLine()) != null) {
                text.add(line);
            }
            return text;

        } catch (IOException e) {
            throw new IllegalArgumentException("can't read file [" + filePath + "]", e);
        } finally {
            closeQuietly(reader);
        }
    }

    /**
     * ファイル読み込み内容を文字列として返却する。<br>
     * 文字コードはUTF-8を使用する。
     * 改行文字も読み込み返却する。
     *
     * @param file 読み込み対象ファイル
     * @return ファイル内容を保有する文字列
     */
    public static String readFileToString(File file) {

        return readFileToString(file, DEFAULT_ENCODING);
    }

    /**
     * ファイル読み込み内容を文字列として返却する。<br>
     * 改行文字も読み込み返却する。
     *
     * @param file 読み込み対象ファイル
     * @param charset 指定文字コード
     * @return ファイル内容を保有する文字列
     */
    public static String readFileToString(File file, Charset charset) {

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();

        try {
            reader = open(file, charset);

            int c;
            while ((c = reader.read()) != -1) {
                sb.append((char) c);
            }

        } catch (IOException e) {
            throw new IllegalArgumentException("can't read file [" + file + "]", e);
        } finally {
            closeQuietly(reader);
        }

        return sb.toString();
    }

    /**
     * ファイルをオープンし、BufferedReaderを返却する。
     *
     * @param file 読み込み対象ファイル
     * @param charset 指定文字コード
     * @return BufferedReaderインスタンス
     * @throws FileNotFoundException 指定したファイルが存在しない場合のエラー
     */
    public static BufferedReader open(File file, Charset charset) throws FileNotFoundException {
        InputStream in = new FileInputStream(file);
        return new BufferedReader(new InputStreamReader(in, charset));
    }

    /**
     * ファイルをオープンし、BufferedReaderを返却する。
     *
     * @param filePath 読み込み対象ファイルパス
     * @param charset 指定文字コード
     * @return BufferedReaderインスタンス
     * @throws FileNotFoundException 指定したファイルが存在しない場合のエラー
     */
    public static BufferedReader open(String filePath, Charset charset) throws FileNotFoundException {
        return open(new File(filePath), charset);
    }

    /**
     * Closeableインタフェース実装クラスに対して、クローズ処理を行う。
     *
     * @param closeable クローズ対象リソース
     */
    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null) {
                closeable.close();
            }
        } catch (Exception ignored) {
            // 例外をthrowしても、ユーザーは対処できない。
            // そのため、処理を行わない。
            return;
        }
    }

    /**
     * XML文書全体を表すDocumentオブジェクトをXMLに変換し、出力する。<br>
     * インデント数はデフォルト値の2を使用する。
     *
     * @param document XML文書全体を表すDocumentオブジェクト
     * @param xmlPath 出力先XMLファイルパス
     */
    public static void outToXml(Document document, String xmlPath) {
        outToXml(document, xmlPath, DEFAULT_INDEX_AMOUNT);
    }

    /**
     * XML文書全体を表すDocumentオブジェクトをXMLに変換し、出力する。
     *
     * @param document XML文書全体を表すDocumentオブジェクト
     * @param xmlPath 出力先XMLファイルパス
     * @param indexAmount インデント数
     */
    private static void outToXml(Document document, String xmlPath, int indexAmount) {

        Transformer transformer = null;

        try {
            TransformerFactory factory = TransformerFactory.newInstance();
            factory.setAttribute("indent-number", 2);
            transformer = factory.newTransformer();
        } catch (TransformerConfigurationException e) {
            throw new RuntimeException(e);
        } catch (TransformerFactoryConfigurationError e) {
            throw new RuntimeException(e);
        }

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.STANDALONE, "no");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.setOutputProperty(OutputKeys.METHOD, "xml");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

        Writer writer = null;
        try {
            writer = new FileWriter(new File(xmlPath));
            transformer.transform(new DOMSource(document), new StreamResult(writer));

        } catch (IOException e) {
            throw new IllegalArgumentException("can't output to xml =[" + xmlPath + "]", e);
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtil.closeQuietly(writer);
        }
    }
}
