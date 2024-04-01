package com.tradetrack.cryptolist.rvAdapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.google.android.material.card.MaterialCardView;
import com.woxthebox.draglistview.DragItemAdapter;

import java.util.List;

import com.tradetrack.cryptolist.R;
import com.tradetrack.cryptolist.dataBase.AppDatabase;
import com.tradetrack.cryptolist.dataBase.DataModelDao;
import com.tradetrack.cryptolist.dataModel.CurrencyModel;

public class CurrencyAdapter extends DragItemAdapter<CurrencyModel, CurrencyAdapter.ViewHolder> {
    private static final String TAG = "CurrencyAdapter";
    private int mLayoutId;
    private int mGrabHandleId;
    private boolean mDragOnLongPress;
    private int selectedPosition = -1;
    private OnCurrencyItemClickListener onCurrencyItemClickListener;
    private DataAdapter dataAdapter;
    List<CurrencyModel> currencyList;
    boolean isChecked = false;

    public interface OnCurrencyItemClickListener {
        void onCurrencyItemClick(CurrencyModel currencyModel);
    }
    public void setOnCurrencyItemClickListener(OnCurrencyItemClickListener listener) {
        this.onCurrencyItemClickListener = listener;
    }
    public CurrencyAdapter(List<CurrencyModel> currencyList, int layoutId, int grabHandleId, boolean dragOnLongPress) {
        mLayoutId = layoutId;
        mGrabHandleId = grabHandleId;
        mDragOnLongPress = dragOnLongPress;
        this.currencyList = currencyList;
        setItemList(currencyList);
    }

    @Override
    public long getUniqueItemId(int position) {
        // Return a unique ID for each item
        return getItemList().get(position).hashCode();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(android.view.ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(mLayoutId, parent, false);
        ViewHolder viewHolder = new ViewHolder(view, mGrabHandleId, mDragOnLongPress);
        this.isChecked = false;
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        super.onBindViewHolder(holder, position);
        // Bind your data to the view
        CurrencyModel currency = getItemList().get(position);
        String currencyShortcut = currency.getCurrencyShortcut();
        holder.mCurrencyShortcut.setText(currencyShortcut);

        if(currencyList.get(holder.getAdapterPosition()).isChecked()) {
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
        } else {
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.windowBackground));
        }

        // Set the background color based on the selected position
        if (currency.isChecked()) {
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.colorPrimary));
            holder.mCurrencyShortcut.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.windowBackground));
        } else {
            holder.mCardView.setCardBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.windowBackground));
            holder.mCurrencyShortcut.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.black));
        }
    }

    public class ViewHolder extends DragItemAdapter.ViewHolder {
        public TextView mCurrencyShortcut;
        public MaterialCardView mCardView;
        public ViewHolder(View itemView, int grabHandleId, boolean dragOnLongPress) {
            super(itemView, grabHandleId, dragOnLongPress);
            mCurrencyShortcut = itemView.findViewById(R.id.currencyshortname);
            mCardView = itemView.findViewById(R.id.currencyCardView);
        }

        @Override
        public void onItemClicked(View view) {
            // Get the position of the clicked item
            int position = getAdapterPosition();
            Log.d(TAG, "Item Clicked On: " + position);
            CurrencyModel currency = getItemList().get(position);

            // Call the onCurrencyItemClick method of the onCurrencyItemClickListener interface
            if (onCurrencyItemClickListener != null) {
                onCurrencyItemClickListener.onCurrencyItemClick(currency);
            }
            selectedPosition = position;
            for (CurrencyModel currencyModel : currencyList) {
                currencyModel.setChecked(false);
                updateDataBased(currencyModel, false);
            }
            updateDataBasedOnCheckedState(currency, true);
            notifyDataSetChanged();
        }

        @Override
        public boolean onItemLongClicked(View view) {
            return true;
        }

        private void updateDataBasedOnCheckedState(CurrencyModel currency, Boolean isChecked) {
            // Update the isChecked state of the currency
            currency.setChecked(isChecked);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Update the currency in the database
                    AppDatabase db = AppDatabase.getInstance(itemView.getContext());
                    DataModelDao dataModelDao = db.dataModelDao();
                    dataModelDao.updateCurrencyModel(currency);
                }
            }).start();
        }
        private void updateDataBased(CurrencyModel currency, Boolean isChecked) {
            // Update the isChecked state of the currency
            currency.setChecked(isChecked);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // Update the currency in the database
                    AppDatabase db = AppDatabase.getInstance(itemView.getContext());
                    DataModelDao dataModelDao = db.dataModelDao();
                    dataModelDao.updateCurrencyModel(currency);
                }
            }).start();
        }
    }

}
