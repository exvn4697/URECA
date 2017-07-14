package com.example.wx.apas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.MenuItem;
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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class QuizListActivity extends AppCompatActivity {

    private static List<Data> datas = new ArrayList<Data>();
    private static  String firsturl;
    private static ListView lv;
    private static int id;
    private static  String title;
    private static  String description;
    private static  String end;
    private static  String instructuion_file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Quiz");

        Bundle bundle = this.getIntent().getExtras();
        id = bundle.getInt("id");
        title = bundle.getString("title");
        getSupportActionBar().setTitle(title);

        String firsturl = Constants.ROOT_URL + "/mobile/quiz-question-view/";
        new JSONTaskPOST().execute(firsturl);
        lv = (ListView)findViewById(R.id.lv);
    }

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
                postdata.put("quiz_id", id);

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
                datas = new ArrayList<>();
                JSONArray submitted = parrentObject.getJSONArray("submitted");
                JSONArray saved_codes = parrentObject.getJSONArray("saved_codes");
                JSONArray parrentArray = parrentObject.getJSONArray("question");

                System.out.println("result = "+parrentArray);
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
                    //question_topicurl =finalObject.getString("question_topic");

                    Data data = new Data();
                    data.setId(url);
                    data.setTitle(title);
                    data.setQuestion_type(question_type);
                    data.setRequired_language(required_language);
                    data.setDifficulty(difficulty);
                    data.setContent(content);
                    data.setSolution(solution);
                    data.setCodeTemplate(code_template);

                    String code = saved_codes.getString(i);
                    boolean submit = submitted.getBoolean(i);
                    data.setSubmitted(submit);
                    if(submit)
                        data.setCodeTemplate(code);


                    datas.add(data);
                }
                lv.setAdapter(new JSONTaskPOST.MyAdapter());
                lv.setOnItemClickListener(new JSONTaskPOST.ItemClickEvent());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private final class ItemClickEvent implements AdapterView.OnItemClickListener {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                                    long arg3) {
                Intent intent =new Intent(QuizListActivity.this, QuizQuestionActivity.class);

                final Data data = datas.get(position);
                String content = data.getContent();
                String solution = data.getSolution();
                int question_id = data.getId();
                String required_language =data.getRequired_language();

                Bundle bundle=new Bundle();
                bundle.putString("content", content);
                bundle.putString("solution", solution);
                bundle.putInt("question_id",question_id);
                bundle.putString("required_language",required_language);
                intent.putExtras(bundle);

                intent.putExtras(bundle);

                startActivity(intent);

            }
        }

        class MyAdapter extends BaseAdapter {

            @Override
            public int getCount() {
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
                View view = View.inflate(QuizListActivity.this, R.layout.item_listview, null);
                final Data data = datas.get(position);

                TextView title = (TextView) view.findViewById(R.id.tv_title);
                TextView question_type = (TextView) view.findViewById(R.id.tv_question_type);
                TextView required_language = (TextView) view.findViewById(R.id.tv_required_language);
                TextView difficulty = (TextView) view.findViewById(R.id.tv_difficulty);
                //TextView question_topic = (TextView) view.findViewById(R.id.tv_question_topic);

                //String.valueOf(data.getTitle());
                if(data.getSubmitted())
                    title.setText("(SUBMITTED)"+data.getTitle());
                else title.setText(data.getTitle());
                //title.setText(String.valueOf(data.getUrl()));
                question_type.setText(data.getQuestion_type());
                required_language.setText(data.getRequired_language());
                difficulty.setText(String.valueOf(data.getDifficulty()));
                //question_topic.setText(data.getQuestion_topic());

                //Log.i("content", content);

                //Log.i("exp_name", datas.get(position).getExp_name());
                return view;
            }
        }
    }
}
