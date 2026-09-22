package com.example.xianhuashangdian;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import com.example.xianhuashangdian.util.ValidationUtils;

import org.junit.Test;

public class ValidationUtilsTest {
    @Test
    public void usernameValidation_acceptsExpectedFormat() {
        assertTrue(ValidationUtils.isValidUsername("demo"));
        assertTrue(ValidationUtils.isValidUsername("flower_user123"));
        assertFalse(ValidationUtils.isValidUsername("abc"));
        assertFalse(ValidationUtils.isValidUsername("bad user"));
        assertFalse(ValidationUtils.isValidUsername("中文账号"));
    }

    @Test
    public void passwordValidation_requiresLettersAndNumbers() {
        assertTrue(ValidationUtils.isValidPassword("123456a"));
        assertTrue(ValidationUtils.isValidPassword("flower2026"));
        assertFalse(ValidationUtils.isValidPassword("123456"));
        assertFalse(ValidationUtils.isValidPassword("password"));
        assertFalse(ValidationUtils.isValidPassword("a1"));
    }

    @Test
    public void quantityValidation_respectsStock() {
        assertTrue(ValidationUtils.isPositiveQuantity(1, 10));
        assertTrue(ValidationUtils.isPositiveQuantity(10, 10));
        assertFalse(ValidationUtils.isPositiveQuantity(0, 10));
        assertFalse(ValidationUtils.isPositiveQuantity(11, 10));
    }
}
