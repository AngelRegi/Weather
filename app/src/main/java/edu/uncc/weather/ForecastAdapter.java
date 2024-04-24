/**
 * Assignment : Group 13 HW06
 * File Name : ForecastAdapter
 * Student Name : Angel Regi Chellathurai Vijayakumari
 * **/

package edu.uncc.weather;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

public class ForecastAdapter extends ArrayAdapter<Forecast> {
    public ForecastAdapter(@NonNull Context context, int resource, @NonNull List<Forecast> objects) {
        super(context, resource, objects);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.forecast_row_item, parent, false);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.textViewDateTime = convertView.findViewById(R.id.textViewDateTime);
            viewHolder.textViewTemp =  convertView.findViewById(R.id.textViewTemp);
            viewHolder.textViewTempMax = convertView.findViewById(R.id.textViewTempMax);
            viewHolder.textViewTempMin =  convertView.findViewById(R.id.textViewTempMin);
            viewHolder.textViewHumidity = convertView.findViewById(R.id.textViewHumidity);
            viewHolder.textViewDesc =  convertView.findViewById(R.id.textViewDesc);
            viewHolder.imageViewWeatherIcon = convertView.findViewById(R.id.imageViewWeatherIcon);
            convertView.setTag(viewHolder);
        }
        ViewHolder viewHolder = (ViewHolder) convertView.getTag();
        Forecast forecast = getItem(position);
        viewHolder.textViewDateTime.setText(forecast.getDateTime());
        viewHolder.textViewTemp.setText(forecast.getTemp() + "F");
        viewHolder.textViewTempMax.setText("Max: " + forecast.getTemp_max() + "F");
        viewHolder.textViewTempMin.setText("Min: " + forecast.getTemp_min() + "F");
        viewHolder.textViewHumidity.setText("Humidity: " + forecast.getHumidity() + "%");
        viewHolder.textViewDesc.setText(forecast.getDesc());
        String iconUrl = "http://openweathermap.org/img/wn/"+ forecast.getIcon() +"@2x.png";
        Picasso.get().load(iconUrl).into( viewHolder.imageViewWeatherIcon);
        return convertView;
    }

    public static class ViewHolder {
        TextView textViewDateTime;
        TextView textViewTemp;
        TextView textViewTempMax;
        TextView textViewTempMin;
        TextView textViewHumidity;
        TextView textViewDesc;
        ImageView imageViewWeatherIcon;
    }
}
