package com.mrhi.ex80httprequestdbtest;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class TalkActivity extends AppCompatActivity {

    ListView listView;
    TalkAdapter adapter;

    ArrayList<TalkItem> talkItems= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talk);

        //데이터를 서버에서 읽어오기
        loadDB();

        listView= findViewById(R.id.listview);
        adapter= new TalkAdapter(getLayoutInflater(), talkItems);
        listView.setAdapter(adapter);
    }

    void loadDB(){

        //Volley library 사용 가능

        //이 예제에서는 전통적인 기법으로..
        new Thread(){
            @Override
            public void run() {

                String serverUrl="http://mrhi2018.dothome.co.kr/Android/loadDB.php";
                //"http://mrhi2018.dothome.co.kr/Android/loadDB.php"
                try {
                    URL url= new URL(serverUrl);

                    HttpURLConnection connection= (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");
                    connection.setDoInput(true);
                    connection.setUseCaches(false);

                    InputStream is= connection.getInputStream();
                    InputStreamReader isr= new InputStreamReader(is);
                    BufferedReader reader= new BufferedReader(isr);

                    final StringBuffer buffer= new StringBuffer();
                    String line= reader.readLine();
                    while (line!=null){
                        buffer.append(line+"\n");
                        line= reader.readLine();
                    }

                    //읽어오는 작업이 성공했는지 확인
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            new AlertDialog.Builder(TalkActivity.this).setMessage(buffer.toString()).create().show();
//                        }
//                    });

                    //대량의 데이터 초기화
                    talkItems.clear();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                    //읽어온 데이터 문자열에서 row(레코드)별로 분리하여 배열로 리턴하기
                    String[] rows= buffer.toString().split(";");

                    for(String row : rows){
                        //한줄 데이터에서 한칸씩 분리
                        String[] datas= row.split("&");
                        if(datas.length!=5) continue;

                        int no= Integer.parseInt(datas[0]);
                        String name= datas[1];
                        String msg= datas[2];
                        String imgPath= "http://mrhi2018.dothome.co.kr/Android/"+datas[3];
                        String date= datas[4];

                        //대량의 데이터 ArrayList에 추가
                        talkItems.add( new TalkItem(no, name, msg, imgPath, date) );

                        //리스트뷰 갱신
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                adapter.notifyDataSetChanged();
                            }
                        });

                    }



                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }.start();

    }
}
