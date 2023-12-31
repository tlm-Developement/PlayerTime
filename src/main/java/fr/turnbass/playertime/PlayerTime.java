package fr.turnbass.playertime;

import fr.turnbass.playertime.Command.PlayedTimeCmd;
import fr.turnbass.playertime.Placeholder.TimeHolder;
import fr.turnbass.playertime.db.Database;
import fr.turnbass.playertime.listeners.CmdUtils;
import fr.turnbass.playertime.listeners.Listeners;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

import static org.bukkit.Bukkit.*;

public final class PlayerTime extends JavaPlugin implements Listener {
    private static PlayerTime instance;
    private Database database;
    private FileConfiguration config;
    private File configFile;

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        // Spécifier l'emplacement du fichier de configuration
        configFile = new File(getDataFolder(), "config.yml");

        // Vérifier si le fichier de configuration n'existe pas, le créer avec des valeurs par défaut
        if (!configFile.exists()) {
            getLogger().info("Le fichier de configuration n'existe pas. Création avec des valeurs par défaut.");
            saveDefaultConfig();
        }

        // Charger la configuration
        config = YamlConfiguration.loadConfiguration(configFile);
        this.database = new Database(this);
        try {
            this.database.initializeDatabase();
        } catch (
                SQLException e) {
            e.printStackTrace();
            System.out.println("Could not initialize database.");


        }
        if (getPluginManager().getPlugin("PlaceholderAPI") != null) {
            getPluginManager().registerEvents(this, this);
            new TimeHolder(database).register();
        } else {
            getPluginManager().disablePlugin(this);
        }
        instance = this;
        getServer().getPluginManager().registerEvents(new Listeners(database), this);
        getServer().getPluginManager().registerEvents(new CmdUtils(database, this), this);
        getCommand("playedtime").setExecutor(new PlayedTimeCmd(new CmdUtils(database,this)));
        getCommand("playedtime").setTabCompleter(new PlayedTimeCmd(new CmdUtils(database,this)));



    }
    public static Plugin getInstance() {
        return instance;
    }
    // Cette méthode peut être utilisée pour accéder à la configuration
    public FileConfiguration getConfig() {
        return config;
    }


    @Override
    public void onDisable() {
        // Sauvegarder la configuration
        try {
            config.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

