package com.kyleduo.rabbits.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;

@Page(name = "TEST")
public class TestActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_test);

		TextView tv = (TextView) findViewById(R.id.params_tv);
		tv.setText(getIntent().getStringExtra("Testing"));

		findViewById(R.id.back_home_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Rabbit.from(TestActivity.this)
						.to("")
						.clearTop()
						.start();
			}
		});
	}
}
