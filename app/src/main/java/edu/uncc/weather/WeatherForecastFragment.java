/**
 * Assignment : Group 13 HW06
 * File Name : WeatherForecastFragment
 * Student Name : Angel Regi Chellathurai Vijayakumari
 * **/

package edu.uncc.weather;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import edu.uncc.weather.databinding.FragmentWeatherForecastBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WeatherForecastFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WeatherForecastFragment extends Fragment {

    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private final OkHttpClient client = new OkHttpClient();
    private DataService.City mCity;
    private ArrayList<Forecast> forecasts = new ArrayList<>();
    ForecastAdapter adapter;
    FragmentWeatherForecastBinding binding;

    public WeatherForecastFragment() {
        // Required empty public constructor
    }

    public static WeatherForecastFragment newInstance(DataService.City city) {
        WeatherForecastFragment fragment = new WeatherForecastFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM_CITY, city);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCity = (DataService.City) getArguments().getSerializable(ARG_PARAM_CITY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding  = FragmentWeatherForecastBinding.inflate(inflater, container, false);
        getForecast();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Weather Forecast");
    }

    void getForecast() {
        double lat = mCity.getLat();
        double lon = mCity.getLon();
        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/forecast").newBuilder()
                .addQueryParameter("lat", String.valueOf(lat))
                .addQueryParameter("lon", String.valueOf(lon))
                .addQueryParameter(MainActivity.API_KEY_NAME, MainActivity.API_KEY_VALUE)
                .addQueryParameter(MainActivity.UNITS, MainActivity.UNITS_VALUE)
                .build();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    try {
                        forecasts.clear();
                        ResponseBody responseBody = response.body();
                        String body = responseBody.string();
                        JSONObject json = new JSONObject(body);
                        JSONArray forecastJson = json.getJSONArray("list");
                        for(int i=0; i < forecastJson.length(); i++) {
                            JSONObject forecastObject = forecastJson.getJSONObject(i);
                            JSONObject tempJson = forecastObject.getJSONObject("main");
                            JSONObject weatherJson = forecastObject.getJSONArray("weather").getJSONObject(0);
                            Forecast forecast = new Forecast();
                            forecast.setDateTime(forecastObject.getString("dt_txt"));
                            forecast.setTemp(tempJson.getDouble("temp"));
                            forecast.setTemp_max(tempJson.getDouble("temp_max"));
                            forecast.setTemp_min(tempJson.getDouble("temp_min"));
                            forecast.setHumidity(tempJson.getDouble("humidity"));
                            forecast.setDesc(weatherJson.getString("description"));
                            forecast.setIcon(weatherJson.getString("icon"));
                            forecasts.add(forecast);
                        }

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.textViewCityName.setText(mCity.getCity() + ", " + mCity.getCountry());
                               ListView listView =  binding.listView;
                                adapter = new ForecastAdapter(getActivity(), R.layout.forecast_row_item, forecasts);
                                listView.setAdapter(adapter);
                            }
                        });

                    } catch(JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    Log.d("demo", "onResponse: Error");
                }
            }
        });
    }
}