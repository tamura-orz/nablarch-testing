package nablarch.common.date;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nablarch.core.validation.ConversionFormat;

/**
 * 年月日フォーマット指定を表すアノテーション。</br>
 * バリデーションの詳細は、{@link DateStringConvertor}のJavaDocを参照。
 *
 * @author Tomokazu Kagawa
 * @deprecated {@link YYYYMMDD}に置き換わりました。
 */
@ConversionFormat
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface DateString {

    /** 入力値として許容する年月日フォーマット。 */
    String allowFormat() default "";

    /** 変換失敗時のメッセージID。 */
    String messageId() default "";
}
