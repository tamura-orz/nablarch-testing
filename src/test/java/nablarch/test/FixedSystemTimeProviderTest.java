package nablarch.test;

import static org.junit.Assert.assertEquals;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;


public class FixedSystemTimeProviderTest {

    private FixedSystemTimeProvider target = new FixedSystemTimeProvider();
    
    
    @Test
    public void testSetyyyyMMddHHmmssSSS() {
        String yyyyMMddHHmmssSSS = "20101122012345678";
        target.setFixedDate(yyyyMMddHHmmssSSS);
        Date actual = target.getDate();
        assertEquals("20101122012345678", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(actual));
     
    }
    
    @Test
    public void testSetyyyyMMddHHmmss() {
        String yyyyMMddHHmmssSSS = "20101122012345";
        target.setFixedDate(yyyyMMddHHmmssSSS);
        Date actual = target.getDate();
    
        assertEquals("20101122012345000", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(actual));  
    }
    
    @Test(expected=IllegalArgumentException.class)
    public void testTooShort() {
        // 最短に対して1桁少ない
        String yyyyMMddHHmmssSSS = "2010112201234";
        target.setFixedDate(yyyyMMddHHmmssSSS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testJustLittleLong() {
        // 最短に対して1桁多い
        String yyyyMMddHHmmssSSS = "201011220123456";
        target.setFixedDate(yyyyMMddHHmmssSSS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testJustLittleShort() {
        // 最長に対して1桁少ない
        String yyyyMMddHHmmssSSS = "2010112201234567";
        target.setFixedDate(yyyyMMddHHmmssSSS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testTooLong() {
        // 最長に対して1桁多い
        String yyyyMMddHHmmssSSS = "201011220123456789";
        target.setFixedDate(yyyyMMddHHmmssSSS);
    }

    @Test(expected=IllegalArgumentException.class)
    public void testInvalidFormat() {
        String yyyyMMddHHmmssSSS = "yyyyMMddHHmmssSSS";
        target.setFixedDate(yyyyMMddHHmmssSSS);
    }
    
    @Test(expected=IllegalStateException.class)
    public void getDate() {
        target.getDate();
    }
    
    @Test
    public void testGetTimestamp() {
        String yyyyMMddHHmmssSSS = "20101122012345678";
        target.setFixedDate(yyyyMMddHHmmssSSS);
        Timestamp actual = target.getTimestamp();
        assertEquals("20101122012345678", new SimpleDateFormat("yyyyMMddHHmmssSSS").format(actual));
    }
}
