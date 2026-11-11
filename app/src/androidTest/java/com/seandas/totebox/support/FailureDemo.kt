package com.seandas.totebox.support

/**
 * Marks tests that fail on purpose, to show what the report looks like when something goes
 * wrong. Normal runs skip them; run them with:
 *
 *     ./gradlew connectedDebugAndroidTest -PfailureDemo
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
annotation class FailureDemo
