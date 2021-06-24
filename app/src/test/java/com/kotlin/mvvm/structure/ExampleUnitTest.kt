package com.kotlin.mvvm.structure

import android.app.Application
import com.kotlin.mvvm.structure.injection.component.AppComponent
import org.hamcrest.CoreMatchers.`is`
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.Mock
import org.mockito.MockitoAnnotations

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */

class ExampleUnitTest {
//    machineuser2@vyzeo.com
//    Vyzeo1!2@3#
    @Mock
    lateinit var application: Application
    private lateinit var appUtils:AppUtils
    private lateinit var activityLogin:ActivityLogin
    private lateinit var loginViewModel: LoginViewModel
    private var appComponent: AppComponent? = null


    @Before
    fun setUp(){
        MockitoAnnotations.initMocks(this)
        appUtils = AppUtils(application)
        activityLogin = ActivityLogin()
        loginViewModel = LoginViewModel()

    }

    @Test
    fun addition_isCorrect() {
        assertEquals(4, 2 + 2)
    }

    @Test
    fun testIsEmailValid() {

        val testEmail = "machineuser2@vyzeo.com"
        Assert.assertThat(
            String.format("Email Validity Test failed for %s ", testEmail),
            appUtils.isEmailValid(testEmail),
            `is`(true)
        )
    }

//    @Test
//    fun testEmailValid() {
//        val testEmail = "machineuser2@vyzeo.com"
//        assertThat(
//            String.format(
//                "Email Validity Test failed for %s ",
//                testEmail
//            ), Utils.checkEmailForValidity(testEmail), `is`(true)
//        )
//    }

    @Test
    fun emailValidator_CorrectEmailSimple_ReturnsTrue() {
        assertTrue(activityLogin.isValid())
    }

    @Test
    fun emailValidator_InvalidEmailNoTld_ReturnsFalse() {
        assertFalse(appUtils.isEmailValid("machineuser2@vyzeo"))
    }

    @Test
    fun emailValidator_InvalidEmailDoubleDot_ReturnsFalse() {
        assertFalse(appUtils.isEmailValid("machineuser2@vyzeo..com"))
    }

    @Test
    fun emailValidator_InvalidEmailNoUsername_ReturnsFalse() {
        assertFalse(appUtils.isEmailValid("@vyzeo.com"))
    }

    @Test
    fun emailValidator_EmptyString_ReturnsFalse() {
        assertFalse(appUtils.isEmailValid(""))
    }
}
