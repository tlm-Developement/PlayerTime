package fr.turnbass.playertime.db;

import fr.turnbass.playertime.PlayerTime;
import fr.turnbass.playertime.model.PlayerStats;

import java.sql.*;

public class Database {
    private PlayerTime plugin;
    public Database(PlayerTime plugin) {
        this.plugin = plugin;
    }
    private Connection connection;

    public Connection getConnection() throws SQLException {

        if(connection != null){
            return connection;
        }
        String user = plugin.getConfig().getString("mysql.username");
        String password = plugin.getConfig().getString("mysql.password");
        String url = "jdbc:mysql://" + plugin.getConfig().getString("mysql.hostname") + ":" +
                plugin.getConfig().getInt("mysql.port") + "/" + plugin.getConfig().getString("mysql.database_name") +
                "?autoReconnect=true";
        //Try to connect to my MySQL database running locally
        Connection connection = DriverManager.getConnection(url, user, password);

        this.connection = connection;

        System.out.println("Connected to database.");

        return connection;
    }

    public void initializeDatabase() throws SQLException {

        Statement statement = getConnection().createStatement();
        String name = plugin.getConfig().getString("info.servername");

        //Create the player_stats table
        String sql = "CREATE TABLE IF NOT EXISTS " + name + " (uuid varchar(36) primary key, playerTime long)";
        String sql2 = "CREATE TABLE IF NOT EXISTS  PlayerTime (uuid varchar(36) primary key, playerTime long)";
        statement.execute(sql);
        statement.execute(sql2);
        statement.close();

    }

    public PlayerStats findPlayerStatsByUUID(String uuid) throws SQLException {

        String name = plugin.getConfig().getString("info.servername");
        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM `" + name + "` WHERE uuid = ?");
        statement.setString(1, uuid);
        ResultSet resultSet = statement.executeQuery();

        PlayerStats playerStats;

        if(resultSet.next()){

            playerStats = new PlayerStats(resultSet.getString("uuid"), (int) resultSet.getLong("playerTime"));

            statement.close();

            return playerStats;
        }

        statement.close();

        return null;
    }
    public void createPlayerStats(PlayerStats playerStats) throws SQLException {
        String name = plugin.getConfig().getString("info.servername");
        String query = "INSERT INTO `" + name + "` (uuid, playerTime) VALUES("
                + "'" + playerStats.getPlayerUUID() + "', "
                + "'" + playerStats.getPlayerTime() + "')";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.executeUpdate();
        statement.close();
    }

    public void updatePlayerStats(PlayerStats playerStats) throws SQLException {
        String name = plugin.getConfig().getString("info.servername");
        String query = "UPDATE `" + name + "` SET playerTime = ? WHERE uuid = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setLong(1, playerStats.getPlayerTime());
        statement.setString(2, playerStats.getPlayerUUID());
        statement.executeUpdate();
        statement.close();
    }

    public void deletePlayerStats(PlayerStats playerStats) throws SQLException {
        String name = plugin.getConfig().getString("info.servername");
        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM `"+name+"` WHERE uuid = ?");
        statement.setString(1, playerStats.getPlayerUUID());

        statement.executeUpdate();

        statement.close();

    }

    // Global Time Player
    public PlayerStats findPlayerTimeByUUID(String uuid) throws SQLException {

        PreparedStatement statement = getConnection().prepareStatement("SELECT * FROM `PlayerTime` WHERE uuid = ?");
        statement.setString(1, uuid);
        ResultSet resultSet = statement.executeQuery();

        PlayerStats playerStats;

        if(resultSet.next()){

            playerStats = new PlayerStats(resultSet.getString("uuid"), (int) resultSet.getLong("playerTime"));

            statement.close();

            return playerStats;
        }

        statement.close();

        return null;
    }
    public void createPlayerTime(PlayerStats playerStats) throws SQLException {
        String query = "INSERT INTO `PlayerTime` (uuid, playerTime) VALUES("
                + "'" + playerStats.getPlayerUUID() + "', "
                + "'" + playerStats.getPlayerTime() + "')";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.executeUpdate();
        statement.close();
    }

    public void updatePlayerTime(PlayerStats playerStats) throws SQLException {
        String query = "UPDATE `PlayerTime` SET playerTime = ? WHERE uuid = ?";
        PreparedStatement statement = getConnection().prepareStatement(query);
        statement.setLong(1, playerStats.getPlayerTime());
        statement.setString(2, playerStats.getPlayerUUID());
        statement.executeUpdate();
        statement.close();
    }
    public void deletePlayerTime(PlayerStats playerStats) throws SQLException {
        PreparedStatement statement = getConnection().prepareStatement("DELETE FROM `PlayerTime` WHERE uuid = ?");
        statement.setString(1, playerStats.getPlayerUUID());

        statement.executeUpdate();

        statement.close();

    }


}

