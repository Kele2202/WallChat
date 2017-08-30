package com.ferit.kele.wallchat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class MainActivity extends AppCompatActivity {

    EditText etNickname;
    ImageButton ibSettings;
    Button btnEnter;
    boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.setUpUI();
        LocationTask locTask = new LocationTask(getApplicationContext());
        locTask.start();
    }
    @Override
    protected void onResume(){
        super.onResume();
        if (flag)
        {
            flag = false;
            finish();
            overridePendingTransition(0, 0);
            startActivity(getIntent());
            overridePendingTransition(0, 0);
        }

    }
    private void setUpUI() {
        this.btnEnter = (Button) this.findViewById(R.id.btnEnter);
        this.ibSettings = (ImageButton) this.findViewById(R.id.ibSettings);
        this.etNickname = (EditText) this.findViewById(R.id.etNickname);

        ibSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                Intent intent = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intent);
            }
        });

        btnEnter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (etNickname.getText().toString().trim().length()!=0)
                {
                    Intent intent = new Intent(getApplicationContext(), WallActivity.class);
                    intent.putExtra("nickname", etNickname.getText().toString());
                    startActivity(intent);
                }
            }
        });
    }
}
