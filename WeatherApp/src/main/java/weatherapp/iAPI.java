package weatherapp;

import com.mashape.unirest.http.JsonNode;
import com.mashape.unirest.http.Unirest;
import org.json.JSONObject;
import org.json.JSONException;


public interface iAPI {
    String API_KEY = "cebdc24ede8381c4d4cf209a8775d769";
    String API_ENDPOINT = "http://api.openweathermap.org/data/2.5";

    default JSONObject lookUpLocation(String loc) {
        try {
            String url = API_KEY + "?q=" + loc + "&appid=" + API_KEY;
            System.out.println("API Request URL: " + url);
            JsonNode response = Unirest.get(API_ENDPOINT + "/weather")
                    .queryString("q", loc)
                    .queryString("appid", API_KEY)
                    .asJson()
                    .getBody();

            JSONObject weatherData = response.getObject();
            JSONObject coord = weatherData.getJSONObject("coord");

            double lat = coord.getDouble("lat");
            double lon = coord.getDouble("lon");

            return new JSONObject().put("lat", lat).put("lon", lon);
        } catch (JSONException e) {
            System.err.println("JSONException: There was an error parsing the API response.");
            return null;
        } catch (Exception e) {
            System.err.println("An unexpected error occurred: " + e.getMessage());
            return null;
        }
    }

    default String getCurrentWeather(double lat, double lon) {
        try {
            JsonNode response = Unirest.get(API_ENDPOINT + "/weather")
                    .queryString("lat", lat)
                    .queryString("lon", lon)
                    .queryString("appid", API_KEY)
                    .asJson()
                    .getBody();

            return response.toString();
        } catch (Exception e) {
            return "Error";
        }
    }

    default String getForecast(double lat, double lon) {
        try {
            JsonNode response = Unirest.get(API_ENDPOINT + "/forecast")
                    .queryString("lat", lat)
                    .queryString("lon", lon)
                    .queryString("appid", API_KEY)
                    .asJson()
                    .getBody();

            return response.toString();
        } catch (Exception e) {
            return "Error";
        }
    }
}
