package com.smartsudoku.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.smartsudoku.business.EnhancedCell;
import com.smartsudoku.business.Grid;

import java.util.ArrayList;
import java.util.Random;

public class DBHelper extends SQLiteOpenHelper {
    private static DBHelper instance;

    private static final int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "contactsManager";

    private static final String TABLE_GRID_CELLS = "gridCells";
    private static final String KEY_ID = "id";
    private static final String KEY_NUM = "num";
    private static final String KEY_FIXED = "fixed";
    private static final String KEY_VALUE = "value";
    private static final String KEY_NOTES = "notes";
    private static final String KEY_RED = "red";
    private static final String KEY_GREEN = "green";
    private static final String KEY_BLUE = "blue";

    private static final String TABLE_RECORDS = "records";
    private static final String KEY_DATE = "date";
    private static final String KEY_LEVEL = "level";
    private static final String KEY_TIME = "time";

    public static synchronized DBHelper getInstance(Context context) {
        if (instance == null)
            instance = new DBHelper(context);

        return instance;
    }

    private DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_GRIDS_TABLE = "CREATE TABLE " + TABLE_GRID_CELLS + "("
                + KEY_ID + " INTEGER,"
                + KEY_NUM + " INTEGER,"
                + KEY_FIXED + " INTEGER NOT NULL,"
                + KEY_VALUE + " INTEGER NOT NULL,"
                + KEY_NOTES + " INTEGER NOT NULL,"
                + KEY_RED + " INTEGER NOT NULL,"
                + KEY_GREEN + " INTEGER NOT NULL,"
                + KEY_BLUE + " INTEGER NOT NULL,"
                + "PRIMARY KEY( " + KEY_ID + ", " + KEY_NUM + " ))";

        String CREATE_RECORDS_TABLE = "CREATE TABLE " + TABLE_RECORDS + "("
                + KEY_DATE + " INTEGER PRIMARY KEY,"
                + KEY_LEVEL + " INTEGER NOT NULL,"
                + KEY_TIME + " INTEGER NOT NULL)";

        db.execSQL(CREATE_GRIDS_TABLE);
        db.execSQL(CREATE_RECORDS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        if (i1 != i) {
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_GRID_CELLS);
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_RECORDS);
            onCreate(sqLiteDatabase);
        }
    }

    public void addGrid(Grid<EnhancedCell> grid) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_GRID_CELLS);

        long id = System.currentTimeMillis();

        int num = 0;
        for (EnhancedCell cell : grid) {
            ContentValues values = new ContentValues();
            values.put(KEY_ID, id);
            values.put(KEY_NUM, num);
            num++;
            values.put(KEY_FIXED, cell.isFixed());
            values.put(KEY_VALUE, cell.getValue());

            int v = 0;
            for (Boolean b : cell.getValues()) {
                v = v << 1;
                v += b ? 1 : 0;
            }
            values.put(KEY_NOTES, v);
            values.put(KEY_RED, cell.getRed());
            values.put(KEY_GREEN, cell.getGreen());
            values.put(KEY_BLUE, cell.getBlue());

            db.insert(TABLE_GRID_CELLS, null, values);
        }
        db.close();
    }

    public Grid<EnhancedCell> getGrid() {
        SQLiteDatabase db = getReadableDatabase();
        String QUERY = "SELECT * FROM " + TABLE_GRID_CELLS;
        Cursor cursor = db.rawQuery(QUERY, null);
        Grid<EnhancedCell> grid = new Grid<>(new EnhancedCell());
        int c = 0;
        try {
            if (cursor.moveToFirst()) {
                do {
                    EnhancedCell cell = new EnhancedCell();
                    int index;

                    index = cursor.getColumnIndex(KEY_FIXED);
                    cell.setFixed(cursor.getInt(index) == 1);

                    index = cursor.getColumnIndex(KEY_VALUE);
                    cell.setValue(cursor.getInt(index));

                    index = cursor.getColumnIndex(KEY_NOTES);
                    int v = cursor.getInt(index);
                    for (int i = 8; i >= 0; i--) {
                        cell.getValues()[i] = v % 2 == 1;
                        v = v >> 1;
                    }
                    index = cursor.getColumnIndex(KEY_RED);
                    cell.setRed(cursor.getInt(index));
                    index = cursor.getColumnIndex(KEY_GREEN);
                    cell.setGreen(cursor.getInt(index));
                    index = cursor.getColumnIndex(KEY_BLUE);
                    cell.setBlue(cursor.getInt(index));
                    grid.set(c, cell);
                    c++;
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("db", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return grid;
    }

    public void addRecord(int level, long time) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_DATE, System.currentTimeMillis());
        values.put(KEY_LEVEL, level);
        values.put(KEY_TIME, time);

        db.insert(TABLE_RECORDS, null, values);

        db.close();
    }

    public ArrayList<Record> getRecords(){
        SQLiteDatabase db = getReadableDatabase();
        String QUERY = "SELECT * FROM " + TABLE_RECORDS
                + " ORDER BY " + KEY_LEVEL + " DESC, "
                + KEY_TIME + " ASC"
                + " LIMIT 25";
        Cursor cursor = db.rawQuery(QUERY, null);
        ArrayList<Record> array = new ArrayList<>();
        try {
            if (cursor.moveToFirst()) {
                do {
                    Record record = new Record();
                    int index;

                    index = cursor.getColumnIndex(KEY_DATE);
                    record.date = cursor.getLong(index);

                    index = cursor.getColumnIndex(KEY_LEVEL);
                    record.level = cursor.getInt(index);

                    index = cursor.getColumnIndex(KEY_TIME);
                    record.time = cursor.getLong(index);

                    array.add(record);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d("db", "Error while trying to get posts from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return array;
    }

    public void populateRecords(){
        Random random = new Random();
        for (int i = 0; i<25; i++){
            addRecord(random.nextInt(3)+1, random.nextInt(1800000));
        }
    }
}
