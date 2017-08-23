package com.example.wx.apas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

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

/**
 * Created by clp on 2017/2/15.
 */

public class ExerciseCodingActivity extends AppCompatActivity {

    //Button btn1, btn2;
    //Toast tst;

    //private TextView tv_content;
    private static int idget;
    private static String required_languageget;
    private EditText inputcode;
    private EditText inputip;
    private static String program_output;
    private String solutionget;
    private String type;
    private String code;
    private String userinput;
    private String code_template;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ecodingscrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Exercise");

        Bundle bundle = this.getIntent().getExtras();
        final String contentget = bundle.getString("content");
        solutionget = bundle.getString("solution");
        idget= bundle.getInt("question_id");
        required_languageget=bundle.getString("required_language");
        code_template = bundle.getString("code_template");
        getSupportActionBar().setTitle(bundle.getString("title"));

        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(contentget);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseCodingActivity.this);
                builder.setTitle("Suggested_Solution");
                builder.setMessage(solutionget);
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        Button bcompilation=(Button) findViewById(R.id.compilation);
        bcompilation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url= Constants.ROOT_URL + "/mobile/compile/";
                type = "compile";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                progressDialog = ProgressDialog.show(ExerciseCodingActivity.this,"COMPILING","",true);
                new JSONTaskPOST().execute(url);
            }
        });

        Button bruninput=(Button) findViewById(R.id.runinput);
        bruninput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=Constants.ROOT_URL + "/mobile/run-input/";
                type = "run-input";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                progressDialog = ProgressDialog.show(ExerciseCodingActivity.this,"RUNNING THE INPUT","",true);
                new JSONTaskPOST().execute(url);
            }
        });

        Button bpretest =(Button) findViewById(R.id.pretest);
        bpretest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=Constants.ROOT_URL + "/mobile/pretest/";
                type = "pretest";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                progressDialog = ProgressDialog.show(ExerciseCodingActivity.this,"CHECKING WITH PRETEST","",true);
                new JSONTaskPOST().execute(url);
            }
        });

        Button balltest =(Button) findViewById(R.id.alltest);
        balltest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=Constants.ROOT_URL + "/mobile/alltest/";
                type = "alltest";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                progressDialog = ProgressDialog.show(ExerciseCodingActivity.this,"CHECKING WITH ALL TESTCASES","",true);
                new JSONTaskPOST().execute(url);
            }
        });
        inputcode =(EditText) findViewById(R.id.et_inputcode);
        inputip =(EditText) findViewById(R.id.et_inputip);
        SyntaxHighlighter.addEffect(inputcode);
        inputcode.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                v.getParent().requestDisallowInterceptTouchEvent(true);
                switch (event.getAction() & MotionEvent.ACTION_MASK){
                    case MotionEvent.ACTION_UP:
                        v.getParent().requestDisallowInterceptTouchEvent(false);
                        break;
                }
                return false;
            }
        });
        inputcode.setText(code_template);
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
                postdata.put("code", code);
                postdata.put("required_language", required_languageget);
                postdata.put("user_input", userinput);
                postdata.put("question_id", idget);

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

                AlertDialog.Builder builder = new AlertDialog.Builder(ExerciseCodingActivity.this);
                if(type.equals("compile")){
                    boolean success = parrentObject.getBoolean("success");
                    String message = parrentObject.getString("message");
                    if( success )
                        builder.setTitle("Compilation is successful!");
                    else {
                        builder.setTitle("Compilation is unsuccessful!");
                        builder.setMessage(message);
                    }

                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }else if(type.equals("run-input")){

                    boolean success = parrentObject.getBoolean("success");
                    String message = parrentObject.getString("message");

                    if(success){
                        program_output = parrentObject.getString("program_output");
                        builder.setTitle("Your program output");
                        builder.setMessage(program_output);
                    }else{
                        builder.setTitle("Compilation is unsuccessful");
                        builder.setMessage(message);
                    }
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }else if(type.equals("pretest")){
                    Integer score = parrentObject.getInt("score");
                    boolean success = parrentObject.getBoolean("success");
                    String message = parrentObject.getString("message");

                    if(success){
                        builder.setTitle("Pretest Score : ");
                        builder.setMessage( score.toString() );
                    }else{
                        builder.setTitle("Compilation is unsuccessful");
                        builder.setMessage(message);
                    }
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }else if(type.equals("alltest")){
                    Integer score = parrentObject.getInt("score");
                    boolean success = parrentObject.getBoolean("success");
                    String message = parrentObject.getString("message");

                    if(success){
                        builder.setTitle("All Testcases Score");
                        builder.setMessage( score.toString() );
                    }else{
                        builder.setTitle("Compilation is unsuccessful");
                        builder.setMessage(message);
                    }
                    builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    builder.show();
                }
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