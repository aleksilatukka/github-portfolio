package weatherapp;

import javafx.scene.control.TextField;
import org.json.JSONException;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import org.json.JSONArray;
import org.json.JSONObject;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javafx.scene.image.ImageView;
import java.util.Locale;

public class WeatherApp extends Application {
    String API_KEY = "123"; //PUT YOUR OWN APIKEY HERE
    private VBox weatherContainer;
    private final iAPI weatherAPI = new iAPI() {
        
    @Override
    public JSONObject lookUpLocation(String loc) {
    try {
        String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
        String urlString = apiUrl + "?q=" + loc + "&appid=" + API_KEY;

        HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
        connection.setRequestMethod("GET");

        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            InputStreamReader reader = new InputStreamReader(connection.getInputStream());
            StringBuilder response = new StringBuilder();
            int data;
            while ((data = reader.read()) != -1) {
                response.append((char) data);
            }
            reader.close();

            JSONObject jsonResponse = new JSONObject(response.toString());
            JSONObject coord = jsonResponse.getJSONObject("coord");
            double lat = coord.getDouble("lat");
            double lon = coord.getDouble("lon");

            return new JSONObject().put("lat", lat).put("lon", lon);
        } else {
            System.err.println("Failed to retrieve coordinates. Response Code: " + connection.getResponseCode());
            return null;
        }
    } catch (IOException e) {
        System.err.println("IOException while looking up location: " + e.getMessage());
        return null;
    }
}
        @Override
        public String getCurrentWeather(double lat, double lon) {
            try {
                String apiUrl = "https://api.openweathermap.org/data/2.5/weather";
                String apiKey = "123"; // Replace with your actual API key
                String urlString = apiUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + apiKey;
                HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");
                
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    StringBuilder response = new StringBuilder();
                    int data;
                    
                    while ((data = reader.read()) != -1) {
                        response.append((char) data);
                    }
                    reader.close();
                    String apiResponse = response.toString();
                    System.out.println("API Response: " + apiResponse);
                    return response.toString();
                    
                } else {
                    System.err.println("Failed to retrieve current weather. Response Code: " + connection.getResponseCode());
                    return null;
                }
            } catch (IOException e) {
                System.err.println("IOException while getting current weather: " + e.getMessage());
                return null;         
            }
        }

        @Override
        public String getForecast(double lat, double lon) {
            try {
                String apiUrl = "https://api.openweathermap.org/data/2.5/forecast";
                String urlString = apiUrl + "?lat=" + lat + "&lon=" + lon + "&appid=" + API_KEY;
                HttpURLConnection connection = (HttpURLConnection) new URL(urlString).openConnection();
                connection.setRequestMethod("GET");

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                    StringBuilder response = new StringBuilder();
                    int data;
                    
                    while ((data = reader.read()) != -1) {
                        response.append((char) data);
                    }
                    reader.close();
                    return response.toString();
                } else {
                    System.err.println("Failed to retrieve forecast. Response Code: " + connection.getResponseCode());
                    return null;
                }
            } catch (IOException e) {
                System.err.println("IOException while getting forecast: " + e.getMessage());
                return null;
            }
        }
    };

    @Override
    public void start( Stage stage) {
        VBox root = new VBox();
        root.setStyle("-fx-background-color: #f0f8ff;");  // Light blue

        // Adding a search feature
        TextField searchField = new TextField();
        searchField.setPromptText("Enter city name");
        Button searchButton = new Button("Search");
        searchButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;"); // Set button style
        searchButton.setOnAction(event -> {
            String cityName = searchField.getText();
            
            if (!cityName.isEmpty()) {
                // When the search button is clicked, update the weather information for the entered city
                updateWeatherInfo(cityName);
            }
        });
        
        Button quitButton = getQuitButton(); // Quit button
        HBox buttonBox = new HBox(searchField, searchButton, quitButton);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.setSpacing(10);
        root.getChildren().addAll(buttonBox);

        weatherContainer = new VBox();
        root.getChildren().add(weatherContainer);

        Scene scene = new Scene(root, 500, 700);
        stage.setScene(scene);
        stage.setTitle("WeatherApp");
        stage.show();
    }

    private Button getQuitButton() {
        Button button = new Button("Quit");
        button.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;"); // Set button style
        button.setOnAction((event) -> {
            Platform.exit();
        });
        return button;
    }

    private void updateWeatherInfo(String cityName) {
        weatherContainer.getChildren().clear();
        try {
            JSONObject coordinatesObj = weatherAPI.lookUpLocation(cityName);
            System.out.println("Coordinates response for " + cityName + ": " + coordinatesObj);

            if (coordinatesObj != null) {
                double latitude = coordinatesObj.getDouble("lat");
                double longitude = coordinatesObj.getDouble("lon");

                String forecast = weatherAPI.getForecast(latitude, longitude);
                displayWeatherForecast(weatherContainer, forecast);

            } else {
                weatherContainer.getChildren().add(new Label("Unable to retrieve coordinates for the specified city."));
            }
        } catch (JSONException e) {
            weatherContainer.getChildren().add(new Label("Error fetching weather information."));
        }
    }

    private void displayWeatherForecast(VBox centerVBox, String forecastData) {
        try {
            JSONObject forecast = new JSONObject(forecastData);
            JSONArray forecastList = forecast.getJSONArray("list");
            
            // Today
            for (int i = 0; i < 5; i++) {
                JSONObject dayData = forecastList.getJSONObject(i * 8);
                long timestamp = dayData.getLong("dt");
                String dayOfWeek = getDayOfWeek(timestamp);
                double temperature = dayData.getJSONObject("main").getDouble("temp");
                double windSpeed = dayData.getJSONObject("wind").getDouble("speed");

                String weatherDescription = dayData.getJSONArray("weather")
                        .getJSONObject(0).getString("description");

                double rainVolume = 0.0;
                if (dayData.has("rain") && dayData.getJSONObject("rain").has("3h")) {
                    rainVolume = dayData.getJSONObject("rain").getDouble("3h");
                }

                String iconCode = parseWeatherIconCode(dayData.toString());
                ImageView weatherIcon = new ImageView(new Image(getWeatherIconUrl(iconCode)));
                weatherIcon.setFitWidth(50); // Adjust the width of the icon
                weatherIcon.setPreserveRatio(true);

                Label weatherLabel;
                if (i == 0) {
                    weatherLabel = new Label(
                            String.format("Today: \nTemperature: %.1f°C / %.1f°F, Wind: %.2f m/s, Rain: %.2f mm, %s",
                                    kelvinToCelsius(temperature), kelvinToFahrenheit(temperature),
                                    windSpeed, rainVolume, weatherDescription)
                    );
                } else {
                    // seuraavat päivät
                    weatherLabel = new Label(
                            String.format("%s:\nTemperature: %.1f°C / %.1f°F, Wind: %.2f m/s, Rain: %.2f mm, %s",
                                    dayOfWeek, kelvinToCelsius(temperature), kelvinToFahrenheit(temperature),
                                    windSpeed, rainVolume, weatherDescription)
                    );
                }
                HBox weatherInfoBox = new HBox(weatherIcon, weatherLabel);
                weatherInfoBox.setAlignment(Pos.CENTER);
                centerVBox.getChildren().add(weatherInfoBox);
            }
        } catch (JSONException e) {
            centerVBox.getChildren().add(new Label("Error parsing forecast data."));
        }
    }

    private double kelvinToCelsius(double temperatureKelvin) {
        return temperatureKelvin - 273.15;
    }

    private double kelvinToFahrenheit(double temperatureKelvin) {
        return (temperatureKelvin - 273.15) * 9 / 5 + 32;
    }

    private String getDayOfWeek(long timestamp) {
        java.util.Date date = new java.util.Date(timestamp * 1000);
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("EEEE", Locale.ENGLISH);
        return sdf.format(date);
    }

    private String parseWeatherIconCode(String json) {
        try {
            JSONObject weatherData = new JSONObject(json);
            JSONArray weatherArray = weatherData.getJSONArray("weather");
            JSONObject weather = weatherArray.getJSONObject(0);
            return weather.getString("icon");
        } catch (JSONException e) {
            return "01d";
        }
    }

    private String getWeatherIconUrl(String iconCode) {
        return "http://openweathermap.org/img/wn/" + iconCode + ".png";
    }
    
    public static void main(String[] args) {
        launch();
        
    }
}