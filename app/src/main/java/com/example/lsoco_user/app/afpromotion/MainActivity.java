package com.example.lsoco_user.app.afpromotion;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(CacheUtil.wasJsonCached(this) || ConnectionUtil.isConnected(this)) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frag_holder, new PromotionListFragment(), null)
                    .commit();
        } else { // no cache and no connection
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.main_frag_holder, new BlankFragment(), null)
                    .commit();
        }
    }
}