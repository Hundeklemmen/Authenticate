package hundeklemmen.authenticate;

import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class utils {

    public static void makeAsyncGetRequest(String rawurl, RequestCallBack asyncCallBack, Plugin plugin) {
        if (plugin == null) {
            return;
        }
        try {
            URL url = new URL(rawurl);
            new BukkitRunnable() {
                @Override
                public void run() {
                    StringBuilder response = new StringBuilder();
                    try {
                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                        connection.connect();
                        //noinspection Duplicates
                        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                            String line = reader.readLine();
                            while (line != null) {
                                response.append(line);
                                line = reader.readLine();
                            }
                            asyncCallBack.callBack(true, response.toString(), null, connection.getResponseCode());
                        }
                    } catch (Exception e) {
                        asyncCallBack.callBack(false, response.toString(), e, -1);
                    }
                }
            }.runTaskAsynchronously(plugin);
        } catch (MalformedURLException e) {
            e.printStackTrace();
            asyncCallBack.callBack(false, null, e, -1);
        }
    };


    @FunctionalInterface
    public interface RequestCallBack {
        void callBack(boolean successful, String response, Exception exception, int responseCode);
    }
}
