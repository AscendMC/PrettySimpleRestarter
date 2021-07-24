package com.robomwm.prettysimplerestarter;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Created on 7/24/2021.
 *
 * @author RoboMWM
 */
public class PrettySimpleRestarter extends JavaPlugin
{
    @Override
    public void onEnable()
    {
        FileConfiguration config = getConfig();
        config.addDefault("restartKick", "The server is performing its regularly scheduled backup and restart." +
                " You can rejoin in a couple minutes.");
        config.addDefault("restartTime", "3:30");
        config.options().copyDefaults(true);
        saveConfig();

        new Restarter(config.getString("restartKick")).scheduleRestart(this, config.getString("restartTime"));
    }
}
