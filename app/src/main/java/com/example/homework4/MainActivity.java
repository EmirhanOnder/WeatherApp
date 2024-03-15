package com.example.homework4;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements LocationListener {

    private RecyclerView.Adapter adapterFourDay;
    private RecyclerView recyclerView;
    EditText searchLocation;
    TextView temperature, highTemperature, lowTemperature, location, airCondition, updateTime, rainPosibility,
            humidityRate, windDirectionSpeed;
    ImageView conditionImage;
    private LocationManager locationManager;
    String cityName;

    FusedLocationProviderClient fusedLocationProviderClient;
    private final static int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        searchLocation = findViewById(R.id.searchLocation);
        temperature = findViewById(R.id.temperature);
        highTemperature = findViewById(R.id.highTemperature);
        lowTemperature = findViewById(R.id.lowTemperature);
        location = findViewById(R.id.location);
        airCondition = findViewById(R.id.airCondition);
        updateTime = findViewById(R.id.updateTime);
        conditionImage = findViewById(R.id.conditionImage);
        rainPosibility = findViewById(R.id.rainPosibility);
        humidityRate = findViewById(R.id.humidityRate);
        windDirectionSpeed = findViewById(R.id.windDirectionSpeed);
        recyclerView = findViewById(R.id.cardView);

        //new HTTPAsyncTask().execute("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + sehir + "/next7days?unitGroup=metric&include=days&key=9UQP55GX7KLWBAL5XX4BLZM9D&contentType=json");

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainActivity.this,new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_CODE);
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);


    }
    public void searchClick(View view) {
        String locationToSearch = String.valueOf(searchLocation.getText());
        new HTTPAsyncTask().execute("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + locationToSearch + "/next7days?unitGroup=metric&include=days&key=9UQP55GX7KLWBAL5XX4BLZM9D&contentType=json");
        searchLocation.setText("");
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        double lat = location.getLatitude();
        double longg = location.getLongitude();

        Geocoder gc = new Geocoder(this, Locale.getDefault());
        try {
            List<Address> addresses = gc.getFromLocation(lat, longg, 10);

            if (addresses != null && addresses.size() > 0) {
                for (Address adr : addresses) {
                    if (adr.getLocality() != null && adr.getLocality().length() > 0) {
                        cityName = adr.getLocality();
                        Log.e("cityName","CityName"+cityName);
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        //Toast.makeText(this, ""+cityName, Toast.LENGTH_SHORT).show();
        Log.e("lat","lat"+lat);
        Log.e("long","long"+longg);
        new HTTPAsyncTask().execute("https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/" + cityName + "/next7days?unitGroup=metric&include=days&key=9UQP55GX7KLWBAL5XX4BLZM9D&contentType=json");

    }


    @Override
    public void onFlushComplete(int requestCode) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {
    }



    private class HTTPAsyncTask extends AsyncTask<String,Void,String>
    {

        @Override
        protected String doInBackground(String... urls) {
            try {
                return HttpGet(urls[0]);
            }catch(IOException e)
            {
                return "Unable to retrieve web page: URL may be invalid";
            }
        }
        protected  void onPostExecute(String result)
        {
            try {
                JSONParser(result);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String HttpGet(String myUrl) throws IOException{
        InputStream inputStream = null;
        String result = "";
        URL url = new URL(myUrl);
        HttpURLConnection conn =( HttpURLConnection) url.openConnection();
        conn.connect();

        inputStream = conn.getInputStream();

        if(inputStream != null){
            result = convertInputStreamToString(inputStream);
        }
        else {
            result ="Did not work";
        }
        return  result;

    }

    private String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private void JSONParser(String json) {
        try {
            Log.d("jsonparser","asdfasdf");
            JSONObject jsonObject = new JSONObject(json);

            JSONArray days = jsonObject.getJSONArray("days");

            JSONObject firstDay = days.getJSONObject(0);

            String locationAddr =  jsonObject.getString("resolvedAddress");

            location.setText(locationAddr);

            double temp = firstDay.getDouble("temp");

            double highTemp = firstDay.getDouble("tempmax");

            double lowTemp = firstDay.getDouble("tempmin");

            temperature.setText(temp +"°");

            highTemperature.setText(highTemp + "°");

            lowTemperature.setText(lowTemp + "°");

            String condition = firstDay.getString("conditions");

            String icons = firstDay.getString("icon");

            setImage(icons,conditionImage);

            airCondition.setText(condition);

            long timeNow = Instant.now().getEpochSecond();

            long datetimeEpoch = firstDay.getLong("datetimeEpoch");

            long timeDiff = timeNow- datetimeEpoch;

            double hourDiff = timeDiff / 3600;

            if(hourDiff < 1)
            {
                updateTime.setText("Just Now");
            }
            else
                updateTime.setText((int) hourDiff+" hours ago");

            double rainPosib = firstDay.getDouble("precipprob");
            rainPosibility.setText("%"+(int)rainPosib);

            double windSpeed = firstDay.getDouble("windspeed");
            double windDirection = firstDay.getDouble("winddir");
            String direction="";
            if (windDirection >= 337.5 || windDirection < 22.5) {
                direction= "N";
            } else if (windDirection >= 22.5 && windDirection < 67.5) {
                direction= "NE";
            } else if (windDirection >= 67.5 && windDirection < 112.5) {
                direction= "E";
            } else if (windDirection >= 112.5 && windDirection < 157.5) {
                direction= "SE";
            } else if (windDirection >= 157.5 && windDirection < 202.5) {
                direction= "S";
            } else if (windDirection >= 202.5 && windDirection < 247.5) {
                direction= "SW";
            } else if (windDirection >= 247.5 && windDirection < 292.5) {
                direction= "W";
            } else if (windDirection >= 292.5 && windDirection < 337.5) {
                direction= "NW";
            }
            windDirectionSpeed.setText(windSpeed+" kmph " + direction);

            double humidity = firstDay.getDouble("humidity");
            humidityRate.setText("% "+humidity);


            ArrayList<FourDayCardView> items = new ArrayList<>();

            for(int i=1 ; i<days.length();i++)
            {
                JSONObject dayofweek = days.getJSONObject(i);
                String dateStr = dayofweek.getString("datetime");
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDate date =  LocalDate.parse(dateStr,formatter);
                DayOfWeek day = date.getDayOfWeek();

                double tempMax = Double.parseDouble(dayofweek.getString("tempmax"));
                double tempMin = Double.parseDouble(dayofweek.getString("tempmin"));
                String icon = dayofweek.getString("icon");

                FourDayCardView cardView = new FourDayCardView(day.toString(),String.valueOf(tempMax),String.valueOf(tempMin),icon);
                items.add(cardView);

            }

            recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false));
            adapterFourDay = new FourDayCardViewAdapter(items);
            recyclerView.setAdapter(adapterFourDay);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public static void setImage(String icons,ImageView conditionImage)
    {
        switch (icons) {
            case "clear-day":
                conditionImage.setImageResource(R.drawable.clear_day);
                break;
            case "clear-night":
                conditionImage.setImageResource(R.drawable.clear_night);
                break;
            case "cloudy":
                conditionImage.setImageResource(R.drawable.cloudy);
                break;
            case "fog":
                conditionImage.setImageResource(R.drawable.fog);
                break;
            case "hail":
                conditionImage.setImageResource(R.drawable.hail);
                break;
            case "partly-cloudy-day":
                conditionImage.setImageResource(R.drawable.partly_cloudy_day);
                break;
            case "partly-cloudy-night":
                conditionImage.setImageResource(R.drawable.partly_cloudy_night);
                break;
            case "rain":
                conditionImage.setImageResource(R.drawable.rainn);
                break;
            case "rain-snow":
                conditionImage.setImageResource(R.drawable.rain_snow);
                break;
            case "rain-snow-showers-day":
                conditionImage.setImageResource(R.drawable.rain_snow_showers_day);
                break;
            case "rain-snow-showers-night":
                conditionImage.setImageResource(R.drawable.rain_snow_showers_night);
                break;
            case "rainy":
                conditionImage.setImageResource(R.drawable.rainy);
                break;
            case "showers-day":
                conditionImage.setImageResource(R.drawable.showers_day);
                break;
            case "showers-night":
                conditionImage.setImageResource(R.drawable.showers_night);
                break;
            case "sleet":
                conditionImage.setImageResource(R.drawable.sleet);
                break;
            case "snow":
                conditionImage.setImageResource(R.drawable.snow);
                break;
            case "snow-showers-day":
                conditionImage.setImageResource(R.drawable.snow_showers_day);
                break;
            case "thunder":
                conditionImage.setImageResource(R.drawable.thunder);
                break;
            case "thunder-rain":
                conditionImage.setImageResource(R.drawable.thunder_rain);
                break;
            case "thunder-showers-day":
                conditionImage.setImageResource(R.drawable.thunder_showers_day);
                break;
            case "thunder-showers-night":
                conditionImage.setImageResource(R.drawable.thunder_showers_night);
                break;
            case "wind":
                conditionImage.setImageResource(R.drawable.windy);
                break;

        }
    }



}