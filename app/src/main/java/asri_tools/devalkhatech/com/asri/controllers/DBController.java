package asri_tools.devalkhatech.com.asri.controllers;

/**
 * Created by Array-PC on 07-Mar-16.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import asri_tools.devalkhatech.com.asri.models.ItemsObject;
import asri_tools.devalkhatech.com.asri.models.SpinnerObject;

public class DBController extends SQLiteOpenHelper {

    private static final String TAG = DBController.class.getSimpleName();

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "asri.db";

    // Table Names
    private static final String TABLE_CUSTOMER = "m_customer";
    private static final String TABLE_USER = "m_user";
    private static final String TABLE_ITEM = "m_item";
    private static final String TABLE_SO = "t_so_entry";
    private static final String TABLE_SO_DETAIL = "t_so_entry_detail";
    private static final String TABLE_ABSEN = "t_absen";
    private static final String T_SO = "so_entry";

    // Common column sales entry
    private static final String KEY_T_SO_ID = "id";
    private static final String KEY_T_SO_CODE = "code";
    private static final String KEY_T_SO_CUSTOMER = "customer_code";
    private static final String KEY_T_SO_SALESMAN = "salesman";
    private static final String KEY_T_SO_REMARK = "remark";
    private static final String KEY_T_SO_DISCOUNT = "is_discount";
    private static final String KEY_T_SO_ITEM = "item";
    private static final String KEY_T_SO_QTY = "qty";
    private static final String KEY_T_SO_CREATED_DATE = "created_date";

    // Common column customers
    private static final String KEY_CUSTOMER_ID = "code";
    private static final String KEY_CUSTOMER_NAME = "name";
    private static final String KEY_CUSTOMER_LAT = "latitude";
    private static final String KEY_CUSTOMER_LANG = "langitude";
    // Common column user
    private static final String KEY_USER_CODE = "code";
    private static final String KEY_USER_USERNAME = "username";
    private static final String KEY_USER_PASSWORD = "password";
    private static final String KEY_USER_NAME = "name";
    private static final String KEY_USER_EMAIL = "email";
    private static final String KEY_USER_PHONE = "phone";

    // Common column item
    private static final String KEY_ITEM_CODE = "code";
    private static final String KEY_ITEM_NAME = "name";

    // Common column sales entry
    private static final String KEY_SO_ID = "id";
    private static final String KEY_SO_CODE = "code";
    private static final String KEY_SO_CUSTOMER = "customer_code";
    private static final String KEY_SO_SALESMAN = "salesman";
    private static final String KEY_SO_REMARK = "remark";
    private static final String KEY_SO_DISC = "is_discount";
    private static final String KEY_CREATED_DATE = "created_date";

    // Common column sales entry detail
    private static final String KEY_SO_ID_DETAIL = "id";
    private static final String KEY_SO_CODE_DETAIL = "so_code";
    private static final String KEY_SO_ITEM = "item";
    private static final String KEY_SO_QTY = "qty";


    // Common column sales entry detail
    private static final String KEY_ABSEN_ID = "id";
    private static final String KEY_ABSEN_CODE_CUSTOMER = "customer_code";
    private static final String KEY_ABSEN_LATITUDE = "latitude";
    private static final String KEY_ABSEN_LANGITUDE = "langitude";
    private static final String KEY_ABSEN_DATE = "created_date";

    private static final String CREATE_TABLE_CUSTOMER = "CREATE TABLE "
            + TABLE_CUSTOMER + "(" + KEY_CUSTOMER_ID + " TEXT PRIMARY KEY," + KEY_CUSTOMER_NAME
            + " TEXT," + KEY_CUSTOMER_LAT + " TEXT," + KEY_CUSTOMER_LANG + " TEXT" + ")";

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER
            + "(" + KEY_USER_CODE + " TEXT," + KEY_USER_USERNAME + " TEXT," + KEY_USER_PASSWORD + " TEXT," +
            KEY_USER_NAME + " TEXT," + KEY_USER_EMAIL + " TEXT," + KEY_USER_PHONE + " TEXT" + ")";

    private static final String CREATE_TABLE_ITEM = "CREATE TABLE "
            + TABLE_ITEM + "(" + KEY_ITEM_CODE + " TEXT PRIMARY KEY,"
            + KEY_ITEM_NAME + " TEXT" + ")";

    private static final String CREATE_TABLE_SO = "CREATE TABLE "
            + TABLE_SO + "(" + KEY_SO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SO_CODE + " TEXT," + KEY_SO_CUSTOMER
            + " TEXT," + KEY_SO_SALESMAN + " TEXT," + KEY_SO_REMARK
            + " TEXT," + KEY_SO_DISC + " TEXT," + KEY_CREATED_DATE + " DATETIME" + ")";

    private static final String CREATE_TABLE_SO_DETAIL = "CREATE TABLE "
            + TABLE_SO_DETAIL + "(" + KEY_SO_ID_DETAIL + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_SO_CODE_DETAIL + " TEXT," + KEY_SO_ITEM
            + " TEXT," + KEY_SO_QTY + " INT" + ")";

    private static final String CREATE_TABLE_ABSEN = "CREATE TABLE "
            + TABLE_ABSEN + "(" + KEY_ABSEN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_ABSEN_CODE_CUSTOMER + " TEXT," + KEY_ABSEN_LATITUDE
            + " TEXT," + KEY_ABSEN_LANGITUDE + " TEXT," + KEY_ABSEN_DATE + " DATETIME" + ")";

    private static final String CREATE_TABLE_SALES_ORDER = "CREATE TABLE "
            + T_SO + "(" + KEY_T_SO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + KEY_T_SO_CODE + " TEXT," + KEY_T_SO_CUSTOMER
            + " TEXT," + KEY_T_SO_SALESMAN + " TEXT," + KEY_T_SO_REMARK
            + " TEXT," + KEY_T_SO_DISCOUNT + " TEXT," + KEY_T_SO_ITEM
            + " TEXT," + KEY_T_SO_QTY + " TEXT," + KEY_T_SO_CREATED_DATE + " DATETIME" + ")";

    public DBController(Context applicationcontext) {
        super(applicationcontext, DATABASE_NAME, null, DATABASE_VERSION);
    }

    //Creates Table
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_CUSTOMER);
        db.execSQL(CREATE_TABLE_USER);
        db.execSQL(CREATE_TABLE_ITEM);
        db.execSQL(CREATE_TABLE_SO);
        db.execSQL(CREATE_TABLE_SO_DETAIL);
        db.execSQL(CREATE_TABLE_ABSEN);
        db.execSQL(CREATE_TABLE_SALES_ORDER);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int version_old, int current_version) {

        db.execSQL("DROP TABLE IF EXISTS" + TABLE_CUSTOMER);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_USER);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_ITEM);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_SO);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_SO_DETAIL);
        db.execSQL("DROP TABLE IF EXISTS" + TABLE_ABSEN);
        db.execSQL("DROP TABLE IF EXISTS" + T_SO);

        onCreate(db);
    }

    /**
     * Inserts Customer into SQLite DB
     *
     * @param queryValues
     */
    public void insertCustomer(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_CUSTOMER_ID, queryValues.get("code"));
        values.put(KEY_CUSTOMER_NAME, queryValues.get("name"));
        values.put(KEY_CUSTOMER_LAT, queryValues.get("latitude"));
        values.put(KEY_CUSTOMER_LANG, queryValues.get("langitude"));

        database.replace(TABLE_CUSTOMER, null, values);
        database.close();
    }

    /**
     * Get list of Customer from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllCustomers() {
        ArrayList<HashMap<String, String>> customerList;
        customerList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_CUSTOMER_ID, cursor.getString(0));
                map.put(KEY_CUSTOMER_NAME, cursor.getString(1));
                map.put(KEY_CUSTOMER_LAT, cursor.getString(2));
                map.put(KEY_CUSTOMER_LANG, cursor.getString(3));

                customerList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return customerList;
    }

    public List<SpinnerObject> getAllLabels() {
        List<SpinnerObject> labels = new ArrayList<SpinnerObject>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_CUSTOMER + " ORDER BY " + KEY_CUSTOMER_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(new SpinnerObject (cursor.getString(0),cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    public List<ItemsObject> getAllItem() {
        List<ItemsObject> labels = new ArrayList<ItemsObject>();

        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_ITEM + " ORDER BY " + KEY_ITEM_NAME + " ASC";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                labels.add(new ItemsObject (cursor.getString(0),cursor.getString(1)));
            } while (cursor.moveToNext());
        }

        // closing connection
        cursor.close();
        db.close();

        // returning lables
        return labels;
    }

    /**
     * Inserts USER into SQLite DB
     *
     * @param queryValues
     */
    public void insertUser(HashMap<String, String> queryValues) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_USER_CODE, queryValues.get("code"));
        values.put(KEY_USER_USERNAME, queryValues.get("username"));
        values.put(KEY_USER_PASSWORD, queryValues.get("password"));
        values.put(KEY_USER_NAME, queryValues.get("name"));
        values.put(KEY_USER_EMAIL, queryValues.get("email"));
        values.put(KEY_USER_PHONE, queryValues.get("phone"));
        db.replace(TABLE_USER, null, values);
        db.close();
    }

    /**
     * Get list of Users from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> userList;
        userList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_USER_CODE, cursor.getString(0));
                map.put(KEY_USER_USERNAME, cursor.getString(1));
                map.put(KEY_USER_PASSWORD, cursor.getString(2));
                map.put(KEY_USER_NAME, cursor.getString(3));
                map.put(KEY_USER_EMAIL, cursor.getString(4));
                map.put(KEY_USER_PHONE, cursor.getString(5));
                userList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return userList;
    }

    /**
     * Getting user data from database
     */
    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> user = new HashMap<String, String>();
        String selectQuery = "SELECT  * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);
        // Move to first row
        cursor.moveToFirst();
        if (cursor.getCount() > 0) {
            user.put("code", cursor.getString(1));
            user.put("username", cursor.getString(2));
            user.put("password", cursor.getString(3));
        }
        cursor.close();
        db.close();
        // return user
        Log.d(TAG, "Fetching user from Sqlite: " + user.toString());

        return user;
    }

    /**
     * Inserts ITEM into SQLite DB
     *
     * @param queryValues
     */
    public void insertItem(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ITEM_CODE, queryValues.get("code"));
        values.put(KEY_ITEM_NAME, queryValues.get("name"));

        database.replace(TABLE_ITEM, null, values);
        database.close();
    }

    /**
     * Get list of Items from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllItems() {
        ArrayList<HashMap<String, String>> itemList;
        itemList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEM;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_ITEM_CODE, cursor.getString(0));
                map.put(KEY_ITEM_NAME, cursor.getString(1));
                itemList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return itemList;
    }

    /**
     * Inserts ITEM into SQLite DB
     *
     * @param queryValues
     */
    public void insertSo(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SO_CODE, queryValues.get("code"));
        values.put(KEY_SO_CUSTOMER, queryValues.get("customer_code"));
        values.put(KEY_SO_SALESMAN, queryValues.get("salesman"));
        values.put(KEY_SO_REMARK, queryValues.get("remark"));
        values.put(KEY_SO_DISC, queryValues.get("is_discount"));
        values.put(KEY_CREATED_DATE, queryValues.get("created_date"));
        database.insert(TABLE_SO, null, values);
        database.close();
    }

    /**
     * Get list of Items from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllSo() {
        ArrayList<HashMap<String, String>> soList;
        soList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "select a.id,a.code,a.customer_code,c.name as customer_name,a.salesman,a.remark,a.created_date from t_so_entry as a left join m_customer c on a.customer_code = c.code";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_SO_ID, cursor.getString(0));
                map.put(KEY_SO_CODE, cursor.getString(1));
                map.put(KEY_SO_CUSTOMER, cursor.getString(2));
                map.put("customer_name", cursor.getString(3));
                map.put(KEY_SO_SALESMAN, cursor.getString(4));
                map.put(KEY_SO_REMARK, cursor.getString(5));
                map.put(KEY_CREATED_DATE, cursor.getString(6));
                soList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return soList;
    }


    /**
     * Inserts ITEM into SQLite DB
     *
     * @param queryValues
     */
    public void insertSoDetail(HashMap<String, String> queryValues) {

        /*// you can use INSERT only
        String sql = "INSERT OR REPLACE INTO " + TABLE_SO_DETAIL + " ( so_code, item, qty) VALUES ( ?, ?, ? )";

        SQLiteDatabase db = this.getWritableDatabase();
        *//*
         * According to the docs http://developer.android.com/reference/android/database/sqlite/SQLiteDatabase.html
         * Writers should use beginTransactionNonExclusive() or beginTransactionWithListenerNonExclusive(SQLiteTransactionListener)
         * to start a transaction. Non-exclusive mode allows database file to be in readable by other threads executing queries.
         *//*
        db.beginTransactionNonExclusive();
        // db.beginTransaction();

        SQLiteStatement stmt = db.compileStatement(sql);

        stmt.bindString(1, queryValues.get("so_code"));
        stmt.bindString(2, queryValues.get("item"));
        stmt.bindString(3, queryValues.get("qty"));

        stmt.execute();
        stmt.clearBindings();

        db.setTransactionSuccessful();
        db.endTransaction();

        db.close();*/
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_SO_CODE_DETAIL, queryValues.get("so_code"));
        values.put(KEY_SO_ITEM, queryValues.get("item"));
        values.put(KEY_SO_QTY, queryValues.get("qty"));

        database.insert(TABLE_SO_DETAIL, null, values);
        database.close();
    }

    /**
     * Deleting a tag
     *//*
    public void deleteTag(Tag tag, boolean should_delete_all_tag_todos) {
        SQLiteDatabase db = this.getWritableDatabase();

        // before deleting tag
        // check if todos under this tag should also be deleted
        if (should_delete_all_tag_todos) {
            // get all todos under this tag
            List<Todo> allTagToDos = getAllToDosByTag(tag.getTagName());

            // delete all todos
            for (Todo todo : allTagToDos) {
                // delete todo
                deleteToDo(todo.getId());
            }
        }

        // now delete the tag
        db.delete(TABLE_TAG, KEY_ID + " = ?",
                new String[] { String.valueOf(tag.getId()) });
    }*/
    /**
     * Get list of Items from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getListSoBySoCode(String i) {
        ArrayList<HashMap<String, String>> soList;
        soList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "select so.id,so.so_code,so.item,item.name,so.qty from t_so_entry_detail as so left join m_item as item on so.item = item.code where so.so_code ='"+ i +"'";

        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("so_id", cursor.getString(1));
                map.put("item", cursor.getString(2));
                map.put("name", cursor.getString(3));
                map.put("qty", cursor.getString(4));

                soList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return soList;
    }
    public ArrayList<HashMap<String, String>> getAllSoDetailList() {
        ArrayList<HashMap<String, String>> soList;
        soList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + TABLE_SO_DETAIL;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(KEY_SO_ID_DETAIL, cursor.getString(0));
                map.put(KEY_SO_CODE_DETAIL, cursor.getString(1));
                map.put(KEY_SO_ITEM, cursor.getString(2));
                map.put(KEY_SO_QTY, cursor.getString(3));


                soList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return soList;
    }

    /**
     * Get list of Items from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllSoDetail(String id) {
        ArrayList<HashMap<String, String>> soList;
        soList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + TABLE_SO_DETAIL + " WHERE " + KEY_SO_CODE_DETAIL + " ='"+ id +"'";
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();

                map.put(KEY_SO_ID_DETAIL, cursor.getString(0));
                map.put(KEY_SO_CODE_DETAIL, cursor.getString(1));
                map.put(KEY_SO_ITEM, cursor.getString(2));
                map.put(KEY_SO_QTY, cursor.getString(3));


                soList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return soList;
    }

    /**
     * Inserts ITEM into SQLite DB
     *
     * @param queryValues
     */
    public void insertAbsen(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_ABSEN_CODE_CUSTOMER, queryValues.get("customer_code"));
        values.put(KEY_ABSEN_LATITUDE, queryValues.get("latitude"));
        values.put(KEY_ABSEN_LANGITUDE, queryValues.get("langitude"));
        values.put(KEY_ABSEN_DATE, queryValues.get("created"));

        database.replace(TABLE_ABSEN, null, values);
        database.close();
    }

    /**
     * Get list of Items from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllAbsen() {
        ArrayList<HashMap<String, String>> absenList;
        absenList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + TABLE_ABSEN;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_ABSEN_ID, cursor.getString(0));
                map.put(KEY_ABSEN_CODE_CUSTOMER, cursor.getString(1));
                map.put(KEY_ABSEN_LATITUDE, cursor.getString(2));
                map.put(KEY_ABSEN_LANGITUDE, cursor.getString(3));
                map.put(KEY_ABSEN_DATE, cursor.getString(4));

                absenList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return absenList;
    }

    public int getHighestID() {
        final String MY_QUERY = "SELECT MAX(id) AS max_id FROM " + TABLE_SO;
        SQLiteDatabase mDb = this.getWritableDatabase();
        Cursor cur = mDb.rawQuery(MY_QUERY, null);
        /*cur.moveToFirst();
        int ID = cur.getInt(0);
        cur.close();*/
        int ID = 0;
        if (cur.moveToFirst()) {
            do {
                ID = cur.getInt(0);
            } while (cur.moveToNext());
        }
        return ID;
    }


    public void insertSoEntry(HashMap<String, String> queryValues) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_T_SO_CODE, queryValues.get("code"));
        values.put(KEY_T_SO_CUSTOMER, queryValues.get("customer_code"));
        values.put(KEY_T_SO_SALESMAN, queryValues.get("salesman"));
        values.put(KEY_T_SO_REMARK, queryValues.get("remark"));
        values.put(KEY_T_SO_DISCOUNT, queryValues.get("is_discount"));
        values.put(KEY_T_SO_ITEM, queryValues.get("item"));
        values.put(KEY_T_SO_QTY, queryValues.get("qty"));
        values.put(KEY_T_SO_CREATED_DATE, queryValues.get("created_date"));

        database.insert(T_SO, null, values);
        database.close();
    }

    /**
     * Get list of Items from SQLite DB as Array List
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getAllSoEntry(String id) {
        ArrayList<HashMap<String, String>> soList;
        soList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT * FROM " + T_SO + " WHERE " + KEY_T_SO_CODE + " =" + id;
        SQLiteDatabase database = this.getWritableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(KEY_T_SO_CODE, cursor.getString(0));
                map.put(KEY_T_SO_CUSTOMER, cursor.getString(1));
                map.put(KEY_T_SO_SALESMAN, cursor.getString(2));
                map.put(KEY_T_SO_REMARK, cursor.getString(3));
                map.put(KEY_T_SO_DISCOUNT, cursor.getString(4));
                map.put(KEY_T_SO_ITEM, cursor.getString(5));
                map.put(KEY_T_SO_QTY, cursor.getString(6));
                map.put(KEY_T_SO_CREATED_DATE, cursor.getString(7));

                soList.add(map);
            } while (cursor.moveToNext());
        }
        database.close();
        return soList;
    }
}

