package com.coffeeandpower.activity;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.coffeeandpower.activity.ActivityLoginPageTest \
 * com.coffeeandpower.tests/android.test.InstrumentationTestRunner
 */
public class ActivityLoginPageTest extends ActivityInstrumentationTestCase2<ActivityLoginPage> {

    public ActivityLoginPageTest() {
        super("com.coffeeandpower.activity", ActivityLoginPage.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testConstruction() {
        setName("Construction");

        final ActivityLoginPage instance = getActivity();
        assertNotNull(instance);
    }

    protected void tearDown() throws Exception {

        super.tearDown();
    }
}
