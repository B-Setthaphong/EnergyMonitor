package com.frederikam.energymonitor;

import com.frederikam.energymonitor.api.Energinet;
import com.frederikam.energymonitor.api.EnergyData;
import com.frederikam.energymonitor.io.Line;
import com.frederikam.energymonitor.io.LineSender;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Scanner;
import org.json.JSONObject;

/**
 * @author Frederikam
 */
public class EnergyMonitor {

    public static String osuApiKey = "";
    public static String carbonPath = "carbon.energy";
    public static String carbonHost = "";
    public static int carbonPort = 2003;
    public static final EnergyMonitor INSTANCE = new EnergyMonitor();
    public static final long INTERVAL = 15 * 60000L;//15 minutes

    public static void main(String[] args) throws InterruptedException, FileNotFoundException {
        //Load configuration
        InputStream is = new FileInputStream(new File("./config.json"));
        Scanner scanner = new Scanner(is);
        JSONObject config = new JSONObject(scanner.useDelimiter("\\A").next());
        scanner.close();

        carbonHost = config.getString("carbonHost");
        carbonPort = config.optInt("carbonPort", carbonPort);
        
        //This is a timer that triggers every <INTERVAL> miliseconds
        while (true) {
            long currentTerm = System.currentTimeMillis() / INTERVAL;
            long nextTermStart = (currentTerm + 1) * INTERVAL;
            long diff = nextTermStart - System.currentTimeMillis();

            //Wait for the remaining time
            synchronized (INSTANCE) {
                INSTANCE.wait(diff);
            }
            uploadData();
        }
    }

    private static void uploadData() {
        try {
            ArrayList<Line> lines = new ArrayList<>();
            EnergyData data = Energinet.getData();
            
            lines.add(new Line(carbonPath + ".fossilEnergy", data.getFossilEnergy()));
            lines.add(new Line(carbonPath + ".netto", data.getNetto()));
            lines.add(new Line(carbonPath + ".pollution", data.getPollution()));
            lines.add(new Line(carbonPath + ".production", data.getProduction()));
            lines.add(new Line(carbonPath + ".solar", data.getSolarEnergy()));
            lines.add(new Line(carbonPath + ".usage", data.getUsage()));
            lines.add(new Line(carbonPath + ".wind", data.getWindEnergy()));
            
            LineSender.submitData(lines);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
