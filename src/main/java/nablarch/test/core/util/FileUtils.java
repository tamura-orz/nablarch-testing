package nablarch.test.core.util;

import nablarch.core.util.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * ファイルユーティリティ。
 *
 * @author Masato Inoue
 */
public final class FileUtils {

    /** privateコンストラクタ。*/
    private FileUtils() {
    }

    /**
     * ディレクトリをコピーする。<br/>
     * コピー先のディレクトリとしてコピー元のサブディレクトリが指定された場合、例外をスローする。
     *
     * @param inDir コピー元ディレクトリ
     * @param outDir コピー先ディレクトリ
     * @param fileFilter ファイルフィルタ
     */
    public static void copyDir(File inDir, File outDir, FileFilter fileFilter) {
        copyDir(inDir, outDir, fileFilter, true);
    }

    /**
     * ディレクトリをコピーする。<br/>
     * コピー先のディレクトリとしてコピー元のサブディレクトリが指定された場合、例外をスローする。
     *
     * @param inDir コピー元ディレクトリ
     * @param outDir コピー先ディレクトリ
     * @param fileFilter ファイルフィルタ
     * @param override コピー先にファイルが存在した場合、コピー元で上書きを行うか否か。(true:上書き,false:コピーしない)
     */
    public static void copyDir(File inDir, File outDir, FileFilter fileFilter, boolean override) {
        if (inDir == null) {
            throw new IllegalArgumentException("input dir is null.");
        } else if (!inDir.exists()) {
            throw new IllegalArgumentException("input dir not found. path=[" + inDir + "]");
        } else if (!inDir.isDirectory()) {
            throw new IllegalArgumentException("input dir is not directory. path=[" + outDir + "]");
        } else if (outDir == null) {
            throw new IllegalArgumentException("output dir is null.");
        }

        // コピー先ディレクトリがコピー元ディレクトリと同一のパス、またはサブディレクトリの場合、例外をスローする
        if (getCanonicalPath(outDir).startsWith(getCanonicalPath(inDir))) {
            throw new IllegalArgumentException("output dir is not directory. path=[" + outDir + "]");
        }
        copyDirRecursively(inDir, outDir, fileFilter, override);
    }

    /**
     * ディレクトリを作成する。<br/>
     * 指定されたディレクトリが既に存在する場合は何もしない。
     * 指定されたディレクトリと同名のファイルが既に存在する場合は例外が発生する。
     *
     * @param dir 作成対象ディレクトリ
     */
    public static void mkdir(File dir) {
        if (dir.exists()) {
            if (!dir.isDirectory()) {
                throw new IllegalArgumentException("specified path is already exists,"
                        + " and not a directory. path=["
                        + dir.getAbsolutePath() + "]");
            }
        } else if (!dir.mkdirs()) {
            throw new RuntimeException("failed to create directory. path=[" + dir + "]");
        }
    }

    /**
     * ディレクトリをコピーする。
     *
     * @param inDir コピー元ディレクトリ
     * @param outDir コピー先ディレクトリ
     * @param fileFilter ファイルフィルタ
     * @param replace コピー先にファイルが存在した場合、コピー元で上書きを行うか否か。(true:上書き,false:コピーしない)
     */
    private static void copyDirRecursively(File inDir, File outDir, FileFilter fileFilter, boolean replace) {

        // 出力先ディレクトリ作成
        mkdir(outDir);
        // ファイルリストを取得する
        File[] fileArray = inDir.listFiles(fileFilter);
        // ディレクトリ配下の要素をコピー
        for (File file : fileArray) {
            // 出力先ファイルまたはディレクトリを同名で生成する。
            File outputFile = new File(outDir, file.getName());
            if (file.isDirectory()) {
                // ディレクトリの場合、自分自身のメソッドを再帰的に呼び出す
                copyDirRecursively(file, outputFile, fileFilter, replace);
            } else {
                if (!replace && outputFile.exists()) {
                    continue;
                }
                copyFile(file, outputFile);
                // ファイルが正常にコピーされた場合、最終更新日をセットする
                copyLastModified(file, outputFile);
            }
        }
        // ディレクトリが正常にコピーされた場合、最終更新日をセットする
        copyLastModified(inDir, outDir);
    }


    /**
     * ファイルをコピーする。
     *
     * @param inFile コピー元ファイル
     * @param outFile コピー先ファイル
     */
    private static void copyFile(File inFile, File outFile) {
        FileChannel in = null;
        FileChannel out = null;
        try {
            in = new FileInputStream(inFile).getChannel();
            out = new FileOutputStream(outFile).getChannel();
            in.transferTo(0, in.size(), out);
        } catch (IOException e) {
            throw new RuntimeException("failed to copy file.　src file path=["
                    + inFile + "]. dest file path=[" + outFile + "]", e);
        } finally {
            FileUtil.closeQuietly(in, out);
        }
    }

    /**
     * 入力ファイルの最終更新日を、出力ファイルに付与する。
     * @param inFile 入力ファイル
     * @param outFile 出力ファイル
     * @return 成否
     */
    private static boolean copyLastModified(File inFile, File outFile) {
        return outFile.setLastModified(inFile.lastModified());
    }

    /**
     * 正規パス名を取得する。
     *
     * @param file ファイル
     * @return 正規パス名
     */
    private static String getCanonicalPath(File file) {
        try {
            return file.getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException("failed to get canonical path.", e);
        }
    }

}
