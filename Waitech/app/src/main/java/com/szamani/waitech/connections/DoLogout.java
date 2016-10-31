package com.szamani.waitech.connections;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.szamani.waitech.interfaces.OnLogoutTaskCompleted;
import com.szamani.waitech.utility.Utility;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Szamani on 9/16/2016.
 */
public class DoLogout {
    private Context context;

    // This should be call only if user is already logged in
    public void doLogout(Context context, OnLogoutTaskCompleted listener) {
        this.context = context;

        new LogoutTask(listener, context)
                .execute();
    }

    private class LogoutTask extends AsyncTask<Void, Void, Void> {
        OnLogoutTaskCompleted listener;
        Context mContext;
        boolean successful;

        public LogoutTask(OnLogoutTaskCompleted listener, Context context) {
            this.listener = listener;
            this.mContext = context;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            Log.d("RegisterTask Sucessful? ", " " + successful);
//            Utility.setLogin(mContext, successful);
            Utility.setLogin(mContext, false);

//            if (successful) {
            Utility.setEmail(mContext, null);
            Utility.setPassword(mContext, null);
            Utility.setLogin(mContext, false);
//            }
        }

        @Override
        protected Void doInBackground(Void... params) {
            OkHttpClient client = new OkHttpClient.Builder()
                    .build();

            RequestBody body = RequestBody
                    .create(MediaType.parse("text/plain; charset=utf-8"),
                            "Logout");
            Request request;

            if (Utility.readFromCookie(mContext) != null) { // you were logged in before (OK)
                request = new Request.Builder()
                        .url("https://restaurant-user.herokuapp.com/logout/")
                        .post(body)
                        .addHeader("Cookie", Utility.readFromCookie(mContext))
                        .build();
                Log.d("Cookie Sent: ", Utility.readFromCookie(mContext));
            } else { // you were not logged in before (Technically this should never be executed)
                //      only if login was not successful
                Log.d("Warning: ", "Not logged in before (Maybe Cookie not set)");
                listener.onLogoutCompleted(null);
                return null;
            }

            try {
                Response response = client.newCall(request).execute();

                Log.d("Response: ", " " + response.toString());

                successful = response.isSuccessful();

                listener.onLogoutCompleted(response.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

}
