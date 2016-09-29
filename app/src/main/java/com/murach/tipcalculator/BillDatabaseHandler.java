package com.murach.tipcalculator;

import android.database.Cursor;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Redd on 9/25/2016.
 */
public class BillDatabaseHandler extends SQLiteOpenHelper {

    private static final int    DATABASE_VERSION    = 2;
    private static final String DATABASE_NAME       = "Bills.db";
    private static final String TABLE_TIPS          = "bills";
    private static final String COLUMN_ID           = "_id";
    private static final String COLUMN_BILL_DATE    = "bill_date";
    private static final String COLUMN_BILL_AMOUNT  = "bill_amount";
    private static final String COLUMN_TIP_PERCENT  = "tip_percent";

    public BillDatabaseHandler(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query =
                "CREATE TABLE " + TABLE_TIPS + "(" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_BILL_DATE + " INTEGER, " +
                    COLUMN_BILL_AMOUNT + " REAL, " +
                    COLUMN_TIP_PERCENT + " REAL" +
                ");";

        sqLiteDatabase.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int newVersion, int oldVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TIPS);
        onCreate(sqLiteDatabase);
    }

    public void clearDatabase ( ) {
        SQLiteDatabase db = getReadableDatabase();
        db.execSQL("DELETE FROM " + TABLE_TIPS + " WHERE 1");
        db.close();
    }

    public float getAverageTip ( ) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.query(TABLE_TIPS, new String[] {"AVG(" + COLUMN_TIP_PERCENT + ")"}, null, null, null, null, null);
        c.moveToFirst();

        float average = c.getFloat(0);

        db.close();
        return average;
    }

    public Tip getLastTip ( ) {
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TIPS + " WHERE (" + COLUMN_ID + " = (SELECT MAX(" + COLUMN_ID + ") FROM " + TABLE_TIPS + "))", null);
        if (c.getCount() <= 0)
            return null;

        c.moveToFirst();

        Tip tip = cursorToTip(c);

        db.close();
        return tip;

    }

    public void addTip (Tip tip) {
        SQLiteDatabase db = getWritableDatabase();

        String insertStatement = "INSERT INTO " + TABLE_TIPS + "(" + COLUMN_BILL_DATE + ", " + COLUMN_BILL_AMOUNT + ", " + COLUMN_TIP_PERCENT + ") VALUES (" +
                tip.getDateMillis() + ", " +
                tip.getBillAmount() + ", " +
                tip.getTipPercent() + ")";

        db.execSQL(insertStatement);
        db.close();
    }

    public List<Tip> getTips ( ) {
        List<Tip> tips = new ArrayList<Tip>( );
        SQLiteDatabase db = getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + TABLE_TIPS + " WHERE 1", null);
        c.moveToFirst();

        while (!c.isAfterLast()) {
            Tip tip = cursorToTip(c);
            tips.add(tip);
            c.moveToNext();
        }

        db.close();
        return tips;
    }

    private Tip cursorToTip(Cursor c) {
        Tip tip = new Tip();
        tip.setId(c.getInt(0));
        tip.setDateMillis(c.getInt(1));
        tip.setBillAmount(c.getFloat(2));
        tip.setTipPercent(c.getFloat(3));
        return tip;
    }
}
