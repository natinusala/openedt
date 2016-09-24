package fr.natinusala.openedt.manager;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;


/**
 * Created by Maveist on 20/09/2016.
 */
public class AuthManager extends AccountAuthenticatorActivity {

    //TODO implement bundle for component data

    public static boolean needAccount(String component, Context context){
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.openedt.auth");
        for(int i = 0; i < accounts.length ; i++){
            String accountComponent = "";
            accountComponent += accountManager.getUserData(accounts[i], "component");
            Log.v("account_c", accountComponent);
            if(component.equals(accountComponent)){
                return false;
            }
        }
        return true;
    }

    public static void addAccount(String id, String pwd, String component, Context context){
        AccountManager accountManager = AccountManager.get(context);
        Account account = new Account(id, "com.openedt.auth");
        accountManager.setUserData(account, "component", component);
        accountManager.addAccountExplicitly(account, pwd, null);
    }

    public static Account getAccount(String component, Context context){
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.openedt.auth");
        for(int i = 0; i < accounts.length ; i++){
            Log.v("account", accounts[i].name);
            String accountComponent = accountManager.getUserData(accounts[i], "component");
            if(component.equals(accountComponent)){
                return accounts[i];
            }
        }
        return null;
    }


}
