package io.happo.gradle

import java.io.File
import kotlin.test.Test
import kotlin.test.assertEquals

class FilenameHelperTest {

    private val filenameHelper = FilenameHelper()

    @Test
    fun `should extract component and variant from new Roborazzi format`() {
        val file = File("com.example.helloworldapp.MainActivityTest.testMainActivity.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("com.example.helloworldapp.MainActivityTest", result.first)
        assertEquals("testMainActivity", result.second)
    }

    @Test
    fun `should extract component and variant from new Roborazzi format with spaces in test method`() {
        val file = File("com.example.app.ui.LoginScreenTest.users can log in.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("com.example.app.ui.LoginScreenTest", result.first)
        assertEquals("users can log in", result.second)
    }

    @Test
    fun `should extract component and variant from legacy Roborazzi format with test file extension`() {
        val file = File("src__components__Button.test.tsx__renders-primary-button.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("src__components__Button", result.first)
        assertEquals("renders-primary-button", result.second)
    }

    @Test
    fun `should extract component and variant from legacy Roborazzi format with complex path`() {
        val file =
                File(
                        "tests__integration__checkout__PaymentForm.test.tsx__should-display-error-message.png"
                )
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("tests__integration__checkout__PaymentForm", result.first)
        assertEquals("should-display-error-message", result.second)
    }

    @Test
    fun `should extract component and variant from legacy Roborazzi format without test extension`() {
        val file = File("Button.test.tsx__primary.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("Button", result.first)
        assertEquals("primary", result.second)
    }

    @Test
    fun `should extract component and variant from legacy Roborazzi format with simple fallback`() {
        val file = File("Component__variant.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("Component", result.first)
        assertEquals("variant", result.second)
    }

    @Test
    fun `should extract component and variant from standard format`() {
        val file = File("Button_primary.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("Button", result.first)
        assertEquals("primary", result.second)
    }

    @Test
    fun `should extract component and variant from standard format with target`() {
        val file = File("Button_primary_mobile.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("Button", result.first)
        assertEquals("primary", result.second)
    }

    @Test
    fun `should extract component and variant from standard format with multiple underscores`() {
        val file = File("My_Component_variant_target.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("My", result.first)
        assertEquals("Component", result.second)
    }

    @Test
    fun `should handle filename without extension`() {
        val file = File("Button_primary")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("Button", result.first)
        assertEquals("primary", result.second)
    }

    @Test
    fun `should handle filename with only component`() {
        val file = File("Button.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("Button", result.first)
        assertEquals("default", result.second)
    }

    @Test
    fun `should prioritize Roborazzi format over legacy format`() {
        // This filename could match both patterns, but Roborazzi should take precedence
        val file = File("com.example.ClassUnderTest.testMethod.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("com.example.ClassUnderTest", result.first)
        assertEquals("testMethod", result.second)
    }

    @Test
    fun `should prioritize legacy format over standard format`() {
        // This filename could match both patterns, but legacy should take precedence
        val file = File("Component__variant.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("Component", result.first)
        assertEquals("variant", result.second)
    }

    @Test
    fun `should handle Roborazzi format with special characters in test method`() {
        val file = File("com.example.FooClassTest.test-method_with~special@chars.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("com.example.FooClassTest", result.first)
        assertEquals("test-method_with~special@chars", result.second)
    }

    @Test
    fun `should handle legacy Roborazzi format with different file extensions`() {
        val file = File("Component.test.js__variant.png")
        val result = filenameHelper.extractComponentAndVariant(file)

        assertEquals("Component", result.first)
        assertEquals("variant", result.second)
    }
}
