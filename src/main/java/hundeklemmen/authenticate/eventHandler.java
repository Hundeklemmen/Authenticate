package hundeklemmen.authenticate;


import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.json.JSONObject;

import static org.bukkit.event.player.AsyncPlayerPreLoginEvent.*;

public class eventHandler implements Listener {

    private MainPlugin mainPlugin;

    public eventHandler(MainPlugin mainplugin){
        this.mainPlugin = mainplugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void OnPlayerPreLogin(AsyncPlayerPreLoginEvent event){
        utils.makeAsyncGetRequest("https://api.mojang.com/users/profiles/minecraft/" + event.getName(), new utils.RequestCallBack() {
            @Override
            public void callBack(boolean successful, String response, Exception exception, int responseCode) {
                if (successful && (responseCode == 200 || responseCode == 204)) {
                    JSONObject object = new JSONObject(response);
                    if(object.has("error")){
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, mainPlugin.mojangError.replaceAll("%error%", object.getString("error")).replaceAll("%errorMessage%", object.getString("errorMessage")));

                        mainPlugin.getLogger().warning("Couldn't authenticate " + event.getName());
                        return;
                    }
                    if(object.has("id")&&object.has("name")){
                        if(event.getName().equals(object.getString("name"))){
                            //Name is correct
                            if(event.getUniqueId().toString().replaceAll("-", "").equals(object.getString("id"))) {
                                event.allow();
                            } else {
                                event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, mainPlugin.mojangError.replaceAll("%error%", "wrong UUID").replaceAll("%errorMessage%", "UUID is not matching"));
                                return;
                            }
                        } else {
                            event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, mainPlugin.mojangError.replaceAll("%error%", "wrong username").replaceAll("%errorMessage%", "Username is not matching"));
                            return;
                        }
                    } else {
                        event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, mainPlugin.requestError);
                        return;
                    }

                } else {
                    event.disallow(AsyncPlayerPreLoginEvent.Result.KICK_OTHER, mainPlugin.requestError);
                    return;
                }
            }
        }, mainPlugin);
    }

}
