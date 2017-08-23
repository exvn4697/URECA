package com.example.wx.apas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ExerciseListActivity extends AppCompatActivity{

    private static final int CHANGE_UI = 1;
    private ListView lv;
    private List<Data> datas = new ArrayList<Data>();
    private static String content;
    private static String solution;
    private static String code_template;
    private static String next;
    private static String previous;
    private static String firsturl,topicurl;
    private static URL currurl;
    private static int question_id;
    private static String required_language;
    private static String question_type;
    private static String username;
    private static String password;
    private static String[] languages;
    private static String[] topics;
    private static String languagenow="";
    private static String topicnow="";

    //private static String question_topicurl;
    //private static String question_topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle bundle = this.getIntent().getExtras();
        username = bundle.getString("username");
        password = bundle.getString("password");

        languages = getResources().getStringArray(R.array.languages);
        topics = getResources().getStringArray(R.array.topics);

        firsturl = Constants.ROOT_URL + "/mobile/questionfilter/?question_topic=&required_language=";
        topicurl = Constants.ROOT_URL + "/mobile/questiontopicfilter/?language=";

        new JSONTaskGET_topic().execute(topicurl);
        new JSONTaskGET().execute(firsturl);

        lv = (ListView) findViewById(R.id.lv);

        //Button
        Button bprevious=(Button) findViewById(R.id.bprevious);
        bprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previous=="null"){
                    Toast.makeText(ExerciseListActivity.this, "First Page!" ,Toast.LENGTH_SHORT).show();}
                else {
                    new JSONTaskGET().execute(previous);
                }
            }
        });

        Button bnext=(Button) findViewById(R.id.bnext);
        bnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(next =="null"){
                    Toast.makeText(ExerciseListActivity.this, "Last Page!" ,Toast.LENGTH_SHORT).show();
                }else
                    new JSONTaskGET().execute(next);
            }
        });

