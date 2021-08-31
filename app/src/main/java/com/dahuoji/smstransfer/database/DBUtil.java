package com.dahuoji.smstransfer.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by 10732 on 2018/5/22.
 */

public class DBUtil {

    private final DBHelper dbHelper;
    private final SQLiteDatabase db;
    private static volatile DBUtil instance = null;

    public static synchronized DBUtil getInstance(Context context) {
        if (instance == null) {
            instance = new DBUtil(context);
        }
        return instance;
    }

    private DBUtil(Context context) {
        dbHelper = new DBHelper(context);
        db = dbHelper.getWritableDatabase();
    }

    //创建表单-字段都是字符串类型
    public DBUtil createTable(String tableName, String[] columns) throws SQLException {
        StringBuilder sqlColumns = new StringBuilder();
        for (int i = 0; i < columns.length; i++) {
            if (i == columns.length - 1) {
                sqlColumns.append(columns[i]).append(" TEXT not null");
            } else {
                sqlColumns.append(columns[i]).append(" TEXT not null, ");
            }
        }
        String sql = "create table if not exists " + formatTableName(tableName) + " (" +
                sqlColumns.toString() +
                ");";
        db.execSQL(sql);
        return this;
    }

    //清空表单数据-字段都是字符串类型
    public SQLiteDatabase deleteData(String tableName, String[] columns) throws SQLException {
        /*
         * 如果表存在则删除数据
         * 如果不存在则创建一个新表
         * */
        String tableNameFormat = formatTableName(tableName);
        if (isExists(tableName)) {
            String sql = "delete from " + tableNameFormat + ";";
            db.execSQL(sql);
        } else {
            createTable(tableName, columns);
        }
        return db;
    }

    public void beginTransaction() {
        db.beginTransaction();
    }

    public void setTransactionSuccessful() {
        db.setTransactionSuccessful();
    }

    public void endTransaction() {
        if (db.inTransaction()) {
            db.endTransaction();
        }
    }

    public void insertData(String table, String nullColumnHack, ContentValues values) {
        db.insert(formatTableName(table), nullColumnHack, values);
    }

    public Cursor queryData(String table, String[] columns, String selection,
                            String[] selectionArgs, String groupBy, String having,
                            String orderBy, String limit) {
        return db.query(formatTableName(table), columns, selection, selectionArgs, groupBy, having, orderBy, limit);
    }

    public void updateData(String table, ContentValues values, String whereClause, String[] whereArgs) {
        db.update(formatTableName(table), values, whereClause, whereArgs);
    }

    public void deleteData(String table, String whereClause, String[] whereArgs) {
        db.delete(formatTableName(table), whereClause, whereArgs);
    }

    private String formatTableName(String tableName) {
        if (tableName.startsWith("`")) {
            return tableName;
        } else {
            return "`" + tableName + "`";
        }
    }

    public boolean isExists(String tableName) {
        boolean isExists = false;
        String sql = "select name from sqlite_master where type='table';";
        Cursor cursor = db.rawQuery(sql, null);
        while (cursor.moveToNext()) {
            String name = cursor.getString(0);
            if (tableName != null && tableName.equals(name)) {
                isExists = true;
                break;
            }
        }
        cursor.close();
        return isExists;
    }

    public void close() {
        dbHelper.close();
    }

}