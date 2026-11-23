package pt.ubi.pdm.grupo21.ui;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import pt.ubi.pdm.grupo21.data.AuthRepository;
import pt.ubi.pdm.grupo21.data.TokenStore;

public class LoginViewModelFactory implements ViewModelProvider.Factory {

    private final AuthRepository authRepository;
    private final TokenStore tokenStore;

    public LoginViewModelFactory(AuthRepository authRepository, TokenStore tokenStore) {
        this.authRepository = authRepository;
        this.tokenStore = tokenStore;
    }

    @NonNull
    @Override
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass.isAssignableFrom(LoginViewModel.class)) {
            return (T) new LoginViewModel(authRepository, tokenStore);
        }
        throw new IllegalArgumentException("ViewModel desconhecido: " + modelClass.getName());
    }
}
