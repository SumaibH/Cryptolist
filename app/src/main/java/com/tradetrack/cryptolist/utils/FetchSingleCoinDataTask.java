package com.tradetrack.cryptolist.utils;

import android.os.AsyncTask;

import com.tradetrack.cryptolist.dataModel.CoinModel;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class FetchSingleCoinDataTask extends AsyncTask<Void, Void, CoinModel> {
    private final String apiKey;
    private final String coinCode;
    private final CoinFetchListener listener;

    public FetchSingleCoinDataTask(String apiKey, String coinCode, CoinFetchListener listener) {
        this.apiKey = apiKey;
        this.coinCode = coinCode;
        this.listener = listener;
    }

    @Override
    protected CoinModel doInBackground(Void... voids) {
        try {
            URL url = new URL("https://api.livecoinwatch.com/coins/single");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("x-api-key", apiKey);
            connection.setDoOutput(true);

            String requestBody = "{\"currency\": \"USD\",\"code\": \"" + coinCode + "\",\"meta\": true}";
            connection.getOutputStream().write(requestBody.getBytes());

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Parse JSON response into a CoinModel object using Gson
                Gson gson = new Gson();
                return gson.fromJson(response.toString(), CoinModel.class);
            } else {
                // Handle non-OK response code here if needed
                if (listener != null) {
                    listener.onCoinFetchError(new Exception("Non-OK response code: " + responseCode));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            if (listener != null) {
                listener.onCoinFetchError(e);
            }
        }
        return null;
    }


    @Override
    protected void onPostExecute(CoinModel coin) {
        if (listener != null) {
            listener.onCoinFetchComplete(coin);
        }
    }

    public interface CoinFetchListener {
        void onCoinFetchComplete(CoinModel coin);

        void onCoinFetchError(Exception e);
    }
}
