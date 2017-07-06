package com.example.wx.apas;

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
import android.text.Editable;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.CharacterStyle;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
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

public class ButtonActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eqscrolling);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        final String contentget = bundle.getString("content");
        solutionget = bundle.getString("solution");
        idget= bundle.getInt("question_id");
        required_languageget=bundle.getString("required_language");

        TextView tv_content = (TextView) findViewById(R.id.tv_content);
        tv_content.setText(contentget);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                //        .setAction("Action", null).show();
                AlertDialog.Builder builder = new AlertDialog.Builder(ButtonActivity.this);
                //builder.setIcon(android.R.drawable.ic_dialog_info);
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
                //Toast.makeText(ButtonActivity.this, "123", Toast.LENGTH_SHORT).show();
                String url= Constants.ROOT_URL + "/mobile/compile/";
                type = "compile";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                new JSONTaskPOST().execute(url);
            }
        });

        Button bruninput=(Button) findViewById(R.id.runinput);
        bruninput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ButtonActivity.this, inputcode.getText().toString(),Toast.LENGTH_SHORT).show();
                String url=Constants.ROOT_URL + "/mobile/run-input/";
                type = "run-input";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                new JSONTaskPOST().execute(url);
            }
        });

        Button bpretest =(Button) findViewById(R.id.pretest);
        bpretest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ButtonActivity.this, inputcode.getText().toString(),Toast.LENGTH_SHORT).show();
                String url=Constants.ROOT_URL + "/mobile/pretest/";
                type = "pretest";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                new JSONTaskPOST().execute(url);
            }
        });

        Button balltest =(Button) findViewById(R.id.alltest);
        balltest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ButtonActivity.this, inputcode.getText().toString(),Toast.LENGTH_SHORT).show();
                String url=Constants.ROOT_URL + "/mobile/alltest/";
                type = "alltest";
                code = inputcode.getText().toString();
                userinput = inputip.getText().toString();
                new JSONTaskPOST().execute(url);
            }
        });
        inputcode =(EditText) findViewById(R.id.et_inputcode);
        inputip =(EditText) findViewById(R.id.et_inputip);
        inputcode.addTextChangedListener(new TextWatcher() {

            ColorScheme keywords = new ColorScheme(
                    Pattern.compile(
                            "\\b(package|transient|strictfp|void|char|short|int|long|double|float|const|static|volatile|byte|boolean|class|interface|native|private|protected|public|final|abstract|synchronized|enum|instanceof|assert|if|else|switch|case|default|break|goto|return|for|while|do|continue|new|throw|throws|try|catch|finally|this|super|extends|implements|import|true|false|null)\\b"),
                    Color.CYAN
            );

            ColorScheme numbers = new ColorScheme(
                    Pattern.compile("(\\b(\\d*[.]?\\d+)\\b)"),
                    Color.BLUE
            );

            final ColorScheme[] schemes = { keywords, numbers };

            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override public void afterTextChanged(Editable s) {
                removeSpans(s, ForegroundColorSpan.class);
                for (ColorScheme scheme : schemes) {
                    for(Matcher m = scheme.pattern.matcher(s); m.find();) {
                        s.setSpan(new ForegroundColorSpan(scheme.color),
                                m.start(),
                                m.end(),
                                Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    }
                }
            }

            void removeSpans(Editable e, Class<? extends CharacterStyle> type) {
                CharacterStyle[] spans = e.getSpans(0, e.length(), type);
                for (CharacterStyle span : spans) {
                    e.removeSpan(span);
                }
            }

            class ColorScheme {
                final Pattern pattern;
                final int color;

                ColorScheme(Pattern pattern, int color) {
                    this.pattern = pattern;
                    this.color = color;
                }
            }

        });
        inputcode.setText(solutionget);
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
            System.out.println(type);
            System.out.println("COMPIL result : "+result);
            try {
                JSONObject parrentObject = new JSONObject(finalJson);

                AlertDialog.Builder builder = new AlertDialog.Builder(ButtonActivity.this);
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