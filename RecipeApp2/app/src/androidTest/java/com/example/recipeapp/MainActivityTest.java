package com.example.recipeapp;

import android.content.Intent;
import android.view.View;
import android.widget.TextView;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.ViewAssertion;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.google.android.material.slider.Slider;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.FixMethodOrder;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.action.ViewActions.*;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;

import static androidx.test.espresso.matcher.ViewMatchers.*;
import static org.hamcrest.Matchers.*;
import static androidx.test.espresso.intent.Intents.init;
import static androidx.test.espresso.intent.Intents.release;

import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.contrib.RecyclerViewActions;
import static androidx.test.espresso.matcher.RootMatchers.isPlatformPopup;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    //Setup and Matchers ========================================================================================================

    @Rule
    public ActivityScenarioRule<MainActivity> activityRule =
            new ActivityScenarioRule<>(MainActivity.class);

    // Slider Action
    public static ViewAction setSliderValue(final float value) {
        return new ViewAction() {
            @Override
            public Matcher<View> getConstraints() {
                return isAssignableFrom(Slider.class);
            }

            @Override
            public String getDescription() {
                return "Set slider value";
            }

            @Override
            public void perform(androidx.test.espresso.UiController uiController, View view) {
                ((Slider) view).setValue(value);
            }
        };
    }

    // Slider Value Matcher
    public static Matcher<View> withSliderValue(final float expected) {
        return new TypeSafeMatcher<View>() {

            @Override
            public void describeTo(Description description) {
                description.appendText("Slider value should be " + expected);
            }

            @Override
            protected boolean matchesSafely(View view) {
                if (!(view instanceof Slider)) return false;

                float actual = ((Slider) view).getValue();
                return Math.abs(actual - expected) < 0.01f;
            }
        };
    }

    // Assert that all items contain text
    public static class RecyclerViewItemTextAssertion {

        public static ViewAssertion allItemsContainText(final int textViewId, final String expected) {
            return (view, noViewFoundException) -> {

                if (noViewFoundException != null) {
                    throw noViewFoundException;
                }

                RecyclerView recyclerView = (RecyclerView) view;
                RecyclerView.Adapter adapter = recyclerView.getAdapter();

                if (adapter == null) {
                    throw new AssertionError("Adapter is null");
                }

                for (int i = 0; i < adapter.getItemCount(); i++) {
                    RecyclerView.ViewHolder holder =
                            recyclerView.findViewHolderForAdapterPosition(i);

                    if (holder == null) continue; // not visible yet

                    TextView textView = holder.itemView.findViewById(textViewId);

                    if (textView == null) {
                        throw new AssertionError("TextView not found in item");
                    }

                    String text = textView.getText().toString().toLowerCase();

                    if (!text.contains(expected.toLowerCase())) {
                        throw new AssertionError(
                                "Item at position " + i + " does not contain: " + expected
                                        + " but was: " + text
                        );
                    }
                }
            };
        }
    }

    // Assert that all time values are less than or equal to
    public static ViewAssertion allItemsTimeLessOrEqual(final int textViewId, final int maxValue, final boolean isTime) {
        return (view, noViewFoundException) -> {

            if (noViewFoundException != null) {
                throw noViewFoundException;
            }

            RecyclerView recyclerView = (RecyclerView) view;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();

            if (adapter == null) {
                throw new AssertionError("Adapter is null");
            }

            for (int i = 0; i < adapter.getItemCount(); i++) {

                RecyclerView.ViewHolder holder =
                        recyclerView.findViewHolderForAdapterPosition(i);

                if (holder == null) continue;

                TextView timeView = holder.itemView.findViewById(textViewId);

                if (timeView == null) {
                    throw new AssertionError("Time TextView not found");
                }

                String str = timeView.getText().toString();

                if(isTime)
                    str = str.substring(2, str.length() - 1).trim();
                else
                    str = str.substring(2).trim();

                int value = Integer.parseInt(str);

                if (value > maxValue) {
                    throw new AssertionError(
                            "Item at position " + i +
                                    " has time " + value +
                                    " which is > " + maxValue
                    );
                }
            }
        };
    }

    // Assert that all items have the given Content Description
    public static ViewAssertion allItemsHave(String str, final int givenId) {
        return (view, noViewFoundException) -> {
            RecyclerView recyclerView = (RecyclerView) view;

            for (int i = 0; i < recyclerView.getChildCount(); i++) {
                View item = recyclerView.getChildAt(i);

                View img = item.findViewById(givenId);

                if (!str.contentEquals(img.getContentDescription())) {
                    throw new AssertionError("Missing / Incorrect image");
                }
            }
        };
    }

    //Tests ========================================================================================================

    //Test 01 - test if Recycler View is displayed
    @Test
    public void test01_testRecyclerViewDisplayed() {
        onView(withId(R.id.recyclerView))
                .check(matches(isDisplayed()));
    }

    //Test 02 - test if Search Input Updates Recipes
    @Test
    public void test02_testSearchInput_updatesRecipes() {
        onView(withId(R.id.searchInput))
                .perform(typeText("chicken"), closeSoftKeyboard());

        onView(withId(R.id.searchInput))
                .check(matches(withText("chicken")));

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        onView(withId(R.id.recyclerView))
                .check(RecyclerViewItemTextAssertion.allItemsContainText(
                        R.id.recipeName,   // <-- your recipe title TextView inside item
                        "chicken"
                ));
    }

    //Test 03 - test if Time Slider correctly filters recipes
    @Test
    public void test03_testTimeSliderChange() {
        onView(withId(R.id.filterButton)).perform(click());

        onView(withId(R.id.timeSlider))
                .perform(setSliderValue(60f));

        onView(withId(R.id.applyFilters)).perform(click());

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        onView(withId(R.id.recyclerView))
                .check(allItemsTimeLessOrEqual(R.id.time, 60, true));
    }

    //Test 04 - test if Ingredient Slider correctly filters recipes
    @Test
    public void test04_testIngredientSliderChange() {
        onView(withId(R.id.filterButton)).perform(click());

        onView(withId(R.id.ingredientSlider))
                .perform(setSliderValue(12f));

        onView(withId(R.id.applyFilters)).perform(click());

        try { Thread.sleep(1500); } catch (InterruptedException ignored) {}

        onView(withId(R.id.recyclerView))
                .check(allItemsTimeLessOrEqual(R.id.ingredientCount, 12, false));
    }

    //Test 05 - test if Country Spinner correctly filters recipes
    @Test
    public void test05_testCountrySelection() {
        onView(withId(R.id.filterButton)).perform(click());

        onView(withId(R.id.countrySpinner)).perform(click());

        onData(allOf(is(instanceOf(String.class)), is("American")))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.applyFilters)).perform(click());

        onView(withId(R.id.recyclerView))
                .check((allItemsHave("American", R.id.flag)));
    }

    //Test 06 - test if Category Spinner correctly filters recipes
    @Test
    public void test06_testCategorySelection() {
        onView(withId(R.id.filterButton)).perform(click());

        onView(withId(R.id.categorySpinner)).perform(click());

        onData(allOf(is(instanceOf(String.class)), is("Beef")))
                .inRoot(isPlatformPopup())
                .perform(click());

        onView(withId(R.id.applyFilters)).perform(click());

        onView(withId(R.id.recyclerView))
                .check((allItemsHave("Beef", R.id.category)));
    }

    //Test 07 - test if Language Spinner correctly changes localisation and if it is saved and loaded back when the app is reopened
    @Test
    public void test07_testLanguageChange() {
        ActivityScenario<MainActivity> scenario =
                ActivityScenario.launch(MainActivity.class);

        onView(withId(R.id.languageSpinner)).perform(click());

        onData(allOf(is(instanceOf(String.class)), is("Spanish")))
                .perform(click());

        onView(withId(R.id.titleText))
                .check(matches(withText("Aplicación de Recetas")));

        scenario.recreate();

        onView(withId(R.id.titleText))
                .check(matches(withText("Aplicación de Recetas")));

        onView(withId(R.id.languageSpinner)).perform(click());

        onData(allOf(is(instanceOf(String.class)), is("English")))
                .perform(click());

        onView(withId(R.id.titleText))
                .check(matches(withText("Recipe App")));
    }

    //Test 08 - test if Clear Filters Button correctly resets all the filters
    @Test
    public void test08_testClearFilters() {
        onView(withId(R.id.filterButton)).perform(click());

        onView(withId(R.id.clearFilters)).perform(click());

        onView(withId(R.id.filterButton)).perform(click());

        onView(withId(R.id.timeSlider))
                .check(matches(withSliderValue(180)));

        onView(withId(R.id.ingredientSlider))
                .check(matches(withSliderValue(30)));

        onView(withId(R.id.countrySpinner))
                .check(matches(withSpinnerText("Select a Country")));

        onView(withId(R.id.categorySpinner))
                .check(matches(withSpinnerText("Select a Category")));
    }

    //Test 09 - test if the Recipe is rendered correctly when it is opened and the video redirect is working
    @Test
    public void test09_testOpenRecipeDetail() {
        init();

        onView(withId(R.id.recyclerView))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //Toolbar title (recipe name)
        onView(withId(R.id.detailName))
                .check(matches(isDisplayed()))
                .check(matches(not(withText(""))));

        //Time
        onView(withId(R.id.detailTime))
                .check(matches(isDisplayed()))
                .check(matches(not(withText(""))));

        //Image
        onView(withId(R.id.detailImage))
                .check(matches(isDisplayed()));

        //Category icon
        onView(withId(R.id.detailCategory))
                .check(matches(isDisplayed()));

        //Flag
        onView(withId(R.id.detailFlag))
                .check(matches(isDisplayed()));

        //Instructions
        onView(withId(R.id.detailInstructions))
                .perform(scrollTo())
                .check(matches(isDisplayed()))
                .check(matches(not(withText(""))));

        //Ingredients list
        onView(withId(R.id.ingredientRecycler))
                .perform(scrollTo())
                .check(matches(isDisplayed()));

        //Video
        onView(withId(R.id.videoContainer))
                .perform(scrollTo())
                .check(matches(anyOf(
                        isDisplayed(),
                        withEffectiveVisibility(Visibility.VISIBLE)
                )));

        onView(withId(R.id.playButton)).perform(click());

        // Verify an intent was fired
        intended(hasAction(Intent.ACTION_VIEW));
        release();
    }

}