package nablarch.test.core.batch;

import nablarch.core.dataformat.DataRecord;
import nablarch.core.util.FilePathSetting;
import nablarch.core.util.FileUtil;
import nablarch.core.util.StringUtil;
import nablarch.fw.DataReader;
import nablarch.fw.ExecutionContext;
import nablarch.fw.Result;
import nablarch.fw.action.BatchAction;
import nablarch.fw.launcher.CommandLine;
import nablarch.fw.reader.FileDataReader;
import nablarch.test.NablarchTestUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Iterator;

/**
 * @author T.Kawasaki
 */
public class FileToFileBatchSample extends BatchAction<DataRecord> {

    private OutputStream out;

    private Iterator<String> outputIterator;

    @Override
    protected void initialize(CommandLine command, ExecutionContext context) {
        out = NablarchTestUtils.openAsOutputStream("work/output.txt");
        outputIterator = Arrays.asList(
            "1000100011HELLO     ",
            "1000200021GOOD BYE. ").iterator();
    }

    @Override
    protected void terminate(Result result, ExecutionContext context) {
        FileUtil.closeQuietly(out);
    }

    @Override
    public Result handle(DataRecord inputData, ExecutionContext ctx) {
        if (outputIterator.hasNext()) {
            String s = outputIterator.next();
            try {
                out.write(StringUtil.getBytes(s, Charset.forName("UTF-8")));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return new Result.Success();
    }

    @Override
    public DataReader<DataRecord> createReader(ExecutionContext ctx) {
        FilePathSetting.getInstance()
                .addBasePathSetting("output", "file:./work")
                .addBasePathSetting("input", "file:./work")
                .addBasePathSetting("format", "file:./work");

        return new FileDataReader()
                .setDataFile("input.txt")
                .setLayoutFile("layout.txt");

    }
}
