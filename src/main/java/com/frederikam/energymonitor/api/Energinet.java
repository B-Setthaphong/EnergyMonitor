package com.frederikam.energymonitor.api;

import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.XML;

public class Energinet {

    private static String requestBody = null;

    public static EnergyData getData() {
        try {
            String xml = Unirest.post("http://www.energinet.dk/_layouts/FlashProxy.asmx").header("content-type", "text/xml").body(getRequestBody()).asString().getBody();

            JSONObject json = XML.toJSONObject(xml);

            JSONObject data = json.getJSONObject("soap:Envelope")
                    .getJSONObject("soap:Body")
                    .getJSONObject("GetSharePointListXMLResponse")
                    .getJSONObject("GetSharePointListXMLResult")
                    .getJSONObject("listitems")
                    .getJSONObject("data")
                    .getJSONObject("row");

            //System.out.println(data);
            return new EnergyData(
                    data.getInt("_x0043_O2"),
                    data.getInt("Vindmoeller"),
                    data.getInt("Solcelle_Produktion"),
                    data.getInt("Centrale_kraftvaerker"),
                    data.getInt("Decentrale_kraftvaerker"),
                    data.getInt("Elforbrug"));
        } catch (UnirestException ex) {
            throw new RuntimeException(ex);
        }
    }

    private static String getRequestBody() {
        if (requestBody != null) {
            return requestBody;
        }

        try {
            String str = "";

            InputStream is = new Energinet().getClass().getClassLoader().getResourceAsStream("requestBody.xml");
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                str = str + inputLine + "\n";
            }
            in.close();

            requestBody = str;

            return str;
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
    }

}
