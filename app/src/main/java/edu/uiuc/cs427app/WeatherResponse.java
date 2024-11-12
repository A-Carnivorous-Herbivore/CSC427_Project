package edu.uiuc.cs427app;

public class WeatherResponse {
    private Main main; // Stores main weather data (temperature, humidity, etc.)
    private Weather[] weather; // Array of weather conditions (e.g., description)
    private String name; // Name of the city
    private Wind wind; // Wind-related data (speed, direction)
    private long dt; // Timestamp of the weather data

    /**
     * Gets the main weather data (temperature, humidity, etc.).
     * @return Main weather data.
     */
    public Main getMain() {
        return main;
    }

    /**
     * Sets the main weather data (temperature, humidity, etc.).
     * @param main The main weather data.
     */
    public void setMain(Main main) {
        this.main = main;
    }

    /**
     * Gets the array of weather conditions (e.g., descriptions).
     * @return An array of weather conditions.
     */
    public Weather[] getWeather() {
        return weather;
    }

    /**
     * Sets the array of weather conditions (e.g., descriptions).
     * @param weather An array of weather conditions.
     */
    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }

    /**
     * Gets the wind-related data (speed and direction).
     * @return Wind data.
     */
    public Wind getWind() {
        return wind;
    }

    /**
     * Sets the wind-related data (speed and direction).
     * @param wind Wind data.
     */
    public void setWind(Wind wind) {
        this.wind = wind;
    }

    /**
     * Gets the timestamp of the weather data.
     * @return The timestamp as a long.
     */
    public long getDt() {
        return dt;
    }

    /**
     * Sets the timestamp of the weather data.
     * @param dt The timestamp as a long.
     */
    public void setDt(long dt) {
        this.dt = dt;
    }

    /**
     * Gets the name of the city.
     * @return The name of the city.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the city.
     * @param name The name of the city.
     */
    public void setName(String name) {
        this.name = name;
    }

    // Class representing the main weather data (e.g., temperature, humidity)
    public class Main {
        private float temp; // Temperature value
        private int humidity; // Humidity percentage

        /**
         * Gets the temperature.
         * @return The temperature.
         */
        public float getTemp() {
            return temp;
        }

        /**
         * Sets the temperature.
         * @param temp The temperature value.
         */
        public void setTemp(float temp) {
            this.temp = temp;
        }

        /**
         * Gets the humidity percentage.
         * @return The humidity percentage.
         */
        public int getHumidity() {
            return humidity;
        }

        /**
         * Sets the humidity percentage.
         * @param humidity The humidity percentage.
         */
        public void setHumidity(int humidity) {
            this.humidity = humidity;
        }
    }

    // Class representing a weather condition (e.g., description)
    public class Weather {
        private String description; // Description of the weather (e.g., "clear sky")

        /**
         * Gets the weather description.
         * @return The weather description.
         */
        public String getDescription() {
            return description;
        }

        /**
         * Sets the weather description.
         * @param description The weather description.
         */
        public void setDescription(String description) {
            this.description = description;
        }
    }

    // Class representing wind data (speed and direction)
    public class Wind {
        private float speed; // Wind speed
        private int deg; // Wind direction in degrees

        /**
         * Gets the wind speed.
         * @return The wind speed.
         */
        public float getSpeed() {
            return speed;
        }

        /**
         * Sets the wind speed.
         * @param speed The wind speed.
         */
        public void setSpeed(float speed) {
            this.speed = speed;
        }

        /**
         * Gets the wind direction in degrees.
         * @return The wind direction.
         */
        public int getDeg() {
            return deg;
        }

        /**
         * Sets the wind direction in degrees.
         * @param deg The wind direction.
         */
        public void setDeg(int deg) {
            this.deg = deg;
        }
    }
}
