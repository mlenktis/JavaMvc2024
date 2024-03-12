package com.example.javamvc224.controllers;

import com.example.javamvc224.models.ForecastModel;
import com.example.javamvc224.models.ForecastTimestamp;
import com.example.javamvc224.models.Root;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
    public ModelAndView index(@RequestParam(required = false)String city) throws IOException {
        ModelAndView modelAndView = new ModelAndView("index");

        var forecasts = getForecasts(city);

        modelAndView.addObject("forecasts", forecasts);

        return modelAndView;

    }


    public static String getMeteoForecastJson(String city) throws IOException {
        URL url = new URL("https://api.meteo.lt/v1/places/"+ city +"/forecasts/long-term");

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

    private static ArrayList<ForecastModel> getForecasts(String city) throws IOException {
        var forecasts = new ArrayList<ForecastModel>();

        if(city!=null){
            var metoForecastsJson = getMeteoForecastJson(city);
            Root meteObj = getObjectFromJson(metoForecastsJson);
            for(var item : meteObj.forecastTimestamps){
                var row = new ForecastModel(item.forecastTimeUtc, item.airTemperature);
                forecasts.add(row);
            }
        }

        return forecasts;
    }

    private static Root getObjectFromJson(String json) throws JsonProcessingException {
        ObjectMapper om = new ObjectMapper();
        return om.readValue(json, Root.class);
    }

}
