package com.happo.gradle

import java.io.File

class FilenameHelper {
    fun extractComponentAndVariant(file: File): Pair<String, String> {
        val nameWithoutExt = file.nameWithoutExtension
        // Check if this is a Roborazzi format:
        // com.example.helloworldapp.MainActivityTest.testMainActivity.png
        // com.example.helloworldapp.MainActivityTest.the whole flow should work.png
        val roborazziPattern = Regex("^(.+Test)\\.(.+)$")
        val roborazziMatch = roborazziPattern.find(nameWithoutExt)

        if (roborazziMatch != null) {
            val fullTestClass = roborazziMatch.groupValues[1]
            val testMethod = roborazziMatch.groupValues[2]

            // Use the full test class (including package) as component and test method as variant
            val component = fullTestClass
            val variant = testMethod

            return Pair(component, variant)
        }

        // Check if this is the legacy Roborazzi format: path__test__variant.png
        if (nameWithoutExt.contains("__")) {
            // For legacy Roborazzi format, extract component by removing .test.tsx__variant part
            val testPattern = Regex("(.+)\\.test\\.[a-z]{2,3}__(.+)$")
            val matchResult = testPattern.find(nameWithoutExt)

            if (matchResult != null) {
                val component = matchResult.groupValues[1]
                val variant = matchResult.groupValues[2]
                return Pair(component, variant)
            } else {
                // Fallback to simple split on last __
                val lastDoubleUnderscoreIndex = nameWithoutExt.lastIndexOf("__")
                val component = nameWithoutExt.substring(0, lastDoubleUnderscoreIndex)
                val variant = nameWithoutExt.substring(lastDoubleUnderscoreIndex + 2)
                return Pair(component, variant)
            }
        }

        // Standard format: component_variant.png or component_variant_target.png
        val parts = nameWithoutExt.split("_", limit = 3)
        val component = parts.getOrNull(0) ?: "unknown"
        val variant = parts.getOrNull(1) ?: "default"
        return Pair(component, variant)
    }
}
