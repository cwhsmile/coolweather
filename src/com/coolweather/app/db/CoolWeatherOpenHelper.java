package com.coolweather.app.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class CoolWeatherOpenHelper extends SQLiteOpenHelper {

	public CoolWeatherOpenHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onCreate(SQLiteDatabase arg0) {
		// TODO Auto-generated method stub
		// 省
		String sql = "create table Province("
				+ "id integer primary key autoincrement,"
				+ "province_name text,province_code text)";
		// 市
		String sql2 = "create table City("
				+ "id integer primary key autoincrement,"
				+ "city_name text,city_code text,province_id integer)";
		// 县
		String sql3 = "create table Country("
				+ "id integer primary key autoincrement,"
				+ "country_name text,country_code text,city_id integer)";
		arg0.execSQL(sql);
		arg0.execSQL(sql2);
		arg0.execSQL(sql3);
	}

	@Override
	public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
		// TODO Auto-generated method stub

	}

}
