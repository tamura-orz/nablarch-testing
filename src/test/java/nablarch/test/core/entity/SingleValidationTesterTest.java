package nablarch.test.core.entity;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import nablarch.core.message.MockStringResourceHolder;
import nablarch.core.repository.SystemRepository;
import nablarch.core.validation.ValidationContext;
import nablarch.test.support.SystemRepositoryResource;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

/**
 * @author T.Kawasaki
 */
public class SingleValidationTesterTest {

    @Rule
    public SystemRepositoryResource repositoryResource = new SystemRepositoryResource(
            "nablarch/test/core/entity/SingleValidationTesterTest.xml");

    private static final String[][] MESSAGES = {
            {"MSG00010", "ja", "{0}を入力してください。"},
            {"MSG00011", "ja", "{0}は{1}桁以上{2}桁以下で入力してください。"},
            {"MSG00012", "ja", "{0}は半角英数字または記号で入力してください。"},
            {"MSG00013", "ja", "{0}は半角英字で入力してください。"},
            {"MSG00014", "ja", "{0}は半角英数字で入力してください。"},
            {"MSG00015", "ja", "{0}は半角文字で入力してください。"},
            {"MSG00016", "ja", "{0}は半角カナで入力してください。"},
            {"MSG00017", "ja", "{0}は全角文字で入力してください。"},
            {"MSG00018", "ja", "{0}は全角ひらがなで入力してください。"},
            {"MSG00019", "ja", "{0}は半角数字で入力してください。"},
            {"MSG00020", "ja", "{0}は全角カタカナで入力してください。"},
            {"MSG00021", "ja", "{0}は{1}から{2}の間の数値を入力してください。"},
            {"MSG00022", "ja", "画面遷移が不正です。"},
            {"MSG00023", "ja", "{0}は{1}桁で入力してください。"},
            {"MSG00024", "ja", "{0}は{2}文字以下で入力して下さい。"},
            {"MSG90001", "ja", "{0}が正しくありません。"},
    };

    @Before
    public void before() {
        repositoryResource.getComponentByType(MockStringResourceHolder.class)
                          .setMessages(MESSAGES);
    }

    /**
     * 妥当な値を投入した場合、バリデーションが成功すること。
     */
    @Test
    public void testInvokeValidationWithValidParam() {
        SingleValidationTester<TestEntity> target = new SingleValidationTester<TestEntity>(TestEntity.class, "ascii");
        ValidationContext<TestEntity> ctx = target.invokeValidation("abc");
        assertTrue(ctx.isValid());
    }

    /**
     * 不正な値を投入した場合、バリデーションは失敗すること。
     */
    @Test
    public void testInvokeValidationWithInvalidParam() {
        SingleValidationTester<TestEntity> target = new SingleValidationTester<TestEntity>(TestEntity.class, "ascii");
        ValidationContext<TestEntity> ctx = target.invokeValidation("あいう"); // ASCIIでない
        assertFalse(ctx.isValid());  // バリデーションはNGのはず
    }

    /**
     * 妥当な値を投入した場合、例外が発生しないこと。
     */
    @Test
    public void testTestSingleValidation1() {
        SingleValidationTester<TestEntity> target = new SingleValidationTester<TestEntity>(TestEntity.class, "ascii");
        target.testSingleValidation("abc123#", "");  // OK
    }

    /**
     * バリデーション失敗時に期待したメッセージが存在しなかった場合、例外が発生すること。
     */
    @Test
    public void testTestSingleValidationFail() {
        SingleValidationTester<TestEntity> target = new SingleValidationTester<TestEntity>(TestEntity.class, "ascii");
        try {
            target.testSingleValidation("abc123#", "MSG999999", "[testTestSingleValidationFail]");  // 期待したメッセージIDではない
            fail();
        } catch (AssertionError e) {
            assertThat(e.getMessage(), containsString("messageId [MSG999999] is expected"));
        }
    }

    /**
     * バリデーション失敗時に期待したメッセージが存在する場合は、例外が発生しないこと。
     */
    @Test
    public void testTestSingleValidation() {
        SingleValidationTester<TestEntity> target = new SingleValidationTester<TestEntity>(TestEntity.class, "ascii");
        target.testSingleValidation("漢字", "MSG00012"); // expected messageId should be returned
    }


    /**
     * リポジトリからインスタンス取得に失敗した場合、例外が発生すること。
     */
    @Test(expected = IllegalStateException.class)
    public void testGetValidationManagerFail() {
        SingleValidationTester<TestEntity> target = new SingleValidationTester<TestEntity>(TestEntity.class, "ascii");
        SystemRepository.clear();  // ValidationManager is removed.
        target.invokeValidation("hoge");  // must throw exception.
    }
}
