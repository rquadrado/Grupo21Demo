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

        binding.buttonLogin.setOnClickListener(v -> viewModel.login(
                text(binding.editUsername),
                text(binding.editPassword)));
        binding.buttonLogout.setOnClickListener(v -> viewModel.logout());

        viewModel.getUiState().observe(this, this::render);
    }

    private void render(LoginUiState state) {
        Log.d(TAG, "Novo estado: " + state.status);

        boolean loggedIn = state.status == LoginUiState.Status.LOGGED_IN;
        binding.layoutLogin.setVisibility(loggedIn ? View.GONE : View.VISIBLE);
        binding.layoutSession.setVisibility(loggedIn ? View.VISIBLE : View.GONE);

        if (loggedIn) {
            binding.textWelcome.setText(getString(R.string.welcome, state.username));
            binding.editPassword.setText("");
        }

        if (state.status == LoginUiState.Status.ERROR) {
            binding.textError.setText(state.errorMessageRes);
            binding.textError.setVisibility(View.VISIBLE);
        } else {
            binding.textError.setVisibility(View.GONE);
        }
    }

    private static String text(EditText editText) {
        return editText.getText() == null ? "" : editText.getText().toString();
    }
}
