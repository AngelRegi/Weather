/**
 * Assignment : Group 13 HW06
 * File Name : CurrentWeatherFragment
 * Student Name : Angel Regi Chellathurai Vijayakumari
 * **/

package edu.uncc.weather;

import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import edu.uncc.weather.databinding.FragmentCurrentWeatherBinding;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class CurrentWeatherFragment extends Fragment {
    private static final String ARG_PARAM_CITY = "ARG_PARAM_CITY";
    private DataService.City mCity;
    private final OkHttpClient client = new OkHttpClient();
    FragmentCurrentWeatherBinding binding;
    CurrentWeatherInterface ICurrentWeather;

    public CurrentWeatherFragment() {
        // Required empty public constructor
    }

    public static CurrentWeatherFragment newInstance(DataService.City city) {
        CurrentWeatherFragment fragment = new CurrentWeatherFragment();
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
        binding = FragmentCurrentWeatherBinding.inflate(inflater, container, false);
        binding.buttonCheckForecast.setOnClickListener(v -> {
            ICurrentWeather.goToForcastFragment(mCity);
        });
        // api to get the current weather forecast
        getCurrentWeather();
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Current Weather");
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        if(context instanceof CurrentWeatherInterface) {
            ICurrentWeather = (CurrentWeatherInterface) context;
        } else {
            throw new RuntimeException(context.toString() + "must implement CurrentWeatherInterface");
        }
    }
    public interface CurrentWeatherInterface {
        void goToForcastFragment(DataService.City city);
    }

    void getCurrentWeather() {
        double lat = mCity.getLat();
        double lon = mCity.getLon();
        HttpUrl url = HttpUrl.parse("https://api.openweathermap.org/data/2.5/weather").newBuilder()
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

                        ResponseBody responseBody = response.body();
                        String body = responseBody.string();
                        JSONObject json = new JSONObject(body);
                        JSONObject weatherJson = json.getJSONArray("weather").getJSONObject(0);
                        JSONObject tempJson = json.getJSONObject("main");
                        JSONObject windJson = json.getJSONObject("wind");
                        JSONObject cloudJson = json.getJSONObject("clouds");
                        Weather weather = new Weather();
                        weather.setDescription(weatherJson.getString("description"));
                        weather.setIcon(weatherJson.getString("icon"));

                        weather.setTemp(tempJson.getDouble("temp"));
                        weather.setTemp_max(tempJson.getDouble("temp_max"));
                        weather.setTemp_min(tempJson.getDouble("temp_min"));
                        weather.setHumidity(tempJson.getDouble("humidity"));

                        weather.setWind_speed(windJson.getDouble("speed"));
                        weather.setWind_deg(windJson.getDouble("deg"));
                        weather.setCloudy(cloudJson.getDouble("all"));


                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                binding.textViewCityName.setText(mCity.getCity() + ", " + mCity.getCountry());
                                binding.textViewTemp.setText(weather.getTemp() + " F");
                                binding.textViewTempMax.setText(weather.getTemp_max() + " F");
                                binding.textViewTempMin.setText(weather.getTemp_min() + " F");
                                binding.textViewDesc.setText(weather.getDescription() + "");
                                binding.textViewHumidity.setText(weather.getHumidity() + "%");
                                binding.textViewWindSpeed.setText(weather.getWind_speed() + " miles/hr");
                                binding.textViewWindDegree.setText(weather.getWind_deg() + " degrees");
                                binding.textViewCloudiness.setText(weather.getCloudy() + "%");
                                String iconUrl = "http://openweathermap.org/img/wn/"+ weather.getIcon() +"@2x.png";
                                Picasso.get().load(iconUrl).into( binding.imageViewWeatherIcon);
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