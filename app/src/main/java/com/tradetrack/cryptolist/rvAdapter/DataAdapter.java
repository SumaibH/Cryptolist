package com.tradetrack.cryptolist.rvAdapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.woxthebox.draglistview.DragItemAdapter;
import com.woxthebox.draglistview.DragItemRecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.tradetrack.cryptolist.R;
import com.tradetrack.cryptolist.dataBase.AppDatabase;
import com.tradetrack.cryptolist.dataBase.DataModelDao;
import com.tradetrack.cryptolist.dataModel.DataModel;

public class DataAdapter extends DragItemAdapter<DataModel, DataAdapter.ViewHolder> {

    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private static int currentPositionEdit = -1;
    private boolean isDragStarted = false;
    boolean isMiniDataFabOpen = false;
    private boolean isCheckBoxVisible = false;
    private final DragItemRecyclerView mDragItemRecyclerView;
    private List<DataModel> dataList;
    private List<DataModel> checkedDataModelList;
    DataModelDao dataModelDao;
    Context context;
    private AppDatabase db;
    private OnDataItemClickListener onDataItemClickListener;
    private static final String TAG = "DataAdapter";

    public void setMiniDataFabOpen(boolean isMiniDataFabOpen) {
        this.isMiniDataFabOpen = isMiniDataFabOpen;
        notifyDataSetChanged();
    }

    public void setCheckBoxVisibility(boolean visible) {
        this.isCheckBoxVisible = visible;
        notifyDataSetChanged();

    }

    public void setCurrentPositionEdit(int currentPositionEdit) {
        this.currentPositionEdit = currentPositionEdit;
        notifyDataSetChanged();
    }

    public interface OnDataItemClickListener {
        void onDataChange(List<DataModel> dataList);
        void onItemCheckboxClicked(List<DataModel> dataList, CheckBox checkBox, int position);
    }

    public void setOnDataItemClickListener(OnDataItemClickListener listener) {
        this.onDataItemClickListener = listener;
    }

