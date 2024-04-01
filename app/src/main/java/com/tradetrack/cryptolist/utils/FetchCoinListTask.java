package com.tradetrack.cryptolist.utils;

import android.os.AsyncTask;

import com.tradetrack.cryptolist.activities.AddCurrencyActivity;
import com.tradetrack.cryptolist.dataModel.CoinModel;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class FetchCoinListTask extends AsyncTask<Void, Void, List<CoinModel>> {
            private final String apiKey;
            private final AddCurrencyActivity.CoinFetcher.CoinFetchListener listener;

            public FetchCoinListTask(String apiKey, AddCurrencyActivity.CoinFetcher.CoinFetchListener listener) {
                this.apiKey = apiKey;
                this.listener = listener;
            }

            @Override
            protected List<CoinModel> doInBackground(Void... voids) {
                try {
                    URL url = new URL("https://api.livecoinwatch.com/coins/list");
                    HttpURLConnection connection = null;
                    try {
                        connection = (HttpURLConnection) url.openConnection();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    connection.setRequestMethod("POST");
                    connection.setRequestProperty("Content-Type", "application/json");
                    connection.setRequestProperty("x-api-key", apiKey);
                    connection.setDoOutput(true);

                    String requestBody = "{\"currency\": \"USD\",\"sort\": \"rank\",\"order\": \"ascending\",\"offset\": 0,\"limit\": 500,\"meta\": true}";
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

                        // Parse JSON response into a list of Coin objects using Gson
                        Gson gson = new Gson();
                        Type coinListType = new TypeToken<List<CoinModel>>() {}.getType();
                        return gson.fromJson(response.toString(), coinListType);
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
            protected void onPostExecute(List<CoinModel> coinList) {
                if (listener != null) {
                    listener.onCoinFetchComplete(coinList);
                }
            }
        }
