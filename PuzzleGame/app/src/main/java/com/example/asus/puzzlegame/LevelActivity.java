package com.example.asus.puzzlegame;

import android.app.Activity;
import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

public class LevelActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_level);

        ImageButton bNewGame9 = (ImageButton) findViewById(R.id.bLevel9);
        bNewGame9.setOnClickListener(onClickListener);
        ImageButton bNewGame15 = (ImageButton) findViewById(R.id.bLevel15);
        bNewGame15.setOnClickListener(onClickListener);

        ImageButton bBackMenu = (ImageButton) this.findViewById(R.id.bBackMenu);
        bBackMenu.setOnClickListener(onClickListener);

    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.bLevel9:
                    newGame(3);
                    break;
                case R.id.bLevel15:
                    newGame(4);
                    break;
                case R.id.bBackMenu:
                    backMenu();
                    break;
                default:
                    break;
            }

        }
    };

    private void newGame(int level) {
        Intent intent = new Intent();
        switch (level) {
            case 3:
                intent = new Intent(LevelActivity.this, GameActivity9.class);
                break;
            case 4:
                intent = new Intent(LevelActivity.this, GameActivity.class);
                break;

        }
        intent.putExtra("keyLevel", level);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    public void backMenu() {
        Intent intent = new Intent(LevelActivity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
}
