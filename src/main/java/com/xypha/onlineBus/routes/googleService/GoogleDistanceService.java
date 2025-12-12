package com.xypha.onlineBus.routes.googleService;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

@Service
public class GoogleDistanceService {

    @Value("${google.api.key}")
    private String apiKey;

    private static final String BASE_URL =
            "https://maps.googleapis.com/maps/api/distancematrix/json?units=metric";

    public double getDistanceKm(String origin, String destination) {
        try {
            // Normalize origin/destination
            origin = normalizeCity(origin);
            destination = normalizeCity(destination);

            String fullUrl = BASE_URL + "&origins=" + URLEncoder.encode(origin, "UTF-8") +
                    "&destinations=" + URLEncoder.encode(destination, "UTF-8") +
                    "&key=" + apiKey;

            HttpURLConnection conn = (HttpURLConnection) new URL(fullUrl).openConnection();
            conn.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(conn.getInputStream())
            );

            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }

            JSONObject json = new JSONObject(response.toString());

            // Check status
            if (!json.getString("status").equals("OK")) {
                throw new RuntimeException("Google API returned status: " + json.getString("status"));
            }

            JSONArray rows = json.getJSONArray("rows");
            if (rows.isEmpty()) {
                throw new RuntimeException("No route data returned from Google API.");
            }

            JSONObject element = rows.getJSONObject(0).getJSONArray("elements").getJSONObject(0);

            if (!element.getString("status").equals("OK")) {
                throw new RuntimeException("No route found: " + element.getString("status"));
            }

            int meters = element.getJSONObject("distance").getInt("value");
            return meters / 1000.0; // convert to km

        } catch (Exception e) {
            throw new RuntimeException("Error fetching distance from Google API: " + e.getMessage());
        }
    }

    private String normalizeCity(String city) {
        city = city.trim();
        // Add Myanmar if not already included
        if (!city.toLowerCase().contains("myanmar")) {
            city += ", Myanmar";
        }
        // Fix common typos (optional)
        city = city.replaceAll("Taung Gyi", "Taunggyi");
        return city;
    }
}
