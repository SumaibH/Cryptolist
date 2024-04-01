package com.tradetrack.cryptolist.dataBase;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

import com.tradetrack.cryptolist.dataModel.CurrencyModel;
import com.tradetrack.cryptolist.dataModel.DataModel;
import com.tradetrack.cryptolist.dataModel.LiveCurrencyModel;

@Dao
public interface DataModelDao {

    // Methods for CurrencyModel
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertCurrencyModel(CurrencyModel currencyModel);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateCurrencyModel(CurrencyModel currencyModel);

    @Query("UPDATE currency_table SET " +
            "currencyLiveRate = :currencyLiveRate, " +
            "currencyMaxRate = :currencyMaxRate " +
            "WHERE currencyFullName = :currencyFullName")
    void updateCurrencyModelByName(String currencyFullName, String currencyLiveRate, String currencyMaxRate);

    @Query("UPDATE currency_table SET " +
            "id = :id, " +
            "currencyFullName = :currencyFullName, " +
            "currencyShortcut = :currencyShortcut, " +
            "currencyLiveRate = :currencyLiveRate, " +
            "currencyMaxRate = :currencyMaxRate, " +
            "currencyIcon = :currencyIcon, " +
            "isChecked = :isChecked, " +
            "isBookMarked = :isBookMarked " +
            "WHERE id = :id")
    void updateCurrencyModelById(int id, String currencyFullName, String currencyShortcut, String currencyLiveRate,String currencyMaxRate,String currencyIcon,boolean isChecked,boolean isBookMarked);

    @Query("UPDATE currency_table SET isChecked = :isCheckedValue, isBookMarked = :isBookmarkedValue WHERE currencyFullName = :fullName")
    void updateCheckedAndBookmarkedStatus(String fullName, boolean isCheckedValue, boolean isBookmarkedValue);

    @Delete
    void deleteCurrencyModel(CurrencyModel currencyModel);

    @Query("DELETE FROM currency_table WHERE currencyFullName = :fullName")
    void deleteCurrencyModelByFullName(String fullName);

    @Query("SELECT id FROM currency_table WHERE currencyShortcut = :currencyShortcut LIMIT 1")
    long getExistingCurrencyId(String currencyShortcut);

    @Query("SELECT * FROM currency_table")
    List<CurrencyModel> getAllCurrencyModels();

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    long insertSelectedCurrencyModel(LiveCurrencyModel currencyModel);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateSelectedCurrencyModel(LiveCurrencyModel currencyModel);

    @Query("UPDATE selected_currency_table SET " +
            "currencyLiveRate = :currencyLiveRate, " +
            "currencyMaxRate = :currencyMaxRate " +
            "WHERE currencyFullName = :currencyFullName")
    void updateSelectedCurrencyModelByName(String currencyFullName, String currencyLiveRate, String currencyMaxRate);

    @Delete
    void deleteSelectedCurrencyModel(LiveCurrencyModel currencyModel);

    @Query("SELECT id FROM selected_currency_table WHERE currencyShortcut = :currencyShortcut LIMIT 1")
    long getSelectedExistingCurrencyId(String currencyShortcut);

    @Query("SELECT * FROM selected_currency_table")
    List<LiveCurrencyModel> getAllSelectedCurrencyModels();

    @Query("SELECT id FROM selected_currency_table WHERE currencyShortcut = :currencyShortcut LIMIT 1")
    long getLiveExistingCurrencyId(String currencyShortcut);
    @Query("UPDATE selected_currency_table SET isChecked = :isCheckedValue, isBookMarked = :isBookmarkedValue WHERE currencyFullName = :fullName")
    void updateLiveCheckedAndBookmarkedStatus(String fullName, boolean isCheckedValue, boolean isBookmarkedValue);

    @Query("SELECT currencyShortcut FROM currency_table")
    List<String> getCurrencyList();

    // Methods for DataModel
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertDataModel(DataModel dataModel);

    @Query("UPDATE data_table SET " +
            "currencyId = :currencyId, " +
            "currencyCode = :currencyCode, " +
            "currentDate = :currentDate, " +
            "colorCode = :colorCode, " +
            "buyPrice = :buyPrice, " +
            "buyQuantity = :buyQuantity, " +
            "sellQuantity = :sellQuantity, " +
            "sellPrice = :sellPrice, " +
            "payment = :payment, " +
            "sale = :sale, " +
            "earning = :earning, " +
            "isChecked = :isChecked " +
            "WHERE id = :dataModelId")
    void updateDataModelById(int dataModelId, int currencyId, String currencyCode, String currentDate, String colorCode,
                             String buyPrice, String buyQuantity, String sellQuantity, String sellPrice,
                             String payment, String sale, String earning, boolean isChecked);


    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateDataModel(DataModel dataModel);

    @Delete
    void deleteDataModel(DataModel dataModel);

    @Query("SELECT * FROM data_table")
    List<DataModel> getAllDataModels();


    @Query("SELECT SUM(earning) FROM data_table WHERE currencyCode = :currencyCode")
    double getTotalEarningsForCurrency(String currencyCode);

    @Query("SELECT SUM(buyQuantity - sellQuantity) FROM data_table WHERE currencyCode = :currencyCode")
    double getTotalRemainingQuantity(String currencyCode);

    @Query("SELECT id FROM currency_table WHERE currencyShortcut = :currencyShortcut LIMIT 1")
    int getCurrencyId(String currencyShortcut);

    @Query("SELECT * FROM data_table WHERE currencyCode = :currencyCode")
    List<DataModel> getSpecificCurrencyDataModels(String currencyCode);
}
