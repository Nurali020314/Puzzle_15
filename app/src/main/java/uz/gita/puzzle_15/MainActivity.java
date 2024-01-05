package uz.gita.puzzle_15;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

public class MainActivity extends AppCompatActivity {
    private Button[][] buttons ;
    private RelativeLayout buttonLayout;
    private ArrayList<Integer> values;
    private TextView step;
    private TextView score;
    private FrameLayout layoutWin;
    private MyShared pref;
    private StringBuilder saveNumber;
    private final static int N =4;
    private int x = 3;
    private int y = 3;
    private int count =0;
    private ArrayList<String> records;
    private Chronometer timer;
    private long deltaTimer;
    private boolean soundOnOff = true;
    private MediaPlayer mediaPlayer;
    private int gamePosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        loadValues();
        loadData();
    }

    @Override
    protected void onStart() {
        long saveTime = pref.getTimer();
        if (saveTime == 0 || gamePosition == 0){
            timer.setBase(SystemClock.elapsedRealtime());
            timer.start();
        }else{
            timer.setBase(SystemClock.elapsedRealtime()+saveTime );
            timer.start();
        }

        super.onStart();
    }

    @Override
    protected void onStop() {
        pref.setMoveCount(count);
        if (saveNumber.length()!=0)  saveNumber.delete(0,saveNumber.length());
        for (Button[] button : buttons) {
            for (Button btn : button) {
                if (!btn.getText().equals(""))
                    saveNumber.append(btn.getText()).append("#");
                else
                    saveNumber.append("0").append("#");
            }
        }
        pref.setSaveNumber(saveNumber.toString());
        timer.stop();
        pref.setTimer(timer.getBase() - SystemClock.elapsedRealtime());
        super.onStop();
    }
    private ArrayList<Integer> getSaveNumber(String str){
        ArrayList<Integer> ans = new ArrayList<>();
        for (String s: str.split("#")){
            ans.add(Integer.parseInt(s));
        }
        return ans;
    }
    private ArrayList<String> getRecordString(String str){
        ArrayList<String> ans = new ArrayList<>();
        Collections.addAll(ans, str.split("#"));
        return ans;
    }

    @SuppressLint("SetTextI18n")
    private void initViews(){
        gamePosition = getIntent().getIntExtra("gamePosition",0);
        buttons = new Button[4][4];
        buttonLayout = findViewById(R.id.buttons);
        step = findViewById(R.id.step);
        layoutWin = findViewById(R.id.layoutWin);
        score = findViewById(R.id.score);
        timer = findViewById(R.id.chronometr);
        pref = MyShared.getInctance(this);
        records = new ArrayList<>();

        count = gamePosition == 0 ? 0 : pref.getMoveCount();
        String save = pref.getSaveNumber();
        saveNumber = new StringBuilder(save);

        mediaPlayer = MediaPlayer.create(this,R.raw.click);


        String stringRecord = pref.getRecords();
        if (!stringRecord.equals("")){
            records.addAll(getRecordString(stringRecord));
        }

        buttonLayout.setBackgroundResource(R.drawable.img_1);
        for (int i = 0; i < buttonLayout.getChildCount(); i++) {
            int currentX = i / 4;
            int currentY = i % 4;
            Button currentButton = (Button) buttonLayout.getChildAt(i);
            currentButton.setTag(new Point(currentX,currentY));
            currentButton.setOnClickListener(this::onClick);
            buttons[currentX][currentY] = currentButton;
        }


         step.setText("Moves: "+ count);
        LinearLayoutCompat restart = findViewById(R.id.restart);
        findViewById(R.id.close).setOnClickListener(v -> finishAffinity());
        findViewById(R.id.refresh).setOnClickListener(this::refresh);
        restart.setOnClickListener(this::refresh);
        findViewById(R.id.back).setOnClickListener(v -> finish());
        FrameLayout pauseLayout = findViewById(R.id.pauseLayout);
        LinearLayoutCompat pause = findViewById(R.id.pause);
        LinearLayoutCompat play = findViewById(R.id.play);
        findViewById(R.id.home).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        pause.setOnClickListener(v -> {
            pauseLayout.setVisibility(View.VISIBLE);
            deltaTimer = SystemClock.elapsedRealtime();
            timer.stop();
            pause.setVisibility(View.GONE);
            play.setVisibility(View.VISIBLE);
            restart.setClickable(false);
        });

        play.setOnClickListener(v -> {
            pauseLayout.setVisibility(View.GONE);
            if (deltaTimer == 0) timer.setBase(SystemClock.elapsedRealtime());
            else  timer.setBase(timer.getBase() + (SystemClock.elapsedRealtime()-deltaTimer));
            timer.start();
            play.setVisibility(View.GONE);
            pause.setVisibility(View.VISIBLE);
            restart.setClickable(true);
        });

        ImageView soundOn = findViewById(R.id.soundOn);
        ImageView soundOff = findViewById(R.id.soundOff);

        soundOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundOn.setVisibility(View.GONE);
                soundOff.setVisibility(View.VISIBLE);
                soundOnOff = false;
            }
        });

        soundOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                soundOff.setVisibility(View.GONE);
                soundOn.setVisibility(View.VISIBLE);
                soundOnOff =true;
            }
        });
    }
    private void loadValues(){
        values = new ArrayList<>();
        if (saveNumber.length()==0 || gamePosition==0){
            for (int i = 0; i < 16; i++) {
                values.add(i);
            }
            shuffle();
        }else{
            values.addAll(getSaveNumber(saveNumber.toString()));
            saveNumber.delete(0,saveNumber.length());
        }
    }
    private void shuffle(){
        Collections.shuffle(values);
        while (!isSolvable(values)){
            Collections.shuffle(values);
        }
    }
    private void loadData(){
        for (int i = 0; i < buttonLayout.getChildCount();i++){
            if (values.get(i) == 0){
                x = i/4;
                y = i%4;
                buttons[x][y].setBackgroundResource(R.drawable.bg_null_button);
                buttons[x][y].setText("");
            }else{
                buttons[i/4][i%4].setText(String.valueOf(values.get(i)));
            }
        }
    }
    @SuppressLint("SetTextI18n")
    private void onClick(View view) {

        if (soundOnOff){
            mediaPlayer.start();
        }

        Button clickedButton = (Button) view;
        Point cordinate = (Point) clickedButton.getTag();

        boolean canMove = Math.abs(cordinate.getX()-x) + Math.abs(cordinate.getY()-y) == 1;
        if (canMove){
            buttons[x][y].setBackgroundResource(R.drawable.bg_button);
            buttons[x][y].setText(clickedButton.getText());
            clickedButton.setBackgroundResource(R.drawable.bg_null_button);
            clickedButton.setText("");
            x = cordinate.getX();
            y = cordinate.getY();
            step.setText("Moves: " + ++count);
        }

        if (x==3 && y==3){
            checkWin();
        }
    }
    @SuppressLint("SetTextI18n")
    private void checkWin(){
        for (int i = 0; i < 15; i++) {
            if(!buttons[i/4][i%4].getText().equals(String.valueOf(i+1))) return;
        }

        if (!records.isEmpty()){
            if (Integer.parseInt(records.get(records.size()-1))>count){
                records.add(String.valueOf(count));

                StringBuilder recordBuilder = new StringBuilder();
                    for (int i = records.size()-1; i >= 0; i--){
                        recordBuilder.append((records.size()-i)).append(". ").append(records.get(i)).append("#");
                    }
                pref.setRecods(recordBuilder.toString());
            }
        }else {
            records.add(String.valueOf(count));

            StringBuilder recordBuilder = new StringBuilder();

                for (int i = records.size()-1; i >= 0; i--){
                    recordBuilder.append((records.size()-i)).append(". ").append(records.get(i)).append("#");
                }
                pref.setRecods(recordBuilder.toString());

        }
        layoutWin.setVisibility(View.VISIBLE);
        score.setText("Score:" + count);
    }
    @SuppressLint("SetTextI18n")
    private void refresh(View view){
        layoutWin.setVisibility(View.GONE);
        count = 0;
        step.setText("Moves: " + count);
        buttons[x][y].setBackgroundResource(R.drawable.bg_button);
        timer.stop();
        timer.setBase(SystemClock.elapsedRealtime());
        timer.start();
        loadValues();
        loadData();
    }
    private int getInvCount(ArrayList<Integer> value){
        int count = 0;
        for (int i = 0; i < N*N-1; i++) {
            for (int j = i+1; j < N*N; j++) {
                if (value.get(i) != 0 && value.get(j)!=0 && value.get(i)>value.get(j))
                    count++;
            }
        }
        return count;
    }
    private int findFixPosition(ArrayList<Integer> value){
        for (int i = N*N-1; i >= 0; i--) {
            if (value.get(i) == 0){
                return N - N*N/4;
            }
        }
        return -1;
    }
    private boolean isSolvable(ArrayList<Integer> value){
        int invCount = getInvCount(value);
        int position = findFixPosition(value);
        if (position % 2 == 1){
            return invCount % 2 == 0;
        }else{
            return invCount % 2 == 1;
        }
    }

}