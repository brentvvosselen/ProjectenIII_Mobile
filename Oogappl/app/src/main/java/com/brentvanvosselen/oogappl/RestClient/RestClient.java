package com.brentvanvosselen.oogappl.RestClient;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;

public class RestClient extends AsyncTask<Void, Void, String> {

    private final String IPADRESS = "";

    private String url;
    private String requestMethod;

    public RestClient(String url, String requestMethod) {
        try {
            this.url = "http://" + IPADRESS + url;
            this.requestMethod = requestMethod;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    public String getJson() {
        HttpURLConnection connection = null;
        BufferedReader reader = null;

        String jsonString = null;

        try {
            URL url = new URL(this.url);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(this.requestMethod);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while((line = reader.readLine()) != null) {
                Log.i("Read", line);
                buffer.append(line + '\n');
            }

            if (buffer.length() == 0) {
                return null;
            }

            jsonString = buffer.toString();
            Log.i("Connection", jsonString);
            return jsonString;

        } catch (ProtocolException e) {
            Log.i("Connection", "Protocolexception");
        } catch (MalformedURLException e) {
            Log.i("Connection", "Bad URL");
        } catch (IOException e) {
            Log.i("Connection", e.getMessage());
        } finally {
            if (connection != null) {
                connection.disconnect();
            }

            if(reader != null) {
                try {
                    reader.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

    @Override
    protected String doInBackground(Void... params) {
        return getJson();
    }
}
