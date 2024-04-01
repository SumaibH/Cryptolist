package com.tradetrack.cryptolist.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.tradetrack.cryptolist.R;
import com.tradetrack.cryptolist.dataBase.AppDatabase;
import com.tradetrack.cryptolist.dataBase.DataModelDao;
import com.tradetrack.cryptolist.dataModel.DataModel;

public class NewDataActivity extends AppCompatActivity {
    private static final String TAG = "NewDataActivity";
    private RelativeLayout submitButton;
    private ImageView backButton;
    private TextInputLayout editBuyPrice, editBuyQuantity, editSellPrice, editSellQuantity;
    private TextView buyTitle, sellTitle;
    private double totalBuyPrice, totalSalePrice, totalEarning;
    private String currentdate, selectedCurrency;
    AppDatabase db;
    DataModelDao dataModelDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, callback);
        setContentView(R.layout.activity_new_data);
        Intent intent = getIntent();
        db = AppDatabase.getInstance(this);
        dataModelDao = db.dataModelDao();
        buyTitle = findViewById(R.id.buyTitle);
        sellTitle = findViewById(R.id.sellTitle);
        submitButton = findViewById(R.id.btnSubmit);
        backButton = findViewById(R.id.backButton);

        this.currentdate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

        // Initialize TextInputLayout fields
        editBuyPrice = findViewById(R.id.editBuyPrice);
        editBuyQuantity = findViewById(R.id.editBuyQuantity);
        editSellPrice = findViewById(R.id.editSellPrice);
        editSellQuantity = findViewById(R.id.editSellQuantity);
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            // set currencyCode and colorCode in TextInputLayout fields
            selectedCurrency = bundle.getString("selectedCurrencyCode");
        }
        buyTitle.setText("Buy " + selectedCurrency);
        sellTitle.setText("Sell " + selectedCurrency);

        // Set up onClickListener for submit button
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editBuyPrice.getEditText().getText().toString().trim().isEmpty() ||
                        editBuyPrice.getEditText().getText().toString().trim().equals(".") ||
                        editBuyPrice.getEditText().getText().toString().trim().equals(" ")) {
                    editBuyPrice.getEditText().setText("0");
                }
                if (editBuyQuantity.getEditText().getText().toString().trim().isEmpty() ||
                        editBuyQuantity.getEditText().getText().toString().trim().equals(".") ||
                        editBuyQuantity.getEditText().getText().toString().trim().equals(" ")) {
                    editBuyQuantity.getEditText().setText("0");
                }
                if (editSellPrice.getEditText().getText().toString().trim().isEmpty() ||
                        editSellPrice.getEditText().getText().toString().trim().equals(".") ||
                        editSellPrice.getEditText().getText().toString().trim().equals(" ")) {
                    editSellPrice.getEditText().setText("0");
                }
                if (editSellQuantity.getEditText().getText().toString().trim().isEmpty() ||
                        editSellQuantity.getEditText().getText().toString().trim().equals(".") ||
                        editSellQuantity.getEditText().getText().toString().trim().equals(" ")) {
                    editSellQuantity.getEditText().setText("0");
                }

                double buyPrice = parseDouble(editBuyPrice.getEditText().getText().toString());
                double buyQuantity = parseDouble(editBuyQuantity.getEditText().getText().toString());
                double sellPrice = parseDouble(editSellPrice.getEditText().getText().toString());
                double sellQuantity = parseDouble(editSellQuantity.getEditText().getText().toString());

                // insert data in database
                calculateResults(buyPrice, buyQuantity, sellPrice, sellQuantity);
                databaseInsert();

                onBackPressed();
                Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content),"Data Inserted", Snackbar.LENGTH_SHORT);
                snackbar.show();
            }
        });

        // Set up onClickListener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                callback.handleOnBackPressed();
            }
        });
    }

    OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            finish();
        }
    };

    private void databaseInsert() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                db.runInTransaction(new Runnable() {
                    @Override
                    public void run() {
                        int currencyId = dataModelDao.getCurrencyId(selectedCurrency);
                        // check if currencyId is valid (greater than 0) before proceeding with DataModel insertion
                        if (currencyId > 0) {
                            String currencyCode = selectedCurrency;
                            String colorCode = "#FFFFFF";
                            String editBuyPriceText = editBuyPrice.getEditText().getText().toString();
                            String editBuyQuantityText = editBuyQuantity.getEditText().getText().toString();
                            String editSellPriceText = editSellPrice.getEditText().getText().toString();
                            String editSellQuantityText = editSellQuantity.getEditText().getText().toString();

                            DataModel dataModel = new DataModel(currencyId,
                                    currencyCode,
                                    colorCode,
                                    currentdate,
                                    editBuyPriceText,
                                    editBuyQuantityText,
                                    editSellQuantityText,
                                    editSellPriceText,
                                    String.valueOf(totalBuyPrice),
                                    String.valueOf(totalSalePrice),
                                    String.valueOf(totalEarning), false);
                            dataModelDao.insertDataModel(dataModel);
                        } else {
                            Log.d(TAG, "run: currencyId is invalid");
                        }
                    }
                });
            }
        }).start();
    }

    private void calculateResults(Double buyPrice, Double buyQuantity, Double sellPrice, Double sellQuantity) {
        // Calculate buy, sell, and earning
        totalBuyPrice = (buyPrice * buyQuantity);
        if (sellQuantity==0){
            totalEarning = 0;
        }else{
            totalSalePrice = (sellPrice * sellQuantity);
            if (totalSalePrice>totalBuyPrice){
                totalEarning = totalSalePrice - totalBuyPrice;
            }else {
                totalEarning = 0;
            }

        }
    }

    private double parseDouble(String text) {
        try {
            if (text == null || text.isEmpty())
                return 0.0;
            if (text.contains(",")) {
                text = text.replace(",", ".");
            }
            if (text.contains(" ")) {
                text = text.replace(" ", "");
            }
            if (text.equals(".")) {
                text = "0.";
            }
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    private TextWatcher createTextWatcher(final TextInputLayout layout) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                updateHint(layout, s);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateHint(layout, s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                updateHint(layout, s);
            }
        };
    }

    private void updateHint(TextInputLayout layout, CharSequence text) {
        if (text == null || text.length() == 0) {
            layout.setHint("Price");
        } else {
            layout.setHint(null); // or use an empty string if you want
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.d(TAG, "onBackPressed: called");
        finish(); // Finish the activity
    }
}
