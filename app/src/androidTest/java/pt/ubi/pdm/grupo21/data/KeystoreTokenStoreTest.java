package pt.ubi.pdm.grupo21.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class KeystoreTokenStoreTest {

    private Context context;
    private KeystoreTokenStore store;

    @Before
    public void setUp() {
        context = ApplicationProvider.getApplicationContext();
        store = new KeystoreTokenStore(context);
        store.clear();
    }

    @Test
    public void tokenFicaCifradoEDecifraCorretamente() {
        String token = "token-secreto-123";
        store.save("aluno", token);

        SharedPreferences prefs =
                context.getSharedPreferences(KeystoreTokenStore.PREFS_NAME, Context.MODE_PRIVATE);
        String guardado = prefs.getString(KeystoreTokenStore.PREF_TOKEN_CIPHERTEXT, null);
        assertNotNull(guardado);
        assertFalse(guardado.contains(token));

        assertEquals(token, store.getToken());
    }

    @Test
    public void clear_apagaASessao() {
        store.save("aluno", "abc");
        store.clear();
        assertNull(store.getToken());
    }
}
