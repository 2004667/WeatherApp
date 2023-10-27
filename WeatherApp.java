import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

class WeatherData {
    private double temperature;
    private double humidity;
    private String conditions;

    public WeatherData(double temperature, double humidity, String conditions) {
        this.temperature = temperature;
        this.humidity = humidity;
        this.conditions = conditions;
    }

    public double getTemperature() {
        return temperature;
    }

    public double getHumidity() {
        return humidity;
    }

    public String getConditions() {
        return conditions;
    }
}

class WeatherAdapter {
    public static WeatherData adaptData(String city, String apiKey) {
        try {
            // Create the URL for the API request
            String apiUrl = "http://api.openweathermap.org/data/2.5/weather?q=" + city + "&units=metric&appid=" + apiKey;

            // Create an HTTP connection
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Parse the response JSON using the org.json library
            JSONObject jsonObject = new JSONObject(response.toString());

            // Extract temperature and humidity from the "main" object
            JSONObject mainObject = jsonObject.getJSONObject("main");
            double temperature = mainObject.getDouble("temp");
            double humidity = mainObject.getDouble("humidity");

            // Access the "weather" array
            JSONArray weatherArray = jsonObject.getJSONArray("weather");

            // Initialize conditions with a default value
            String conditions = "Unknown";

            if (weatherArray.length() > 0) {
                // Get the first object in the "weather" array
                JSONObject weatherObject = weatherArray.getJSONObject(0);


                // Extract the "description" field
                conditions = weatherObject.getString("description");
            }

            return new WeatherData(temperature, humidity, conditions);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

public class WeatherApp {
    public static void main(String[] args) {
        String apiKey = "15592ccee0d70c42e8b3efa10f23dcbb";  // Replace with your actual API key

        // Allow the user to input the city
        System.out.print("Enter a city (e.g., London,UK): ");
        Scanner scanner = new Scanner(System.in);
        String city = scanner.nextLine();

        WeatherData data = WeatherAdapter.adaptData(city, apiKey);
        if (data != null) {
            System.out.println("Weather Data for " + city + ":");
            System.out.println("Temperature: " + data.getTemperature() + "°C");
            System.out.println("Humidity: " + data.getHumidity() + "%");
            System.out.println("Conditions: " + data.getConditions());
        } else {
            System.out.println("Failed to fetch weather data.");
        }
    }
}
