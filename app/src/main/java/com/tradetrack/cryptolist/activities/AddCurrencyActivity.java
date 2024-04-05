package com.tradetrack.cryptolist.activities;

import static com.tradetrack.cryptolist.utils.DateUtils.getTimeDifference;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.tradetrack.cryptolist.dataBase.AppDatabase;
import com.tradetrack.cryptolist.dataBase.DataModelDao;
import com.tradetrack.cryptolist.dataModel.CoinModel;
import com.tradetrack.cryptolist.dataModel.CurrencyModel;
import com.tradetrack.cryptolist.dataModel.LiveCurrencyModel;
import com.tradetrack.cryptolist.databinding.ActivityAddCurrencyBinding;
import com.tradetrack.cryptolist.rvAdapter.CoinAdapter;
import com.tradetrack.cryptolist.utils.FetchCoinListTask;
import com.tradetrack.cryptolist.utils.NetworkUtils;
import com.tradetrack.cryptolist.utils.SharedPreferencesManager;
import com.tradetrack.cryptolist.utils.SlideInBottomAnimator;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.tradetrack.cryptolist.R;
import com.madapps.liquid.LiquidRefreshLayout;

public class AddCurrencyActivity extends AppCompatActivity {

    private ActivityAddCurrencyBinding binding;
    private List<CoinModel> coinLiveList;
    private SharedPreferencesManager sharedPrefs;
    private Handler handler = new Handler();
    private boolean isHandlerRunning = false;
    private List<LiveCurrencyModel> coinList;
    private CoinAdapter adapter;
    private List<LiveCurrencyModel> filteredList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, callback);
        binding = ActivityAddCurrencyBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        filteredList = new ArrayList<>();
        coinList = new ArrayList<>();
        coinLiveList = new ArrayList<>();
        sharedPrefs = SharedPreferencesManager.getInstance(getApplicationContext());
        setAdapter();
        setupTextWatcher();
        fetchFromDatabase();
        handlerCallBack();

        binding.refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callApi(true);
            }
        });
        binding.refreshLayout.setOnRefreshListener(new LiquidRefreshLayout.OnRefreshListener() {
            @Override
            public void completeRefresh() {
                // Called when you call refreshLayout.finishRefreshing()
            }

            @Override
            public void refreshing() {
                callApi(false);
            }
        });

        // Set up onClickListener for back button
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.handleOnBackPressed();
            }
        });
    }

    private void setAdapter() {
        binding.currenciesRV.setLayoutManager(new LinearLayoutManager(this));
        SlideInBottomAnimator slideInBottomAnimator = new SlideInBottomAnimator();
        binding.currenciesRV.setItemAnimator(slideInBottomAnimator);
        adapter = new CoinAdapter(this, coinList);
        binding.currenciesRV.setAdapter(adapter);

        adapter.setOnBookmarkClickListener(position -> {
            if (filteredList.size()==0){
                filteredList.addAll(coinList);
            }
            filteredList.get(position).setBookMarked(!filteredList.get(position).isBookMarked());

            adapter.notifyDataSetChanged();
            updateCurrencyStatus(filteredList.get(position),filteredList.get(position).getCurrencyFullName(),
                    filteredList.get(position).isChecked(),filteredList.get(position).isBookMarked());
        });
    }

    private void callApi(boolean from) {
        binding.refresh.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));
        if (NetworkUtils.isNetworkAvailable(this)) {
            if (from) {
                binding.showProgress.setVisibility(View.VISIBLE);
            }
            coinLiveList.clear();
            CoinFetcher coinFetcher = new CoinFetcher();
            coinFetcher.fetchCoinList("dca4a4de-ea6c-4968-8740-04f75852cb50", new CoinFetcher.CoinFetchListener() {
                @Override
                public void onCoinFetchComplete(List<CoinModel> coins) {
                    // Handle the retrieved list of coins
                    if (coins != null) {
                        // Process the list of coins
                        coinLiveList.addAll(coins);

                        for (int i = 0;i<coinLiveList.size();i++) {
                            // Find the corresponding currency in the currencies list
                            LiveCurrencyModel currencyModel;
                            currencyModel = new LiveCurrencyModel(
                                    coinLiveList.get(i).getName(),
                                    coinLiveList.get(i).getCode(),
                                    String.valueOf(coinLiveList.get(i).getRate()),
                                    String.valueOf(coinLiveList.get(i).getAllTimeHighUSD()),
                                    coinLiveList.get(i).getPng32Url(),
                                    false,
                                    false
                            );
                            databaseInsert(currencyModel);
                        }
                    }
                }

                @Override
                public void onCoinFetchError(Exception e) {
                    // Handle errors in fetching coins
                    Log.e("CoinFetch", "Error fetching coins: " + e.getMessage());
                }
            });
        } else {
            showSnackBar();
        }
    }

    private void handlerCallBack(){
        handler.removeCallbacksAndMessages(null);
        isHandlerRunning = false;
        updateTimeDifference();
    }

    private void updateTimeDifference() {
        if (!isHandlerRunning) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String timeValue = sharedPrefs.getString("LastUpdated", "0");
                    String timeDifference = getTimeDifference(Long.parseLong(timeValue));
                    if (timeValue.equals("0")){
                        binding.rate.setText("Currency Rates");
                    }else {
                        binding.rate.setText("Last Updated | " + timeDifference);
                    }

                    handler.postDelayed(this, 1000);
                }
            });
            isHandlerRunning = true;
        }
    }
    OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            if (binding.searchBar.hasFocus()){
                // Check if the cross icon is clicked and clear the EditText
                binding.searchBar.setText("");
                binding.searchBar.clearFocus(); // Remove focus
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.searchBar.getWindowToken(), 0);
            }else{
                finish();
            }
        }
    };

    public static class CoinFetcher {

        public interface CoinFetchListener {
            void onCoinFetchComplete(List<CoinModel> coinList);
            void onCoinFetchError(Exception e);
        }

        public void fetchCoinList(String apiKey, CoinFetchListener listener) {
            new FetchCoinListTask(apiKey, listener).execute();
        }

    }
