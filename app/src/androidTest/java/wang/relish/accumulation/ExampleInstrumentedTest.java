package wang.relish.accumulation;

import android.content.Context;
import android.media.MediaMetadataRetriever;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Locale;

import static org.hamcrest.core.IsEqual.equalTo;
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

        assertEquals("cn.studyjams.s1.contest.accumulation", appContext.getPackageName());
    }



    @Test
    public void test(){
        A a = new A();
        A b = new A();
        MediaMetadataRetriever s;
        Assert.assertEquals(a,equalTo(b));
    }

    class A{

    }
}
