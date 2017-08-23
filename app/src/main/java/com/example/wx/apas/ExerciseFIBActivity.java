package com.example.wx.apas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.Arrays;

/**
 * Created by clp on 2017/2/15.
 */

public class ExerciseFIBActivity extends AppCompatActivity {

    //Button btn1, btn2;
    //Toast tst;

    //private TextView tv_content;
    private static int idget,number_of_blanks;
    private String[] userinput;
    private EditText[] blanks;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_efibscrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exercise");

        Bundle bundle = this.getIntent().getExtras();
        final String contentget = bundle.getString("content");
        idget= bundle.getInt("question_id");
        getSupportActionBar().setTitle(bundle.getString("title"));

        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(Html.fromHtml(contentget));

        number_of_blanks = bundle.getInt("number_of_blanks");
        blanks = new EditText[number_of_blanks];
        userinput = new String[number_of_blanks];

        Button balltest =(Button) findViewById(R.id.submit);
        balltest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=Constants.ROOT_URL + "/mobile/submit-fib/";
                //get the all answer to send
                for(int i=0;i<number_of_blanks;i++){
                    userinput[i] = blanks[i].getText().toString();
                }
                progressDialog = ProgressDialog.show(ExerciseFIBActivity.this,"Submitting The Answer","",true);
                new JSONTaskPOST().execute(url);
            }
        });
        //set all blanks
        LinearLayout parrent = (LinearLayout) findViewById(R.id.blanks);
        for(int i=0;i<number_of_blanks;i++){
            EditText toAdd = new EditText(ExerciseFIBActivity.this);
            blanks[i] = toAdd;
            parrent.addView(toAdd);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class JSONTaskPOST extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            final String username = "student1";
            final String password = "1";

            JSONObject postdata = new JSONObject();
            try {
                postdata.put("question_id", idget);
                JSONArray input = new JSONArray(Arrays.asList(userinput));
                postdata.put("user_input",input);

            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " +
                        Base64.encode((username + ":" + password).getBytes(), Base64.NO_WRAP).toString() );
                connection.setDoInput(true);
                connection.setDoOutput(true);
                connection.setUseCaches(false);
                connection.setRequestMethod("POST");
                connection.setRequestProperty("Content-Type","application/json");
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();
                byte[] postdatabytes = postdata.toString().getBytes("UTF-8");
                System.out.println("json = "+postdata.toString() );

                OutputStream os = connection.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os, "UTF-8");
                osw.write( postdata.toString() );
                osw.flush();
                osw.close();

                System.out.println("status POST = "+ connection.getResponseCode() );
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }

                return buffer.toString();
/*
            String finalJson = results;
            JSONObject parrentObject = new JSONObject(finalJson);
            JSONArray parrentArray = parrentObject.getJSONArray("results");
            for(int i=0;i<parrentArray.length();i++){
                JSONObject finalObject = parrentArray.getJSONObject(i);
                String questionURL = finalObject.getString("url");
                String title = finalObject.getString("title");
                String content = finalObject.getString("content");
                String suggested_solution = finalObject.getString("suggested_solution");

            }*/

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } /*catch (JSONException e) {
                e.printStackTrace();
            }*/
        finally {
                if (connection != null)
                    connection.disconnect();

                try {
                    if (reader != null)
                        reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            String finalJson = result;
            progressDialog.dismiss();
            try {
                JSONObject parrentObject = new JSONObject(finalJson);

                AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseFIBActivity.this);
                //success submitting
                boolean success = parrentObject.getBoolean("success");
                Integer score = parrentObject.getInt("score");
                if (success) {
                    builder.setTitle("Successful!");
                    builder.setMessage("Score = " + score.toString());
                }else {
                    builder.setTitle("Unsuccessful!");
                    //   builder.setMessage(message);
                }

                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

}