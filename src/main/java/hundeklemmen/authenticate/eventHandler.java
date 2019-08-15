package hundeklemmen.authenticate;


import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.*;

public class eventHandler implements Listener {

    private MainPlugin mainPlugin;

    public eventHandler(MainPlugin mainplugin){
        this.mainPlugin = mainplugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void OnPlayerPreLogin(PlayerJoinEvent event){
        utils.makeAsyncGetRequest("https://api.mojang.com/users/profiles/minecraft/" + event.getPlayer().getName(), new utils.RequestCallBack() {
            @Override
            public void callBack(boolean successful, String response, Exception exception, int responseCode) {
                mainPlugin.getServer().getScheduler().runTask(mainPlugin, new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (successful && (responseCode == 200 || responseCode == 204)) {
                            JSONObject object = new JSONObject(response);
                            if (object.has("error")) {
                                event.getPlayer().kickPlayer(mainPlugin.mojangError.replaceAll("%error%", object.getString("error")).replaceAll("%errorMessage%", object.getString("errorMessage")));
                                return;
                            }

                            if (object.has("id") && object.has("name")) {
                                if (event.getPlayer().getName().equals(object.getString("name"))) {
                                    //Name is correct
                                    if (!event.getPlayer().getUniqueId().toString().replaceAll("-", "").equals(object.getString("id"))) {
                                        event.getPlayer().kickPlayer(mainPlugin.mojangError.replaceAll("%error%", "wrong UUID").replaceAll("%errorMessage%", "UUID is not matching"));
                                        return;
                                    }
                                } else {
                                    event.getPlayer().kickPlayer(mainPlugin.mojangError.replaceAll("%error%", "wrong username").replaceAll("%errorMessage%", "Username is not matching"));
                                    return;
                                }
                            } else {
                                event.getPlayer().kickPlayer(mainPlugin.requestError);
                                return;
                            }
                        } else {
                            event.getPlayer().kickPlayer(mainPlugin.requestError);
                            return;
                        }
                    }

                });
            }
        }, mainPlugin);
    }

}
