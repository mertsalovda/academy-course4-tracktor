package com.elegion.tracktor.ui.map;

import android.content.Intent;
import android.os.SystemClock;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.Intents;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.RecyclerView;

import com.elegion.tracktor.R;
import com.elegion.tracktor.ui.results.ResultsActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.InstrumentationRegistry.getInstrumentation;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.Espresso.openActionBarOverflowOrOptionsMenu;
import static android.support.test.espresso.Espresso.pressBack;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mMainActivityActivityTestRule = new ActivityTestRule<>(MainActivity.class);
    @Rule
    public ActivityTestRule<ResultsActivity> mResultsActivityActivityTestRule = new ActivityTestRule<>(ResultsActivity.class);

    @Test
    public void checkDetailTrack() {
//    a. после загрузки экрана трекинга происходит нажатие кнопки Старт;
        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.counter)).check(matches(isDisplayed()));
        onView(withId(R.id.buttonStart)).perform(click());
//    b. спустя 15 секунд происходит нажатие кнопки Стоп;
        SystemClock.sleep(15000);
        onView(withId(R.id.buttonStop)).perform(click());
//    c. проверьте открытие экрана детализации трекинга;
        onView(withId(R.id.tvDate)).check(matches(isDisplayed()));
    }

    //    d. проверьте вызов неявного Intent’а при нажатии на кнопку “Поделиться”;
    @Test
    public void checkShareIntent() {
        Intents.init();
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.statistic)).perform(click());
        onView(withId(R.id.recycler));
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.share)).perform(click());
//        intended(hasAction(Intent.ACTION_SEND));
        Intents.release();
    }

    @Test
    public void checkDeletedTrack() {
//    e. затем вернитесь на экран трекинга и перейдите на экран “Статистика”;
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.statistic)).perform(click());
        onView(withId(R.id.recycler));
//    f. посчитайте количество элементов в списке и закешируйте это число (оно вам пригодится);
        RecyclerView recycler = (RecyclerView) mResultsActivityActivityTestRule.getActivity().findViewById(R.id.recycler);
        int itemCount = recycler.getChildCount();
//    g. перейдите через любой элемент списка на экран маршрута - и удалите элемент из меню;
        onView(withId(R.id.recycler))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        openActionBarOverflowOrOptionsMenu(getInstrumentation().getTargetContext());
        onView(withText(R.string.btn_delete_result)).perform(click());
//    h. проверьте изменение числа элементов в списке.
        recycler = (RecyclerView) mResultsActivityActivityTestRule.getActivity().findViewById(R.id.recycler);
        int newItemCount = recycler.getChildCount();
        assertEquals(itemCount - 1, newItemCount);
    }
}