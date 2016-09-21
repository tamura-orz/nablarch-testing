package nablarch.common.date;

import java.lang.annotation.Annotation;
import java.text.SimpleDateFormat;
import java.util.Date;

import nablarch.core.util.FormatSpec;
import nablarch.core.util.StringUtil;
import nablarch.core.validation.ValidationContext;
import nablarch.core.validation.ValidationResultMessageUtil;
import nablarch.core.validation.convertor.ConversionUtil;
import nablarch.core.validation.convertor.ExtendedStringConvertor;

/**
 * 年月日を表す文字列に値を変換するクラス。
 * <p/>
 * 本クラスで変換するプロパティには、必ず{@link DateString}アノテーションを付与しておく必要がある。
 * 本クラスでは、{@link DateString}アノテーションの属性を下記の通り使用する。</br>
 * <table border=1>
 *     <tr bgcolor="#cccccc">
 *         <td>{@link DateString}アノテーションの属性名</td><td>説明</td>
 *     </tr>
 *     <tr>
 *         <td>allowFormat</td>
 *         <td>入力値として許容する年月日フォーマット。
 *             java.text.SimpleDateFormatが規定している構文で指定する。
 *             パターン文字は、y(年)、M(月)、d(月における日)のみ指定可能。
 *         </td>
 *     </tr>
 *     <tr>
 *         <td>messageId</td>
 *         <td>変換失敗時のメッセージID。
 *             指定がない場合は{@link DateStringConvertor}に設定されたメッセージIDを使用する。
 *         </td>
 *     </tr>
 * </table>
 *
 * <p>
 * <b>バリデーション仕様</b>
 * <p>
 * {@link DateString}アノテーションの設定によって行われるバリデーション仕様の例を以下に示す。</br>
 * <p>
 * フォーマットの設定とバリデーション内容：
 * <pre>
 * allowFormat=yyyy/MM/ddの場合</br>
 *     「2011/09/28」:有効
 *     「20110928」:有効。年月日の区切り文字(=パターン文字以外の文字)を取り除いたフォーマット(yyyyMMdd)も有効となる。
 *     「2011-09-28」:無効。年月日の区切り文字が異なる。
 *     「2011928」:無効。フォーマット(yyyyMMdd)にも一致しない。
 * </pre>
 * 
 * <p>
 * <b>国際化</b>
 * <p>
 * 年月日の記述は、言語によってはフォーマットが異なる(MM/dd/yyyyなど)。</br>
 * Nablarchのカスタムタグで国際化機能を使用した場合、本クラスはカスタムタグで指定されたフォーマットを使用する。
 * </p>
 * @author Tomokazu Kagawa
 * @deprecated {@link YYYYMMDDConvertor}に置き換わりました。
 */
public class DateStringConvertor implements ExtendedStringConvertor {

    /**
     * 指定された年月日への変換失敗時のメッセージID
     */
    private String parseFailedMessageId;

