package com.seandas.totebox.data

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LoginRulesTest {
    @Test
    fun validEmails() {
        listOf("sam@example.com", "first.last+tag@mail.example.co.uk", "  sam@example.com  ")
            .forEach { assertTrue(it, LoginRules.isValidEmail(it)) }
    }

    @Test
    fun invalidEmails() {
        listOf("", "sam", "sam@", "@example.com", "sam@example", "sam example@mail.com", "sam@@example.com")
            .forEach { assertFalse(it, LoginRules.isValidEmail(it)) }
    }

    @Test
    fun passwordWithEightCharactersANumberAndASpecialCharacterIsValid() {
        listOf("abcdef1!", "Secret#2026", "12345678@", "pass word 9?")
            .forEach { assertTrue(it, LoginRules.isValidPassword(it)) }
    }

    @Test
    fun passwordMissingARuleIsInvalid() {
        listOf(
            "abc1!xy",      // 7 characters
            "abcdefg!",     // no number
            "abcdefg1",     // no special character
            "abcd efg1",    // a space is not a special character
            "",
        ).forEach { assertFalse(it, LoginRules.isValidPassword(it)) }
    }
}
