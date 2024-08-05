package com.app.dognutrition;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.app.dognutrition.ui.LoginActivity;
import com.app.dognutrition.utils.SQLiteUtils;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Button btnMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Intatiate the database
        SQLiteUtils dbHelper = new SQLiteUtils(this);

        btnMain = findViewById(R.id.btnMain);

        btnMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

}