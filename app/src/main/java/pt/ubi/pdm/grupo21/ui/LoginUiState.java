package pt.ubi.pdm.grupo21.ui;

import androidx.annotation.StringRes;

public final class LoginUiState {

    public enum Status { LOGGED_OUT, LOGGED_IN, ERROR }

    public static final int NO_ERROR = 0;

    public final Status status;
    public final String username;

    @StringRes
    // id do texto em strings.xml
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
