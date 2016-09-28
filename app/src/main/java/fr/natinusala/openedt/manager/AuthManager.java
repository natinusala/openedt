package fr.natinusala.openedt.manager;

import android.accounts.Account;
import android.accounts.AccountAuthenticatorActivity;
import android.accounts.AccountManager;
import android.content.Context;
import android.content.SharedPreferences;


import com.google.gson.Gson;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;

import java.util.HashMap;


import fr.natinusala.openedt.data.Component;


/**
 * Created by Maveist on 20/09/2016.
 */
public class AuthManager extends AccountAuthenticatorActivity {

    public static String ACCOUNT_DIRECTORY_PREF = "ACCOUNT_DIRECTORY";
    public static String ACCOUNT_DIRECTORY = "directory";

    //TODO implement bundle for component data

    public static HashMap<String, String> getAccountDirectory(Context context){
        SharedPreferences directoryStored = context.getSharedPreferences(ACCOUNT_DIRECTORY_PREF, 0);
        String directoryJson = directoryStored.getString(ACCOUNT_DIRECTORY, null);
        HashMap<String, String> directory = null;
        if(directoryJson != null) {
            Gson gson = new Gson();
            directory = gson.fromJson(directoryJson, HashMap.class);
        }else{
            directory = new HashMap<>();
        }
        return directory;
    }

    public static void saveAccountDirectory(HashMap<String, String> directory, Context context){
        Gson gson = new Gson();
        String directoryJson = gson.toJson(directory, HashMap.class);
        SharedPreferences storage = context.getSharedPreferences(ACCOUNT_DIRECTORY_PREF, 0);
        SharedPreferences.Editor editor = storage.edit();
        editor.putString(ACCOUNT_DIRECTORY, directoryJson);
        editor.commit();
    }

    public static boolean needAccount(String component, Context context){
        HashMap<String, String> directory = getAccountDirectory(context);
        String accountName = directory.get(component);
        return (accountName == null);
    }


    public static void addAccount(String id, String pwd, String componentName, Context context){
        AccountManager accountManager = AccountManager.get(context);
        Account account = new Account(id, "com.openedt.auth");
        accountManager.setUserData(account, "component", componentName);
        accountManager.addAccountExplicitly(account, pwd, null);
        HashMap<String, String> directory = getAccountDirectory(context);
        directory.put(componentName, id);
        saveAccountDirectory(directory, context);

    }

    public static Account getAccount(String component, Context context){
        HashMap<String, String> directory = getAccountDirectory(context);
        String idAccount = directory.get(component);
        AccountManager accountManager = AccountManager.get(context);
        Account[] accounts = accountManager.getAccountsByType("com.openedt.auth");
        for(int i = 0; i < accounts.length ; i++){
            if(idAccount.equals(accounts[i].name)){
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
        return (status == 200);


    }

}
