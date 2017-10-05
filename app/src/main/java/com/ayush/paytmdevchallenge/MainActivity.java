package com.ayush.paytmdevchallenge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import adapter.GridViewAdapter;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import model.RootObject;
import retrofit.CurrencyClient;
import retrofit.CurrencyService;
import timer.TimerService;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private CurrencyService apiInterface;
    private Map<String, Double> currencyList = new HashMap<>();
    private RecyclerView recyclerView;
    private Context context;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private EditText currencyText;
    private Button button;
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        initViews();
        startService(new Intent(this, TimerService.class));
        initRetrofit();
    }

    private void initViews() {
        recyclerView = (RecyclerView) findViewById(R.id.gridView);
        recyclerViewLayoutManager = new GridLayoutManager(context, 3);
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        recyclerViewAdapter = new GridViewAdapter(context, currencyList);
        recyclerView.setAdapter(recyclerViewAdapter);
        currencyText = (EditText) findViewById(R.id.editText);
        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!currencyText.getText().toString().isEmpty()) {
                    int text = Integer.parseInt(currencyText.getText().toString());
                    ((GridViewAdapter) recyclerViewAdapter).setRateConstant(text);
                    recyclerViewAdapter.notifyDataSetChanged();
                }
            }
        });
        spinner = (Spinner) findViewById(R.id.spinner);
    }

    private void initRetrofit() {
        apiInterface = CurrencyClient.getClient().create(CurrencyService.class);
        getCurrencyData("");
    }

    private void getCurrencyData(String base) {
        Observable<RootObject> data = null;
        if(!base.isEmpty()){
            data = apiInterface.getData(base);
        }else{
            data = apiInterface.getData();
        }
        data.subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(currencyData -> {
                    getAllCurrencyKeys(currencyData);
                    if(base.isEmpty()) {
                        populateSpinnerData();
                    }
                    populateCurrencyData();
                });
    }

    private void populateSpinnerData() {
        String[] arrayList = currencyList.keySet().toArray(new String[currencyList.size()]);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, arrayList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
    }

    private void populateCurrencyData() {
        recyclerViewAdapter = new GridViewAdapter(context, currencyList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerViewAdapter.notifyDataSetChanged();
    }

    private void getAllCurrencyKeys(RootObject body) {
        Iterator<?> keys = null;
        try {
            JSONObject jsonObject = ((JSONObject) (new JSONObject(new Gson().toJson(body)).get("rates")));
            keys = jsonObject.keys();
            do {
                String name = keys.next().toString();
                currencyList.put(name, (Double) jsonObject.get(name));
            } while (keys.hasNext());
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private BroadcastReceiver br = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.v("TAG", "Recieved data from Broadcast rcvr");
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(br, new IntentFilter(TimerService.COUNTDOWN_BR));
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(br);
    }

    @Override
    public void onStop() {
        try {
            unregisterReceiver(br);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, TimerService.class));
        super.onDestroy();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String selectedItem = adapterView.getItemAtPosition(i).toString();
        getCurrencyData(selectedItem);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
