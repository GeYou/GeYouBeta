package com.thesis.geyoubeta.factory;

import android.os.AsyncTask;
import android.util.Log;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thesis.geyoubeta.entity.User;
import com.thesis.geyoubeta.callback.RestCallback;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by ivanwesleychua on 09/01/2016.
 */
public class RestServiceFactory {

    private static RestServiceFactory factory;

    private RestServiceFactory() {
    }

    public static RestServiceFactory getInstance() {
        if (factory == null) {
            factory = new RestServiceFactory();
        }
        return factory;
    }

    public void sendGetRequest(RestCallback delagate) {
        String url = "http://10.0.3.2:8080/geyou/user/get/1";

        GeYouRest task = new GeYouRest();
        task.setDelegate(delagate);
        task.execute(url);
    }

    public void createUser() {
        String url = "http://10.0.3.2:8080/geyou/user/create";



    }

    private class GeYouRest extends AsyncTask<String, Void, Object> {

        private RestCallback delegate;

        public void setDelegate(RestCallback delegate){
            this.delegate = delegate;
        }

        @Override
        protected void onPostExecute(Object data) {
            delegate.onRestFinish(data);
        }

        @Override
        protected Object doInBackground(String... params) {
            InputStream in = null;

            User u = null;
            try{
                URL url = new URL(params[0]);
                URLConnection connection = url.openConnection();

                connection.connect();

                in = connection.getInputStream();

                ObjectMapper mapper = new ObjectMapper();

                u = mapper.readValue(in, User.class);

                return u;

            } catch (MalformedURLException e) {
                Log.e("Rest service", "Error: malformed url");
                e.printStackTrace();
            } catch (IOException e) {
                Log.e("Rest service", "Error: IOe");
                e.printStackTrace();
            } finally {
                try{
                    if(in != null){
                        in.close();
                    }
                } catch (IOException e) {
                    Log.e("Rest service", "Error: Close error IO");
                    e.printStackTrace();
                }
            }
            return u;
        }
    }
}
