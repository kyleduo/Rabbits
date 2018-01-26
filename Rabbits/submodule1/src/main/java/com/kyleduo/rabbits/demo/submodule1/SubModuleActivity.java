package com.kyleduo.rabbits.demo.submodule1;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

/**
 * for Module
 * Created by kyleduo on 2017/5/8.
 */
//@Module(name = "sub1", standalone = SubConf.STANDALONE)
//@Page(name = "SUB_MODULE_1")
public class SubModuleActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_sub_module_1);

        if (SubConf.STANDALONE) {
//            RConfig config = RConfig.get()
//                    .scheme("demo")
//                    .defaultHost("rabbits.kyleduo.com")
//                    .forceUpdatePersist(BuildConfig.DEBUG);
//            Rabbit.init(config);
//
//            // syc setup
//            Rabbit.setup(this);
        }

        findViewById(R.id.textView).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Rabbit.from(SubModuleActivity.this).to(P_SUB1.SUB_MODULE_2).start();
            }
        });
    }
}
