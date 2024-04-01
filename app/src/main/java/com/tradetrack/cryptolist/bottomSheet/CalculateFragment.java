package com.tradetrack.cryptolist.bottomSheet;

import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Locale;

import com.tradetrack.cryptolist.R;
import com.tradetrack.cryptolist.dataModel.DataModel;

public class CalculateFragment extends BottomSheetDialogFragment {

    private List<DataModel> checkedDataModelList;
    private RelativeLayout btnDone;
    double sumBuyCurrency, sumSellCurrency, sumBuyPayments, sumSalePayments, sumEarnings, averageBuyPrice;
    BottomSheetListener bottomSheetListener;

    public interface BottomSheetListener {
        void onCalculate();
    }
    public void setBottomSheetListener(BottomSheetListener bottomSheetListener) {
        this.bottomSheetListener = bottomSheetListener;
    }
    public CalculateFragment(List<DataModel> checkedDataModelList) {
        this.checkedDataModelList = checkedDataModelList;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_calculate, container, false);

        // Accessing TextViews from the layout
        TextView textViewSumBuyCurrency = view.findViewById(R.id.textViewSumBuyCurrency);
        TextView textViewSumSellCurrency = view.findViewById(R.id.textViewSumSellCurrency);
        TextView textViewSumBuyPayments = view.findViewById(R.id.textViewSumBuyPayments);
        TextView textViewSumSalePayments = view.findViewById(R.id.textViewSumSalePayments);
        TextView textViewSumEarnings = view.findViewById(R.id.textViewSumEarnings);
        TextView textViewBuyPriceAverage = view.findViewById(R.id.textViewBuyPriceAverage);
        btnDone = view.findViewById(R.id.btnDone);

        if(checkedDataModelList.size() > 0) {
            calculate();

            // round the values to 3 decimal places
            sumBuyCurrency = Double.parseDouble(String.format(Locale.US, "%.3f", sumBuyCurrency));
            sumSellCurrency = Double.parseDouble(String.format(Locale.US, "%.3f", sumSellCurrency));
            sumBuyPayments = Double.parseDouble(String.format(Locale.US, "%.3f", sumBuyPayments));
            sumSalePayments = Double.parseDouble(String.format(Locale.US, "%.3f", sumSalePayments));
            sumEarnings = Double.parseDouble(String.format(Locale.US, "%.3f", sumEarnings));
            averageBuyPrice = averageBuyPrice / checkedDataModelList.size();
            averageBuyPrice = Double.parseDouble(String.format(Locale.US, "%.3f", averageBuyPrice));

            // Setting the text of the TextViews
            textViewSumBuyCurrency.setText(sumBuyCurrency + " " + checkedDataModelList.get(0).getCurrencyCode());
            textViewSumSellCurrency.setText(sumSellCurrency + " " + checkedDataModelList.get(0).getCurrencyCode());
            textViewSumBuyPayments.setText("$" + sumBuyPayments);
            textViewSumSalePayments.setText("$" + sumSalePayments);
            textViewSumEarnings.setText("$" + sumEarnings);
            textViewBuyPriceAverage.setText("$" + averageBuyPrice);
        } else {
            textViewSumBuyCurrency.setText("0");
            textViewSumSellCurrency.setText("0");
            textViewSumBuyPayments.setText("$0");
            textViewSumSalePayments.setText("$0");
            textViewSumEarnings.setText("$0");
            textViewBuyPriceAverage.setText("$0");
        }

        btnDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return view;
    }

    private void calculate() {
        for(DataModel dataModel : checkedDataModelList) {
            double buyQuantity = Double.parseDouble(dataModel.getBuyQuantity());
            double buyPrice = Double.parseDouble(dataModel.getBuyPrice());
            double sellQuantity = Double.parseDouble(dataModel.getSellQuantity());
            double sellPrice = Double.parseDouble(dataModel.getSellPrice());
            double buyPayments = buyQuantity * buyPrice;
            double salePayments = sellQuantity * sellPrice;
            double earnings = salePayments - buyPayments;

            sumBuyCurrency += buyQuantity;
            sumSellCurrency += sellQuantity;
            sumBuyPayments += buyPayments;
            sumSalePayments += salePayments;
            sumEarnings += earnings;
            averageBuyPrice += buyPrice;

            bottomSheetListener.onCalculate();
        }
    }

    private double parseDouble(String text) {
        try {
            return Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return 0.0;
        }
    }
}
