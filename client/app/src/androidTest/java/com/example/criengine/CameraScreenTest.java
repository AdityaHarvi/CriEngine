package com.example.criengine;

import android.widget.EditText;

import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.rule.ActivityTestRule;

import com.example.criengine.Activities.CameraActivity;
import com.example.criengine.Activities.LoginActivity;
import com.example.criengine.Activities.MyBookActivity;
import com.example.criengine.Activities.RootActivity;
import com.robotium.solo.Solo;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static junit.framework.TestCase.assertTrue;


/**
 * Test class for the camera activity. All the UI tests are written here. Robotium test
 * framework is used.
 * Note: We cannot test the camera as it is considered to be a separate application. Robotium will
 * not extend onto it.
 * Note: We cannot test the delete button in this test as it is dependent on a image initially
 * saved into the db. Without being able to automate the camera, we cannot guarantee an image being
 * available and therefore unable to guarantee the existence of the delete button.
 */
public class CameraScreenTest {
    private Solo solo;

    @Rule
    public ActivityTestRule<LoginActivity> rule =
            new ActivityTestRule<>(LoginActivity.class, true, true);

    /**
     * Runs before all tests and creates solo instance.
     */
    @Before
    public void setUp() {
        solo = new Solo(InstrumentationRegistry.getInstrumentation(), rule.getActivity());
        TestUtilityMethods.login(solo, "intentTestingUser@email.com");
        TestUtilityMethods.addBook(solo);

        solo.clickInList(0);

        // Asserts that the current activity is the MyBookActivity.
        solo.assertCurrentActivity("Wrong Activity", MyBookActivity.class);

        solo.clickOnButton("Edit Book");

        solo.clickOnView(solo.getView("bookView_image"));

        // Asserts that the current activity is the Camera activity.
        solo.assertCurrentActivity("Wrong Activity", CameraActivity.class);
    }

    /**
     * Test to see if the save button works. Should return user to view the book details.
     */
    @Test
    public void clickOnSaveButtonTest() {
        solo.clickOnButton("Save");

        // Asserts that the current activity is the My Book activity.
        solo.assertCurrentActivity("Wrong Activity", MyBookActivity.class);
    }

    /**
     * Test to see if the user cannot edit an image while not in editing mode.
     */
    @Test
    public void clickImageWithoutBeingInEditTest() {
        solo.goBack();
        solo.clickOnView(solo.getView("bookView_image"));

        // Make sure that the user is still in the same activity.
        solo.assertCurrentActivity("Wrong Activity", MyBookActivity.class);
    }

    /**
     * Test to see if the confirmation dialog opens up in the right circumstances
     * (ie. when a change is made)
     * Also tests the button functionality for the dialong.
     */
    @Test
    public void checkConfirmationDialog() {
        solo.goBack();
        solo.clickOnButton("Edit Book");

        // Enter a new title.
        solo.clearEditText((EditText) solo.getView(R.id.bookView_title));
        solo.enterText((EditText) solo.getView(R.id.bookView_title), "This is a better title.");

        // Test if discarding the changes reverts to the old title.
        solo.clickOnView(solo.getView("bookView_image"));
        solo.clickOnText("DISCARD & GO");

        solo.assertCurrentActivity("Wrong Activity", CameraActivity.class);

        solo.goBack();

        solo.assertCurrentActivity("Wrong Activity", MyBookActivity.class);
        assertTrue(solo.waitForText("Mock Title", 1, 2000));

        // Modify the title again but this time we will save the changes with the dialog.
        solo.clickOnButton("Edit Book");

        solo.clearEditText((EditText) solo.getView(R.id.bookView_title));
        solo.enterText((EditText) solo.getView(R.id.bookView_title), "This is a better title.");

        solo.clickOnView(solo.getView("bookView_image"));
        solo.clickOnText("SAVE & GO");
        solo.assertCurrentActivity("Wrong Activity", CameraActivity.class);

        // Make sure the new title is saved.
        solo.goBack();
        solo.assertCurrentActivity("Wrong Activity", MyBookActivity.class);
        assertTrue(solo.waitForText("This is a better title.", 1, 2000));
    }

    /**
     * Closes the activity after each test
     */
    @After
    public void tearDown() {
        TestUtilityMethods.cleanup(solo);
        solo.finishOpenedActivities();
    }
}
