package adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ayush.paytmdevchallenge.R;

import java.util.Map;

import model.Rates;

public class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    private Map<String, Double> currencyData;
    private Context context;
    private int constant = 1;

    public GridViewAdapter(Context context, Map<String, Double> currencyData) {
        this.currencyData = currencyData;
        this.context = context;
    }

    public void setRateConstant(int constant){
        this.constant = constant;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView country;
        TextView value;

        ViewHolder(View v) {
            super(v);
            country = v.findViewById(R.id.country);
            value = v.findViewById(R.id.currency_val);
        }
    }

    @Override
    public GridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.grid, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        viewHolder.country.setText(currencyData.keySet().toArray()[position].toString());
        viewHolder.value.setText(String.valueOf((constant*(Double)currencyData.values().toArray()[position])));
    }

    @Override
    public int getItemCount() {
        return currencyData.size();
    }
}
