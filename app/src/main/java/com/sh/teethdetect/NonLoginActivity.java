package com.sh.teethdetect;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class NonLoginActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_non_login);

        bottomNavigationView = findViewById(R.id.navi_bar);

        bottomNavigationView.setOnNavigationItemReselectedListener(new BottomNavigationView.OnNavigationItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()){
                    case R.id.item_fragment1:
                        Intent intent = new Intent(NonLoginActivity.this, CariesActivity.class);
                        startActivity(intent);

                        break;
                    case R.id.item_fragment2:
                        Intent intent1 = new Intent(NonLoginActivity.this, VideodetectActivity.class);
                        startActivity(intent1);
                        break;

                }
                return;
            }
        });
    }

    public void onBackPressed() {

        super.onBackPressed();
    }

}