    public DataAdapter(ArrayList<DataModel> list, int layoutId, int grabHandleId, boolean dragOnLongPress, Context context, Boolean isMiniDataFabOpen, DragItemRecyclerView dragItemRecyclerView, ArrayList<DataModel> checkedDataModelList) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        dataList = list;
        this.context = context;
        this.isMiniDataFabOpen = isMiniDataFabOpen;
        mDragItemRecyclerView = dragItemRecyclerView;
        this.checkedDataModelList = checkedDataModelList;
        setItemList(dataList);
    }


    @Override
    public long getUniqueItemId(int position) {
        return getItemList().get(position).hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        db = AppDatabase.getInstance(parent.getContext());
        dataModelDao = db.dataModelDao();
        return new ViewHolder(view, mGrabHandleId, mDragOnLongPress);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        DataModel data = getItemList().get(position);

        String buyPrice = formatDouble(data.getBuyPrice());
        String buyQuantity = formatDouble(data.getBuyQuantity());
        String sellPrice = formatDouble(data.getSellPrice());
        String sellQuantity = formatDouble(data.getSellQuantity());
        String payment = formatDouble(data.getPayment());
        String sale = formatDouble(data.getSale());
        String earning = formatDouble(data.getEarning());
        holder.mDate.setText(data.getCurrentDate());
        holder.mBuyPrice.setText(buyPrice);
        holder.buyQuantity.setText(buyQuantity);
        holder.sellQuantity.setText(sellQuantity);
        holder.sellPrice.setText(sellPrice);
        holder.payment.setText(payment);
        holder.sale.setText(sale);
        holder.earning.setText(earning);
        int color = Color.parseColor(data.getColorCode());
        holder.cardView.setCardBackgroundColor(color);
        holder.checkBox.setOnClickListener(holder.checkBoxListener);
        holder.checkBox.setChecked(data.getChecked());
        holder.visibleCheckBox(); // Call the updated method

        if(checkedDataModelList.size() > 0) {
            for (int i = 0; i < checkedDataModelList.size(); i++) {
                if (checkedDataModelList.get(i).getId() == data.getId()) {
                    holder.checkBox.setChecked(true);
                }
            }
        }

        holder.mBuyPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(holder.mBuyPrice, data, "Edit Buy Price", "Enter new price here", "Buy Price", data.getBuyPrice(), position);
            }
        });
        holder.buyQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(holder.buyQuantity, data, "Edit Buy Quantity", "Enter new quantity here", "Buy Quantity", data.getBuyQuantity(), position);
            }
        });
        holder.sellQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(holder.sellQuantity, data, "Edit Sell Quantity", "Enter new quantity here", "Sell Quantity", data.getSellQuantity(), position);
            }
        });
        holder.sellPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInputDialog(holder.sellPrice, data, "Edit Sell Price", "Enter new price here", "Sell Price", data.getSellPrice(), position);
            }
        });

    }

    public class ViewHolder extends DragItemAdapter.ViewHolder {
        public TextView mDate, mBuyPrice, buyQuantity, sellQuantity, sellPrice, payment, sale, earning;
        public CheckBox checkBox;
        MaterialCardView cardView;
        private static final String TAG = "ViewHolder";
        public ViewHolder(final View itemView, int grabHandleId, boolean dragOnLongPress) {
            super(itemView, grabHandleId, dragOnLongPress);
            mDate = itemView.findViewById(R.id.header_date);
            mBuyPrice = itemView.findViewById(R.id.header_buyprice);
            buyQuantity = itemView.findViewById(R.id.header_buyquantity);
            sellQuantity = itemView.findViewById(R.id.header_sellquantity);
            sellPrice = itemView.findViewById(R.id.header_sellprice);
            payment = itemView.findViewById(R.id.header_payment);
            sale = itemView.findViewById(R.id.header_sale);
            earning = itemView.findViewById(R.id.header_earning);
            checkBox = itemView.findViewById(R.id.checkbox);
            cardView = itemView.findViewById(R.id.card_view);
        }
        View.OnClickListener checkBoxListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDataItemClickListener != null) {
                    int position = getAdapterPosition();
                    DataModel data = getItemList().get(position);
                    onDataItemClickListener.onItemCheckboxClicked(getItemList(), checkBox, position);
                }
            }
        };



        private void visibleCheckBox() {
            if (isCheckBoxVisible || isMiniDataFabOpen) {
                checkBox.setVisibility(View.VISIBLE);
            } else {
                checkBox.setVisibility(View.GONE);
            }
        }

    }

    private static String formatDouble(String input) {
        if(input == null || input.isEmpty() || input.equals(".")) {
            return "0.00";
        } else if (input.startsWith("0.")){
            double value = Double.parseDouble(input);
            String formattedValue = String.format(Locale.getDefault(), "%.8f", value);

            // Remove trailing zeros after the decimal point
            formattedValue = formattedValue.replaceAll("\\.?0*$", "");

            return formattedValue;
        }else {
            double value = Double.parseDouble(input);
            String formattedValue = String.format(Locale.getDefault(), "%.3f", value);

            // Remove trailing zeros after the decimal point
            formattedValue = formattedValue.replaceAll("\\.?0*$", "");

            return formattedValue;
        }
    }

    private void showInputDialog(final TextView textView, final DataModel data, String title, String message, String hint, String originalValue, int pos) {
        // Create a Dialog
        Dialog dialog = new Dialog(textView.getContext());

        // Set the custom layout for the dialog
        dialog.setContentView(R.layout.edittext_dialog_layout);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawableResource(R.color.transparent);
        dialog.getWindow().setLayout(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        // Find views in the custom layout
        TextView dialogTitle = dialog.findViewById(R.id.dialogTitle);
        TextView dialogMessage = dialog.findViewById(R.id.dialogMessage);
        TextInputLayout input = dialog.findViewById(R.id.editText);
        RelativeLayout btnCancel = dialog.findViewById(R.id.btnCancel);
        RelativeLayout btnOk = dialog.findViewById(R.id.btnOk);

        // Set values for views
        dialogTitle.setText(title);
        dialogMessage.setText(message);
        input.setHint(hint);
        if(originalValue != null) {
            Objects.requireNonNull(input.getEditText()).setText(originalValue);
        }



        // Set click listener for the OK button
        btnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User clicked OK button
                String value = Objects.requireNonNull(input.getEditText()).getText().toString().trim(); // Get the value from the EditText
                if(value.isEmpty() ||
                        value.equals(".") ||
                        value.equals(" ") ||
                        value.equals("0.") ||
                        value.equals("0 ") ||
                        value.equals("0") ||
                        value.equals("0.0") ||
                        value.equals(".0") ||
                        value.equals(".00")) {
                    value = "0";
                }

                if(textView.getId() == R.id.header_buyprice) {
                    data.setBuyPrice(value);
                } else if(textView.getId() == R.id.header_buyquantity) {
                    data.setBuyQuantity(value);
                } else if(textView.getId() == R.id.header_sellquantity) {
                    data.setSellQuantity(value);
                } else if(textView.getId() == R.id.header_sellprice) {
                    data.setSellPrice(value);
                }

                textView.setText(value);
                // Update the database
                ExecutorService executor = Executors.newSingleThreadExecutor();

                // Execute a task in the background thread.
                executor.execute(new Runnable() {
                    @Override
                    public void run() {
                        updateDatabase(calculateResults(data));
                        getItemList().set(pos, data);

                        // callback on the UI thread
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if (onDataItemClickListener != null) {
                                    onDataItemClickListener.onDataChange(getItemList());
                                }
                            }
                        });
                    }
                });
                //must shutdown executor
                executor.shutdown();

                dialog.dismiss();
            }
        });
        // Set click listener for the Cancel button
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // User clicked Cancel button
                dialog.dismiss();
            }
        });
        // Show the dialog
        dialog.show();
    }

    private void updateDatabase(DataModel data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run: " + data.getId() + " " + data.getBuyPrice() + " "
                        + data.getBuyQuantity() + " " + data.getSellQuantity() + " " + data.getSellPrice()
                        + " " + data.getPayment() + " " + data.getSale() + " " + data.getEarning() + " " + data.getColorCode()
                        + " " + data.getCurrentDate() + " " + data.getChecked() + "");

                dataModelDao.updateDataModel(data);
            }
        }).start();
    }

    private DataModel calculateResults(DataModel data) {
        double buyPrice = parseDouble(data.getBuyPrice());
        double buyQuantity = parseDouble(data.getBuyQuantity());
        double sellPrice = parseDouble(data.getSellPrice());
        double sellQuantity = parseDouble(data.getSellQuantity());

        double totalBuyPrice = buyPrice * buyQuantity;
        double totalEarning = 0;
        double totalSalePrice = 0;
        if (sellQuantity==0){
            totalEarning = 0;
        }else{
            totalSalePrice = sellPrice * sellQuantity;
            if (totalSalePrice>totalBuyPrice){
                totalEarning = totalSalePrice - totalBuyPrice;
            }else {
                totalEarning = 0;
            }
        }

        data.setPayment(String.valueOf(totalBuyPrice));
        data.setSale(String.valueOf(totalSalePrice));

        data.setEarning(String.valueOf(totalEarning));

        return data;
    }

    private double parseDouble(String text) {
        try {
            if (text == null || text.isEmpty())
                return 0.0;
            if (text.contains(",")) {
                text = text.replace(",", ".");
            }
            if(text.contains(" ")){
                text = text.replace(" ", "");
            }
            if(text == "."){
                text = "0.";
            }
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
