package com.tann.vattana.currencyexchangeapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.support.v7.widget.Toolbar;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity {

    private String[] currencyName = {"USD", "KHR", "EUR", "CNY", "MYR", "SGD", "AUD", "THB", "GBP", "CAD"};
    private String[] currencyFullName = {"United States Dollar", "Cambodia Riel", "European Euro", "China Yuan", "Malaysia Ringgit",
                                            "Singapore Dollar", "Australia Dollar", "Thailand Baht", "United Kingdom Pound", "Canada Dollar"};
    private String[] currency = {"United States Dollar-USD", "Cambodia Riel-KHR", "European Euro-EUR", "China Yuan-CNY", "Malaysia Ringgit-MYR",
                                            "Singapore Dollar-SGD", "Australia Dollar-AUD", "Thailand Baht-THB", "United Kingdom Pound-GBP", "Canada Dollar-CAD"};
    private String[] value = new String[10];
    private Integer[] countryFlag = {R.drawable.ic_usa, R.drawable.ic_cambodia, R.drawable.ic_euro, R.drawable.ic_china, R.drawable.ic_malaysia,
                                        R.drawable.ic_singapore, R.drawable.ic_australia, R.drawable.ic_thailand, R.drawable.ic_uk, R.drawable.ic_canada};
    private EditText txtValue;
    private Button convertBtn;
    private Toolbar toolbar;
    private ListView listView;
    private String[] rateArray = new String[10];
    private Spinner spinner;
    private CustomListView customListView;
    private CustomSpinner customSpinner;
    private int index;
    private double inputVal;
    private int indexOfDot;
    private ConnectivityManager connectivityManager;
    private NetworkInfo wifi, mobileData;
    private OfflineCurrency offlineCurrency;
    private SharedPreferences pref;
    private String readPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializedUI();
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Home");

        //Create and populate custom listView
        customListView = new CustomListView(MainActivity.this, currencyName, currencyFullName, value, countryFlag);
        listView.setAdapter(customListView);

        //Create and populate the spinner
        customSpinner = new CustomSpinner(MainActivity.this, currency, countryFlag);
        spinner.setAdapter(customSpinner);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                index = i;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        pref = getSharedPreferences("MyPref", MODE_PRIVATE);

        convertBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //save input from user
                SharedPreferences.Editor editor = pref.edit();
                editor.putString("amount", txtValue.getText().toString());
                editor.commit();

                if (!txtValue.getText().toString().isEmpty()) {
                    inputVal = Double.parseDouble(txtValue.getText().toString());
                }

                //check the internet connectivity
                if (wifi.isConnected() || mobileData.isConnected()){
                    new calRate().execute("http://apilayer.net/api/live?access_key=9612589001310cc0d199bb869c76ec7d&currencies=USD,KHR,EUR,CNY,MYR,SGD,AUD,THB,GBP,CAD&source=USD&format=1");

                }
                else {
                    //offline calculation
                    for (int i  = 0; i < value.length; i++){
                        value[i] = String.valueOf(inputVal * Double.parseDouble(offlineCurrency.value[i]));
                    }
                    customListView.notifyDataSetChanged();
                }

            }
        });


        //read previous data
        readPref = pref.getString("amount", "");
        txtValue.setText(readPref);
    }

    //readJSON
    public String readJSONFeed(String URL){
        StringBuilder sBuilder = new StringBuilder();
        HttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(URL);
        try{
            HttpResponse response = client.execute(httpGet);
            StatusLine sLine = response.getStatusLine();
            int statusCode = sLine.getStatusCode();
            if (statusCode == 200){
                HttpEntity entity = response.getEntity();
                InputStream inputStream = entity.getContent();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = reader.readLine()) != null){
                    sBuilder.append(line);
                }
                inputStream.close();
            } else {
                Log.d("readJSONFeed", "Falied");
            }
        } catch (Exception e){
            Log.d("readJSONFeed", e.getLocalizedMessage());
        }
        return sBuilder.toString();
    }//end of readJSONFeed

    //get currency rate
    private class calRate extends AsyncTask<String, Void, String> {

        protected String doInBackground(String... urls) {
            return readJSONFeed(urls[0]);
        }//end of doInBackground

        protected void onPostExecute(String result) {
            //read JSON and get currency rate
            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONObject rate = new JSONObject(jsonObject.getString("quotes"));

                rateArray[0] = rate.getString("USDUSD");
                rateArray[1] = rate.getString("USDKHR");
                rateArray[2] = rate.getString("USDEUR");
                rateArray[3] = rate.getString("USDCNY");
                rateArray[4] = rate.getString("USDMYR");
                rateArray[5] = rate.getString("USDSGD");
                rateArray[6] = rate.getString("USDAUD");
                rateArray[7] = rate.getString("USDTHB");
                rateArray[8] = rate.getString("USDGBP");
                rateArray[9] = rate.getString("USDCAD");


            }catch (Exception e) {
                Log.d("calRate", e.getLocalizedMessage());
            }//end of catch

            //Compute currency rate
            if (index == 0){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * Double.parseDouble(rateArray[i]));
                }
                cutRate();
            }//end of if
            else if (index == 1){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[1])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            else if (index == 2){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[2])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            else if (index == 3){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[3])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            else if (index == 4){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[4])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            else if (index == 5){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[5])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            else if (index == 6){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[6])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            else if (index == 7){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[7])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            else if (index == 8){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[8])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            else if (index == 9){
                for (int i = 0; i < rateArray.length; i++){
                    value[i] = String.valueOf(inputVal * (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[9])) / (Double.parseDouble(rateArray[0]) / Double.parseDouble(rateArray[i])));
                }
                cutRate();
            }
            customListView.notifyDataSetChanged();

        }//end of onPostExecute
    }// end of calRate

    //reduce decimal
    private String[] cutRate(){
        for (int i = 0; i < rateArray.length; i++){
            indexOfDot = value[i].indexOf(".");
            value[i] = value[i].substring(0, indexOfDot + 2);
        }
        return value;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_info, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.aboutBtn) {
            startActivity(new Intent(getApplicationContext(), AboutPage.class));
        }
        return super.onOptionsItemSelected(item);
    }

    private void initializedUI() {
        txtValue = (EditText) findViewById(R.id.currencyEditText);
        convertBtn = (Button) findViewById(R.id.convertBtn);
        toolbar = (Toolbar) findViewById(R.id.actionBar);
        listView = (ListView) findViewById(R.id.listView);
        spinner = (Spinner) findViewById(R.id.spinner);
        connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        wifi = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        mobileData = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        offlineCurrency = new OfflineCurrency(index);
    }
}
