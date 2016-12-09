package com.kyleduo.rabbits.demo;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import com.kyleduo.rabbits.Rabbit;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class ExampleInstrumentedTest {
	@Test
	public void useAppContext() throws Exception {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getTargetContext();

		assertEquals("com.kyleduo.rabbits.demo", appContext.getPackageName());
	}

	@Rule
	public ActivityTestRule<MainActivity> mMainActivityRule = new ActivityTestRule<>(MainActivity.class, true);

	@Test
	public void testGoTest() throws Exception {
		onView(withId(R.id.start_test_bt)).perform(click());
		onView(withId(R.id.back_home_bt)).check(matches(isDisplayed()));

		onView(withId(R.id.back_home_bt)).perform(click());
		onView(withId(R.id.start_test_bt)).check(matches(isDisplayed()));
	}

	@Test
	public void testRabbitsParams() {
		Rabbit.from(mMainActivityRule.getActivity())
				.to("demo://kyleduo.com/rabbits/test/testing")
				.start();
		onView(withId(R.id.params_tv)).check(matches(withText("testing")));
	}

	@Test
	public void testUriParse() {
		String url = "test/test2";
		Uri uri = Uri.parse(url);
		Log.d("uri", "scheme: " + uri.getScheme());
		Log.d("uri", "host: " + uri.getHost());
		Log.d("uri", "path: " + uri.getPath());

		Uri.Builder builder = uri.buildUpon();
		if (uri.getScheme() == null) {
			builder.scheme("demo");
		}
		if (uri.getHost() == null) {
			builder.authority("kyleduo.com");
		}

		uri = builder.build();
		Log.i("uri", "uri: " + uri.toString());
		Log.i("uri", "scheme: " + uri.getScheme());
		Log.i("uri", "host: " + uri.getHost());
		Log.i("uri", "path: " + uri.getPath());
	}
}
