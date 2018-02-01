package com.kyleduo.rabbits.demo;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.kyleduo.rabbits.Rabbit;
import com.kyleduo.rabbits.annotations.Page;
import com.kyleduo.rabbits.demo.base.BaseActivity;

@Page("/")
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
                    String url = ((TextView) view).getText().toString();
                    Toast.makeText(MainActivity.this, url, Toast.LENGTH_SHORT).show();
//                    Rabbit.from(MainActivity.this)
//                            .to(url)
//                            .setTransitionAnimations(new int[]{R.anim.fadein, R.anim.fadeout})
//                            .start();
                    Rabbit.from(MainActivity.this)
                            .to(url)
                            .putExtra("param", "testing")
                            .start();
                }
            });
        }

        findViewById(R.id.update_bt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Rabbit.updateMappings(MainActivity.this, MappingsSource.fromJson("{\n" +
//                        "  \"allowed_hosts\": [\n" +
//                        "    \"allowed.kyleduo.com\"\n" +
//                        "  ],\n" +
//                        "  \"mappings\": {\n" +
//                        "    \"demo://rabbits.kyleduo.com\": \"MAIN\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/test\": \"DUMP\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/test/listing\": \"DUMP\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/test/{testing}\": \"DUMP\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/test_fragment\": \"DUMP\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/second/{id:l}\": \"DUMP\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/common\": \"COMMON\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/dump\": \"DUMP\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/web\": \"DUMP\",\n" +
//                        "    \"demo://rabbits.kyleduo.com/crazy/{name:s}/{age:i}/{finish:b}/end\": \"DUMP\"\n" +
//                        "  }\n" +
//                        "}").fullyUpdate(false));
            }
        });
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
