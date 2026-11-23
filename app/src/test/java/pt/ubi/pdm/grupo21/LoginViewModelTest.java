package pt.ubi.pdm.grupo21;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import pt.ubi.pdm.grupo21.data.FakeAuthRepository;
import pt.ubi.pdm.grupo21.ui.LoginUiState;
import pt.ubi.pdm.grupo21.ui.LoginViewModel;

/**
 * Testes unitários: correm na JVM do computador, sem emulador, em milissegundos.
 * Executar: botão direito na pasta "test" -> Run 'Tests in pt.ubi...'
 */
public class LoginViewModelTest {

    // Faz o LiveData correr de forma síncrona durante os testes
    @Rule
    public InstantTaskExecutorRule instantTaskExecutorRule = new InstantTaskExecutorRule();

    private FakeTokenStore tokenStore;
    private LoginViewModel viewModel;

    @Before
    public void setUp() {
        tokenStore = new FakeTokenStore();
        viewModel = new LoginViewModel(new FakeAuthRepository(), tokenStore);
    }

    @Test
    public void login_comCamposVazios_mostraErro() {
        viewModel.login("", "");

        LoginUiState state = viewModel.getUiState().getValue();
        assertEquals(LoginUiState.Status.ERROR, state.status);
        assertEquals(R.string.error_empty_fields, state.errorMessageRes);
    }

    @Test
    public void login_comPalavraPasseErrada_mostraErroENaoGuardaToken() {
        viewModel.login("aluno", "errada");

        LoginUiState state = viewModel.getUiState().getValue();
        assertEquals(LoginUiState.Status.ERROR, state.status);
        assertEquals(R.string.error_invalid_credentials, state.errorMessageRes);
        assertNull(tokenStore.getToken());
    }

    @Test
    public void login_comCredenciaisCorretas_guardaTokenEEntra() {
        viewModel.login("aluno", "pdm2026");

        LoginUiState state = viewModel.getUiState().getValue();
        assertEquals(LoginUiState.Status.LOGGED_IN, state.status);
        assertEquals("aluno", state.username);
        assertNotNull(tokenStore.getToken());
    }

    @Test
    public void login_comEspacosNoNome_normalizaEEntra() {
        viewModel.login("  aluno  ", "pdm2026");

        LoginUiState state = viewModel.getUiState().getValue();
        assertEquals(LoginUiState.Status.LOGGED_IN, state.status);
        assertEquals("aluno", state.username);
        assertEquals("aluno", tokenStore.getUsername());
    }

    @Test
    public void logout_apagaTokenEVoltaAoLogin() {
        viewModel.login("aluno", "pdm2026");
        viewModel.logout();

        assertEquals(LoginUiState.Status.LOGGED_OUT, viewModel.getUiState().getValue().status);
        assertNull(tokenStore.getToken());
    }

    @Test
    public void novoViewModel_comSessaoGuardada_restauraSessao() {
        tokenStore.save("aluno", "token-existente");

        LoginViewModel novo = new LoginViewModel(new FakeAuthRepository(), tokenStore);

        assertEquals(LoginUiState.Status.LOGGED_IN, novo.getUiState().getValue().status);
    }
}
