package com.dicoding.storyapp.ui.login

import androidx.test.espresso.Espresso
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.assertion.ViewAssertions
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import com.dicoding.storyapp.R
import com.dicoding.storyapp.utils.EspressoIdlingResource
import org.junit.Rule

@RunWith(AndroidJUnit4::class)
class LoginActivityTest {

    @get:Rule
    val activity = ActivityScenarioRule(LoginActivity::class.java)

    @Before
    fun setup() {
        IdlingRegistry.getInstance().register(EspressoIdlingResource.countingIdlingResource)
    }

    @After
    fun tearDown() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.countingIdlingResource)
    }

    @Test
    fun testLogin() {

        Espresso.onView(ViewMatchers.withId(R.id.ed_login_email)).perform(ViewActions.typeText("landak@gmail.com"))
        Espresso.onView(ViewMatchers.withId(R.id.ed_login_password)).perform(ViewActions.typeText("landakmimi"))
        Espresso.onView(ViewMatchers.withId(R.id.loginButton)).perform(ViewActions.click())

        Espresso.onView(ViewMatchers.withId(R.id.progressBar)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))

        EspressoIdlingResource.increment()

        Thread.sleep(2000)

        EspressoIdlingResource.decrement()

        Espresso.onView(ViewMatchers.withId(R.id.progressBar)).check(ViewAssertions.matches(ViewMatchers.withEffectiveVisibility(ViewMatchers.Visibility.GONE)))

        Espresso.onView(ViewMatchers.withId(R.id.activity_main)).check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }

}