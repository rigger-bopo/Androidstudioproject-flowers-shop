package com.example.xianhuashangdian;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import com.example.xianhuashangdian.util.PasswordUtils;

import org.junit.Test;

public class PasswordUtilsTest {
    @Test
    public void passwordHash_isDeterministicAndCaseInsensitiveForUsername() {
        String first = PasswordUtils.hashPassword("Demo", "123456a");
        String second = PasswordUtils.hashPassword("demo", "123456a");
        assertEquals(first, second);
    }

    @Test
    public void passwordHash_changesWithPassword() {
        assertNotEquals(
                PasswordUtils.hashPassword("demo", "123456a"),
                PasswordUtils.hashPassword("demo", "654321a"));
    }

    @Test
    public void answerHash_trimsAndIgnoresCase() {
        assertEquals(
                PasswordUtils.hashAnswer(" Flower "),
                PasswordUtils.hashAnswer("flower"));
    }
}
