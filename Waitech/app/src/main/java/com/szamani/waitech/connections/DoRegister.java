package com.szamani.waitech.connections;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.szamani.waitech.interfaces.OnLoginTaskCompleted;
import com.szamani.waitech.interfaces.OnRegisterTaskCompleted;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Szamani on 9/23/2016.
 */
public class DoRegister {
    private Context context;

    public void doRegister(Context context, OnRegisterTaskCompleted listener,
                         String email, String password) {
        this.context = context;
        new RegisterTask(listener,
                context,
                email,
                password)
                .execute();
    }

    private class RegisterTask extends AsyncTask<Void, Void, Void> {
        OnRegisterTaskCompleted listener;
        String email;
        Context mContext;
        String password;
        boolean successful;

        public RegisterTask(OnRegisterTaskCompleted listener, Context context,
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
        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            RequestBody formBody = new FormBody.Builder()
                    .add("email", email)
                    .add("password1", password)
                    .add("password2", password)
                    .build();

            Request request = new Request.Builder()
                    .url("https://restaurant-user.herokuapp.com/register/")
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

                listener.onRegisterCompleted(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}
