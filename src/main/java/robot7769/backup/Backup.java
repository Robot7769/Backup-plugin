package robot7769.backup;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class Backup {

    private final List<String> foldersToBackup;

    private final JavaPlugin plugin;
    public Backup(JavaPlugin plugin){
        this.plugin = plugin;
        this.foldersToBackup = plugin.getConfig().getStringList("backup-worlds");
    }
    public void createBackup() {
        if (plugin.getConfig().getBoolean("backup-start-notify")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + plugin.getConfig().getString("backup-start-notify-message") );
        }

        File serverFile = new File(plugin.getDataFolder() + "/../../");
        new BukkitRunnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
                Date date = Date.from(Instant.now());
                folderToZip(serverFile, sdf.format(date) + ".zip");
            }
        }.runTaskAsynchronously(plugin);
    }

    private File folderToZip(File folder, String name) {
        String backupFolderName = plugin.getConfig().getString("backup-folder");
        if(backupFolderName == null){
            backupFolderName = "backup";
        }
        try {
            File backupsFolder = new File(plugin.getDataFolder(), backupFolderName);
            backupsFolder.mkdirs();

            purgeBackups(backupsFolder);

            File zipFile = new File(backupsFolder, name);
            zipFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);

            addFolderToZip("", folder, zos);

            zos.close();
            fos.close();

            if (plugin.getConfig().getBoolean("backup-finish-notify")) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "" + ChatColor.BOLD + plugin.getConfig().getString("backup-finish-notify-message") );
            }
            if (plugin.getConfig().getBoolean("backup-webhook")) {
                DiscordWebhook backUpFile = new DiscordWebhook(plugin.getConfig().getString("backup-webhook-url"));
                backUpFile.addEmbed(new DiscordWebhook.EmbedObject()
                        .setTitle("Záloha")
                        .setDescription("Záloha byla úspěšně vytvořena. " + zipFile.getName()));
                backUpFile.execute();
                backUpFile.sendFile(zipFile.getPath());
            }
            return zipFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public void purgeBackups(File logDir){
        int maxAmountOfBackups = plugin.getConfig().getInt("max-amount-of-backups");
        if(maxAmountOfBackups == -1){
            return;
        }
        File[] logFiles = logDir.listFiles();
        long oldestDate = Long.MAX_VALUE;
        File oldestFile = null;
        if( logFiles != null && logFiles.length >= maxAmountOfBackups){
            for(File f: logFiles){
                if(f.lastModified() < oldestDate){
                    oldestDate = f.lastModified();
                    oldestFile = f;
                }
            }

            if(oldestFile != null){
                deleteFolder(oldestFile);
            }
        }
    }

    private void addFolderToZip(String parentPath, File folder, ZipOutputStream zos) throws IOException {
        if(folder.getName().equals(plugin.getConfig().getString("backup-folder"))) {
            return;
        }

        for (File file : folder.listFiles()) {
            if (file.isDirectory()) {
                String path = parentPath + file.getName() + "/";
                if (checkIfFileToBackup(path)) {
                    continue;
                }
                ZipEntry zipEntry = new ZipEntry(path);
                zos.putNextEntry(zipEntry);
                addFolderToZip(path, file, zos);
                zos.closeEntry();
            } else {
                if (checkIfFileToBackup(parentPath + file.getName())) {
                    continue;
                }
                try {
                    ZipEntry zipEntry = new ZipEntry(parentPath + file.getName());
                    zos.putNextEntry(zipEntry);

                    FileInputStream fis = new FileInputStream(file);
                    byte[] buffer = new byte[1024];
                    int length;
                    while ((length = fis.read(buffer)) > 0) {
                        zos.write(buffer, 0, length);
                    }

                    fis.close();
                    zos.closeEntry();
                } catch (Exception e) {
                    Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "" + ChatColor.BOLD + "Skipped " + file.getName() + " due to OS protection");
                }
            }
        }
    }

    private void deleteFolder(File folder) {
        if(folder == null) {
            return;
        }
        if (folder.isDirectory()) {
            // recursively delete all files and subfolders
            for (File file : folder.listFiles()) {
                deleteFolder(file);
            }
        }

        // delete the folder itself
        folder.delete();
    }

    private boolean checkIfFileToBackup(String path) {
        for (String folderName : foldersToBackup) {
            if (path.contains(folderName)) {
                return false;
            }
        }
        return true;
    }
}

