package com.kyleduo.rabbits.demo;

import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;

@Page(name = "MAIN")
public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		findViewById(R.id.start_test_bt).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				/* Testing update mappings.
				String json = "{\n" +
						"  \"version\": 2,\n" +
						"  \"mappings\": {\n" +
						"    \"demo://com.kyleduo.rabbits/test\": \"TEST\",\n" +
						"    \"demo://com.kyleduo.rabbits\": \"MAIN\",\n" +
						"    \"demo://com.kyleduo.rabbits/\": \"MAIN\"\n" +
						"  }\n" +
						"}";

				File file = new File(Environment.getExternalStorageDirectory(), "testing.json");
				Rabbit.updateMappings(MainActivity.this, file);*/

				Rabbit.from(MainActivity.this)
						.to("demo://kyleduo.com/rabbits/test?Testing=This is a 参数.")
						.start();
			}
		});
	}
}
