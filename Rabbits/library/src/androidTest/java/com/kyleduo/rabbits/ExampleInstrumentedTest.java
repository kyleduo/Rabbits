package com.kyleduo.rabbits;

import android.content.Context;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;
import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
	@Test
	public void useAppContext() throws Exception {
		// Context of the app under test.
		Context appContext = InstrumentationRegistry.getTargetContext();

		assertEquals("com.kyleduo.rabbits.test", appContext.getPackageName());
	}

	@Test
	public void testMappings() {
		String url = "demo://kyleduo.com/rabbits/test/loading";
		Target match = Mappings.match(Uri.parse(url));
		assert match != null;
		Log.d("mapping", match.toString());
		assert match.getExtras().getString("Testing").equals("loading");
	}
}
