package com.example.wx.apas;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import java.util.Objects;

public class ScrollingActivity2 extends AppCompatActivity{

    private static final int CHANGE_UI = 1;
    private static final int ERROR = 2;
    private ListView lv;
    private List<Data> datas = new ArrayList<Data>();
    private static String content;
    private static String solution;
    private static String next;
    private static String previous;
    private static String urlget;
    private static String currurl;
    private static String posget;
    private static int question_id;
    private static String required_language;
    private static String username;
    private static String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling2);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //新页面接收数据
        Bundle bundle = this.getIntent().getExtras();
        //接收name值
        urlget = bundle.getString("url");
        username = bundle.getString("username");
        password = bundle.getString("password");

        new JSONTaskGET().execute(urlget);

        lv = (ListView) findViewById(R.id.lv);

        //Button
        Button bprevious=(Button) findViewById(R.id.bprevious);
        bprevious.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(previous=="null"){
                    Toast.makeText(ScrollingActivity2.this, "First Page!" ,Toast.LENGTH_SHORT).show();}
                else {
                    Intent intent = new Intent(ScrollingActivity2.this, ScrollingActivity2.class);
                    //用Bundle携带数据
                    Bundle bundle = new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("url", previous);
                    //bundle.putString("solution", previous);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            }
        });

        Button bnext=(Button) findViewById(R.id.bnext);
        bnext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ScrollingActivity2.this, ScrollingActivity2.class);
                //用Bundle携带数据
                Bundle bundle=new Bundle();
                //传递name参数为tinyphp
                bundle.putString("url", next);
                //bundle.putString("solution", previous);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

//下拉菜单的实现
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                String[] languages = getResources().getStringArray(R.array.languages);
                if(languages[pos].equals("C")){
                    currurl= Constants.ROOT_URL + "/mobile/questionfilter/?question_topic=&required_language=C";
                    Intent intent = new Intent(ScrollingActivity2.this, ScrollingActivity2.class);
                    //用Bundle携带数据
                    Bundle bundle = new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("url", currurl);
                    //bundle.putString("solution", previous);
                    intent.putExtras(bundle);
                    startActivity(intent);}
                else if(languages[pos].equals("CPP")){
                    currurl= Constants.ROOT_URL + "/mobile/questionfilter/?question_topic=&required_language=CPP";
                    Intent intent = new Intent(ScrollingActivity2.this, ScrollingActivity2.class);
                    //用Bundle携带数据
                    Bundle bundle = new Bundle();
                    //传递name参数为tinyphp
                    bundle.putString("url", currurl);
                    //bundle.putString("solution", previous);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
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
                System.out.println("status GET = " + connection.getResponseCode());
                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                }
                return buffer.toString();
            }
            catch (MalformedURLException e) {
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

            Handler handler = new Handler() {
                public void handleMessage(Message msg) {
                    if (msg.what == CHANGE_UI) {

                        try {
                            String finalJson = result;
                            JSONObject parrentObject = new JSONObject(finalJson);
                            next = parrentObject.getString("next");
                            previous = parrentObject.getString("previous");

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

                                Data data = new Data();
                                data.setId(url);
                                data.setTitle(title);
                                data.setQuestion_type(question_type);
                                data.setRequired_language(required_language);
                                data.setDifficulty(difficulty);
                                data.setContent(content);
                                data.setSolution(solution);

                                datas.add(data);
                            }
                            lv.setAdapter(new MyAdapter());
                            lv.setOnItemClickListener(new ItemClickEvent());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            handler.sendMessage(message);
            //tvData.setText(result);
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
                Intent intent =new Intent(ScrollingActivity2.this, ButtonActivity.class);

                final Data data = datas.get(position);
                content = data.getContent();
                solution = data.getSolution();
                question_id = data.getId();
                required_language =data.getRequired_language();

                Bundle bundle=new Bundle();
                bundle.putString("content", content);
                bundle.putString("solution", solution);
                bundle.putInt("question_id",question_id);
                bundle.putString("required_language",required_language);
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
                View view = View.inflate(ScrollingActivity2.this, R.layout.item_listview, null);
                final Data data = datas.get(position);

                TextView title = (TextView) view.findViewById(R.id.tv_title);
                TextView question_type = (TextView) view.findViewById(R.id.tv_question_type);
                TextView required_language = (TextView) view.findViewById(R.id.tv_required_language);
                TextView difficulty = (TextView) view.findViewById(R.id.tv_difficulty);

                title.setText(data.getTitle());
                question_type.setText(data.getQuestion_type());
                required_language.setText(data.getRequired_language());
                difficulty.setText(String.valueOf(data.getDifficulty()));

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
