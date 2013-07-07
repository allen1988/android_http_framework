package com.allen.http;

import java.io.InputStream;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.allen.http.MyParser.Item;
import com.allen.http.framework.HttpAsyncTaskManager;
import com.allen.http.framework.InputStreamTaskHandler;
import com.allen.http.framework.JsonObjectTaskHandler;
import com.allen.http.framework.StringTaskHandler;

public class MainActivity extends Activity implements OnClickListener {
	Button btn1, btn2, btn3, btn4;

	HttpAsyncTaskManager http;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		btn3 = (Button) findViewById(R.id.btn3);
		btn4 = (Button) findViewById(R.id.btn4);
		btn1.setOnClickListener(this);
		btn2.setOnClickListener(this);
		btn3.setOnClickListener(this);
		btn4.setOnClickListener(this);
		http = new HttpAsyncTaskManager(this);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn1:// handler string
			String url = "http://www.12306.cn/mormhweb/kyfw/ypcx/";
			http.request(url, new StringTaskHandler() {
				@Override
				public void onNetError() {
					// TODO Auto-generated method stub
					System.out.println("---------neterror---------");
				}

				@Override
				public void onSuccess(String result) {
					// TODO Auto-generated method stub
					System.out.println("#####" + result);
				}

				@Override
				public void onFail() {
					// TODO Auto-generated method stub
					System.out.println("---------onFail---------");
				}
			});
			break;

		case R.id.btn2:// handler jsonObject
			String url2 = "https://www.googleapis.com/customsearch/v1?key=AIzaSyBmSXUzVZBKQv9FJkTpZXn0dObKgEQOIFU&cx=014099860786446192319:t5mr0xnusiy&q=AndroidDev&alt=json&searchType=image";
			http.request(url2, new JsonObjectTaskHandler() {

				@Override
				public void onSuccess(JSONObject result) {
					// TODO Auto-generated method stub
					try {
						System.out.println("----" + result.getString("kind"));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

				}

				@Override
				public void onNetError() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFail() {
					// TODO Auto-generated method stub

				}
			});
			break;

		case R.id.btn3:// handler jsonArray

			break;

		case R.id.btn4:// handler xml Stream
			String url3 = "http://www.11huaiyun.com/sitemap.xml";
			http.request(url3, new InputStreamTaskHandler() {

				@Override
				public void onSuccess(InputStream result) {
					// TODO Auto-generated method stub
					try {
						List<Item> list = new MyParser().parse(result, "utf-8");
						System.out.println("****"+list);
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}

				@Override
				public void onNetError() {
					// TODO Auto-generated method stub

				}

				@Override
				public void onFail() {
					// TODO Auto-generated method stub

				}
			});
			break;
		}
	}
}
