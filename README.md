
# Backup Plugin

This is a Java-based plugin designed to create backups of your Minecraft server. It uses the Bukkit API and is compatible with any server that supports this API, such as Spigot and PaperMC.

## Features

- Automatically creates backups at a specified interval.
- Allows manual creation of backups through a server command.
- Sends notifications to a Discord channel via a webhook when a backup is successfully created.

## Usage

First, add the plugin to your server's `plugins` directory and restart the server. The plugin will create a default configuration file.

You can adjust the backup interval in the configuration file:

```yaml
backup-interval: 24 # Interval in hours
```

The plugin will automatically create backups at the specified interval. The interval is in hours and the minimum value is 1.

To manually create a backup, use the `/backup` command in your server console or in-game:

```bash
/backup
```

To enable Discord notifications, you need to set `backup-webhook` to `true` and provide a valid webhook URL in `backup-webhook-url` in the configuration file:

```yaml
backup-webhook: true
backup-webhook-url: "your-webhook-url"
```

## Requirements

- Java 8 or higher
- A Minecraft server that supports the Bukkit API

## Disclaimer

This plugin is not affiliated with or endorsed by Mojang or Microsoft. Use it responsibly and ensure you have sufficient storage for your backups.


Please replace `24` in `backup-interval: 24` with your desired backup interval in hours.
Insert your webhook url to `backup-webhook-url: ""` with the actual webhook URL of your Discord channel.