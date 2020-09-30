package andrey.elin.weatherapp3;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import javax.net.ssl.HttpsURLConnection;

import andrey.elin.weatherapp3.interfaces.OpenWeather;
import andrey.elin.weatherapp3.model.WeatherRequest;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

//    private static final String TAG = "WEATHER";
//    private static final String WEATHER_URL = "https://api.openweathermap.org/data/2.5/weather?q=Moscow&APPID=d06e60407fcb25cb3b554cc5fdf89690";

    private static final float AbsoluteZero = -273.15f;

    private OpenWeather openWeather;
    private SharedPreferences sharedPref;
    private String editApiKey = "d06e60407fcb25cb3b554cc5fdf89690";

    private EditText temperature;
    private EditText pressure;
    private EditText humidity;
    private EditText windSpeed;
    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initRetorfit();
        init();
//        initPreferences();
        initEvents();

        picasso();

        Toolbar toolbar = initToolbar();
        initDrawer(toolbar);

    }

    private void picasso() {
        Picasso.get()
                .load("https://images.unsplash.com/photo-1513326738677-b964603b136d?ixlib=rb-1.2.1&auto=format&fit=crop&w=687&q=80")
                .into(imageView);
    }

    // находим вьюшки
    private void init() {
        imageView = findViewById(R.id.imageView);
        temperature = findViewById(R.id.textTemprature);
        pressure = findViewById(R.id.textPressure);
        humidity = findViewById(R.id.textHumidity);
        windSpeed = findViewById(R.id.textWindspeed);
//        Button refresh = findViewById(R.id.refresh);
//        refresh.setOnClickListener(clickListener);
//        refresh.setOnClickListener(clickAlertDialog1);
    }

    private Toolbar initToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        return toolbar;
    }

    private void initDrawer(Toolbar toolbar) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    private final View.OnClickListener clickAlertDialog1 = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            // Создаем билдер и передаем контекст приложения
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            // в билдере указываем заголовок окна
            builder.setTitle(R.string.exclamation)
                    // указываем сообщение в окне
                    .setMessage(R.string.press_button)
                    // можно указать и пиктограмму
                    .setIcon(R.mipmap.ic_launcher_round)
                    // из этого окна нельзя выйти кнопкой back
                    .setCancelable(false)
                    // устанавливаем кнопку
                    .setPositiveButton(R.string.button,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    Toast.makeText(MainActivity.this, "Кнопка нажата", Toast.LENGTH_SHORT).show();
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    };

//    View.OnClickListener clickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//            try {
//                final String url = WEATHER_URL;
//
//                final URL uri = new URL(url);
//                final Handler handler = new Handler(); // Запоминаем основной поток
//                new Thread(new Runnable() {
//                    @RequiresApi(api = Build.VERSION_CODES.N)
//                    public void run() {
//                        HttpsURLConnection urlConnection = null;
//                        try {
//                            urlConnection = (HttpsURLConnection) uri.openConnection();
//                            urlConnection.setRequestMethod("GET"); // установка метода получения данных -GET
//                            urlConnection.setReadTimeout(10000); // установка таймаута - 10 000 миллисекунд
//                            BufferedReader in = new BufferedReader(new InputStreamReader(urlConnection.getInputStream())); // читаем  данные в поток
//                            String result = getLines(in);
//                            // преобразование данных запроса в модель
//                            Gson gson = new Gson();
//                            final WeatherRequest weatherRequest = gson.fromJson(result, WeatherRequest.class);
//                            // Возвращаемся к основному потоку
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    displayWeather(weatherRequest);
//                                }
//                            });
//                        } catch (Exception e) {
//                            Log.e(TAG, "Fail connection", e);
//                            e.printStackTrace();
//                        } finally {
//                            if (null != urlConnection) {
//                                urlConnection.disconnect();
//                            }
//                        }
//                    }
//                }).start();
//            } catch (MalformedURLException e) {
//                Log.e(TAG, "Fail URI", e);
//                e.printStackTrace();
//            }
//        }

    // собираем строку
//        @RequiresApi(api = Build.VERSION_CODES.N)
//        private String getLines(BufferedReader in) {
//            return in.lines().collect(Collectors.joining("\n"));
//        }
//
//        // отображение на экран
//        @SuppressLint("DefaultLocale")
//        private void displayWeather(WeatherRequest weatherRequest) {
//            city.setText(weatherRequest.getName());
//            temperature.setText(String.format("%.2f", weatherRequest.getMain().getTemp() - 273.0));
//            pressure.setText(String.format("%d", weatherRequest.getMain().getPressure()));
//            humidity.setText(String.format("%d", weatherRequest.getMain().getHumidity()));
//            windSpeed.setText(String.format("%d", weatherRequest.getWind().getSpeed()));
//        }
//    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Здесь определяем меню приложения (активити)
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem search = menu.findItem(R.id.action_search); // поиск пункта меню поиска

        final SearchView searchText = (SearchView) search.getActionView(); // строка поиска
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // реагирует на конец ввода поиска
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(searchText, query, Snackbar.LENGTH_LONG).show();
                return true;
            }

            // реагирует на нажатие каждой клавиши
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Обработка выбора пункта меню приложения (активити)
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START))
            drawer.closeDrawer(GravityCompat.START);
        else
            super.onBackPressed();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }

//    private void initPreferences() {
//        sharedPref = getPreferences(MODE_PRIVATE);
//        loadPreferences();                   // Загружаем настройки
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        savePreferences();
//    }

//    private void savePreferences() {
//        SharedPreferences.Editor editor = sharedPref.edit();
//        editor.putString("apiKey", editApiKey);
//        editor.commit();
//    }
//
//    private void loadPreferences() {
//        String loadedApiKey = sharedPref.getString("apiKey", "d06e60407fcb25cb3b554cc5fdf89690");
//        editApiKey.setText(loadedApiKey);
//    }

    // Создаём обработку клика кнопки
    private void initEvents() {
        Button button = findViewById(R.id.refresh);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                savePreferences();            // Сохраняем настройки
                requestRetrofit("Moscow", editApiKey);
            }
        });
    }


    private void initRetorfit() {
        Retrofit retrofit;
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/") // Базовая часть
                // адреса
                // Конвертер, необходимый для преобразования JSON
                // в объекты
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        // Создаём объект, при помощи которого будем выполнять запросы
        openWeather = retrofit.create(OpenWeather.class);
    }

    private void requestRetrofit(String city, String keyApi) {
        openWeather.loadWeather(city, editApiKey)
                .enqueue(new Callback<WeatherRequest>() {
                    @SuppressLint({"SetTextI18n", "DefaultLocale"})
                    @Override
                    public void onResponse(Call<WeatherRequest> call, Response<WeatherRequest> response) {
                        if (response.body() != null) {
                            float result = response.body().getMain().getTemp() + AbsoluteZero;
                            temperature.setText(String.format("%.2f", result));
                            pressure.setText(String.format("%d", response.body().getMain().getPressure()));
                            humidity.setText(String.format("%d", response.body().getMain().getHumidity()));
                            windSpeed.setText(String.format("%d", response.body().getWind().getSpeed()));

                        }
                    }

                    @Override
                    public void onFailure(Call<WeatherRequest> call, Throwable t) {
                        temperature.setText("Error");
                    }
                });
    }

}