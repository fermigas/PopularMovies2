package com.example.android.popularmovies.app;

import com.example.android.popularmovies.app.BuildConfig;
import com.example.android.popularmovies.app.MainActivity;

import org.junit.Before;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 23)
public class TestMainActivity {
    private MainActivity mainActivity;

    @Before public void setUp() throws Exception {
        mainActivity = Robolectric.setupActivity(MainActivity.class);
    }

//    @Test public void shouldInjectSomething() throws Exception {
//        TextView greeting = (TextView) mainActivity.findViewById(R.id.greeting);
//        assertEquals("Fake greeting", greeting.getText());
//    }
//
//    @Test public void shouldInjectSomething() throws Exception {
//        TextView number = (TextView) mainActivity.findViewById(R.id.number);
//        assertEquals("The magic number is 4.", number.getText());
//    }
}
