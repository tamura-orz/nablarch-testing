package nablarch.test.core.messaging.sample;

import java.util.Map;

import nablarch.core.validation.ValidateFor;
import nablarch.core.validation.ValidationContext;
import nablarch.core.validation.ValidationUtil;

/**
 * メッセージ同期送信テスト用オンラインAction。
 * 
 * @author Masato Inoue
 */
public class W11AD03Form {

    private String title;
    private String publisher;
    private String authors;

    public W11AD03Form(Map<String, Object> params) {
        title = (String) params.get("title");
        publisher = (String) params.get("publisher");
        authors = (String) params.get("authors");
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPublisher() {
        return publisher;
    }
    
    public void setPublisher(String publisher) {
        this.publisher = publisher;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    @ValidateFor("validateForSend")
    public static void validateForSend(ValidationContext<W11AD03Form> context) {
        ValidationUtil.validate(context,
                                new String[] { "title", "publisher", "authors"});
    }
}
