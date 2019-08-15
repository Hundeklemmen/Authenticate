package hundeklemmen.authenticate;

import org.bstats.bukkit.Metrics;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;


public class MainPlugin extends JavaPlugin {

    private static MainPlugin instance;

    public String requestError = ChatColor.translateAlternateColorCodes('&', "&cUnable to authenticate - no data provided from the Mojang API");
    public String mojangError = ChatColor.translateAlternateColorCodes('&', "&cUnable to authenticate - %error%");
    public String invalidData = ChatColor.translateAlternateColorCodes('&', "&cUnable to authenticate - Username and Uuid is not matching");

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();

        if(this.getConfig().contains("requestError")){
            this.requestError = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("requestError"));
        }
        if(this.getConfig().contains("mojangError")){
            this.mojangError = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("mojangError"));
        }
        if(this.getConfig().contains("invalidData")){
            this.invalidData = ChatColor.translateAlternateColorCodes('&', this.getConfig().getString("invalidData"));
        }

        Metrics metrics = new Metrics(this);

        instance.getLogger().info("Authenticate have been enabled");
        instance.getServer().getPluginManager().registerEvents(new eventHandler(instance), this);

    }

    @Override
    public void onDisable(){
        instance.getLogger().info("Authenticate have been disabled");
    }

    public MainPlugin getInstance(){
        return this.instance;
    }

}