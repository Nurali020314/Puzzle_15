package uz.gita.puzzle_15;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("gamePosition",1);
                startActivity(intent);
            }
        });

        findViewById(R.id.newGame).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                intent.putExtra("gamePosition",0);
                startActivity(intent);
            }
        });

        findViewById(R.id.record).setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, RecordActivity.class);
            startActivity(intent);
        });

        findViewById(R.id.info).setOnClickListener( v -> {
            Intent intent = new Intent(HomeActivity.this, InfoActivity.class);
            startActivity(intent);
        });






    }
}