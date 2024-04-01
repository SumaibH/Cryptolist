package com.tradetrack.cryptolist.rvAdapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.tradetrack.cryptolist.R;
import com.tradetrack.cryptolist.dataModel.LiveCurrencyModel;
import com.squareup.picasso.Picasso;

import java.util.List;

public class CoinAdapter extends RecyclerView.Adapter<CoinAdapter.CoinViewHolder> {
    private List<LiveCurrencyModel> coinList;

    private CoinViewHolder.OnBookmarkClickListener bookmarkClickListener;
    public void setOnBookmarkClickListener(CoinViewHolder.OnBookmarkClickListener listener) {
        bookmarkClickListener = listener;
    }

    private Context context;

    public CoinAdapter(Context context, List<LiveCurrencyModel> coinList) {
        this.context = context;
        this.coinList = coinList;
    }

    @NonNull
    @Override
    public CoinViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.currency_item_view, parent, false);
        CoinViewHolder viewHolder = new CoinViewHolder(view);

        // Pass the click listener to the ViewHolder when it's created
        viewHolder.setOnBookmarkClickListener(bookmarkClickListener);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull CoinViewHolder holder, int position) {
        LiveCurrencyModel coin = coinList.get(position);
        holder.bind(coin,context);
    }

    @Override
    public int getItemCount() {
        return coinList.size();
    }

    public static class CoinViewHolder extends RecyclerView.ViewHolder {
        TextView coinNameTextView;
        TextView coinFullNameTextView;
        TextView currentRate;
        TextView risenRate;
        ImageView bookmark;
        ImageView currencyIcon;
        RelativeLayout parent;

        public CoinViewHolder(@NonNull View itemView) {
            super(itemView);
            coinFullNameTextView = itemView.findViewById(R.id.currencyFullName);
            coinNameTextView = itemView.findViewById(R.id.currencyShortName);
            currentRate = itemView.findViewById(R.id.liveRate);
            risenRate = itemView.findViewById(R.id.increase);
            bookmark = itemView.findViewById(R.id.bookmark);
            currencyIcon = itemView.findViewById(R.id.currencyIcon);
            parent = itemView.findViewById(R.id.parent);

            bookmark.setOnClickListener(v -> {
                if (bookmarkClickListener != null) {
                    bookmarkClickListener.onBookmarkClicked(getAdapterPosition());
                }
            });
        }

        public void bind(LiveCurrencyModel coin, Context context) {
            coinFullNameTextView.setText(coin.getCurrencyFullName());
            coinNameTextView.setText(coin.getCurrencyShortcut());
            currentRate.setText("$"+coin.getCurrencyLiveRate());
            risenRate.setText("$"+coin.getCurrencyMaxRate());
            if (coin.getCurrencyIcon() != null && !coin.getCurrencyIcon().isEmpty()) {
                Picasso.get().load(coin.getCurrencyIcon()).into(currencyIcon);
            }
            if (coin.isBookMarked()) {
                bookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.bookmarked));
            } else {
                bookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.un_booked));
            }
        }
        // Interface to communicate click events to the adapter
        public interface OnBookmarkClickListener {
            void onBookmarkClicked(int position);
        }

        private OnBookmarkClickListener bookmarkClickListener;

        // Method to set the click listener from the adapter
        public void setOnBookmarkClickListener(OnBookmarkClickListener listener) {
            bookmarkClickListener = listener;
        }
    }

    public void updateList(List<LiveCurrencyModel> filteredList) {
        coinList = filteredList;
        notifyDataSetChanged(); // Notify the adapter that the dataset has changed
    }
}
