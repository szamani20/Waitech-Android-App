package com.szamani.waitech.connections;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.szamani.waitech.interfaces.OnLoginTaskCompleted;
import com.szamani.waitech.utility.Utility;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Szamani on 9/15/2016.
 */

public class DoLogin {
    private Context context;

    // this should be call only if it is really needed or user implicitly want it
    // loading orders for example (orders are save on server)
    public void doLogin(Context context, OnLoginTaskCompleted listener,
                        String email, String password) {
        this.context = context;
        new LoginTask(listener,
                context,
                email,
                password)
                .execute();
    }

    private class LoginTask extends AsyncTask<Void, Void, Void> {
        OnLoginTaskCompleted listener;
        String email;
        Context mContext;
        String password;
        boolean successful;

        public LoginTask(OnLoginTaskCompleted listener, Context context,
                         String email, String password) {
            this.email = email;
            this.password = password;
            this.listener = listener;
            this.mContext = context;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("RegisterTask Sucessful?" + successful,
                    "Email: " + email + ", Password: " + password);
            Utility.setLogin(mContext, true);

//            if (successful) {
                Utility.setEmail(mContext, this.email);
                Utility.setPassword(mContext, this.password);
//            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("username", email)
                    .add("password", password)
                    .build();

            Request request;

            String savedCookie = Utility.readFromCookie(mContext);

            if (savedCookie != null) {
                request = new Request.Builder()
                        .url("https://restaurant-user.herokuapp.com/login/")
                        .post(formBody)
                        .addHeader("Cookie", savedCookie)
                        .build();
                Log.d("Cookie Sent: ", savedCookie);
            } else request = new Request.Builder()
                    .url("https://restaurant-user.herokuapp.com/login/")
                    .post(formBody)
                    .build();

            try {
                Response response = client.newCall(request).execute();

                Log.d("Response: ", " " + response.toString());

                Headers headers = response.headers();
                String cookieReceived = "";

                for (String item : headers.values("Set-Cookie"))
                    if (item.contains("sessionid")) {
                        cookieReceived = item;
                        Log.d("item: ", item);
                        break;
                    }

                Log.d("Cookie received: ", cookieReceived);

                successful = response.isSuccessful();

                listener.onLoginCompleted(cookieReceived);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}