package com.robomwm.prettysimplerestarter;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created on 1/12/2019.
 *
 * @author RoboMWM
 */
public class Restarter
{
    private BukkitTask restartTask;
    private String kickMessage;

    public Restarter(String kickMessage)
    {
        this.kickMessage = kickMessage;
    }

    public boolean cancelRestart()
    {
        if (restartTask != null)
        {
            restartTask.cancel();
            return true;
        }
        return false;
    }

    /**
     * Schedule a restart to occur at the specified clock time.
     * @param plugin
     * @param time syntax is HH:mm, and will always be in the future.
     */
    public void scheduleRestart(Plugin plugin, String time)
    {
        try
        {
            //Java's time/calendar/whatever API is stoopid
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(new SimpleDateFormat("HH:mm").parse(time));
            Calendar currentCalendar = Calendar.getInstance();
            currentCalendar.set(Calendar.HOUR_OF_DAY, calendar.get(Calendar.HOUR_OF_DAY));
            currentCalendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE));
            currentCalendar.set(Calendar.SECOND, 0);

            long restartTime = currentCalendar.getTimeInMillis();

            //If it returns a time we already passed, advance it to the next day
            if (System.currentTimeMillis() > restartTime)
                restartTime += 86400000L; //86400000ms = 1 day

            long ticksToScheduleRestart = (restartTime - System.currentTimeMillis()) / 50;

            scheduleRestart(plugin, ticksToScheduleRestart);
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
    }

    private void scheduleRestart(Plugin plugin, long ticks)
    {
        if (restartTask != null)
            restartTask.cancel();

        plugin.getLogger().info("Scheduling a restart to occur in " + ticks + " ticks. (" +
                ticks / 20 / 60 + " minutes)");

        restartTask = new BukkitRunnable()
        {
            @Override
            public void run()
            {
                if (plugin.getServer().getOnlinePlayers().size() > 0)
                {
                    plugin.getLogger().warning("Was going to restart, but players were on the server.");
                    plugin.getServer().broadcastMessage(ChatColor.LIGHT_PURPLE +
                            "Regularly scheduled server backup and restart will occur in 2 minutes.");
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            for (Player player : plugin.getServer().getOnlinePlayers())
                                player.kickPlayer(kickMessage);
                            plugin.getServer().spigot().restart();
                        }
                    }.runTaskLater(plugin, 2400L);
                }
                else
                    plugin.getServer().spigot().restart();
            }
        }.runTaskLater(plugin, ticks);
    }
}
