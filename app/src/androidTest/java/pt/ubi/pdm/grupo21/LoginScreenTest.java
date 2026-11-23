package pt.ubi.pdm.grupo21;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.content.Context;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import pt.ubi.pdm.grupo21.data.KeystoreTokenStore;
import pt.ubi.pdm.grupo21.ui.MainActivity;

/**
 * Testes de interface (Espresso): correm no emulador e simulam um utilizador real.
 * Executar: botão direito na pasta "androidTest" -> Run 'Tests in pt.ubi...'
 */
@RunWith(AndroidJUnit4.class)
public class LoginScreenTest {

    @Before
    public void limparSessao() {
        Context context = ApplicationProvider.getApplicationContext();
        new KeystoreTokenStore(context).clear();
    }

    @Test
    public void credenciaisErradas_mostraMensagemDeErro() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.editUsername)).perform(typeText("aluno"), closeSoftKeyboard());
            onView(withId(R.id.editPassword)).perform(typeText("errada"), closeSoftKeyboard());
            onView(withId(R.id.buttonLogin)).perform(click());

            onView(withId(R.id.textError))
                    .check(matches(withText(R.string.error_invalid_credentials)));
        }
    }

    @Test
    public void credenciaisCorretas_mostraEcraDeSessao() {
        try (ActivityScenario<MainActivity> ignored = ActivityScenario.launch(MainActivity.class)) {
            onView(withId(R.id.editUsername)).perform(typeText("aluno"), closeSoftKeyboard());
            onView(withId(R.id.editPassword)).perform(typeText("pdm2026"), closeSoftKeyboard());
            onView(withId(R.id.buttonLogin)).perform(click());

            onView(withId(R.id.textWelcome)).check(matches(withText("Olá, aluno!")));
            onView(withId(R.id.buttonLogout)).check(matches(isDisplayed()));
        }
    }
}
