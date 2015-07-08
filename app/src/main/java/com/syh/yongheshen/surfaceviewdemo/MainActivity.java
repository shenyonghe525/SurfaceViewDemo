package com.syh.yongheshen.surfaceviewdemo;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private SurfaceViewLuckPan luckPan;
    private ImageButton start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        luckPan= (SurfaceViewLuckPan) findViewById(R.id.pan_sf);
        start = (ImageButton) findViewById(R.id.imageButton);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              if (!luckPan.isStart()){
                  luckPan.luckStart(getPrize());
                  start.setImageResource(R.drawable.stop);
              }else {
                  if (!luckPan.isShouldEnd()){
                      luckPan.luckEnd();
                      start.setImageResource(R.drawable.start);
                  }
              }
            }
        });
    }

    /**
     * 0 -> 单反  5%
     * 1 -> ipad 4%
     * 2 -> 恭喜发财 39%
     * 3 -> 肾六 3%
     * 4 -> 衣服 10%
     * 5 -> 恭喜发财 39%
     * @return
     */
    private int getPrize(){
        int index = 0;
        Random random = new Random();
        int randomNub = random.nextInt(101);
        //5%的中奖率
        if (randomNub <= 5){
           index = 0;
        }else if (randomNub <= 9){
            index = 1;
        }else if (randomNub <= 48){
            index = 2;
        }else if (randomNub <= 51){
            index = 3;
        }else if (randomNub <= 61){
            index = 4;
        }else {
            index = 5;
        }
        return  index;
    }
}
