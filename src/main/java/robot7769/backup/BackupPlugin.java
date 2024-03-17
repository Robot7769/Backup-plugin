package robot7769.backup;

import org.bukkit.plugin.java.JavaPlugin;

public final class BackupPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        saveDefaultConfig();
        // Plugin startup logic

        long interval = getConfig().getLong("backup-interval");
        if (interval > 1) {
            interval = interval * 60 * 60 * 20; // Convert hours to ticks
            getServer().getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {
                @Override
                public void run() {
                    //create backup
                    new Backup(BackupPlugin.this).createBackup();
                }
            }, interval, interval);
        }
        getCommand("backup").setExecutor(new BackupCommand(this));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
