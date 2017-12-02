package th.ac.tu.cs.puzzlegame;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class MenuActivity extends Activity {
    boolean check;
    ImageView bСontinue;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_menu);
        ImageView bGame = (ImageView) findViewById(R.id.bNewGame);
        bGame.setOnClickListener(onClickListener);
        bСontinue = (ImageView) findViewById(R.id.bСontinue);
        bСontinue.setOnClickListener(onClickListener);
        if(appGetFirstTimeRun() == 0){
            bСontinue.setVisibility(View.GONE);
        }
        ImageView bExit = (ImageView) findViewById(R.id.bExit);
        bExit.setOnClickListener(onClickListener);
        ImageView bHelp = (ImageView) findViewById(R.id.bHelp);
        bHelp.setOnClickListener(onClickListener);
        ImageView bScore = (ImageView) findViewById(R.id.bScore);
        bScore.setOnClickListener(onClickListener);
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.bNewGame:
                    newGame();
                    break;
                case R.id.bСontinue:
                    check = true;
                    continueGame();
                    break;
                case R.id.bExit:
                    exit();
                    break;
                case R.id.bHelp:
                    help();
                    break;
                case R.id.bScore:
                    Score();
                    break;
                default:
                    break;
            }
        }

    };


    private void help() {
        Intent intent = new Intent(MenuActivity.this, HelpActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void Score() {
        Intent intent = new Intent(MenuActivity.this, Level2Activity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }


    private void newGame() {
        Intent intent = new Intent(MenuActivity.this, LevelActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void continueGame() {
        try {
            int n = Integer.parseInt(readFile("fileN"));
            Intent intent = new Intent();
            switch (n) {
                case 3:
                    intent = new Intent(MenuActivity.this, GameActivity9.class);
                    break;
                case 4:
                    intent = new Intent(MenuActivity.this, GameActivity.class);
                    break;
            }
            intent.putExtra("keyGame", 1);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        } catch (Exception e) {
            Toast.makeText(MenuActivity.this, R.string.notSave, Toast.LENGTH_SHORT).show();
        }
    }

    private void exit() {
        super.onDestroy();
        finish();
    }

    public String readFile(String FILENAME) {
        String text;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(FILENAME)));
            text = br.readLine();
        } catch (IOException e) {
            text = "0";
        }
        return text;
    }

    private int appGetFirstTimeRun() {
        //Check if App Start First Time
        SharedPreferences appPreferences = getSharedPreferences("MyAPP", 0);
        int appCurrentBuildVersion = BuildConfig.VERSION_CODE;
        int appLastBuildVersion = appPreferences.getInt("app_first_time", 0);

        //Log.d("appPreferences", "app_first_time = " + appLastBuildVersion);

        if (appLastBuildVersion == appCurrentBuildVersion ) {
            return 1; //ya has iniciado la appp alguna vez

        } else {
            appPreferences.edit().putInt("app_first_time",
                    appCurrentBuildVersion).apply();
            if (appLastBuildVersion == 0) {
                return 0; //es la primera vez
            } else {
                return 2; //es una versión nueva
            }
        }
    }
    @Override
    public void onBackPressed(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                exit();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}