//下拉菜单的实现
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                languagenow = languages[pos];
                if(languagenow.equals("---"))languagenow = "";
                try {
                    currurl= new URL(Constants.ROOT_URL + "/mobile/questionfilter/?question_topic="+topicnow+"&required_language="+languagenow);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
                new JSONTaskGET().execute(currurl.toString().replaceAll(" ","%20"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        Spinner spinner2 = (Spinner) findViewById(R.id.spinner2);
        spinner2.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                topicnow = topics[pos];
                System.out.println("topicnow = "+topicnow);
                if(topicnow.equals("---"))topicnow = "";
                try {
                    currurl = new URL(Constants.ROOT_URL + "/mobile/questionfilter/?question_topic=" + topicnow + "&required_language=" + languagenow);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                new JSONTaskGET().execute(currurl.toString().replaceAll(" ","%20"));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Another interface callback
            }
        });

        // 注意 这里没有 @Override 标签
/*    public void onClick(View v) {
        // TODO Auto-generated method stub
        switch (v.getId()) {
            case R.id.button_1:
                Intent intent=new Intent();
                intent.setClass(this, EQScrollingActivity.class);
                startActivity(intent);
                break;
            /*case R.id.button2:
                tst = Toast.makeText(this, "222222222", Toast.LENGTH_SHORT);
                tst.show();
                break;*/
       /*     default:
                break;
        }
    }*/
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    class JSONTaskGET_topic extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            System.out.println("QUEST TEST");
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " +
                        Base64.encode((username + ":" + password).getBytes(), Base64.NO_WRAP).toString());
                connection.setDoInput(true);
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                //connection.getOutputStream().write(postdatabytes);
                //connection.connect();
                System.out.println("status GET = " + connection.getResponseCode());
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            } /*catch (JSONException e) {
                e.printStackTrace();
            }*/ finally {
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

        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = CHANGE_UI;
            message.obj = result;

            System.out.println("result topic = " + result);
            try {
                String finalJson = result;
                JSONObject parrentObject = new JSONObject(finalJson);
                next = parrentObject.getString("next");
                previous = parrentObject.getString("previous");
                int count = parrentObject.getInt("count");
                topics = new String[count+1];
                JSONArray parrentArray = parrentObject.getJSONArray("results");
                for (int i = 0; i < parrentArray.length(); i++) {
                    JSONObject finalObject = parrentArray.getJSONObject(i);
                    String url = finalObject.getString("url");
                    String title = finalObject.getString("title");
                    topics[i+1]=title;
                }
                topics[0]="---";
                Spinner s = (Spinner) findViewById(R.id.spinner2);
               // s.setBackgroundResource(R.drawable.spinner);
                ArrayAdapter<String> adapter = new ArrayAdapter<>(ExerciseListActivity.this,
                        android.R.layout.simple_spinner_item, topics);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                s.setAdapter(adapter);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    class JSONTaskGET extends AsyncTask<String, String, String>{

        @Override
        protected String doInBackground(String... params) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            Authenticator.setDefault(new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(username, password.toCharArray());
                }
            });

            try {
                URL url = new URL(params[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestProperty("Authorization", "Basic " +
                        Base64.encode((username + ":" + password).getBytes(), Base64.NO_WRAP).toString());
                connection.setDoInput(true);
                connection.setRequestProperty("Accept", "application/json");
                connection.connect();

                //connection.getOutputStream().write(postdatabytes);
                //connection.connect();
                System.out.println("status GET = " + connection.getResponseCode());
                InputStream stream = connection.getInputStream();

                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();

                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();

            } catch (IOException e) {
                e.printStackTrace();
            } /*catch (JSONException e) {
                e.printStackTrace();
            }*/ finally {
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

        protected void onPostExecute(final String result) {
            super.onPostExecute(result);
            Message message = new Message();
            message.what = CHANGE_UI;
            message.obj = result;

            System.out.println("result question = "+result);

            try {
                String finalJson = result;
                JSONObject parrentObject = new JSONObject(finalJson);
                next = parrentObject.getString("next");
                previous = parrentObject.getString("previous");
                datas = new ArrayList<>();
                JSONArray parrentArray = parrentObject.getJSONArray("results");
                for (int i = 0; i < parrentArray.length(); i++) {
                    JSONObject finalObject = parrentArray.getJSONObject(i);
                    String url = finalObject.getString("url");
                    String title = finalObject.getString("title");
                    String question_type = finalObject.getString("question_type");
                    String required_language = finalObject.getString("required_language");
                    int difficulty = finalObject.getInt("difficulty");
                    String content = finalObject.getString("content");
                    String solution = finalObject.getString("suggested_solution");
                    String code_template = finalObject.getString("code_template");
                    String question_topic =finalObject.getJSONObject("question_topic").getString("title");
                    int number_of_options = finalObject.getInt("number_of_options");
                    int number_of_blanks = finalObject.getInt("number_of_blanks");
                    int number_of_files = finalObject.getInt("number_of_files");

                    Data data = new Data();
                    data.setId(url);
                    data.setTitle(title);
                    data.setQuestion_topic(question_topic);
                    data.setQuestion_type(question_type);
                    data.setRequired_language(required_language);
                    data.setDifficulty(difficulty);
                    data.setContent(content);
                    data.setSolution(solution);
                    data.setCodeTemplate(code_template);
                    data.setNumber_of_blanks(number_of_blanks);
                    data.setNumber_of_files(number_of_files);
                    data.setNumber_of_options(number_of_options);

                    datas.add(data);
                }
                lv.setAdapter(new MyAdapter());
                lv.setOnItemClickListener(new ItemClickEvent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        private final class ItemClickEvent implements AdapterView.OnItemClickListener {
            @Override
            //这里需要注意的是第三个参数arg2，这是代表单击第几个选项
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {

                final Data data = datas.get(position);
                content = data.getContent();
                solution = data.getSolution();
                question_id = data.getId();
                required_language =data.getRequired_language();
                code_template = data.getCodeTemplate();
                question_type = data.getQuestion_type();

                Bundle bundle=new Bundle();
                bundle.putString("title",data.getTitle());
                bundle.putString("username",username);
                bundle.putString("password",password);
                bundle.putString("question_type",question_type);
                bundle.putString("content", content);
                bundle.putInt("question_id",question_id);

                if(question_type.equals("Coding")) {
                    Intent intent = new Intent(ExerciseListActivity.this, ExerciseCodingActivity.class);

                    bundle.putString("solution", solution);
                    bundle.putString("code_template", code_template);
                    bundle.putString("required_language",required_language);
                    bundle.putInt("number_of_files",data.getNumber_of_files());
                    intent.putExtras(bundle);

                    startActivity(intent);
                }else if(question_type.equals("FIB")){
                    Intent intent = new Intent(ExerciseListActivity.this, ExerciseFIBActivity.class);

                    bundle.putInt("number_of_blanks",data.getNumber_of_blanks());
                    intent.putExtras(bundle);

                    startActivity(intent);
                }else if(question_type.equals("MCQ")){
                    Intent intent = new Intent(ExerciseListActivity.this, ExerciseMCQActivity.class);

                    bundle.putInt("number_of_options",data.getNumber_of_options());
                    intent.putExtras(bundle);

                    startActivity(intent);
                }


            }
        }

        class MyAdapter extends BaseAdapter {

            @Override
            public int getCount() {
                // Log.d("AAA", "" + datas.size());
                return datas.size();
            }

            @Override
            public Object getItem(int position) {
                return datas.get(position);
            }

            @Override
            public long getItemId(int position) {
                return position;
            }


            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                View view = View.inflate(ExerciseListActivity.this, R.layout.item_listview, null);
                final Data data = datas.get(position);

                TextView title = (TextView) view.findViewById(R.id.tv_title);
                TextView question_type = (TextView) view.findViewById(R.id.tv_question_type);
                TextView required_language = (TextView) view.findViewById(R.id.tv_required_language);
                TextView difficulty = (TextView) view.findViewById(R.id.tv_difficulty);
                TextView question_topic = (TextView) view.findViewById(R.id.tv_question_topic);

                //String.valueOf(data.getTitle());

                title.setText(data.getTitle());
                question_type.setText(data.getQuestion_type());
                required_language.setText(data.getRequired_language());
                difficulty.setText(String.valueOf(data.getDifficulty()));
                question_topic.setText(data.getQuestion_topic());

                //Log.i("content", content);

                //Log.i("exp_name", datas.get(position).getExp_name());
                return view;
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
    }
}