    /**
     * 指定された年月日への変換失敗時のメッセージIDを設定する。
     *
     * @param parseFailedMessageId 指定された年月日への変換失敗時のメッセージID
     */
    public void setParseFailedMessageId(String parseFailedMessageId) {
        this.parseFailedMessageId = parseFailedMessageId;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * 値がnullまたは空文字の場合は値をそのまま返す。
     * 値の変換は、{@link #convert(String, String)}メソッドに委譲する。
     */
    public <T> Object convert(ValidationContext<T> context, String propertyName, Object value, Annotation format) {

        String stringValue = (String) value;
        if (StringUtil.isNullOrEmpty(stringValue)) {
            // nullまたは空文字の場合は値をそのまま返す。
            return stringValue;
        }

        DateString yyyymmddFormat = (DateString) format;
        String allowFormat = getAllowFormat(context, propertyName, yyyymmddFormat);
        return convert(stringValue, allowFormat);
    }

    /**
     * 指定されたフォーマットで値を変換する。
     * <pre>
     * はじめに指定されたフォーマットで値の解析を試みる。
     * 指定されたフォーマットで解析できない場合は、
     * {@link #getNumbersOnlyFormat(String)}メソッドを使用し、
     * 年月日の区切り文字を取り除いたフォーマットで解析する。
     * 
     * さいごに解析結果として取得できるDateオブジェクトを"yyyyMMdd"形式の文字列に変換する。
     * </pre>
     * @param value 値
     * @param format フォーマット
     * @return 変換後の値
     */
    protected String convert(String value, String format) {
        Date date = DateUtil.getParsedDate(value, format);
        if (date == null) {
            date = DateUtil.getParsedDate(value, getNumbersOnlyFormat(format));
        }
        return new SimpleDateFormat("yyyyMMdd").format(date);
    }

    /**
     * {@inheritDoc}
     */
    public Class<?> getTargetClass() {
        return String.class;
    }

    /**
     * {@inheritDoc}
     * <p/>
     * 値がnullまたは空文字の場合はconvertメソッドでnullを返すため、本メソッドはtrueを返す。
     * 値の変換可否チェックは、{@link #isConvertible(String, String)}メソッドに委譲する。
     * <p/>
     * 値が変換不可の場合は、バリデーション結果メッセージを設定しfalseを返す。
     * メッセージIDは、{@link DateString}アノテーションのmessageId属性の値を使用する。
     * {@link DateString}アノテーションにメッセージIDが指定されていない場合は、
     * 本クラスのparseFailedMessageIdプロパティの値をメッセージIDに使用する。
     */
    public <T> boolean isConvertible(ValidationContext<T> context, String propertyName,
                                       Object propertyDisplayName, Object value, Annotation format) {

        String stringValue = (String) value;
        if (StringUtil.isNullOrEmpty(stringValue)) {
            // nullまたは空文字の場合はconvertメソッドでnullを返すためtrueを返す。
            return true;
        }

        DateString yyyymmddFormat;
        if (format instanceof DateString) {
            yyyymmddFormat = (DateString) format;
        } else {
            throw new IllegalArgumentException(
                "Must specify @" + getTargetAnnotation().getSimpleName() + " annotation. property=" + propertyName);
        }

        if (!isConvertible(stringValue, getAllowFormat(context, propertyName, yyyymmddFormat))) {
            // 指定されたフォーマットで変換不可
            // かつ年月日の区切り文字を取り除いたフォーマットで変換不可な場合
            String messageId = yyyymmddFormat.messageId();
            ValidationResultMessageUtil.addResultMessage(
                            context, propertyName,
                            StringUtil.hasValue(messageId) ? messageId : parseFailedMessageId,
                            propertyDisplayName);
            return false;
        }

        return true;
    }

    /**
     * 指定されたフォーマットで値が変換可能か否かを判定する。
     * <pre>
     * はじめに指定されたフォーマットで値が変換可能か否かを判定する。
     * 指定されたフォーマットで変換不可の場合は、
     * {@link #getNumbersOnlyFormat(String)}メソッドを使用し、
     * 年月日の区切り文字を取り除いたフォーマットで変換可能か否かを判定する。
     * </pre>
     * @param value 値
     * @param format フォーマット
     * @return 変換可能な場合はtrue
     */
    protected boolean isConvertible(String value, String format) {
        if (DateUtil.isValid(value, format)) {
            return true;
        }
        String numbersOnlyFormat = getNumbersOnlyFormat(format);
        return numbersOnlyFormat != null && DateUtil.isValid(value, numbersOnlyFormat);
    }

    /**
     * {@inheritDoc}
     */
    public Class<? extends Annotation> getTargetAnnotation() {
        return DateString.class;
    }

    /**
     * プロパティの変換に使用するフォーマットを取得する。
     * <p/>
     * {@link ConversionUtil#getFormatSpec(ValidationContext, String)}を呼び出し、
     * プロパティに対する有効なフォーマット仕様(dateString)が取得できた場合は、
     * フォーマット仕様に設定されたフォーマットを返す。
     * <p/>
     * プロパティに対する有効なフォーマット仕様が存在しない場合は、
     * {@link DateString#allowFormat()}の戻り値を返す。
     * 
     * @param <T> バリデーション結果で取得できる型
     * @param context ValidationContext
     * @param propertyName プロパティ名
     * @param yyyymmddFormat {@link DateString}
     * @return 変換に使用するフォーマット
     */
    protected <T> String getAllowFormat(ValidationContext<T> context, String propertyName, DateString yyyymmddFormat) {
        FormatSpec formatSpec = ConversionUtil.getFormatSpec(context, propertyName);
        if (formatSpec == null || !"dateString".equals(formatSpec.getDataType())) {
            return yyyymmddFormat.allowFormat();
        }
        String format = formatSpec.getFormatOfPattern();
        return StringUtil.hasValue(format) ? format : yyyymmddFormat.allowFormat();
    }

    /**
     * フォーマット文字列から年月日の区切り文字を取り除いた値を返す。
     * <p/>
     * {@link DateUtil#getNumbersOnlyFormat(String)}に処理を委譲する。
     * @param format フォーマット文字列
     * @return フォーマット文字列から年月日の区切り文字を取り除いた値
     */
    protected String getNumbersOnlyFormat(String format) {
        return DateUtil.getNumbersOnlyFormat(format);
    }
}
