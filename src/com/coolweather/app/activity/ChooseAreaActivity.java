package com.coolweather.app.activity;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.coolweather.app.R;
import com.coolweather.app.db.CoolWeatherDb;
import com.coolweather.app.model.City;
import com.coolweather.app.model.Country;
import com.coolweather.app.model.Province;
import com.coolweather.app.util.HttpCallbackListener;
import com.coolweather.app.util.HttpUtil;
import com.coolweather.app.util.Utility;

public class ChooseAreaActivity extends Activity {
	public static final int LEVEL_PROVINCE = 0;
	public static final int LEVEL_CITY = 1;
	public static final int LEVEL_COUNTRY = 2;
	private ProgressDialog progressDialog;
	private TextView title_text;
	private ListView list_view;
	private ArrayAdapter<String> adapter;
	private CoolWeatherDb db;
	private List<String> dataList = new ArrayList<String>();
	private List<Province> provinces;
	private List<City> cities;
	private List<Country> countries;
	private Province selectedProvince;
	private City selectedCity;
	// 当前级别
	private int currentLevel = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.choose_area);
		db = CoolWeatherDb.getInstance(this);
		title_text = (TextView) findViewById(R.id.title_text);
		list_view = (ListView) findViewById(R.id.list_view);
		adapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_list_item_1, dataList);
		list_view.setAdapter(adapter);
		list_view.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (currentLevel == LEVEL_PROVINCE) {
					selectedProvince = provinces.get(arg2);
					queryCities();
				} else if (currentLevel == LEVEL_CITY) {
					selectedCity = cities.get(arg2);
					queryCountries();
				} else if (currentLevel == LEVEL_COUNTRY) {
					String countryCode = countries.get(arg2).getCountryCode();
					Intent intent = new Intent(ChooseAreaActivity.this,
							WeatherActivity.class);
					intent.putExtra("country_code", countryCode);
					startActivity(intent);
					finish();
					
				}

			}
		});
		queryProvinces();
	}

	private void queryProvinces() {
		provinces = db.loadProvinces();
		if (provinces.size() > 0) {
			dataList.clear();
			for (Province p : provinces) {
				dataList.add(p.getProvinceName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText("中国");
			currentLevel = LEVEL_PROVINCE;
		} else {
			queryFromServer(null, "province");
		}

	}

	private void queryCities() {
		cities = db.loadCities(selectedProvince.getId());
		if (cities.size() > 0) {
			dataList.clear();
			for (City c : cities) {
				dataList.add(c.getCityName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectedProvince.getProvinceName());
			currentLevel = LEVEL_CITY;
		} else {
			queryFromServer(selectedProvince.getProvinceCode(), "city");
		}

	}

	private void queryCountries() {
		countries = db.loadCountries(selectedCity.getId());
		if (countries.size() > 0) {
			dataList.clear();
			for (Country c : countries) {
				dataList.add(c.getCountryName());
			}
			adapter.notifyDataSetChanged();
			list_view.setSelection(0);
			title_text.setText(selectedCity.getCityName());
			currentLevel = LEVEL_COUNTRY;
		} else {
			queryFromServer(selectedCity.getCityCode(), "country");
		}

	}

	private void queryFromServer(final String code, final String type) {
		String address;
		if (!TextUtils.isEmpty(code)) {
			address = "http://www.weather.com.cn/data/list3/city" + code
					+ ".xml";
		} else {
			address = "http://www.weather.com.cn/data/list3/city.xml";
		}
		showProgressDialog();
		HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {

			@Override
			public void onFinish(String response) {
				// TODO Auto-generated method stub
				boolean flag = false;
				if ("province".equals(type)) {
					flag = Utility.handleProvincesResponse(db, response);
				} else if ("city".equals(type)) {
					flag = Utility.handleCitiesResponse(db, response,
							selectedProvince.getId());
				} else if ("country".equals(type)) {
					flag = Utility.handleCountiesResponse(db, response,
							selectedCity.getId());
				}
				if (flag) {
					// 回到主线程进行ui处理
					runOnUiThread(new Runnable() {

						@Override
						public void run() {
							// TODO Auto-generated method stub
							closeProgressDialog();
							if ("province".equals(type)) {
								queryProvinces();
							} else if ("city".equals(type)) {
								queryCities();
							} else if ("country".equals(type)) {
								queryCountries();
							}

						}
					});
				}
			}

			@Override
			public void onError(Exception e) {
				// TODO Auto-generated method stub
				runOnUiThread(new Runnable() {

					@Override
					public void run() {
						// TODO Auto-generated method stub
						closeProgressDialog();
						Toast.makeText(ChooseAreaActivity.this, "加载失败",
								Toast.LENGTH_LONG).show();
					}
				});
			}
		});
	}

	public void showProgressDialog() {
		if (progressDialog == null) {
			progressDialog = new ProgressDialog(this);
			progressDialog.setMessage("正在加载...");
			progressDialog.setCanceledOnTouchOutside(false);
		}
		progressDialog.show();
	}

	private void closeProgressDialog() {
		if (progressDialog != null) {
			progressDialog.dismiss();
		}
	}

	// 按back键的效果
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		if (currentLevel == LEVEL_COUNTRY) {
			queryCities();
		} else if (currentLevel == LEVEL_CITY) {
			queryProvinces();
		} else {
			finish();
		}
	}

}
