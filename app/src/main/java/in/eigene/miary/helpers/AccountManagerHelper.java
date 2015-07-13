package in.eigene.miary.helpers;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.Context;

import in.eigene.miary.sync.SyncAdapter;

public class AccountManagerHelper {

    /**
     * Gets the first Miary account if any.
     */
    public static Account getAccount(final Context context) {
        final Account[] accounts = AccountManager.get(context).getAccountsByType(SyncAdapter.ACCOUNT_TYPE);
        return accounts.length != 0 ? accounts[0] : null;
    }
}
