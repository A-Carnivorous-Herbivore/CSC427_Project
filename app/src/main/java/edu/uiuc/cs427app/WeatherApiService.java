package edu.uiuc.cs427app;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface WeatherApiService {
    /*
     * Fetches weather details for a specific city.
     */
    @GET("weather")
    Call<WeatherResponse> getWeatherDetails(
            @Query("q") String cityName,
            @Query("appid") String apiKey,
            @Query("units") String units);
}
