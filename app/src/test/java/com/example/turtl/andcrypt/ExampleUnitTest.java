package com.example.turtl.andcrypt;

import org.junit.Ignore;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    makeId makeId = new makeId();
    MainActivity mainActivity = new MainActivity();
    Hasher hasher = new Hasher();
    String testPass = "utututt";
    //Start of tests
    @Test
    public void isExistingUser() throws Exception {
        mainActivity.setUpContext();
        mainActivity.setupFile(mainActivity.context);
        assertEquals(false,mainActivity.isExistingUser());
    }


    @Test
    public void duplicateStringWorks() {
        assertEquals("testtest",duplicateString("test"));
    }

    public String duplicateString(String s) {
        String complete = s;
        for (int x =0; x < s.length(); x++) {
            s+=s.charAt(x);
        }
        return complete;
    }

}