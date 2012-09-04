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
 * -e class com.coffeeandpower.activity.ActivityAutoCheckinListTest \
 * com.coffeeandpower.tests/android.test.InstrumentationTestRunner
 */
public class ActivityAutoCheckinListTest extends ActivityInstrumentationTestCase2<ActivityAutoCheckinList> {

    public ActivityAutoCheckinListTest() {
        super("com.coffeeandpower.activity", ActivityAutoCheckinList.class);
    }

    protected void setUp() throws Exception {
        super.setUp();

    }

    public void testConstruction() {
        setName("Construction");

        final ActivityAutoCheckinList instance = getActivity();
        assertNotNull(instance);
    }

    @UiThreadTest
    public void testToggleAutoCheckin() {
        setName("ToggleAutoCheckin");

        final ActivityAutoCheckinList instance = getActivity();

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
