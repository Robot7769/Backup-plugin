package robot7769.backup;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class BackupCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public BackupCommand(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (strings.length == 0) {
            commandSender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + plugin.getConfig().getString("backup-start-notify-message"));
            new Backup(plugin).createBackup();
            return true;
        }
        return false;
    }
}