//
    private void setupTextWatcher() {
        binding.searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not used, but required by the interface
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Text changed, perform search or filtering based on the entered text
                if (s.length() > 0) {
                    binding.searchBar.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_clear_24, 0);
                } else {
                    binding.searchBar.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.baseline_search_24, 0);
                }
                String searchText = s.toString();
                // Perform search or filtering based on 'searchText'
                performSearch(searchText);
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not used, but required by the interface
            }
        });
        binding.searchBar.setOnTouchListener((v, event) -> {
            if (event.getAction() == MotionEvent.ACTION_UP &&
                    event.getRawX() >= (binding.searchBar.getRight() - binding.searchBar.getCompoundDrawables()[2].getBounds().width())) {
                // Check if the cross icon is clicked and clear the EditText
                binding.searchBar.setText("");
                binding.searchBar.clearFocus(); // Remove focus
                // Hide keyboard
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.searchBar.getWindowToken(), 0);
                return true;
            }
            return false;
        });
    }

    private void performSearch(String searchText) {

        filteredList.clear();

        // Create an iterator to safely iterate over the original list
        Iterator<LiveCurrencyModel> iterator = coinList.iterator();
        while (iterator.hasNext()) {
            LiveCurrencyModel item = iterator.next();

            // Perform a case-insensitive search based on item names
            if (item.getCurrencyShortcut().toLowerCase().contains(searchText.toLowerCase())
                    || item.getCurrencyFullName().toLowerCase().contains(searchText.toLowerCase())) {
                filteredList.add(item); // Use iterator to add the item to the filtered list
            }
        }

        // Update the RecyclerView adapter with the filtered list
        adapter.updateList(filteredList);
    }



    private void fetchFromDatabase(){
        AppDatabase db = AppDatabase.getInstance(this);
        DataModelDao dataModelDao = db.dataModelDao();

        new Thread(new Runnable() {
            @Override
            public void run() {
                // Fetch the list of currencies from the database
                coinList.clear();
                coinList.addAll(dataModelDao.getAllSelectedCurrencyModels());

                // Update the UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (coinList.size()==0){
                            callApi(true);
                        }else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).start();
    }

    public void updateCurrencyStatus(LiveCurrencyModel currencyModel, String fullName, boolean isCheckedValue, boolean isBookmarkedValue) {
        AppDatabase db = AppDatabase.getInstance(this);
        DataModelDao dataModelDao = db.dataModelDao();
        // Execute the update query
        new Thread(new Runnable() {
            @Override
            public void run() {
                CurrencyModel model = new CurrencyModel(
                        currencyModel.getCurrencyFullName(),
                        currencyModel.getCurrencyShortcut(),
                        currencyModel.getCurrencyLiveRate(),
                        currencyModel.getCurrencyMaxRate(),
                        currencyModel.getCurrencyIcon(),
                        false,
                        false
                );
                if (isBookmarkedValue){
                    dataModelDao.insertCurrencyModel(model);
                }else{
                    dataModelDao.deleteCurrencyModelByFullName(model.getCurrencyFullName());
                }
                dataModelDao.updateLiveCheckedAndBookmarkedStatus(fullName, isCheckedValue, isBookmarkedValue);
            }
        }).start();

    }
    private void databaseInsert(LiveCurrencyModel model) {
        AppDatabase db = AppDatabase.getInstance(this);
        DataModelDao dataModelDao = db.dataModelDao();
        new Thread(new Runnable() {
            @Override
            public void run() {
                // start a transaction
                db.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        long existingCurrencyId = dataModelDao.getLiveExistingCurrencyId(model.getCurrencyShortcut());
                        if (existingCurrencyId <= 0) {
                            coinList.add(model);
                            dataModelDao.insertSelectedCurrencyModel(model);
                        } else {
                            for (LiveCurrencyModel coin : coinList) {
                                if (coin.getCurrencyFullName().equals(model.getCurrencyFullName())) {
                                    // Update the prices
                                    coin.setCurrencyLiveRate(model.getCurrencyLiveRate());
                                    coin.setCurrencyMaxRate(model.getCurrencyMaxRate());
                                    break; // Exit the loop after updating the prices
                                }
                            }
                            dataModelDao.updateCurrencyModelByName(model.getCurrencyFullName(),model.getCurrencyLiveRate(),model.getCurrencyMaxRate());
                        }
                    }
                });
                // After the database operation is completed, update the adapter on the main/UI thread
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        // Update the adapter here
                        binding.showProgress.setVisibility(View.GONE);
                        binding.refreshLayout.finishRefreshing();
                        handlerCallBack();
                        binding.refresh.clearAnimation();
                        sharedPrefs.saveString("LastUpdated", String.valueOf(System.currentTimeMillis()));
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }

    private void showSnackBar(){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), getString(R.string.please_connect_to_an_active_internet_connection), Snackbar.LENGTH_INDEFINITE);

        LayoutInflater inflater = LayoutInflater.from(snackbar.getContext());
        View customSnackbarView = inflater.inflate(R.layout.custom_snackbar_layout, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.addView(customSnackbarView);

        TextView textView = customSnackbarView.findViewById(R.id.snackbar_text);
        Button retryButton = customSnackbarView.findViewById(R.id.snackbar_retry_button);

        textView.setText(getString(R.string.ooops_no_internet));
        retryButton.setOnClickListener(v -> {
            callApi(true);
            snackbar.dismiss();
        });

        snackbar.show();

    }

}
