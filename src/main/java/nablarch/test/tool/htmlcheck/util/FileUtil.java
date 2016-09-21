package nablarch.test.tool.htmlcheck.util;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import nablarch.core.util.annotation.Published;


/**
 * ファイル関連ユーティリティクラス。
 *
 * @author Tomokazu Kagawa
 */
@Published(tag = "architect")
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
     * CSVファイルを読み込む。<br>
     * 文字コードはデフォルト(UTF-8)を使用する。<br>
     * 返却値はList<String[]>であり、CSVファイルの各行がListの1レコードに対応する。
     * 
     * @param csvPath 読み込み対象CSVファイルパス 
     * @return 読み込む結果
     */
    public static List<String[]> readCsv(String csvPath) {
        return readCsv(csvPath, DEFAULT_ENCODING);
    }

    /**
     * CSVファイルを読み込む。<br>
     * 返却値はList<String[]>であり、CSVファイルの各行がListの1レコードに対応する。
     * 
     * @param csvPath 読み込み対象CSVファイル
     * @param charset 読み込み対象CSVファイルの文字コード
     * @return 読み込む結果
     */
    public static List<String[]> readCsv(String csvPath, Charset charset) {

        BufferedReader reader = null;
        try {
            reader = open(csvPath, charset);
            List<String[]> csv = new ArrayList<String[]>();
            String line;
            while ((line = reader.readLine()) != null) {
                String[] elements = line.split(",", -1);
                csv.add(elements);
            }
            return csv;
        } catch (IOException e) {
            throw new RuntimeException("can't read file [" + csvPath + "]", e);
        } finally {
            closeQuietly(reader);
        }
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
}
