package com.tradetrack.cryptolist.activities;

import static com.tradetrack.cryptolist.utils.DateUtils.getTimeDifference;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tradetrack.cryptolist.R;
import com.tradetrack.cryptolist.dataModel.CoinModel;
import com.tradetrack.cryptolist.utils.FetchSingleCoinDataTask;
import com.tradetrack.cryptolist.utils.NetworkUtils;
import com.tradetrack.cryptolist.utils.SharedPreferencesManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.woxthebox.draglistview.DragItem;
import com.woxthebox.draglistview.DragItemRecyclerView;
import com.woxthebox.draglistview.DragListView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tradetrack.cryptolist.bottomSheet.CalculateFragment;
import com.tradetrack.cryptolist.bottomSheet.ColorChangeFragment;
import com.tradetrack.cryptolist.dataBase.AppDatabase;
import com.tradetrack.cryptolist.dataBase.DataModelDao;
import com.tradetrack.cryptolist.dataModel.CurrencyModel;
import com.tradetrack.cryptolist.dataModel.DataModel;
import com.tradetrack.cryptolist.rvAdapter.CurrencyAdapter;
import com.tradetrack.cryptolist.rvAdapter.DataAdapter;

public class MainActivity extends AppCompatActivity
        implements CurrencyAdapter.OnCurrencyItemClickListener,
        DataAdapter.OnDataItemClickListener{
    private static final String TAG = "MainActivity";
    DataModelDao dataModelDao;
    Snackbar snackbar;
    private Handler handler = new Handler();
    private boolean isHandlerRunning = false;
    DragListView currencyRecyclerView;
    RelativeLayout newdata;
    RelativeLayout btn_new_currency, btn_delete_currency, btn_delete_item, btn_color_change, btn_calculate_item;
    MaterialCardView mini_buttons_layout;
    ImageView btn_done, refresh;
    int currentPositionEdit;
    TextView total_crypto_tv, total_earning_tv, rate, lastUpdated;
    int finalFromPosition, finalToPosition, finalCurrencyFromPos, finalCurrencyToPos;
    DragListView dataRecyclerView;
    public CurrencyAdapter currencyAdapter;
    public DataAdapter dataAdapter;
    DragItemRecyclerView recyclerView;

    boolean isMiniDataFabOpen = false;
    private List<DataModel> dataList;
    private List<DataModel> tempDataList;
    private List<CurrencyModel> currencyList;
    String currentdate;
    Boolean isAllFAbsVisible = false;
    String selectedCurrencyCode;
    RelativeLayout layout_data_header;
    private AppDatabase db;
    private SharedPreferencesManager sharedPrefs;
    private List<DataModel> checkedDataModelList = new ArrayList<DataModel>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getOnBackPressedDispatcher().addCallback(this, callback);
        setContentView(R.layout.activity_main);
        db = AppDatabase.getInstance(this);
        dataModelDao = db.dataModelDao();
        sharedPrefs = SharedPreferencesManager.getInstance(getApplicationContext());

        this.currentdate = new SimpleDateFormat("dd-MM-yyyy",
                Locale.getDefault()).format(new Date());

        dataList = new ArrayList<>();
        tempDataList = new ArrayList<>();
        currencyList = new ArrayList<>();
        btn_new_currency = findViewById(R.id.btn_new_currency);
        rate = findViewById(R.id.live_rate_tv);
        layout_data_header = findViewById(R.id.layout_data_header);
        btn_delete_currency = findViewById(R.id.btn_delete_currency);
        btn_delete_item = findViewById(R.id.btn_delete_item);
        btn_color_change = findViewById(R.id.btn_color_item);
        btn_calculate_item = findViewById(R.id.btn_calculate_item);
        total_earning_tv = findViewById(R.id.total_earning_tv);
        total_crypto_tv = findViewById(R.id.total_crypto_tv);
        newdata = findViewById(R.id.add_data_fab);
        mini_buttons_layout = findViewById(R.id.mini_buttons_layout);
        mini_buttons_layout.setVisibility(View.GONE);
        btn_done = findViewById(R.id.btn_done);
        refresh = findViewById(R.id.refresh);
        lastUpdated = findViewById(R.id.lastUpdated);
        btn_done.setVisibility(View.GONE);

        showLiveRate();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLivePrice();
            }
        });
        // currency recycler view setup
        currencyRecyclerView = findViewById(R.id.currency_recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        currencyRecyclerView.setLayoutManager(layoutManager);
        // data recycler view setup
        dataRecyclerView = findViewById(R.id.data_recycler_view);
        recyclerView = (DragItemRecyclerView) dataRecyclerView.getRecyclerView();
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(this);
        layoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        dataRecyclerView.setLayoutManager(layoutManager2);
        // set drag listener
        currencyRecyclerView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                currencyRecyclerView.setDragEnabled(false);
                finalCurrencyFromPos = position;
            }

            @Override
            public void onItemDragging(int itemPosition, float x, float y) {
                super.onItemDragging(itemPosition, x, y);
                finalCurrencyToPos = itemPosition;
                // Handle item dragging actions if needed
            }

            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                currencyRecyclerView.setDragEnabled(true);
                CurrencyModel currencyModel = currencyList.get(fromPosition);

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                if (fromPosition<toPosition){
                    for (int i = fromPosition; i < toPosition; i++) {
                        CurrencyModel fromCurrencyModel = currencyList.get(i);
                        CurrencyModel toCurrencyModel = currencyList.get(i + 1);

                        // Submit tasks to the executor service
                        executorService.submit(() -> {
                            // update new position
                            dataModelDao.updateCurrencyModelById(fromCurrencyModel.getId(),
                                    toCurrencyModel.getCurrencyFullName(),
                                    toCurrencyModel.getCurrencyShortcut(),
                                    toCurrencyModel.getCurrencyLiveRate(),
                                    toCurrencyModel.getCurrencyMaxRate(),
                                    toCurrencyModel.getCurrencyIcon(),
                                    toCurrencyModel.isChecked(),
                                    toCurrencyModel.isBookMarked());

                            // update old position
                            dataModelDao.updateCurrencyModelById(toCurrencyModel.getId(),
                                    fromCurrencyModel.getCurrencyFullName(),
                                    fromCurrencyModel.getCurrencyShortcut(),
                                    fromCurrencyModel.getCurrencyLiveRate(),
                                    fromCurrencyModel.getCurrencyMaxRate(),
                                    fromCurrencyModel.getCurrencyIcon(),
                                    fromCurrencyModel.isChecked(),
                                    fromCurrencyModel.isBookMarked());
                        });
                    }
                }else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        CurrencyModel fromCurrencyModel = currencyList.get(i);
                        CurrencyModel toCurrencyModel = currencyList.get(i - 1);
                        // Submit tasks to the executor service
                        executorService.submit(() -> {
                            // update new position
                            dataModelDao.updateCurrencyModelById(fromCurrencyModel.getId(),
                                    toCurrencyModel.getCurrencyFullName(),
                                    toCurrencyModel.getCurrencyShortcut(),
                                    toCurrencyModel.getCurrencyLiveRate(),
                                    toCurrencyModel.getCurrencyMaxRate(),
                                    toCurrencyModel.getCurrencyIcon(),
                                    toCurrencyModel.isChecked(),
                                    toCurrencyModel.isBookMarked());


                            // update old position
                            dataModelDao.updateCurrencyModelById(toCurrencyModel.getId(),
                                    fromCurrencyModel.getCurrencyFullName(),
                                    fromCurrencyModel.getCurrencyShortcut(),
                                    fromCurrencyModel.getCurrencyLiveRate(),
                                    fromCurrencyModel.getCurrencyMaxRate(),
                                    fromCurrencyModel.getCurrencyIcon(),
                                    fromCurrencyModel.isChecked(),
                                    fromCurrencyModel.isBookMarked());
                        });
                    }
                }

                // Scroll to the dropped item's position
                currencyRecyclerView.getRecyclerView().scrollToPosition(toPosition);
            }
        });

        dataRecyclerView.setDragListListener(new DragListView.DragListListenerAdapter() {
            @Override
            public void onItemDragStarted(int position) {
                dataRecyclerView.setDragEnabled(false);

                // Get the position of the item where it is dragged
                finalFromPosition = position;
            }
            @Override
            public void onItemDragging(int itemPosition, float x, float y) {
                super.onItemDragging(itemPosition, x, y);
                // Get the position of the item where it is dropped
                finalToPosition = itemPosition;
            }
            @Override
            public void onItemDragEnded(int fromPosition, int toPosition) {
                dataRecyclerView.setDragEnabled(true);
                DataModel initialFromDataModel = tempDataList.get(fromPosition);

                ExecutorService executorService = Executors.newSingleThreadExecutor();
                if (fromPosition < toPosition) {
                    for (int i = fromPosition; i < toPosition; i++) {
                        DataModel fromDataModel = tempDataList.get(i);
                        DataModel toDataModel = tempDataList.get(i + 1);

                        // Submit tasks to the executor service
                        executorService.submit(() -> {
                            // update new position
                            dataModelDao.updateDataModelById(fromDataModel.getId(),
                                    toDataModel.getCurrencyId(),
                                    toDataModel.getCurrencyCode(), toDataModel.getCurrentDate(),
                                    toDataModel.getColorCode(),
                                    toDataModel.getBuyPrice(), toDataModel.getBuyQuantity(),
                                    toDataModel.getSellQuantity(), toDataModel.getSellPrice(),
                                    toDataModel.getPayment(), toDataModel.getSale(),
                                    toDataModel.getEarning(), toDataModel.getChecked());

                            // update old position
                            dataModelDao.updateDataModelById(toDataModel.getId(),
                                    fromDataModel.getCurrencyId(),
                                    fromDataModel.getCurrencyCode(), fromDataModel.getCurrentDate(),
                                    fromDataModel.getColorCode(),
                                    fromDataModel.getBuyPrice(), fromDataModel.getBuyQuantity(),
                                    fromDataModel.getSellQuantity(), fromDataModel.getSellPrice(),
                                    fromDataModel.getPayment(), fromDataModel.getSale(),
                                    fromDataModel.getEarning(),
                                    fromDataModel.getChecked());
                        });
                    }
                } else {
                    for (int i = fromPosition; i > toPosition; i--) {
                        DataModel fromDataModel = tempDataList.get(i);
                        DataModel toDataModel = tempDataList.get(i - 1);
                        // Submit tasks to the executor service
                        executorService.submit(() -> {
                            // update new position
                            dataModelDao.updateDataModelById(fromDataModel.getId(),
                                    toDataModel.getCurrencyId(),
                                    toDataModel.getCurrencyCode(), toDataModel.getCurrentDate(),
                                    toDataModel.getColorCode(),
                                    toDataModel.getBuyPrice(), toDataModel.getBuyQuantity(),
                                    toDataModel.getSellQuantity(), toDataModel.getSellPrice(),
                                    toDataModel.getPayment(), toDataModel.getSale(),
                                    toDataModel.getEarning(), toDataModel.getChecked());

                            // update old position
                            dataModelDao.updateDataModelById(toDataModel.getId(),
                                    fromDataModel.getCurrencyId(),
                                    fromDataModel.getCurrencyCode(), fromDataModel.getCurrentDate(),
                                    fromDataModel.getColorCode(),
                                    fromDataModel.getBuyPrice(), fromDataModel.getBuyQuantity(),
                                    fromDataModel.getSellQuantity(), fromDataModel.getSellPrice(),
                                    fromDataModel.getPayment(), fromDataModel.getSale(),
                                    fromDataModel.getEarning(),
                                    fromDataModel.getChecked());
                        });
                    }
                }

                // After the loop, update the toDataModel with initial fromDataModel
                DataModel toDataModel = tempDataList.get(toPosition);
                executorService.submit(() -> {
                    dataModelDao.updateDataModelById(toDataModel.getId(),
                            initialFromDataModel.getCurrencyId(),
                            initialFromDataModel.getCurrencyCode(),
                            initialFromDataModel.getCurrentDate(),
                            initialFromDataModel.getColorCode(),
                            initialFromDataModel.getBuyPrice(),
                            initialFromDataModel.getBuyQuantity(),
                            initialFromDataModel.getSellQuantity(),
                            initialFromDataModel.getSellPrice(),
                            initialFromDataModel.getPayment(),
                            initialFromDataModel.getSale(),
                            initialFromDataModel.getEarning(),
                            initialFromDataModel.getChecked());
                });

                executorService.submit(() -> {
                    // Perform background tasks here
                    new Handler(Looper.getMainLooper()).post(() -> {
                        // Update UI on the main thread
                        specificCurrencyListUpdate(selectedCurrencyCode);
                        tempDataListUpdate(selectedCurrencyCode);
                    });
                });



                executorService.shutdown();
                try {
                    executorService.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                dataAdapter.notifyDataSetChanged();
                currencyAdapter.notifyDataSetChanged();
                // Scroll to the dropped item's position
                dataRecyclerView.getRecyclerView().scrollToPosition(toPosition);
                miniBtnsOpen();

            }
        });

        setCurrencyAdapter();
        setDataAdapter();

        currencyRecyclerView.setCanDragHorizontally(true);
        currencyRecyclerView.setCanDragVertically(true);
        currencyRecyclerView.setCustomDragItem(new DragItem(this, R.layout.currencyitem));

        dataRecyclerView.setCanDragHorizontally(true);
        dataRecyclerView.setCanDragVertically(true);
        dataRecyclerView.setCustomDragItem(new DragItem(this, R.layout.column_recyclerview_new));

        // done btn
        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                miniLayoutCheckedClear();
                miniBtnsClose();
            }
        });
        // mini buttons top currency
        btn_new_currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewCurrencyActivity();
            }
        });
        btn_delete_currency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedCurrencyCode == null){
                    showToast("No currency selected");
                } else {
                    String message = String.format(Locale.getDefault(),
                            "Are you sure you want to delete %s and Its Data?",
                            selectedCurrencyCode);
                    showDeleteConfirmationDialog(message, true);
                }
            }
        });
        // mini buttons bottom data item and new data
        btn_delete_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkedDataModelList.size() == 0){
                    showToast("No item selected");
                } else {
                    String message = "Are you sure you want to delete " +
                            checkedDataModelList.size() + " items?";
                    showDeleteConfirmationDialog(message, false);
                }
            }
        });
        btn_color_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkedDataModelList.size() == 0){
                    showToast("No item selected");
                }
                else {
                    if(isAllFAbsVisible){
                        isMiniDataFabOpen = true;
                    } else {
                        isMiniDataFabOpen = false;
                    }
                    Log.d(TAG, "onClick: " + "isMiniCurrencyFabOpen" + isMiniDataFabOpen);
                    ColorChangeFragment fragment = new ColorChangeFragment(checkedDataModelList);
                    fragment.setBottomSheetListener(new ColorChangeFragment.BottomSheetListener() {
                        @Override
                        public void onColorChanged() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    for(DataModel checkDataModel : checkedDataModelList){
                                        checkDataModel.setChecked(false);
                                        for(DataModel mainDataModel : dataList){
                                            if(checkDataModel.getId() == mainDataModel.getId()){
                                                mainDataModel.setColorCode(checkDataModel.getColorCode());
                                            }
                                        }
                                    }

                                    dataAdapter.notifyDataSetChanged();
                                    currencyAdapter.notifyDataSetChanged();

                                    miniLayoutCheckedClear();
                                }
                            });
                        }
                    });
                    fragment.show(getSupportFragmentManager(), "ColorChangeFragment");
                }
            }
        });
        btn_calculate_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkedDataModelList.size() == 0){
                    showToast("No item selected");

                } else {
                    if(isAllFAbsVisible){
                        isMiniDataFabOpen = true;
                    } else {
                        isMiniDataFabOpen = false;
                    }
                    CalculateFragment fragment = new CalculateFragment(checkedDataModelList);
                    fragment.show(getSupportFragmentManager(), "CalculateFragment");
                    fragment.setBottomSheetListener(new CalculateFragment.BottomSheetListener() {
                        @Override
                        public void onCalculate() {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    // Update UI here
                                    dataAdapter.notifyDataSetChanged();
                                    currencyAdapter.notifyDataSetChanged();
                                    miniLayoutCheckedClear();
                                }
                            });
                        }
                    });
                    for (DataModel dataModel : checkedDataModelList) {
                        dataModel.setChecked(false);
                    }

                    dataAdapter.notifyDataSetChanged();
                    currencyAdapter.notifyDataSetChanged();
                }
            }
        });
        newdata.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(selectedCurrencyCode == null){
                    showToast("No currency selected");
                } else {
                    loadNewDataActivity();
                }
            }
        });

    }

    private void handlerCallBack(){
        handler.removeCallbacksAndMessages(null);
        isHandlerRunning = false;
        updateTimeDifference();
    }

    private void showSnackBar(String message){
        Snackbar snackbar = Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_INDEFINITE);

        LayoutInflater inflater = LayoutInflater.from(snackbar.getContext());
        View customSnackbarView = inflater.inflate(R.layout.custom_snackbar_layout, null);

        snackbar.getView().setBackgroundColor(Color.TRANSPARENT);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
        snackbarLayout.addView(customSnackbarView);

        TextView textView = customSnackbarView.findViewById(R.id.snackbar_text);
        Button retryButton = customSnackbarView.findViewById(R.id.snackbar_retry_button);

        textView.setText(message);
        retryButton.setOnClickListener(v -> {
            getLivePrice();
            snackbar.dismiss();
        });

        snackbar.show();

    }
    private void showLiveRate() {
        handlerCallBack();
        refresh.clearAnimation();
        String retrievedValue = sharedPrefs.getString(selectedCurrencyCode+"Price", "00.00");
        rate.setText("$"+retrievedValue);

        updateTimeDifference();

        if (retrievedValue.equals("00.00")){
            getLivePrice();
        }else{
            rate.setText("$"+retrievedValue);
        }
    }

    private void updateTimeDifference() {
        if (!isHandlerRunning) {
            handler.post(new Runnable() {
                @Override
                public void run() {
                    String timeValue = sharedPrefs.getString(selectedCurrencyCode+"time", "0");
                    String timeDifference = getTimeDifference(Long.parseLong(timeValue));
                    if (timeValue.equals("0")){
                        lastUpdated.setVisibility(View.INVISIBLE);
                    }else {
                        lastUpdated.setVisibility(View.VISIBLE);
                    }
                    lastUpdated.setText("Last Updated | " + timeDifference);
                    handler.postDelayed(this, 1000);
                }
            });
            isHandlerRunning = true;
        }
    }



    OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
        @Override
        public void handleOnBackPressed() {
            if(isAllFAbsVisible){
                miniLayoutCheckedClear();
                miniBtnsClose();
            } else {
                showExitConfirmationDialog();
            }
        }
    };
    private void miniBtnsClose(){

        // animation on mini buttons
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_down);
        mini_buttons_layout.startAnimation(animation);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mini_buttons_layout.setVisibility(View.GONE);
            }
        }, 300);
        btn_done.setVisibility(View.GONE);
        isAllFAbsVisible = false;
        dataAdapter.setCheckBoxVisibility(false);
        isMiniDataFabOpen = false;
        dataAdapter.setCurrentPositionEdit(currentPositionEdit);
        dataAdapter.setMiniDataFabOpen(isMiniDataFabOpen);
    }
    private void miniBtnsOpen(){
        // animation on mini buttons
        Animation animation = android.view.animation.AnimationUtils.loadAnimation(this, R.anim.slide_up);
        mini_buttons_layout.startAnimation(animation);
        mini_buttons_layout.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.VISIBLE);
        isAllFAbsVisible = true;
        isMiniDataFabOpen = true;
        dataAdapter.setCurrentPositionEdit(currentPositionEdit);
        dataAdapter.setCheckBoxVisibility(true);
        dataAdapter.setMiniDataFabOpen(isMiniDataFabOpen);
    }

    @Override
    protected void onResume() {
        super.onResume();
        currencyListWithFirstData();
        if(checkedDataModelList.size() > 0){
            miniBtnsOpen();
        } else {
            miniBtnsClose();
        }
    }

    private void miniLayoutCheckedClear(){
        checkedDataModelList.clear();
        checkedDataModelList.size();
    }

    private void showDeleteConfirmationDialog(String message, Boolean iscurrency) {
        // Create a custom dialog
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_delete_confirmation);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        TextView dialogMessage = dialog.findViewById(R.id.subtitleText);
        RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
        RelativeLayout btnDelete = dialog.findViewById(R.id.btnDelete);
        dialogMessage.setText(message);

        // Set click listeners for buttons
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        if(!iscurrency){
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isAllFAbsVisible){
                        isMiniDataFabOpen = true;
                    } else {
                        isMiniDataFabOpen = false;
                    }
                    Log.d(TAG, "onClick: " + "isMiniCurrencyFabOpen" + isMiniDataFabOpen);
                    deleteDatabaseData();
                    dialog.dismiss();

                    for (DataModel checkDataModel : checkedDataModelList) {
                        checkDataModel.setChecked(false);

                        Iterator<DataModel> iterator = dataList.iterator();
                        while (iterator.hasNext()) {
                            DataModel mainDataModel = iterator.next();
                            if (checkDataModel.getId() == mainDataModel.getId()) {
                                iterator.remove();
                            }
                        }
                    }
                    Log.d(TAG, "showDeleteConfirmationDialog: " + "dataList " + dataList.size());

                    dataAdapter.notifyDataSetChanged();
                    currencyAdapter.notifyDataSetChanged();
                    miniLayoutCheckedClear();
                }
            });
        } else {
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteCurrency();
                    dialog.dismiss();
                    selectedCurrencyCode = null;
                    currencyList.clear();
                    dataList.clear();
                    miniLayoutCheckedClear();
                    currencyListWithFirstData();
                    dataAdapter.notifyDataSetChanged();
                    currencyAdapter.notifyDataSetChanged();
                }
            });
        }

        // Show the dialog
        dialog.show();
    }
    private void deleteCurrency(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (CurrencyModel currencyModel : currencyList) {
                    if(currencyModel.isChecked()){
                        dataModelDao.deleteCurrencyModel(currencyModel);
                        dataModelDao.updateLiveCheckedAndBookmarkedStatus(currencyModel.getCurrencyFullName(),false,false);
                    }
                }
            }
        }).start();
    }
    private void deleteDatabaseData(){
        ExecutorService executorService = Executors.newSingleThreadExecutor();
        for (DataModel dataModel : checkedDataModelList) {
            executorService.submit(() -> {
                dataModelDao.deleteDataModel(dataModel);
            });
        }
        executorService.shutdown();
    }
    private void setCurrencyAdapter() {
        currencyAdapter = new CurrencyAdapter(
                (ArrayList<CurrencyModel>) currencyList,
                R.layout.currencyitem,
                R.id.rv_currencyCardView,
                true);

        currencyRecyclerView.setAdapter(currencyAdapter, true);
        currencyAdapter.setOnCurrencyItemClickListener(this);
    }
    private void setDataAdapter(){
        dataAdapter = new DataAdapter((ArrayList<DataModel>) dataList,
                R.layout.column_recyclerview_new,
                R.id.item_layout,
                true, this, isMiniDataFabOpen, recyclerView, (ArrayList<DataModel>) checkedDataModelList);
        dataRecyclerView.setAdapter(dataAdapter, true);
        dataAdapter.setOnDataItemClickListener(this);
    }

    private void totalCryptoUpdate(String selectedCurrencyCode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Fetch the list of currencies from the database
                double totalCrypto = Double.parseDouble(String.format(Locale.getDefault(),
                        "%.3f",
                        dataModelDao.getTotalRemainingQuantity(selectedCurrencyCode)));
                double totalEarning = Double.parseDouble(String.format(Locale.getDefault(),
                        "%.3f",
                        dataModelDao.getTotalEarningsForCurrency(selectedCurrencyCode)));

                DecimalFormat decimalFormat = new DecimalFormat("#.###");
                String formattedTotalCrypto = decimalFormat.format(totalCrypto);
                String formattedTotalEarning = decimalFormat.format(totalEarning);

                // Update the UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        total_crypto_tv.setText(formattedTotalCrypto + " " + selectedCurrencyCode);
                        total_earning_tv.setText("$ " + formattedTotalEarning);
                        if(selectedCurrencyCode == null){
                            total_crypto_tv.setText("0.0");
                            total_earning_tv.setText("0.0");
                        }
                    }
                });
            }
        }).start();
    }
    private void currencyListWithFirstData() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Fetch the list of currencies from the database
                currencyList.clear();
                currencyList.addAll(dataModelDao.getAllCurrencyModels());

                // Update the UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (CurrencyModel currencyModel : currencyList) {
                            if (currencyModel.isChecked()){
                                specificCurrencyListUpdate(currencyModel.getCurrencyShortcut());
                                selectedCurrencyCode = currencyModel.getCurrencyShortcut();
                                handlerCallBack();
                                showLiveRate();
                                tempDataListUpdate(selectedCurrencyCode);
                                totalCryptoUpdate(selectedCurrencyCode);
                                currencyAdapter.notifyDataSetChanged();
                                return;
                            }
                        }

                        if(selectedCurrencyCode == null && currencyList.size() > 0){
                            currencyList.get(0).setChecked(true);
                            specificCurrencyListUpdate(currencyList.get(0).getCurrencyShortcut());
                            selectedCurrencyCode = currencyList.get(0).getCurrencyShortcut();
                            handlerCallBack();
                            showLiveRate();
                            tempDataListUpdate(selectedCurrencyCode);
                            totalCryptoUpdate(selectedCurrencyCode);
                            currencyAdapter.notifyDataSetChanged();
                        } else if (currencyList.size() > 0){
                            for(CurrencyModel currencyModel : currencyList){
                                if(currencyModel.getCurrencyShortcut().equals(selectedCurrencyCode)){
                                    currencyModel.setChecked(true);
                                }
                            }
                            specificCurrencyListUpdate(selectedCurrencyCode);
                            tempDataListUpdate(selectedCurrencyCode);
                            totalCryptoUpdate(selectedCurrencyCode);

                            currencyAdapter.notifyDataSetChanged();
                        }else if (currencyList.size() == 0){
                            selectedCurrencyCode = null;
                            handlerCallBack();
                            showLiveRate();
                            specificCurrencyListUpdate(selectedCurrencyCode);
                            tempDataListUpdate(selectedCurrencyCode);
                            totalCryptoUpdate(selectedCurrencyCode);
                            currencyAdapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        }).start();
    }

    private void getLivePrice(){
        if (NetworkUtils.isNetworkAvailable(this)) {
            if (selectedCurrencyCode!=null){
                // Execute the AsyncTask in your MainActivity
                refresh.startAnimation(AnimationUtils.loadAnimation(this, R.anim.rotation));

                String accessKey = "dca4a4de-ea6c-4968-8740-04f75852cb50";

                FetchSingleCoinDataTask convertCurrencyTask = new FetchSingleCoinDataTask(accessKey, selectedCurrencyCode, new FetchSingleCoinDataTask.CoinFetchListener() {
                    @Override
                    public void onCoinFetchComplete(CoinModel coin) {
                        if (coin!=null){
                            sharedPrefs.saveString(selectedCurrencyCode + "Price", String.valueOf(coin.getRate()));
                            sharedPrefs.saveString(selectedCurrencyCode + "time", String.valueOf(System.currentTimeMillis()));
                            handlerCallBack();
                            showLiveRate();
                        }
                    }

                    @Override
                    public void onCoinFetchError(Exception e) {
                        showSnackBar(getString(R.string.ooops_an_error_occurred));
                    }
                });

                // Execute the AsyncTask
                convertCurrencyTask.execute();
            }
        }else{
            showSnackBar(getString(R.string.please_connect_to_an_active_internet_connection));
        }
    }

    private void specificCurrencyListUpdate(String currencyCode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                List<DataModel> data = dataModelDao.getSpecificCurrencyDataModels(currencyCode);
                // Update the UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dataList.clear();
                        dataList.addAll(data);
                        if (dataList.size()==0){
                            findViewById(R.id.noData).setVisibility(View.VISIBLE);
                            layout_data_header.setVisibility(View.GONE);
                        }else{
                            findViewById(R.id.noData).setVisibility(View.GONE);
                            layout_data_header.setVisibility(View.VISIBLE);
                        }
                        dataAdapter.notifyDataSetChanged();
                    }
                });
            }
        }).start();
    }
    private void tempDataListUpdate(String currencyCode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                // Fetch the list of currencies from the database
                List<DataModel> data = dataModelDao.getSpecificCurrencyDataModels(currencyCode);

                // Update the UI on the main thread
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tempDataList.clear();
                        tempDataList.addAll(data);
                    }
                });
            }
        }).start();
    }
    private void loadNewDataActivity() {
        Log.d(TAG, "loadNewDataActivity: Selected Currency Code: " + selectedCurrencyCode);
        Intent intent = new Intent(this, NewDataActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("selectedCurrencyCode", selectedCurrencyCode);
        intent.putExtras(bundle);
        startActivity(intent);
    }
    private void loadNewCurrencyActivity() {
        Intent intent = new Intent(this, AddCurrencyActivity.class);
        startActivity(intent);
    }

    @Override
    public void onCurrencyItemClick(CurrencyModel currencyModel) {
        Log.d(TAG, "onCurrencyItemClick: " + "Item Clicked");
        specificCurrencyListUpdate(currencyModel.getCurrencyShortcut());
        selectedCurrencyCode = currencyModel.getCurrencyShortcut();
        handlerCallBack();
        showLiveRate();
        tempDataListUpdate(selectedCurrencyCode);
        totalCryptoUpdate(selectedCurrencyCode);
        miniLayoutCheckedClear();
    }

    @Override
    public void onDataChange(List<DataModel> dataList) {
        this.dataList = dataList;
        this.tempDataList = dataList;
        totalCryptoUpdate(selectedCurrencyCode);
        dataAdapter.notifyDataSetChanged();

    }
    @Override
    public void onItemCheckboxClicked(List<DataModel> dataList, CheckBox checkBox, int position) {
        boolean isChecked = checkBox.isChecked();
        if(isChecked){
            dataList.get(position).setChecked(true);
            this.dataList = dataList;
            checkedDataModelList.add(dataList.get(position));
            dataAdapter.notifyDataSetChanged();
        } else {
            dataList.get(position).setChecked(false);
            this.dataList = dataList;
            checkedDataModelList.remove(dataList.get(position));
            dataAdapter.notifyDataSetChanged();
        }

        if(isAllFAbsVisible && checkedDataModelList.size() == 0){
            miniBtnsClose();
        } else if(!isAllFAbsVisible && checkedDataModelList.size() > 0){
            miniBtnsOpen();
        }

        dataRecyclerView.getRecyclerView().scrollToPosition(position);
    }


    private void showExitConfirmationDialog() {
        // Create a custom dialog
        Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_exit_confirmation);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true);

        RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
        RelativeLayout btnClose = dialog.findViewById(R.id.btnDelete);

        // Set click listeners for buttons
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        // Show the dialog
        dialog.show();
    }

    private void showToast(String message) {
        // Cancel the current toast if it exists

        snackbar = Snackbar.make(findViewById(android.R.id.content),message, Toast.LENGTH_SHORT);
        snackbar.show();
    }
}