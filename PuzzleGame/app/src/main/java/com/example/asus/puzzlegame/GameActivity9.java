package com.example.asus.puzzlegame;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity9 extends Activity {
    int score;
    private final int N = 3;
    Cards cards;
    int count;
    private ImageButton[][] button;
    private final int BUTTON_ID[][] = {{R.id.b900, R.id.b901, R.id.b902},
            {R.id.b910, R.id.b911, R.id.b912},
            {R.id.b920, R.id.b921, R.id.b922}};
    private final int CADRS_ID[] = {R.drawable.zero, R.drawable.one, R.drawable.two, R.drawable.three,
            R.drawable.four, R.drawable.five, R.drawable.six, R.drawable.seven,
            R.drawable.eight};

    private TextView tScore, timetxt;
    private int numbSteps;
    private int numbBestSteps;
    Intent intent;
    private boolean check;
    Timer T;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_game9);
        button = new ImageButton[N][N];
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                button[i][j] = (ImageButton) this.findViewById(BUTTON_ID[i][j]);
                button[i][j].setOnClickListener(onClickListener);
            }
        ImageButton bNewGame = (ImageButton) this.findViewById(R.id.bNewGame);
        bNewGame.setOnClickListener(onClickListener);
        ImageButton bBackMenu = (ImageButton) this.findViewById(R.id.bBackMenu);
        bBackMenu.setOnClickListener(onClickListener);

        TextView tSScore = (TextView) this.findViewById(R.id.tSScore);
        tScore = (TextView) this.findViewById(R.id.tScore);

        TextView time = (TextView) this.findViewById(R.id.time);
        timetxt = (TextView) this.findViewById(R.id.timetxt);

        cards = new Cards(N, N);
        try {
            if (getIntent().getExtras().getInt("keyGame") == 1) {
                continueGame();
                checkFinish();
            } else newGame();
        } catch (Exception e) {
            newGame();
        }


        startClock();
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (!check)
                for (int i = 0; i < N; i++)
                    for (int j = 0; j < N; j++)
                        if (v.getId() == BUTTON_ID[i][j])
                            buttonFunction(i, j);

            switch (v.getId()) {
                case R.id.bNewGame:
                    newGame();
                    break;
                case R.id.bBackMenu:
                    backMenu();
                    break;
                default:
                    break;
            }
        }
    };

    public void buttonFunction(int row, int columb) {
        cards.moveCards(row, columb);
        if (cards.resultMove()) {
            numbSteps++;
            showGame();
            checkFinish();
        }
    }

    public void newGame() {
        cards.getNewCards();
        numbSteps = 0;
        count = 0;
        numbBestSteps = Integer.parseInt(readFile("fbs9"));
        showGame();
        check = false;
    }

    private void continueGame() {
        String text = getPreferences(MODE_PRIVATE).getString("savePyatnashki", "");
        int k = 0;
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                cards.setValueBoard(i, j, Integer.parseInt("" + text.charAt(k) + text.charAt(k + 1)));
                k += 2;
            }
        count = Integer.parseInt(readFile("fileTime"));
        numbSteps = Integer.parseInt(readFile("fileScore"));
        numbBestSteps = Integer.parseInt(readFile("fbs9"));
        showGame();
        check = false;
    }

    public void backMenu() {
        saveValueBoard();
        Intent intent = new Intent(GameActivity9.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }

    private void saveValueBoard() {
        String text = "";
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                if (cards.getValueBoard(i, j) < 10)
                    text += "0" + cards.getValueBoard(i, j);
                else text += cards.getValueBoard(i, j);
            }

        getPreferences(MODE_PRIVATE).edit().putString("savePyatnashki", text).commit();
        writeFile(Integer.toString(count), "fileTime");
        writeFile(Integer.toString(numbSteps), "fileScore");
        writeFile(Integer.toString(N), "fileN");
    }

    public void showGame() {
        tScore.setText(Integer.toString(numbSteps));
        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                button[i][j].setImageResource(CADRS_ID[cards.getValueBoard(i, j)]);

    }

    public void checkFinish() {
        if (cards.finished(N, N)) {
            showGame();
            T.cancel();
            timetxt.setText(""+count);
            final AlertDialog.Builder viewDialog = new AlertDialog.Builder(GameActivity9.this);
            final EditText input = new EditText(GameActivity9.this);
            input.setHint("Please enter your name");
            input.requestFocus();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            input.setLayoutParams(lp);
            viewDialog.setView(input);
            viewDialog.setMessage("You have won! Congratulations!" + "\n\n" + "Move : " + numbSteps
                                    + "\n\n"+ "Time : " + count + " second.");
            viewDialog.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    try {
                        String url = "http://horrorshortfilm.online/sorawit/saveData.php";
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("player_name", input.getText().toString()));
                        params.add(new BasicNameValuePair("move", numbSteps + ""));
                        params.add(new BasicNameValuePair("finish_time", count + ""));
                        String resultServer = getHttpPost(url, params);
                        String strStatusID = "0";
                        String strError = "Unknown Status";
                        JSONObject c;
                        try {
                            c = new JSONObject(resultServer);
                            strStatusID = c.getString("StatusID");
                            strError = c.getString("Error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if (strStatusID.equals("0")) {
                            Toast.makeText(GameActivity9.this, strError, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(GameActivity9.this, "Inserted", Toast.LENGTH_SHORT).show();
                        }
                    } finally { //close connection with DB
                    }
                    intent = new Intent(GameActivity9.this, ResultActivity.class);
                    startActivity(intent);
                }
            });
            viewDialog.show();
            if ((numbSteps < numbBestSteps) || (numbBestSteps == 0)) {
                writeFile(Integer.toString(numbSteps), "fbs9");
            }
            check = true;
        }
    }

    public String getHttpPost(String url, List<NameValuePair> params) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(url);
        try {
            httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
            HttpResponse response = client.execute(httpPost);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            if (statusCode == 200) { // Status OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content));
                String line;
                while ((line = reader.readLine()) != null) {
                    str.append(line);
                }
            } else {
                Log.e("Log", "Failed to download result..");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.toString();
    }

    public void writeFile(String text, String file) {
        try {
            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(openFileOutput(file, MODE_PRIVATE)));
            bw.write(text);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readFile(String file) {
        String text;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(openFileInput(file)));
            text = br.readLine();
        } catch (IOException e) {
            text = "0";
        }
        return text;
    }

    private void startClock() {
        T = new Timer();
        T.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        timetxt.setText(""+count);
                        count++;
                    }
                });
            }
        }, 1000, 1000);

    }
}