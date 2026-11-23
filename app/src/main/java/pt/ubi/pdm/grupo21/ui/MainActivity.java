package pt.ubi.pdm.grupo21.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import pt.ubi.pdm.grupo21.R;
import pt.ubi.pdm.grupo21.data.FakeAuthRepository;
import pt.ubi.pdm.grupo21.data.KeystoreTokenStore;
import pt.ubi.pdm.grupo21.databinding.ActivityMainBinding;

/**
 * A View: é "passiva". Só faz duas coisas:
 *   1. envia os eventos do utilizador para o ViewModel;
 *   2. desenha o ecrã a partir do estado que o ViewModel publica.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Grupo21Demo";

    private ActivityMainBinding binding;
    private LoginViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        LoginViewModelFactory factory = new LoginViewModelFactory(
                new FakeAuthRepository(),
                new KeystoreTokenStore(getApplicationContext()));
        viewModel = new ViewModelProvider(this, factory).get(LoginViewModel.class);

        // Eventos: UI -> ViewModel
        binding.buttonLogin.setOnClickListener(v -> viewModel.login(
                text(binding.editUsername),
                text(binding.editPassword)));
        binding.buttonLogout.setOnClickListener(v -> viewModel.logout());

        // Estado: ViewModel -> UI
        viewModel.getUiState().observe(this, this::render);
    }

    private void render(LoginUiState state) {
        Log.d(TAG, "Novo estado: " + state.status); // visível no Logcat

        boolean loggedIn = state.status == LoginUiState.Status.LOGGED_IN;
        binding.layoutLogin.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        binding.layoutSession.setVisibility(loggedIn ? View.VISIBLE : View.GONE);

        if (loggedIn) {
            binding.textWelcome.setText(getString(R.string.welcome, state.username));
            binding.editPassword.setText(""); // não deixar a palavra-passe no ecrã
        }

        if (state.status == LoginUiState.Status.ERROR) {
            binding.textError.setText(state.errorMessageRes); // texto vem do strings.xml
            binding.textError.setVisibility(View.VISIBLE);
        } else {
            binding.textError.setVisibility(View.GONE);
        }
    }

    private static String text(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }
}
