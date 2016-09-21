package nablarch.test.core.file;

import nablarch.core.dataformat.DataRecordFormatterSupport.Directive;
import nablarch.core.dataformat.VariableLengthDataRecordFormatter.VariableLengthDirective;
import nablarch.core.util.Builder;

import static nablarch.core.dataformat.VariableLengthDataRecordFormatter.VariableLengthDirective.FIELD_SEPARATOR;

/**
 * 可変長ファイルを表すクラス。<br/>
 *
 * @author T.Kawasaki
 */
public class VariableLengthFile extends DataFile {

    /** タブを表す文字 */
    private static final String TAB_EXPRESSION = "\\t";

    /** ディレクティブのデフォルト値取得用キー */
    private static final String DEFAULT_DIRECTIVES = "variableLengthDirectives";

    /**
     * コンストラクタ。<br/>
     *
     * @param path ファイルパス
     */
    public VariableLengthFile(String path) {
        super(path);
        setDirective(FIELD_SEPARATOR.getName(), ",");
        prepareDefaultDirectives(DEFAULT_DIRECTIVES);
    }

    /**
     * {@inheritDoc}
     * 可変長ファイルを表すファイルタイプが返却される。
     */
    @Override
    protected String getFileType() {
        return "Variable";
    }

    /**
     * {@inheritDoc}
     * 可変長ファイルの断片が返却される。
     * @see VariableLengthFileFragment
     */
    @Override
    protected DataFileFragment createNewFragment() {
        return new VariableLengthFileFragment(this);
    }

    /**
     * {@inheritDoc}
     * 可変長ファイルのディレクティブ定義から対応するディレクティブが返却される。
     * @see VariableLengthDirective
     */
    @Override
    protected Directive valueOf(String directiveName) {
        return VariableLengthDirective.valueOf(directiveName);
    }

    /**
     * {@inheritDoc}
     * 区切り文字に"\\t"が指定された場合、タブに変換する。
     */
    @Override
    protected Object convertDirectiveValue(Directive directive, String stringValue) {
        if (directive.equals(FIELD_SEPARATOR)) {
            // タブの変換
            if (stringValue.equals(TAB_EXPRESSION)) {
                return "\t";
            }

            // フィールド区切り文字は１文字でなければならない
            if (stringValue.length() != 1) {
                throw new IllegalArgumentException(Builder.concat(
                        FIELD_SEPARATOR.getName(), " must be one character.",
                        "but was ", stringValue));
            }
        }
        return super.convertDirectiveValue(directive, stringValue);
    }
}
