package com.techpalle.json_listviewexample1;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {
    Button button;
    ListView listView;
    MyTask myTask;
    MyAdapter myAdapter;
    ArrayList<Contacts> arrayList;

    public class MyTask extends AsyncTask<String, Void, String>{
        URL myUrl;
        HttpURLConnection httpURLConnection;
        InputStream inputStream;
        InputStreamReader streamReader;
        BufferedReader bufferedReader;
        String line;
        StringBuilder builder = new StringBuilder();
        @Override
        protected String doInBackground(String... strings) {
            try {
                myUrl = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) myUrl.openConnection();
                inputStream = httpURLConnection.getInputStream();
                streamReader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(streamReader);
                line = bufferedReader.readLine();
                while (line != null){
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                return builder.toString();
            }
            catch (MalformedURLException e) {
                e.printStackTrace();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
            catch (SecurityException e) {
                e.printStackTrace();
            }
            return "Some Thing Went Wrong";
        }

        @Override
        protected void onPostExecute(String s) {
            try {
                JSONObject jsonObject = new JSONObject(s);
                JSONArray k = jsonObject.getJSONArray("contacts");
                for (int i = 0; i<k.length(); i++){
                    JSONObject m = k.getJSONObject(i);
                    String name = m.getString("name");
                    String email = m.getString("email");
                    JSONObject phone = m.getJSONObject("phone");
                    String mobile = phone.getString("mobile");
                    Contacts contacts = new Contacts();
                    contacts.setSno(i+1);
                    contacts.setName(name);
                    contacts.setEmail(email);
                    contacts.setMobile(mobile);
                    arrayList.add(contacts);
                }
                myAdapter.notifyDataSetChanged();
            } catch (JSONException e) {
                e.printStackTrace();
                Log.d("B34", "JSON PARSING ERROR");
            }
            super.onPostExecute(s);
        }
    }


    public class MyAdapter extends BaseAdapter{
        @Override
        public int getCount() {
            return arrayList.size();
        }

        @Override
        public Object getItem(int i) {
            return arrayList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            Contacts c = arrayList.get(i);
            View v = getActivity().getLayoutInflater().inflate(R.layout.row1, viewGroup, false);
            TextView textViewNo = (TextView) v.findViewById(R.id.text_view_no);
            TextView textViewName = (TextView) v.findViewById(R.id.text_view_name);
            TextView textViewEmail = (TextView) v.findViewById(R.id.text_view_email);
            TextView textViewMobile = (TextView) v.findViewById(R.id.text_view_mobile);

            textViewNo.setText(c.getSno()+".");
            textViewName.setText("Name: "+c.getName());
            textViewEmail.setText("Email: "+c.getEmail());
            textViewMobile.setText("Mobile: "+c.getMobile());
            return v;
        }
    }

    public boolean checkConnection(){
        ConnectivityManager manager = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        if(networkInfo == null || networkInfo.isConnected() == false){
            return false;
        }
        return true;
    }

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        button = (Button) v.findViewById(R.id.button);
        listView = (ListView) v.findViewById(R.id.lv);
        arrayList = new ArrayList<Contacts>();
        myAdapter =  new MyAdapter();
        listView.setAdapter(myAdapter);
        myTask = new MyTask();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkConnection()){
                    if(myTask.getStatus() == AsyncTask.Status.RUNNING || myTask.getStatus() == AsyncTask.Status.FINISHED){
                        Toast.makeText(getActivity(), "Already Running, Please Wait......", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    myTask.execute("http://api.androidhive.info/contacts/");
                }
                else {
                    Toast.makeText(getActivity(), "PLEASE Check Your Internet Connection", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return v;
    }
}
