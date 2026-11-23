package pt.ubi.pdm.grupo21.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pt.ubi.pdm.grupo21.R;
import pt.ubi.pdm.grupo21.data.AuthRepository;
import pt.ubi.pdm.grupo21.data.TokenStore;

/**
 * Detém o estado do ecrã de login.
 * - Sobrevive à rotação do ecrã (a Activity é recriada, o ViewModel não).
 * - Não tem referências a Activity, Context ou View (evita fugas de memória).
 * - Não depende da framework Android para a lógica, por isso é testável na JVM.
 */
public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final TokenStore tokenStore;

    // Privado e mutável dentro do ViewModel; exposto só para leitura à View
    private final MutableLiveData<LoginUiState> uiState = new MutableLiveData<>();

    public LoginViewModel(AuthRepository authRepository, TokenStore tokenStore) {
        this.authRepository = authRepository;
        this.tokenStore = tokenStore;

        // Se já existe uma sessão guardada (cifrada), entra diretamente
        if (tokenStore.getToken() != null) {
            uiState.setValue(LoginUiState.loggedIn(tokenStore.getUsername()));
        } else {
            uiState.setValue(LoginUiState.loggedOut());
        }
    }

    public LiveData<LoginUiState> getUiState() {
        return uiState;
    }

    /** Evento vindo da interface: o utilizador carregou em "Entrar". */
    public void login(String username, String password) {
        if (isBlank(username) || isBlank(password)) {
            uiState.setValue(LoginUiState.error(R.string.error_empty_fields));
            return;
        }

        String token = authRepository.login(username.trim(), password);
        if (token == null) {
            uiState.setValue(LoginUiState.error(R.string.error_invalid_credentials));
            return;
        }

        tokenStore.save(username.trim(), token); // guardado cifrado
        uiState.setValue(LoginUiState.loggedIn(username.trim()));
    }

    /** Evento vindo da interface: o utilizador carregou em "Terminar sessão". */
    public void logout() {
        tokenStore.clear();
        uiState.setValue(LoginUiState.loggedOut());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
