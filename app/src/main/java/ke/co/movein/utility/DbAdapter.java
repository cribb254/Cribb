package ke.co.movein.utility;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import ke.co.movein.R;

public class DbAdapter {

	private static final String DATABASE_NAME = "movein_co";
	private static final int DATABASE_VERSION = 9;
	
	private DbHelper mDbHelper;
	private SQLiteDatabase mDb;

	private Context mCtx;

	private static class DbHelper extends SQLiteOpenHelper {

		Context context;

		public DbHelper(Context context){
			super(context, DATABASE_NAME, null, DATABASE_VERSION);
			this.context = context;
		}

		@Override
		public void onCreate(SQLiteDatabase db) {
			db.execSQL(context.getString(R.string.create_tbl_post));
			db.execSQL(context.getString(R.string.create_tbl_image));
			db.execSQL(context.getString(R.string.create_tbl_pty_type));
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
			db.execSQL("DROP TABLE IF EXISTS tbl_post");
			db.execSQL("DROP TABLE IF EXISTS tbl_image");
			db.execSQL("DROP TABLE IF EXISTS tbl_pty_type");

			onCreate(db);
		}
	}

	public DbAdapter(Context ctx) {
		this.mCtx = ctx;
	}

	public DbAdapter open() throws SQLException {
		mDbHelper = new DbHelper(mCtx);
		mDb = mDbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		if (mDbHelper != null) {
			mDbHelper.close();
		}
	}

	public long addEntry(String tableName, JSONObject jObt){
		ContentValues cv = new ContentValues();

		try {
			for (String key : getKeys(jObt)) {
				cv.put(key, jObt.getString(key));
			}
		} catch (Exception ex) {
			ex.getMessage();
		}

		return mDb.insertOrThrow(tableName, null, cv);
	}

	public Cursor browseTable(String tableName) throws SQLException{
		String query =
				String.format("SELECT * FROM  %s", tableName);

		Cursor mCursor = mDb.rawQuery(query,null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

    public String getPtyType(int id) throws SQLException{
        String query =
                String.format("SELECT name FROM tbl_pty_type WHERE id = '%d'", id);

        try{
            String result;
            Cursor mCursor = mDb.rawQuery(query,null);
            mCursor.moveToFirst();
            result = mCursor.getString(0);
            mCursor.close();
            return result;
        }catch (Exception ex){
            return "";
        }
    }


    public String getImagePath(int postID) throws SQLException{
		String query =
				String.format("SELECT path FROM tbl_image WHERE tbl_post_id = '%d' AND status = '1'", postID);

		try{
			String result;
			Cursor mCursor = mDb.rawQuery(query,null);
			mCursor.moveToFirst();
			result = mCursor.getString(0);
			mCursor.close();
			return result;
		}catch (Exception ex){
			return null;
		}
	}

	public List<String> getImages(int postId) throws SQLException {

		String query =
				String.format("SELECT path FROM tbl_image WHERE tbl_post_id = '%d' AND status = '1'", postId);
		Cursor crs = mDb.rawQuery(query, null);
		if(crs.moveToFirst()) {
			List<String> list = new ArrayList<>();

			for (int i = 0; i < crs.getCount(); i++) {
				crs.moveToPosition(i);
				list.add(crs.getString(0));
			}
			return list;
		}
		crs.close();

		return null;
	}

	public Cursor getPostList(int propertyType) throws SQLException{

		String query = propertyType == 0 ?
				"SELECT * FROM tbl_post WHERE a_units > 0 ORDER BY last_update DESC" :
				String.format("SELECT * FROM tbl_post WHERE tbl_pty_type_id = '%d' AND a_units > 0 ORDER BY last_update DESC", propertyType);

		Cursor mCursor = mDb.rawQuery(query,null);

		if (mCursor != null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

    public Cursor getPost(int postID) throws SQLException{

        String query =
                String.format("SELECT * FROM tbl_post WHERE id = '%d'", postID);

        Cursor mCursor = mDb.rawQuery(query,null);

        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

	public int updateEntry(String tableName, JSONObject jObt){
		ContentValues cv = new ContentValues();

		try {
			for (String key : getKeys(jObt)) {
				cv.put(key, jObt.getString(key));
			}
		} catch (Exception ex) {
			ex.getMessage();
		}

		return mDb.update(tableName, cv, "id ='"+cv.getAsInteger("id")+"'", null);
	}


	public String getLastSync(String tableName) throws SQLException{
		String query =
				String.format("SELECT last_update FROM %s ORDER BY last_update DESC LIMIT 1", tableName);

		Cursor mCursor = mDb.rawQuery(query,null);

		if (mCursor != null && mCursor.moveToFirst()) {
			String lastSynced = mCursor.getString(0);
			mCursor.close();
			return lastSynced;
		}
		return "2016-01-01 00:00:00";
	}

	public boolean isDbEmpty(){
		String query = "SELECT id FROM tbl_post WHERE a_units > 0 ORDER BY last_update DESC LIMIT 1";

		Cursor mCursor = mDb.rawQuery(query,null);

		boolean bln = !mCursor.moveToFirst();
		mCursor.close();

		return bln;
	}

	public int markMsgAsRead(int rowID){
		ContentValues cv = new ContentValues();
		cv.put("status", 0);
		return mDb.update("tbl_msg", cv, "msg_id ='" + rowID + "'", null);
	}

	public boolean delete(String tableName) {

		int doneDelete = 0;
		doneDelete = mDb.delete(tableName, null, null);
		//Log.w("DELETED ROWS", Integer.toString(doneDelete));
		return doneDelete > 0;
	}

	List<String> getKeys(JSONObject jsonObject){
		Iterator<String> iterator = jsonObject.keys();
		List<String> list = new ArrayList<>();

		do{
			list.add(iterator.next());
		}while (iterator.hasNext());

		return list;
	}

}