package com.example.wx.apas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by clp on 2017/2/15.
 */

public class QuizQuestionActivity extends AppCompatActivity {

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
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_question);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quiz");

        Bundle bundle = this.getIntent().getExtras();
        final String contentget = bundle.getString("content");
        solutionget = bundle.getString("solution");
        idget= bundle.getInt("question_id");
        required_languageget=bundle.getString("required_language");

        getSupportActionBar().setTitle(bundle.getString("title"));

        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(Html.fromHtml(contentget));

        Button bcompilation=(Button) findViewById(R.id.compilation);
        bcompilation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url= Constants.ROOT_URL + "/mobile/compile/";
                type = "compile";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                progressDialog = ProgressDialog.show(QuizQuestionActivity.this,"COMPILING","",true);
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
                progressDialog = ProgressDialog.show(QuizQuestionActivity.this,"RUNNING THE INPUT","",true);
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
                progressDialog = ProgressDialog.show(QuizQuestionActivity.this,"CHECKING WITH PRETEST","",true);
                new JSONTaskPOST().execute(url);
            }
        });

        Button bsubmit =(Button) findViewById(R.id.submit);
        bsubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url=Constants.ROOT_URL + "/mobile/quiz-submit/";
                type = "submit";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                progressDialog = ProgressDialog.show(QuizQuestionActivity.this,"Submitting the code","",true);
                new JSONTaskPOST().execute(url);
            }
        });
        inputcode =(EditText) findViewById(R.id.et_inputcode);
        inputip =(EditText) findViewById(R.id.et_inputip);
        SyntaxHighlighter.addEffect(inputcode);
        inputcode.setText(solutionget);
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

                AlertDialog.Builder builder = new AlertDialog.Builder(QuizQuestionActivity.this);
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
                }else if(type.equals("submit")){

                    boolean submit = parrentObject.getBoolean("submitted");

                    if(submit){
                        builder.setTitle("Code submitted");
                    }else{
                        builder.setTitle("Submission is unsuccessful");
                        builder.setMessage("please try again!");
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