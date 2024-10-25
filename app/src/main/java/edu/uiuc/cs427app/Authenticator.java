package edu.uiuc.cs427app;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;

public class Authenticator extends AbstractAccountAuthenticator {
    public static final String PREFS_NAME = "MyAppPrefs";
    public static final String KEY_USERNAME = "username";
    public static final String KEY_PASSWORD = "password";
    private final Context mContext;
    public Authenticator(Context context) {
        super(context);
        this.mContext = context;
    }
    private Context getContext() {
        return mContext; // Return the stored context
    }

    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) {
        // Save the account details in SharedPreferences or a database
        SharedPreferences prefs = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();

        // For simplicity, we assume the username and password are provided via options
        String username = options.getString(KEY_USERNAME);
        String password = options.getString(KEY_PASSWORD);

        editor.putString(KEY_USERNAME, username);
        editor.putString(KEY_PASSWORD, password);
        editor.apply();

        // Create the new account
        Account newAccount = new Account(username, accountType);
        Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ACCOUNT_NAME, username); // Use KEY_ACCOUNT_NAME
        result.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType); // Use KEY_ACCOUNT_TYPEresult.putParcelable(AccountManager.KEY_ACCOUNT, newAccount); // Optional: use if you want to return the Account object

        return result; // Return a Bundle with account data
    }

    @Override
    public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) {
        // Code to confirm credentials for an existing account
        return null; // Return a Bundle indicating success or failure
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
        // Code to return an authentication token
        return null; // Return a Bundle with the auth token
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // Return a label for the auth token type
        return null; // e.g., "MyApp Auth Token"
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) {
        // Code to update credentials for an existing account
        return null; // Return a Bundle indicating success or failure
    }

    @Override
    public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) {
        // Return a Bundle indicating whether the account has certain features
        return Bundle.EMPTY; // Typically returns Bundle.EMPTY
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        // This method is required but may not be needed for your authenticator
        return null; // Return null if editing properties is not applicable
    }
}
