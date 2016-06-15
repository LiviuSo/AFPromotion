package com.example.lsoco_user.app.afpromotion.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.lsoco_user.app.afpromotion.util.CacheUtil;
import com.example.lsoco_user.app.afpromotion.util.ConnectionUtil;
import com.example.lsoco_user.app.afpromotion.R;
import com.example.lsoco_user.app.afpromotion.fragment.BlankFragment;
import com.example.lsoco_user.app.afpromotion.fragment.PromotionListFragment;

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