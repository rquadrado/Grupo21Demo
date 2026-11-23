package pt.ubi.pdm.grupo21.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import pt.ubi.pdm.grupo21.R;
import pt.ubi.pdm.grupo21.data.AuthRepository;
import pt.ubi.pdm.grupo21.data.TokenStore;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;
    private final TokenStore tokenStore;

    private final MutableLiveData<LoginUiState> uiState = new MutableLiveData<>();

    public LoginViewModel(AuthRepository authRepository, TokenStore tokenStore) {
        this.authRepository = authRepository;
        this.tokenStore = tokenStore;

        // sessão guardada: entra diretamente
        if (tokenStore.getToken() != null) {
            uiState.setValue(LoginUiState.loggedIn(tokenStore.getUsername()));
        } else {
            uiState.setValue(LoginUiState.loggedOut());
        }
    }

    public LiveData<LoginUiState> getUiState() {
        return uiState;
    }

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

        tokenStore.save(username.trim(), token);
        uiState.setValue(LoginUiState.loggedIn(username.trim()));
    }

    public void logout() {
        tokenStore.clear();
        uiState.setValue(LoginUiState.loggedOut());
    }

    private static boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
