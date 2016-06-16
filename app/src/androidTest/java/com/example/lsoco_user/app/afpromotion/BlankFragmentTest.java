package com.example.lsoco_user.app.afpromotion;


import android.support.test.espresso.Espresso;
import android.support.test.espresso.ViewAction;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.View;

import com.example.lsoco_user.app.afpromotion.activity.MainActivity;
import com.example.lsoco_user.app.afpromotion.fragment.BlankFragment;
import com.example.lsoco_user.app.afpromotion.util.CacheUtil;
import com.example.lsoco_user.app.afpromotion.util.ConnectionUtil;

import org.hamcrest.Matchers;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * BlankFragment Hierarchy Test
 */
@RunWith(AndroidJUnit4.class)
public class BlankFragmentTest {

    @Rule
    public ActivityTestRule<MainActivity> activityTestRule = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void resetToFirstTimeConditions() {
        // if network connection, disconnect
        if(BuildConfig.DEBUG && ConnectionUtil.isConnected(activityTestRule.getActivity())) {
            // todo
//            ConnectionUtil.disconnect();
        }

        // if cache, delete cache
        if(BuildConfig.DEBUG && CacheUtil.wasJsonCached(activityTestRule.getActivity())) {
            // todo
//            CacheUtil.resetCache();
        }
    }

    @Test
    public void testDisplayed() {
        Espresso.onView(ViewMatchers.withId(R.id.frag_blank_ll))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @Test
    public void testIsClickable() {
        Espresso.onView(ViewMatchers.withId(R.id.frag_blank_ll))
                .check(ViewAssertions.matches(ViewMatchers.isClickable()));
    }

    @Test
    public void testDisplaysMessage() {
       Espresso.onView(ViewMatchers.withId(R.id.frag_blank_tv_msg))
               .check(ViewAssertions.matches(ViewMatchers.withText(R.string.you_are_offline)));
    }

    @Test
    public void testDisplaysInstructions() {
        Espresso.onView(ViewMatchers.withId(R.id.frag_blank_tv_instr))
                .check(ViewAssertions.matches(ViewMatchers.withText(R.string.offline_instructions)));
    }


    @Test
    public void testClickOffline() {
        Espresso.onView(ViewMatchers.withId(R.id.frag_blank_ll))
                .perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.recyclerview_promo))
                .check(ViewAssertions.doesNotExist());
    }

    @Test
    public void testClickOnline() {
        // connect
        // // TODO: 6/16/2016
        // put BlankFragment back
        activityTestRule.getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.main_frag_holder, new BlankFragment())
                .commit();
        // click on it
        Espresso.onView(ViewMatchers.withId(R.id.frag_blank_ll))
                .perform(ViewActions.click());
        // check
        Espresso.onView(ViewMatchers.withId(R.id.recyclerview_promo))
                .check(ViewAssertions.matches(ViewMatchers.isDisplayed()));
    }

    @After
    public void resetToConnectedAndWithCache() {
        // todo
        final String json = "...";
        // reconnect to network
        // todo
//        ConnectionUtil.connect();
        // write the manually cache
//        CacheUtil.saveJsonToCache(activityTestRule.getActivity(), json);
//        CacheUtil.markJsonWasCached(activityTestRule.getActivity());
    }
}