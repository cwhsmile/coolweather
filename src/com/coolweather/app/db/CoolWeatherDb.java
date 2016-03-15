package com.coolweather.app.db;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;

public class CoolWeatherDb {
	private static final String DB_NAME = "cool_weather";
	private static final int VERSION = 1;
	private static CoolWeatherDb coolWeatherDb;
	private SQLiteDatabase db;

	private CoolWeatherDb(Context context) {
		// TODO Auto-generated constructor stub
		CoolWeatherOpenHelper dbHelper = new CoolWeatherOpenHelper(context,
				DB_NAME, null, VERSION);
		db = dbHelper.getWritableDatabase();
	}

	/**
	 * 单例获取db实例
	 * 
	 * @param context
	 * @return
	 */
	public synchronized static CoolWeatherDb getInstance(Context context) {
		if (coolWeatherDb == null) {
			coolWeatherDb = new CoolWeatherDb(context);
		}
		return coolWeatherDb;
	}

	public void saveProvince(Province province) {
		if (province != null) {
			ContentValues values = new ContentValues();
			values.put("province_name", province.getProvinceName());
			values.put("province_code", province.getProvinceCode());
			db.insert("Province", null, values);
		}
	}

	public List<Province> loadProvinces() {
		List<Province> list = new ArrayList<Province>();
		Cursor cursor = db
				.query("Province", null, null, null, null, null, null);
		while (cursor.moveToNext()) {
			Province p = new Province();
			p.setId(cursor.getInt(cursor.getColumnIndex("id")));
			p.setProvinceCode(cursor.getString(cursor
					.getColumnIndex("province_code")));
			p.setProvinceName(cursor.getString(cursor
					.getColumnIndex("province_name")));
			list.add(p);
		}
		return list;
	}

	public void saveCity(City city) {
		if (city != null) {
			ContentValues values = new ContentValues();
			values.put("city_code", city.getCityCode());
			values.put("city_name", city.getCityName());
			values.put("province_id", city.getProvinceId());

			db.insert("City", null, values);
		}
	}

	public List<City> loadCities(int provinceId) {
		List<City> list = new ArrayList<City>();
		Cursor cursor = db.query("city", null, "province_id=?",
				new String[] { String.valueOf(provinceId) }, null, null, null);
		while (cursor.moveToNext()) {
			City city = new City();
			city.setId(cursor.getInt(cursor.getColumnIndex("id")));
			city.setCityCode(cursor.getString(cursor
					.getColumnIndex("city_code")));
			city.setCityName(cursor.getString(cursor
					.getColumnIndex("city_name")));
			city.setProvinceId(cursor.getInt(cursor
					.getColumnIndex("province_id")));
			list.add(city);
		}
		return list;

	}

	public void saveCountry(Country country) {
		if (country != null) {
			ContentValues values = new ContentValues();
			values.put("country_name", country.getCountryName());
			values.put("country_code", country.getCountryCode());
			values.put("city_id", country.getCityId());
			db.insert("Country", null, values);
		}
	}

	public List<Country> loadCountries(int cityId) {
		List<Country> list = new ArrayList<Country>();
		Cursor cursor = db.query("Country", null, "city_id=?",
				new String[] { String.valueOf(cityId) }, null, null, null);
		while (cursor.moveToNext()) {
			Country country = new Country();
			country.setId(cursor.getInt(cursor.getColumnIndex("id")));
			country.setCountryCode(cursor.getString(cursor
					.getColumnIndex("country_code")));
			country.setCountryName(cursor.getString(cursor
					.getColumnIndex("country_name")));
			country.setCityId(cursor.getInt(cursor.getColumnIndex("city_id")));
			list.add(country);

		}
		return list;
	}

}
