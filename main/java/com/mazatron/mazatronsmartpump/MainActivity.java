package com.mazatron.mazatronsmartpump;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mazatron.mazatronsmartpump.R;


public class MainActivity extends AppCompatActivity {

    public EditText gsmNumber;
    public String numberGSM;
    final String  MY_PREFS_NAME = "MyMazatronNumber";
    public String finalNumber;
    ImageView mMazatronView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefGet =  getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        finalNumber  = prefGet.getString("GSMnumber",null);

        if (finalNumber != null){
            Intent PumpIntent = new Intent(MainActivity.this, PumpActivity.class);
            PumpIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PumpIntent.putExtra("FinalNumber",finalNumber);
            finish();
            startActivity(PumpIntent);
        }

        gsmNumber = findViewById(R.id.gsmnumber);
        gsmNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasfocus) {
                if (hasfocus){
                    gsmNumber.setHint("");
                }else{
                    gsmNumber.setHint("+91");
                }
            }
        });

        mMazatronView = findViewById(R.id.mazlogo);
        mMazatronView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentWeb = new Intent();
                intentWeb.setAction(Intent.ACTION_VIEW);
                intentWeb.addCategory(Intent.CATEGORY_BROWSABLE);
                intentWeb.setData(Uri.parse("https://www.mazatron.com/index.php?route=information/contact"));
                startActivity(intentWeb);
            }
        });
    }

    public void save_number(View view) {
        numberGSM = gsmNumber.getText().toString();
        if (numberGSM.length()== 10) {
            SharedPreferences pref = getApplicationContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
            SharedPreferences.Editor editor = pref.edit();
            editor.putString("GSMnumber", numberGSM);
            editor.apply();

            Intent PumpIntent2 = new Intent(MainActivity.this, PumpActivity.class);
            PumpIntent2.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            PumpIntent2.putExtra("FinalNumber", numberGSM);
            finish();
            startActivity(PumpIntent2);
        }else {
            Toast.makeText(getApplicationContext(), "सही number लिखें !",  Toast.LENGTH_SHORT).show();
        }
    }

}
