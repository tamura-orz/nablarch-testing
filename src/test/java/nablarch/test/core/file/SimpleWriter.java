package nablarch.test.core.file;

import nablarch.test.tool.sanitizingcheck.util.FileUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;

/**
 * 簡易ファイル作成クラス。<br/>
 * {@link nablarch.tool.Hereis}が改行とか文字コード適当だったので
 *
 * @author T.Kawasaki
 */
public abstract class SimpleWriter {

    public static File touch(String path) {
        return touch(new File(path));
    }

    public static File touch(File file) {
        renew(file);
        return file;
    }

    public enum LS {
        CR("\r"),
        LF("\n"),
        CRLF("\r\n");

        private final String literal;

        LS(String literal) {
            this.literal = literal;
        }

        public String asLiteral() {
            return literal;
        }
    }

    private final Charset encoding;

    private LS ls = LS.CRLF;

    protected SimpleWriter() {
        this("UTF-8");
    }

    protected SimpleWriter(String encodingName) {
        this(Charset.forName(encodingName));
    }

    protected SimpleWriter(Charset encoding) {
        this.encoding = encoding;
    }

    protected abstract void write();

    public SimpleWriter with(LS ls) {
        this.ls = ls;
        return this;
    }

    public final AdditionalOperation to(String path) {
        return to(new File(path));
    }

    public final AdditionalOperation to(File file) {
        renew(file);
        doWrite(file);
        return new AdditionalOperation(file);
    }
    
    private static void renew(File file) {
        if (file.exists()) {
            assert file.delete();
        }
        try {
            assert file.createNewFile();
            assert file.exists();
            new FileOutputStream(file).close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    private BufferedWriter writer = null;

    protected final void print(Object... elements) {
        for (Object e : elements) {
            doPrint(String.valueOf(e));
        }
    }

    protected final void println(Object... elements) {
        print(elements);
        println();
    }

    protected final void println() {
        doPrint(ls.asLiteral());
    }

    private void doPrint(String s) {
        try {
            writer.write(s);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void doWrite(File file) {
        try {
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), encoding));
            write();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            FileUtil.closeQuietly(writer);
        }
    }

    public static class AdditionalOperation {
        public final File file;

        private AdditionalOperation(File file) {
            this.file = file;
        }

        AdditionalOperation deleteOnExit() {
            file.deleteOnExit();
            return this;
        }
    }
}
