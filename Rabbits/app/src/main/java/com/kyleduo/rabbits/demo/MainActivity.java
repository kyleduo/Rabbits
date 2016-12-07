package com.kyleduo.rabbits.demo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kyleduo.rabbits.Rabbit;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.start_test_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Rabbit.from(MainActivity.this)
						.to("demo://com.kyleduo.rabbits/test?Testing=This is a 参数.")
						.start();
			}
		});
	}
}
