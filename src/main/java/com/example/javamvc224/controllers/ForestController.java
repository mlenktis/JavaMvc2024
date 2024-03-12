package com.example.javamvc224.controllers;

import com.example.javamvc224.models.ForecastModel;
import com.example.javamvc224.models.ForecastTimestamp;
import com.example.javamvc224.models.Root;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

@Controller
public class ForestController {


    @GetMapping("/")
    public ModelAndView index() throws IOException {
        ModelAndView modelAndView = new ModelAndView("index");

        var text = getMeteoForecastJson();
        System.out.println(text);

        var forecasts = getForecastModels(text);
        modelAndView.addObject("forecasts", forecasts);

        return modelAndView;

    }


    public static String getMeteoForecastJson() throws IOException {
        URL url = new URL("https://api.meteo.lt/v1/places/vilnius/forecasts/long-term");

        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        String text = "";
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            text += scanner.nextLine();
        }
        scanner.close();
        return text;

    }

    private static ArrayList<ForecastModel> getForecastModels(String json) throws JsonProcessingException {
        var forecasts = new ArrayList<ForecastModel>();

        Root meteoObj = getObjectFromJson(json);

        for (var item : meteoObj.forecastTimestamps) {
            var row = new ForecastModel(item.forecastTimeUtc, item.airTemperature);
            forecasts.add(row);
        }

        return forecasts;
    }

    private static Root getObjectFromJson(String json) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, Root.class);
    }

}
