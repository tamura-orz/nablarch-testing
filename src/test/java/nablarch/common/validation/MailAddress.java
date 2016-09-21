package nablarch.common.validation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


import nablarch.core.util.annotation.Published;
import nablarch.core.validation.Validation;

/**
 * メールアドレスであることを表すアノテーション。<br>
 *
 * @author Tomokazu Kagawa
 */
@Validation
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Published
public @interface MailAddress {

    /**
     * メールアドレスがバリデーションエラー時のメッセージID。<br/>
     * 指定しなかった場合、デフォルトが使用される。
     */
    String messageId() default "";
}
