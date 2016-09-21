package nablarch.test.core.file;

import nablarch.core.dataformat.InvalidDataFormatException;
import nablarch.core.dataformat.convertor.datatype.ByteStreamDataSupport;
import nablarch.core.dataformat.convertor.datatype.DataType;
import nablarch.core.util.Builder;
import nablarch.core.util.StringUtil;

/**
 * 文字列とバイト列の相互変換を行うテスト用データタイプ。
 * <p/>
 * テストケースに記載した入力ファイル、出力ファイルのデータを、そのまま文字列として使用する場合に使用する。
 * <p/>
 * このデータタイプを使用する場合は、データのサイズとフィールド長が一致する必要があり、一致しなければ例外がスローされる。
 * 
 * @author Masato Inoue
 */
public class StringDataType extends ByteStreamDataSupport<String> {

    /** {@inheritDoc} */
    @Override
    public DataType<String, byte[]> initialize(Object... args) {
        setSize((Integer) args[0]);
        return this;
    }

    /** {@inheritDoc} */
    @Override
    public String convertOnRead(byte[] data) {
        return StringUtil.toString(data, getField().getEncoding());
    }

    /** {@inheritDoc} */
    @Override
    public byte[] convertOnWrite(Object data) {
        
        byte[] bytes = StringUtil.getBytes((String) data, getField().getEncoding());
        
        if (bytes.length != getSize()) {
            throw new InvalidDataFormatException(Builder.concat(
                "invalid parameter was specified. "
              , "data byte length did not match specified field size." 
              , " data size=["  , bytes.length  , "],"
              , " field size=[" , getSize(), "],"
              , " data=[", data, "]."
            ));
        }

        return bytes;
    }
}
