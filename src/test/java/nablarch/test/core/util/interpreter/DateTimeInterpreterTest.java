package nablarch.test.core.util.interpreter;

import org.junit.Before;
import org.junit.Test;

import nablarch.test.FixedSystemTimeProvider;

import static org.junit.Assert.assertEquals;

/**
 *
 *
 * @author T.Kawasaki
 */
public class DateTimeInterpreterTest {

    /** テスト対象クラス */
    private DateTimeInterpreter target = new DateTimeInterpreter();

    /**
     * 前準備
     */
    @Before
    public void setUp() {
        FixedSystemTimeProvider provider = new FixedSystemTimeProvider();
        provider.setFixedDate("20110411012345");
        target.setSystemTimeProvider(provider);
        target.setSetUpDateTime("2010-12-13 12:34:56.0");
    }

    /**
     * システム時刻が解釈できること
     */
    @Test
    public void testInterpretSystemTime() {

        InterpretationContext ctx = new InterpretationContext("${systemTime}", target);
        assertEquals("2011-04-11 01:23:45.0", ctx.invokeNext());
    }

    /**
     * 更新日時が解釈できること
     */
    @Test
    public void testInterpretUpdateTime() {

        InterpretationContext ctx = new InterpretationContext("${updateTime}", target);

        assertEquals("2011-04-11 01:23:45.0", ctx.invokeNext());
    }

    /**
     * DBセットアップ日時が解釈できること
     */
    @Test
    public void testInterpretSetUpTime() {

        InterpretationContext ctx = new InterpretationContext("${setUpTime}", target);
        assertEquals("2010-12-13 12:34:56.0", ctx.invokeNext());
    }

    /**
     * 日時を表す記法でない場合は、入力値がそのまま返却されること。
     */
    @Test
    public void testInterpretNotApplicable() {
        InterpretationContext ctx = new InterpretationContext("${hoge}", target);
        assertEquals("${hoge}", ctx.invokeNext());
    }

    /**
     * setSystemTimeProviderにnullが渡されたとき、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetSystemTimeProviderNull() {
        target.setSystemTimeProvider(null);
    }

    /**
     * setSetUpDateTimeにnullが渡されたとき、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetSetUpDateTimeNull() {
        target.setSetUpDateTime(null);
    }

    /**
     * setSetUpDateTimeに不正な形式の値が渡されたとき、例外が発生すること。
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSetSetUpDateTimeInvalid() {
        target.setSetUpDateTime("invalid argument");
    }

}
