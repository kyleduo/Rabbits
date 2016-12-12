package com.kyleduo.rabbits.demo;

import android.content.Intent;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseActivity;

import java.util.zip.Inflater;

@Page(name = "MAIN")
public class MainActivity extends BaseActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewGroup view = (ViewGroup) LayoutInflater.from(this).inflate(R.layout.activity_main, null);
		setContentView(view);

		for (int i = 0; i < view.getChildCount(); i++) {
			View v = view.getChildAt(i);
			if (!(v instanceof TextView)) {
				continue;
			}
			v.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View view) {
					Rabbit.from(MainActivity.this)
							.to(((TextView) view).getText().toString())
							.start();
				}
			});
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu_main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.show_mappings) {
			Rabbit.from(this).to("/dump").start();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}
