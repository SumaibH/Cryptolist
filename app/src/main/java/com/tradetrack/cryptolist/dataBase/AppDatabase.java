package com.tradetrack.cryptolist.dataBase;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

import com.tradetrack.cryptolist.dataModel.CurrencyModel;
import com.tradetrack.cryptolist.dataModel.DataModel;
import com.tradetrack.cryptolist.dataModel.LiveCurrencyModel;

@Database(entities = {CurrencyModel.class, DataModel.class, LiveCurrencyModel.class}, version = 2, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "tradeTrackDB";
    private static AppDatabase instance;

    public abstract DataModelDao dataModelDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;
    }
}
