package com.example.wx.apas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
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
import java.util.ArrayList;
import java.util.List;

public class QuizActivity extends AppCompatActivity {

    private static final int CHANGE_UI = 1;
    private static final int ERROR = 2;

    private static List<Data> datas = new ArrayList<Data>();
    private static  String firsturl;
    private static ListView lv2;
    private static int id;
    private static  String title;
    private static  String description;
    private static  String end;
    private static  String instructuion_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firsturl = Constants.ROOT_URL + "/mobile/quiz-view/";
/*        new JSONTaskGET().execute(firsturl);
        lv2 = (ListView)findViewById(R.id.lv2);*/

        new JSONTaskPOST().execute(firsturl);
        lv2 = (ListView)findViewById(R.id.lv2);
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
                postdata.put("user_id", 3);

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

                // connection.getOutputStream().write(postdatabytes);
                //connection.connect();
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
        protected void onPostExecute(String assignment) {
            super.onPostExecute(assignment);
            String finalJson = assignment;
            try {
                //String finalJson = assignment;
                JSONObject parrentObject = new JSONObject(finalJson);
                //next = parrentObject.getString("next");
                // previous = parrentObject.getString("previous");

                JSONArray parrentArray = parrentObject.getJSONArray("quiz");
                for (int i = 0; i < parrentArray.length(); i++) {
                    JSONObject finalObject = parrentArray.getJSONObject(i);
                    String url2 = finalObject.getString("url");
                    String title2 = finalObject.getString("title");
                    String description = finalObject.getString("description");
                    String start_submission_time = finalObject.getString("start_submission_time");
                    //int difficulty = finalObject.getInt("difficulty");
                    String end_submission_time = finalObject.getString("end_submission_time");
                    //question_topicurl =finalObject.getString("question_topic");
                    //String question_topic =finalObject.getString("happy");

                    Data data = new Data();
                    data.setId2(url2);
                    data.setTitle2(title2);
                    data.setDescription(description);
                    //data.setStart_submission_time(start_submission_time);
                    //data.setDifficulty(difficulty);
                    data.setEnd_submission_time(end_submission_time);

                    //data.setQuestion_topic(question_topic);
                    //这个地方可以获取到值但是适配器那位0
                    datas.add(data);
                }
                lv2.setAdapter(new MyAdapter());
                lv2.setOnItemClickListener(new ItemClickEvent());
                //new JSONTaskGET().execute(question_topicurl);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private final class ItemClickEvent implements AdapterView.OnItemClickListener {
            @Override
            //这里需要注意的是第三个参数arg2，这是代表单击第几个选项
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                //通过单击事件，获得单击选项的内容
                //String text = lv.getItemAtPosition(arg2)+"";
                //通过吐丝对象显示出来。
                //Toast.makeText(getApplicationContext(), text, 1).show();

                //新建一个显式意图，第一个参数为当前Activity类对象，第二个参数为你要打开的Activity类
                Intent intent =new Intent(QuizActivity.this, QuizListActivity.class);

                final Data data = datas.get(position);
                id = data.getId2();
                title = data.getTitle2();
                description = data.getDescription();
                end = data.getEnd_submission_time();
                Bundle bundle=new Bundle();
                bundle.putInt("id",id);
                bundle.putString("title",title);
                bundle.putString("description",description);
                bundle.putString("end",end);

                intent.putExtras(bundle);

                startActivity(intent);

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
                View view = View.inflate(QuizActivity.this, R.layout.item_listviewass, null);
                final Data data = datas.get(position);
                //Log.d("aaaaa", datas.get(position).getExp_name());

                TextView title = (TextView) view.findViewById(R.id.tv_t);
                TextView description = (TextView) view.findViewById(R.id.tv_d);
                //TextView start_submission_time = (TextView) view.findViewById(R.id.tv_s);
                TextView end_submission_time = (TextView) view.findViewById(R.id.tv_e);

                //String.valueOf(data.getTitle());

                title.setText(data.getTitle2());
                //title.setText(String.valueOf(data.getUrl()));
                description.setText(data.getDescription());
                end_submission_time.setText(data.getEnd_submission_time());
                //question_topic.setText(data.getQuestion_topic());

                //Log.i("content", content);
                //Log.i("exp_name", datas.get(position).getExp_name());
                return view;
            }
        }
        /*
            String finalJson = result;
            JSONObject parrentObject = new JSONObject(finalJson);
            JSONArray parrentArray = parrentObject.getJSONArray("results");
            for(int i=0;i<parrentArray.length();i++){
                JSONObject finalObject = parrentArray.getJSONObject(i);
                String questionURL = finalObject.getString("url");
                String title = finalObject.getString("title");
                String content = finalObject.getString("content");
                String suggested_solution = finalObject.getString("suggested_solution");
            }
*/
    }
}
