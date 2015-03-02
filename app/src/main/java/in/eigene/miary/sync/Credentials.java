package in.eigene.miary.sync;

public class Credentials {

    private final String email;

    private final String password;

    private final boolean signUp;

    public Credentials(final String email, final String password, final boolean signUp) {
        this.email = email;
        this.password = password;
        this.signUp = signUp;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public boolean getSignUp() {
        return signUp;
    }
}
