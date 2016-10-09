package ua.kyslytsia.tct.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

public class TctDb {

    public DbHelper dbHelper;
    public SQLiteDatabase sqLiteDatabase;

    public TctDb (Context context) {
        dbHelper = new DbHelper(context);
        sqLiteDatabase = dbHelper.getWritableDatabase();
    }
}
