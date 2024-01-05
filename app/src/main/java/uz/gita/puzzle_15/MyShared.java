package uz.gita.puzzle_15;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.SystemClock;

public class MyShared {
    private static SharedPreferences pref;
    private static MyShared myPref;
    private MyShared(){

    }
    public static  MyShared getInctance(Context context){
        if (myPref == null){
            pref = context.getSharedPreferences("Puzzle",Context.MODE_PRIVATE);
            myPref = new MyShared();
        }
        return myPref;
    }

    public void setMoveCount(int count){
        pref.edit().putInt("step",count).apply();
    }

    public void setSaveNumber(String str){
        pref.edit().putString("saveNumber",str).apply();
    }
    public void setRecods(String str){
        pref.edit().putString("records",str).apply();
    }

    public void setTimer(long time){
        pref.edit().putLong("timer",time).apply();
    }


    public int getMoveCount(){
        return pref.getInt("step",0);
    }

    public String getSaveNumber(){
        return pref.getString("saveNumber","");
    }

    public String getRecords(){
        return pref.getString("records","");
    }

    public long getTimer(){
        return pref.getLong("timer", 0);
    }




}
