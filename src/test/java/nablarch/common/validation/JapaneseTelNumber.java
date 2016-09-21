package nablarch.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import nablarch.core.validation.Validation;

/**
 * 日本の電話番号であることを表すアノテーション。<br>
 *
 * @author Tomokazu Kagawa
 */
@Validation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface JapaneseTelNumber {

    /**
     * 電話番号がバリデーションエラー時のメッセージID。<br/>
     * 指定しなかった場合、デフォルトが使用される。
     */
    String messageId() default "";
}
