package nablarch.test.core.db;

import nablarch.core.message.Message;
import nablarch.core.message.MessageLevel;
import nablarch.core.validation.ValidationResultMessage;
import nablarch.test.core.db.EntityTestSupport.StringMessageMock;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * @author T.Kawasaki
 */
public class MessageComparatorTest {

    private MessageComparator target = new MessageComparator();

    @Test
    public void testMessageComparison() {
        assertThat(target.compare(message("M001"), message("M002")), is(-1));
        assertThat(target.compare(message("M001"), message("M001")), is(0));
        assertThat(target.compare(message("M002"), message("M001")), is(1));
    }

    private Message message(String id) {
        return new Message(MessageLevel.ERROR, new StringMessageMock(id));
    }

    @Test
    public void testValidationResultMessageComparisonWithDifferentProperty() {
        //
        assertThat(target.compare(validationResultMessage("prop1", "M002"),
                                  validationResultMessage("prop2", "M001")),
                   is(-1));

        assertThat(target.compare(validationResultMessage("prop2", "M001"),
                                  validationResultMessage("prop1", "M002")),
                   is(1));
    }


    @Test
    public void testValidationResultMessageComparisonWithSameProperty() {

        assertThat(target.compare(validationResultMessage("same", "M001"),
                                  validationResultMessage("same", "M002")),
                   is(-1));

        assertThat(target.compare(validationResultMessage("same", "M001"),
                                  validationResultMessage("same", "M001")),
                   is(0));

        assertThat(target.compare(validationResultMessage("same", "M002"),
                                  validationResultMessage("same", "M001")),
                   is(1));
    }

    private ValidationResultMessage validationResultMessage(String prop, String id) {
        return new ValidationResultMessage(prop, new StringMessageMock(id), null);
    }

    @Test
    public void testSort() {
        List<Message> orig = Arrays.asList(
                message("M001"),
                validationResultMessage("prop1", "M001"),
                validationResultMessage("prop2", "M002"),
                validationResultMessage("prop2", "M001"),
                validationResultMessage("prop1", "M002"),
                message("M002")
        );

        assertThat(MessageComparator.sort(orig), is(Arrays.asList(
                validationResultMessage("prop1", "M001"),
                validationResultMessage("prop1", "M002"),
                validationResultMessage("prop2", "M001"),
                validationResultMessage("prop2", "M002"),
                message("M001"),
                message("M002")
        )));
    }

    @Test
    public void testSortNull() {
        assertThat(MessageComparator.sort(null), is(nullValue()));
    }

    @Test
    public void testSortEmpty() {
        assertThat(MessageComparator.sort(Collections.<Message>emptyList()).isEmpty(),
                   is(true));
    }

}
