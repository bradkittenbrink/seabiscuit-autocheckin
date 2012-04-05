package com.coffeandpower.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.coffeeandpower.cont.MapUserData;

public class CAPDao {


	// Database fields
	private SQLiteDatabase database;
	private CASPSQLiteDatabase dbHelper;



	public CAPDao(Context context) {
		dbHelper = new CASPSQLiteDatabase(context);
	}


	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}


	public void close() {
		dbHelper.close();
	}


	/**
	 * Put getCheckedInBoundsOverTime data into database, with uniq foursquareId, in this case COLUMN_CONTROL_FQS_ID
	 * @param MapUserData object
	 * @param foursquareId
	 * @return
	 */
	public boolean putMapsUsersData (MapUserData mud, String foursquareId){

		ContentValues cv = new ContentValues();

		cv.put(CASPSQLiteDatabase.COLUMN_CONTROL_FQS_ID, foursquareId); // important!!!

		cv.put(CASPSQLiteDatabase.COLUMN_CHECK_IN_ID, mud.getCheckInId());
		cv.put(CASPSQLiteDatabase.COLUMN_USER_ID , mud.getUserId());
		cv.put(CASPSQLiteDatabase.COLUMN_CHECK_IN_COUNT , mud.getCheckInCount());
		cv.put(CASPSQLiteDatabase.COLUMN_CHECKED_IN , mud.getCheckedIn());
		cv.put(CASPSQLiteDatabase.COLUMN_NICK_NAME , mud.getNickName());
		cv.put(CASPSQLiteDatabase.COLUMN_STATUS_TEXT , mud.getStatusText());
		cv.put(CASPSQLiteDatabase.COLUMN_PHOTO , mud.getPhoto());
		cv.put(CASPSQLiteDatabase.COLUMN_MAJOR_JOB , mud.getMajorJobCategory());
		cv.put(CASPSQLiteDatabase.COLUMN_MINOR_JOB , mud.getMinorJobCategory());
		cv.put(CASPSQLiteDatabase.COLUMN_HEAD_LINE , mud.getHeadLine());
		cv.put(CASPSQLiteDatabase.COLUMN_FILE_NAME , mud.getFileName());
		cv.put(CASPSQLiteDatabase.COLUMN_FOURSQUARE_ID , mud.getFoursquareId());
		cv.put(CASPSQLiteDatabase.COLUMN_VENUE_NAME , mud.getVenueName());
		cv.put(CASPSQLiteDatabase.COLUMN_SKILLS , mud.getSkills());
		cv.put(CASPSQLiteDatabase.COLUMN_LAT , mud.getLat()+"");
		cv.put(CASPSQLiteDatabase.COLUMN_LNG , mud.getLng()+"");
		if (mud.isMet()){
			cv.put(CASPSQLiteDatabase.COLUMN_MET , "YES");
		} else {
			cv.put(CASPSQLiteDatabase.COLUMN_MET , "NO");
		}

		database.insert(CASPSQLiteDatabase.TABLE_MAP_USER_DATA, null, cv);

		return true;
	}


	/**
	 * Get MapUserData array from database, with foursquareId
	 * @param foursquareId
	 * @return
	 */
	public ArrayList<MapUserData> getMapsUsersData (String foursquareId){

		ArrayList<MapUserData> tempArray = new ArrayList<MapUserData>();

		Cursor c = database.rawQuery("SELECT * from " + CASPSQLiteDatabase.TABLE_MAP_USER_DATA + " WHERE " + CASPSQLiteDatabase.COLUMN_CONTROL_FQS_ID + "='" + foursquareId + "'", null);
		if (c != null) {
			while (c.moveToNext()) {

				int checkInId = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_CHECK_IN_ID) == -1 ) ? 0 : c.getInt(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_CHECK_IN_ID)));
				int userId= (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_USER_ID) == -1 ) ? 0 : c.getInt(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_USER_ID)));
				int checkInCount = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_CHECK_IN_COUNT) == -1 ) ? 0 : c.getInt(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_CHECK_IN_COUNT)));
				int checkedIn = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_CHECKED_IN) == -1 ) ? 0 : c.getInt(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_CHECKED_IN)));
				String nickName = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_NICK_NAME) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_NICK_NAME)));
				String statusText = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_STATUS_TEXT) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_STATUS_TEXT)));
				String photo = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_PHOTO) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_PHOTO)));
				String majorJobCategory = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_MAJOR_JOB) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_MAJOR_JOB)));
				String minorJobCategory = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_MINOR_JOB) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_MINOR_JOB)));
				String headLine = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_HEAD_LINE) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_HEAD_LINE)));
				String fileName = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_FILE_NAME) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_FILE_NAME)));
				String foursquareIdS = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_FOURSQUARE_ID) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_FOURSQUARE_ID)));
				String venueName = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_VENUE_NAME) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_VENUE_NAME)));
				String skills = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_SKILLS) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_SKILLS)));

				String latS = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_LAT) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_LAT)));
				String lngS = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_LNG) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_LNG)));
				String metS = (( c.getColumnIndex(CASPSQLiteDatabase.COLUMN_MET) == -1 ) ? "" : c.getString(c.getColumnIndex(CASPSQLiteDatabase.COLUMN_MET)));

				double lat = 0.0d;
				double lng = 0.0d; 

				try {
					lat = Double.parseDouble(latS);
					lng = Double.parseDouble(lngS);
				} catch (NumberFormatException e){
					e.printStackTrace();
				}

				boolean met = metS.equals("YES") ? true : false;

				tempArray.add(new MapUserData(checkInId, userId, nickName, statusText, photo, majorJobCategory, minorJobCategory, 
						headLine, fileName, lat, lng, checkedIn, foursquareIdS, venueName, checkInCount, skills, met));
			}
		}
		c.close();

		return tempArray;
	}


	/**
	 * Delete all data from table
	 * @param tableName
	 */
	public void deleteAllFromTable (String tableName){

		database.rawQuery("DELETE FROM " + tableName, null);
	}

}
