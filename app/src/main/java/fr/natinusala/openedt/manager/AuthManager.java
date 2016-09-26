package fr.natinusala.openedt.manager;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import fr.natinusala.openedt.data.Component;


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

    public static void addAccount(String id, String pwd, Component component, Context context){
        AccountManager accountManager = AccountManager.get(context);
        Account account = new Account(id, "com.openedt.auth");
        accountManager.setUserData(account, "component", component.name);
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

    public static boolean checkLogin(Component component, String id, String pwd){
        String url = component.groups_url;
        String login = id+":"+pwd;
        String b64login = new String(android.util.Base64.encode(login.getBytes(), android.util.Base64.DEFAULT));
        int status = -1;
        try {
            Connection.Response resp = Jsoup.connect(url).header("Authorization", "Basic " + b64login).execute();
            status = resp.statusCode();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("stat", Integer.toString(status));
        return (status == 200);


    }

}
