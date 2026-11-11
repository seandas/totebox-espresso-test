package com.seandas.totebox.data

/** What a login needs: a valid email and a password of 8+ characters with a number and a special character. */
object LoginRules {
    /** Also written into the login screen's helper and error text (strings.xml). */
    const val MIN_PASSWORD_LENGTH = 8

    // Plain Kotlin (not android.util.Patterns) so the rules run in JVM unit tests
    private val emailPattern = Regex("""[A-Za-z0-9._%+-]+@[A-Za-z0-9-]+(\.[A-Za-z0-9-]+)*\.[A-Za-z]{2,}""")

    fun isValidEmail(email: String): Boolean = emailPattern.matches(email.trim())

    /** A special character is anything that is not a letter, digit or space, such as ! @ # $ or %. */
    fun isValidPassword(password: String): Boolean =
        password.length >= MIN_PASSWORD_LENGTH &&
            password.any { it.isDigit() } &&
            password.any { !it.isLetterOrDigit() && !it.isWhitespace() }
}
