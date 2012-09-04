package com.coffeeandpower.activity;

import android.test.ActivityInstrumentationTestCase2;
import android.test.UiThreadTest;
import android.widget.ToggleButton;

import com.coffeeandpower.R;


/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class com.coffeeandpower.activity.ActivityCheckInTest \
 * com.coffeeandpower.tests/android.test.InstrumentationTestRunner
 */
public class ActivityCheckInTest extends ActivityInstrumentationTestCase2<ActivityCheckIn> {

    public ActivityCheckInTest() {
        super("com.coffeeandpower.activity", ActivityCheckIn.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testConstruction() {
        setName("Construction");

        final ActivityCheckIn instance = getActivity();
        assertNotNull(instance);
    }

    @UiThreadTest
    public void testCheckInSetAuto() {
        setName("CheckInSetAuto");

        final ActivityCheckIn instance = getActivity();

        final ToggleButton toggleAutoCheckin = (ToggleButton) instance.findViewById(R.id.toggleButtonMasterAutoCheckinToggle);
        assertNotNull(toggleAutoCheckin);

        final boolean checked = toggleAutoCheckin.isChecked();

        toggleAutoCheckin.setChecked(!checked);
        assertEquals(!checked, toggleAutoCheckin.isChecked());

        toggleAutoCheckin.setChecked(checked);
        assertEquals(checked, toggleAutoCheckin.isChecked());
    }

    protected void tearDown() throws Exception {

        super.tearDown();
    }
}
