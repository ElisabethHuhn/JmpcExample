package com.huhn.jmpcexample

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.huhn.jmpcexample.ui.WeatherRoute
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

//use the AndroidJUnit4 test runner.
@RunWith(AndroidJUnit4::class)
class JmpcComposeUITests {


    //initialize the Compose UI framework
    //build the UI test hierarchy with the ComposeTestRule class.
    // The rule sets up the necessary environment for the Compose UI tests
    // and provides various utility functions.
    @get:Rule
    val composeTestRule = createComposeRule()


    @Test
    fun testUIFieldCitySuccess() {
        // Set up the test environment

        // Render the Compose UI
        composeTestRule.setContent {
            // Call the entry point composable function
            WeatherRoute(
                screenTitle = R.string.weather_test_title
            )
        }
        composeTestRule.waitForIdle()

        val testCity = "Atlanta"
        composeTestRule
            .onNodeWithContentDescription("city field")
            .performTextReplacement(testCity)

        // Interact with the UI and trigger user actions
        composeTestRule.onNodeWithText("Get Weather by City").performClick()

        //assert input is correct
        composeTestRule.onNodeWithText(testCity).assertIsDisplayed()

        composeTestRule.waitForIdle()

        //assert no errors in API call
        composeTestRule.onNodeWithText("Error").assertDoesNotExist()

        composeTestRule
            .onNodeWithTag("description_field")
            .assertExists("Description field exists")
    }


    @Test
    fun testUIFieldCityFail() {
        // Set up the test environment

        // Render the Compose UI
        composeTestRule.setContent {
            // Call the entry point composable function
            WeatherRoute(
                screenTitle = R.string.weather_test_title
            )
        }
        val testCity = "Bos"
        composeTestRule
            .onNodeWithContentDescription("city field")
            .performTextReplacement(testCity)

        // Interact with the UI and trigger user actions
        composeTestRule.onNodeWithText("Get Weather by City").performClick()

        //assert input is correct
        composeTestRule.onNodeWithText(testCity).assertIsDisplayed()

        //assert errors in API call
        composeTestRule
            .onNodeWithTag("error_field")
            .assertExists("Error field exists")
    }


}
