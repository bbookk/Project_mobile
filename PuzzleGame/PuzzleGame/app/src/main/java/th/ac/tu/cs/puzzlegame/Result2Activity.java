package th.ac.tu.cs.puzzlegame;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;

public class Result2Activity extends Activity {
    ImageButton back;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result2);

        back = (ImageButton)findViewById(R.id.bBackMenu);
        back.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                backMenu();
            }
        });

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        TextView nameTxt = (TextView)findViewById(R.id.col_name);
        TextView scoreTxt = (TextView)findViewById(R.id.col_move);
        TextView timeTxt = (TextView) findViewById(R.id.col_time);
        String url = "http://horrorshortfilm.online/sorawit/getAdData.php";
        try{
            JSONArray data = new JSONArray(getJSONUrl(url));
            final ArrayList<HashMap<String,String>> myArr = new ArrayList<HashMap<String, String>>();
            HashMap<String,String> map;
            for(int i=0;i<data.length();i++){
                JSONObject c = data.getJSONObject(i);
                map = new HashMap<String, String>();
                map.put("Ad_Player_Name",c.getString("Ad_Player_Name"));
                map.put("Ad_Move",c.getString("Ad_Move"));
                map.put("Ad_Finish_Time",c.getString("Ad_Finish_Time"));
                myArr.add(map);
            }
            StringBuilder builder = new StringBuilder();
            StringBuilder builder2 = new StringBuilder();
            StringBuilder builder3 = new StringBuilder();
            for(int i=0;i<myArr.size();i++){
                builder.append(myArr.get(i).get("Ad_Player_Name").toString()+"\n");
                builder2.append(myArr.get(i).get("Ad_Move").toString()+"\n");
                builder3.append(myArr.get(i).get("Ad_Finish_Time").toString()+"s \n");
            }
            nameTxt.setText(builder);
            scoreTxt.setText(builder2);
            timeTxt.setText(builder3);
        }catch(JSONException e){
            e.printStackTrace();
        }

    }

    public String getJSONUrl(String url) {
        StringBuilder str = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(url);
        try {
            HttpResponse response = client.execute(httpGet);
            StatusLine statusLine = response.getStatusLine();
            int statusCode = statusLine.getStatusCode();
            Log.e("Log",statusCode+"");
            if (statusCode == 200) { // Download OK
                HttpEntity entity = response.getEntity();
                InputStream content = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(content,"UTF-8"));
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

    public void backMenu() {
        Intent intent = new Intent(Result2Activity.this, MenuActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
        finish();
    }
    @Override
    public void onBackPressed(){
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?").setPositiveButton("Yes",new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
                Result2Activity.super.onBackPressed();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                }).show();
    }
}