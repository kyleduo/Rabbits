package com.kyleduo.rabbits.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.kyleduo.rabbits.annotations.Page;

@Page(name = "TEST")
public class TestActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		TextView tv = (TextView) findViewById(R.id.params_tv);
		tv.setText(getIntent().getStringExtra("Testing"));
	}
}
