package uz.gita.puzzle_15;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);

        MyShared pref = MyShared.getInctance(this);
        String records = pref.getRecords();
        StringBuilder sb = new StringBuilder();

        if (!records.equals("")){
           String[] s = records.split("#");
           if (s.length>3){
               for (int i = 0; i < 3; i++) {
                   sb.append(s[i]).append("\n");
               }
           }else {
               for (String str: s ){
                   sb.append(str).append("\n");
               }
           }
        }

        TextView tv = findViewById(R.id.record1);
        tv.setText(sb.toString());

    }
}