package com.example.wx.apas;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
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

public class QuizListActivity extends AppCompatActivity {

    private static int idget;
    private static String titleget;
    private static String descriptionget;
    private static String endget;

    private static final int CHANGE_UI = 1;
    private static final int ERROR = 2;

    private static List<Data> datas = new ArrayList<Data>();
    private static  String firsturl;
    private static ListView lv2;
    private static int assignment_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Bundle bundle = this.getIntent().getExtras();
        idget= bundle.getInt("id");
        titleget=bundle.getString("title");
        descriptionget=bundle.getString("description");
        endget=bundle.getString("end");

        TextView tv_title = (TextView) findViewById(R.id.tv_title3);
        tv_title.setText("Title: " + titleget);
        TextView tv_description = (TextView) findViewById(R.id.tv_de);
        tv_description.setText("Description: " + descriptionget);
        TextView tv_end = (TextView) findViewById(R.id.tv_end);
        tv_end.setText("End Submission Time: " + endget);

        firsturl = Constants.ROOT_URL + "/mobile/quiz-question-view/";
        new JSONTaskPOST().execute(firsturl);
        //lv2 = (ListView)findViewById(R.id.lv2);

        Button bcompilation=(Button) findViewById(R.id.c1);
        bcompilation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ButtonActivity.this, "123", Toast.LENGTH_SHORT).show();
                //String url="http://10.0.2.2:8000/mobile/run-input/";
                //new JSONTaskPOST().execute(url);
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizListActivity.this);
                //builder.setIcon(android.R.drawable.ic_dialog_info);
                //builder.setTitle("Suggested_Solution");
                builder.setMessage("Compilation is successful!");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        Button bcompilation2=(Button) findViewById(R.id.c2);
        bcompilation2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(ButtonActivity.this, "123", Toast.LENGTH_SHORT).show();
                //String url="http://10.0.2.2:8000/mobile/run-input/";
                //new JSONTaskPOST().execute(url);
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizListActivity.this);
                //builder.setIcon(android.R.drawable.ic_dialog_info);
                //builder.setTitle("Suggested_Solution");
                builder.setMessage("Compilation is successful!");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });

        Button bruninput=(Button) findViewById(R.id.s);
        bruninput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizListActivity.this);
                //builder.setIcon(android.R.drawable.ic_dialog_info);
                builder.setMessage("Submission is successful!");
                builder.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                builder.show();
            }
        });
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
                postdata.put("quiz_id", idget);

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
        protected void onPostExecute(String question) {
            super.onPostExecute(question);
            String finalJson = question;
            try {
                //String finalJson = assignment;
                JSONObject parrentObject = new JSONObject(finalJson);
                //next = parrentObject.getString("next");
                // previous = parrentObject.getString("previous");
                JSONArray parrentArray = parrentObject.getJSONArray("question");
                for (int i = 0; i < parrentArray.length(); i++) {
                    JSONObject finalObject = parrentArray.getJSONObject(i);
                    //String url = finalObject.getString("url");
                    String title = finalObject.getString("title");
                    int difficulty = finalObject.getInt("difficulty");
                    String content = finalObject.getString("content");
                    //question_topicurl =finalObject.getString("question_topic");
                    //String question_topic =finalObject.getString("happy");
                    //ht tp :/ /1 27 .0 .0 .1 :8 00 0/ mo bi le /q ue st io n/ 168/
                    //String[] str = url.split("/");
                    //int question_id = Integer.parseInt(str[5]);
                    if(i==0)
                    {
                        TextView tv_title2 = (TextView) findViewById(R.id.textView13);
                        tv_title2.setText("Title: " + title);
                        TextView difficulty2 = (TextView) findViewById(R.id.textView14);
                        difficulty2.setText("Difficulty: " + String.valueOf(difficulty));
                        TextView content2 = (TextView) findViewById(R.id.textView17);
                        content2.setText("Content: " + content);
                    }
                    else
                    {
                        TextView tv_title2 = (TextView) findViewById(R.id.textView18);
                        tv_title2.setText("Title: " + title);
                        TextView difficulty2 = (TextView) findViewById(R.id.textView19);
                        difficulty2.setText("Difficulty: " + String.valueOf(difficulty));
                        TextView content2 = (TextView) findViewById(R.id.textView21);
                        content2.setText("Content: " + content);
                    }
                    /*
                    Data data = new Data();
                    //data.setId(url);
                    data.setTitle(title);
                    data.setDifficulty(difficulty);
                    data.setContent(content);*/
                }
                //lv2.setAdapter(new MyAdapter());
                //lv2.setOnItemClickListener(new ItemClickEvent());
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
                /*Intent intent =new Intent(QuizActivity.this, QuizListActivity.class);

                final Data data = datas.get(position);
                assignment_id = data.getId2();
                ass_title = data.getTitle2();
                ass_description = data.getDescription();
                ass_end = data.getEnd_submission_time();
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putInt("assignment_id",assignment_id);
                bundle.putString("ass_title",ass_title);
                bundle.putString("ass_description",ass_description);
                bundle.putString("ass_end",ass_end);

                intent.putExtras(bundle);

                startActivity(intent);*/

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
                View view = View.inflate(QuizListActivity.this, R.layout.item_listviewass, null);
                final Data data = datas.get(position);
                //Log.d("aaaaa", datas.get(position).getExp_name());

                TextView title = (TextView) view.findViewById(R.id.tv_t);
                TextView description = (TextView) view.findViewById(R.id.tv_d);
                //TextView start_submission_time = (TextView) view.findViewById(R.id.tv_s);
                TextView end_submission_time = (TextView) view.findViewById(R.id.tv_e);
                TextView max_number_of_attempts = (TextView) view.findViewById(R.id.tv_n);

                //String.valueOf(data.getTitle());

                title.setText(data.getTitle2());
                //title.setText(String.valueOf(data.getUrl()));
                description.setText(data.getDescription());
                end_submission_time.setText(data.getEnd_submission_time());
                max_number_of_attempts.setText(String.valueOf(data.getMax_number_of_attempts()));
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
