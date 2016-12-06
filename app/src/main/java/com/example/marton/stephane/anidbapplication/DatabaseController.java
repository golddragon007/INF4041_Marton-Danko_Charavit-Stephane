package com.example.marton.stephane.anidbapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Marton on 2014.12.04..
 * Sample: http://www.tutorialspoint.com/android/android_sqlite_database.htm
 *
 * Managing SQLite querys and other things
 */
public class DatabaseController extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "AniDBApp";

    /* Class constructor
     * @param Context context Pass here activity's context
     */
    public DatabaseController(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* When the first execution is
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE  TABLE  IF NOT EXISTS \"hotanime\" (" +
                " \"id\" INTEGER PRIMARY KEY  AUTOINCREMENT  NOT NULL," +
                " \"aid\" INTEGER DEFAULT (0)," +
                " \"title\" VARCHAR NOT NULL," +
                " \"timestamp\" INTEGER NOT NULL DEFAULT (strftime('%s', 'now')))");
        db.execSQL("CREATE INDEX IF NOT EXISTS \"hotanime_timestamp\" ON hotanime(\"timestamp\")");
        db.execSQL("CREATE INDEX IF NOT EXISTS \"hotanime_aid\" ON hotanime(\"aid\")");
    }

    /* When there was a previous create and now need to update it
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //do.... nothing
    }

    /* Insert hotanime files to SQLite
     * @param ArrayList<String> hotanime Hot Anime list items from the xml.
     * @return boolean It returns the execution success (true) or not (false)
     */
    public boolean addUploadFiles(ArrayList<AnimeListItem> hotanime){
        this.deleteHotAnime();
        SQLiteDatabase db = this.getWritableDatabase();

        db.beginTransaction();
        try {
            for (int i = 0; i < hotanime.size(); i++) {
                ContentValues contentValues = new ContentValues();

                contentValues.put("aid", hotanime.get(i).getId());
                contentValues.put("title", hotanime.get(i).getTitle());

                db.insert("hotanime", null, contentValues);
            }
            db.setTransactionSuccessful();
        }finally {
            db.endTransaction();
        }

        db.close();

        return true;
    }

    /* Get information about there's new in the xml or not.
     * @return boolean True if there's new item in the xml file.
     */
    public boolean isNew(ArrayList<AnimeListItem> hotAnime){
        SQLiteDatabase db = this.getReadableDatabase();

        int isIn = 1;
        for (int i = 0; i < hotAnime.size(); i++) {
            Cursor res = db.rawQuery("select count(*) from hotanime WHERE aid = ?", new String[]{hotAnime.get(i).getId()});
            res.moveToFirst();
            boolean ret = (res.getInt(0) > 0);

            if (ret) {
                isIn++;
            }
        }
        db.close();

        return isIn < hotAnime.size();
    }

    /* It's delete all hotanime
     * @return String Deleted rows number
     */
    public Integer deleteHotAnime(){
        SQLiteDatabase db = this.getWritableDatabase();
        Integer inte = db.delete("hotanime", null, null);

        db.close();
        return inte;
    }
}
