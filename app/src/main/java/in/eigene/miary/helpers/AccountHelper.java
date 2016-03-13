package in.eigene.miary.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;

import in.eigene.miary.sync.SyncAdapter;

public class AccountHelper {

    public static final String KEY_DROPBOX_ACCESS_TOKEN = "dropboxAccessToken";
    public static final String KEY_DROPBOX_EMAIL = "dropboxEmail";

    public static Account getAccount(final AccountManager accountManager) {
        final Account[] accounts = accountManager.getAccountsByType(SyncAdapter.ACCOUNT_TYPE);
        return accounts.length != 0 ? accounts[0] : createAccount(accountManager);
    }

    private static Account createAccount(final AccountManager accountManager) {
        final Account account = new Account("Miary", SyncAdapter.ACCOUNT_TYPE);
        ContentResolver.setSyncAutomatically(account, SyncAdapter.AUTHORITY, true);
        ContentResolver.setIsSyncable(account, SyncAdapter.AUTHORITY, 1);
        ContentResolver.addPeriodicSync(account, SyncAdapter.AUTHORITY, Bundle.EMPTY, 1800L);
        accountManager.addAccountExplicitly(account, null, Bundle.EMPTY);
        return account;
    }
}
