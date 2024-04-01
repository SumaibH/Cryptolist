package com.tradetrack.cryptolist.bottomSheet;

import android.content.res.ColorStateList;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.List;
import java.util.Objects;

import com.tradetrack.cryptolist.R;
import com.tradetrack.cryptolist.dataBase.AppDatabase;
import com.tradetrack.cryptolist.dataBase.DataModelDao;
import com.tradetrack.cryptolist.dataBase.DefinedColors;
import com.tradetrack.cryptolist.dataModel.DataModel;
import com.tradetrack.cryptolist.rvAdapter.ColorAdapter;

public class ColorChangeFragment extends BottomSheetDialogFragment {

    private RelativeLayout btnSubmit, btnCancel;
    private RelativeLayout currentColor, newColor;
    private static final String TAG = "ColorChangeFragment";
    private final List<DataModel> checkedDataModelList;
    DataModelDao dataModelDao;
    AppDatabase db;
    private String colorCode;
    private int selectedColor;
    private RecyclerView colorPaletteRecyclerView;
    BottomSheetListener mListener;

    public interface BottomSheetListener {
        void onColorChanged();
    }

    public void setBottomSheetListener(BottomSheetListener listener) {
        mListener = listener;
    }

    public ColorChangeFragment(List<DataModel> checkedDataModelList) {
        this.checkedDataModelList = checkedDataModelList;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_color_change, container, false);
        btnSubmit = view.findViewById(R.id.btnConfirm);
        btnCancel = view.findViewById(R.id.btnCancel);
        currentColor = view.findViewById(R.id.currentColor);
        newColor = view.findViewById(R.id.newColor);
        colorPaletteRecyclerView = view.findViewById(R.id.colorPaletteRecyclerView);
        db = AppDatabase.getInstance(requireContext());
        dataModelDao = db.dataModelDao();

        List<DefinedColors.ColorItem> colorItemList = DefinedColors.colorList;
        ColorAdapter colorAdapter = new ColorAdapter(colorItemList, this::onColorItemClick);
        colorPaletteRecyclerView.setAdapter(colorAdapter);

        return view;
    }

    private void onColorItemClick(String colorCode) {
        setSelectedColor(colorCode);
        this.colorCode = colorCode;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // set current color form checkedDataModelList last item
        colorCode = checkedDataModelList.get(checkedDataModelList.size()-1).getColorCode();
        selectedColor = android.graphics.Color.parseColor(colorCode);
        if(!Objects.equals(colorCode, "#FFFFFF")) {
            ColorStateList colorStateList = ColorStateList.valueOf(selectedColor);
            currentColor.setBackgroundTintList(colorStateList);
            newColor.setBackgroundTintList(colorStateList);
        }

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(colorCode != null) {
                    changeColorDatabase(colorCode);
                    dismiss();
                } else {
                    Toast.makeText(requireContext(), "Please select a color", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void setSelectedColor(String colorCode){
        selectedColor = android.graphics.Color.parseColor(colorCode);
        colorCode = String.format("#%06X", (0xFFFFFF & selectedColor));
        // Convert the color code to ColorStateList
        ColorStateList colorStateList = ColorStateList.valueOf(selectedColor);
        newColor.setBackgroundTintList(colorStateList);
        Log.d(TAG, "setSelectedColor: " + selectedColor + " " + colorCode + " " + colorStateList);
    }

    private void changeColorDatabase(String colorCode){
        new Thread(new Runnable() {
            @Override
            public void run() {
                for (DataModel dataModel : checkedDataModelList) {
                    dataModel.setColorCode(colorCode);
                    dataModel.setChecked(false);
                    dataModelDao.updateDataModel(dataModel);
                }
                if(mListener != null) {
                    mListener.onColorChanged();
                }
            }
        }).start();
    }

}