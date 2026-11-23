package pt.ubi.pdm.grupo21.ui;

import androidx.annotation.StringRes;

/**
 * Estado completo do ecrã num único objeto imutável.
 * A interface desenha-se SEMPRE a partir deste estado (fluxo unidirecional de dados).
 */
public final class LoginUiState {

    public enum Status { LOGGED_OUT, LOGGED_IN, ERROR }

    /** Valor de errorMessageRes quando não há erro. */
    public static final int NO_ERROR = 0;

    public final Status status;
    public final String username;     // preenchido quando LOGGED_IN

    /**
     * Mensagem de erro como id de recurso (R.string...), preenchido quando ERROR.
     * O ViewModel não tem Context, por isso não pode chamar getString():
     * guarda só o id e é a Activity que obtém o texto do strings.xml.
     */
    @StringRes
    public final int errorMessageRes;

    private LoginUiState(Status status, String username, @StringRes int errorMessageRes) {
        this.status = status;
        this.username = username;
        this.errorMessageRes = errorMessageRes;
    }

    public static LoginUiState loggedOut() {
        return new LoginUiState(Status.LOGGED_OUT, null, NO_ERROR);
    }

    public static LoginUiState loggedIn(String username) {
        return new LoginUiState(Status.LOGGED_IN, username, NO_ERROR);
    }

    public static LoginUiState error(@StringRes int messageRes) {
        return new LoginUiState(Status.ERROR, null, messageRes);
    }
}
