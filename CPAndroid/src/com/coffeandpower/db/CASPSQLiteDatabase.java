package com.coffeandpower.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CASPSQLiteDatabase extends SQLiteOpenHelper
{

	private static final String DATABASE_NAME = "candp.db";

	// Table MapUserData
	public static final String TABLE_MAP_USER_DATA = "map_user_data";
	public static final String COLUMN_ID = "_id";
	// in this column foursquareId is written, for getting data from db
	public static final String COLUMN_CONTROL_FQS_ID = "controlfqsid";
	public static final String COLUMN_CHECK_IN_ID = "checkInId";
	public static final String COLUMN_USER_ID = "userId";
	public static final String COLUMN_NICK_NAME = "nickName";
	public static final String COLUMN_STATUS_TEXT = "statusText";
	public static final String COLUMN_PHOTO = "photo";
	public static final String COLUMN_MAJOR_JOB = "majorJobCategory";
	public static final String COLUMN_MINOR_JOB = "minorJobCategory";
	public static final String COLUMN_HEAD_LINE = "headLine";
	public static final String COLUMN_FILE_NAME = "fileName";
	public static final String COLUMN_LAT = "lat";
	public static final String COLUMN_LNG = "lng";
	public static final String COLUMN_CHECKED_IN = "checkedIn";
	public static final String COLUMN_FOURSQUARE_ID = "foursquareId";
	public static final String COLUMN_VENUE_NAME = "venueName";
	public static final String COLUMN_CHECK_IN_COUNT = "checkInCount";
	public static final String COLUMN_SKILLS = "skills";
	public static final String COLUMN_MET = "met";

	private static final int DATABASE_VERSION = 1;

	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table " + TABLE_MAP_USER_DATA + "( " + COLUMN_ID
			+ " integer primary key asc, " + COLUMN_CONTROL_FQS_ID + " text, " + COLUMN_CHECK_IN_ID + " integer, "
			+ COLUMN_USER_ID + " integer, " + COLUMN_NICK_NAME + " text, " + COLUMN_STATUS_TEXT + " text, "
			+ COLUMN_PHOTO + " text, " + COLUMN_MAJOR_JOB + " text, " + COLUMN_MINOR_JOB + " text, " + COLUMN_HEAD_LINE
			+ " text, " + COLUMN_FILE_NAME + " text, " + COLUMN_LAT + " text, " + COLUMN_LNG + " text, "
			+ COLUMN_CHECKED_IN + " integer, " + COLUMN_FOURSQUARE_ID + " text, " + COLUMN_VENUE_NAME + " text, "
			+ COLUMN_CHECK_IN_COUNT + " integer, " + COLUMN_SKILLS + " text, " + COLUMN_MET + " text);";

	public CASPSQLiteDatabase(Context context)
	{
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}

	public CASPSQLiteDatabase(Context context, String name, CursorFactory factory, int version)
	{
		super(context, name, factory, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db)
	{
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
	{

		db.execSQL("DROP TABLE IF EXISTS " + TABLE_MAP_USER_DATA);
		onCreate(db);
	}

}
