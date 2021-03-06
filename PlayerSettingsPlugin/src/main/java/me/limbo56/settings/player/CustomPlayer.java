package me.limbo56.settings.player;

import me.limbo56.settings.PlayerSettings;
import me.limbo56.settings.managers.ConfigurationManager;
import me.limbo56.settings.utils.Utilities;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.UUID;

/**
 * Created by lim_bo56
 * On 8/11/2016
 * At 11:50 AM
 */
public class CustomPlayer {

    private static HashMap<Player, CustomPlayer> playerList = new HashMap<>();
    public Boolean doubleJumpStatus;
    private Player player;
    private UUID uuid;
    private Boolean visibility;
    private Boolean stacker;
    private Boolean chat;
    private Boolean vanish;
    private Boolean fly;
    private Boolean speed;
    private Boolean jump;
    private Boolean radio;
    private Boolean doubleJump;

    public CustomPlayer(Player player) {
        this.player = player;
        this.uuid = player.getUniqueId();
        this.visibility = ConfigurationManager.getDefault().getBoolean("Default.Visibility");
        this.stacker = ConfigurationManager.getDefault().getBoolean("Default.Stacker");
        this.chat = ConfigurationManager.getDefault().getBoolean("Default.Chat");
        this.vanish = ConfigurationManager.getDefault().getBoolean("Default.Vanish");
        this.fly = ConfigurationManager.getDefault().getBoolean("Default.Fly");
        this.speed = ConfigurationManager.getDefault().getBoolean("Default.Speed");
        this.jump = ConfigurationManager.getDefault().getBoolean("Default.Jump");
        this.radio = (Utilities.hasRadioPlugin() && ConfigurationManager.getDefault().getBoolean("Default.Radio"));
        this.doubleJump = ConfigurationManager.getDefault().getBoolean("Default.DoubleJump");
        this.doubleJumpStatus = false;
        if (ConfigurationManager.getConfig().getBoolean("Debug")) {
            PlayerSettings.getInstance().log("CustomPlayer: UUID '" + uuid + "' created:");
            PlayerSettings.getInstance().log("CustomPlayer: Visibility: " + visibility + ", Stacker: " + stacker + ", Chat: " + chat + ", Vanish: " + vanish + ", Fly: " + fly + ", Speed: " + speed + ", Jump: " + jump + ", Radio: " + radio + ", DoubleJump: " + doubleJump);
        }
    }

    public static HashMap<Player, CustomPlayer> getPlayerList() {
        return playerList;
    }

    public Player getPlayer() {
        return player;
    }

    private UUID getUuid() {
        return uuid;
    }

    public boolean containsPlayer() {
        if (PlayerSettings.getInstance().getConfig().getBoolean("MySQL.enable")) {
            try {
                PreparedStatement sql = PlayerSettings.getMySqlConnection().getCurrentConnection().prepareStatement(
                        "SELECT UUID FROM `PlayerSettings` WHERE UUID = '" + getUuid().toString() + "'");
                ResultSet resultset = sql.executeQuery();
                boolean containsPlayerUUID = resultset.next();
                if (ConfigurationManager.getConfig().getBoolean("Debug"))
                    PlayerSettings.getInstance().log("containsPlayer: Checking UUID '" + getUuid().toString() + "', returning " + containsPlayerUUID);

                sql.close();
                resultset.close();

                return containsPlayerUUID;
            } catch (SQLException exception) {
                if (ConfigurationManager.getConfig().getBoolean("Debug"))
                    PlayerSettings.getInstance().log("containsPlayer: SQLException, returning false");
                exception.printStackTrace();
                return false;
            }
        } else {
            if (ConfigurationManager.getConfig().getBoolean("Debug"))
                PlayerSettings.getInstance().log("containsPlayer: MySQL is Disabled, returning false");
            return false;
        }
    }

    public void addPlayer() {
        if (PlayerSettings.getInstance().getConfig().getBoolean("MySQL.enable")) {

            final int
                    visibility = (this.visibility) ? 1 : 0,
                    stacker = (this.stacker) ? 1 : 0,
                    chat = (this.chat) ? 1 : 0,
                    vanish = (this.vanish) ? 1 : 0,
                    fly = (this.fly) ? 1 : 0,
                    speed = (this.speed) ? 1 : 0,
                    jump = (this.jump) ? 1 : 0,
                    radio = (this.radio) ? 1 : 0,
                    doubleJump = (this.doubleJump) ? 1 : 0;

            Bukkit.getScheduler().runTaskAsynchronously(PlayerSettings.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {
                        PreparedStatement sql = PlayerSettings.getMySqlConnection().getCurrentConnection().prepareStatement(
                                "INSERT INTO `PlayerSettings` (UUID, Visibility, Stacker, Chat, Vanish, Fly, Speed, Jump, Radio, DoubleJump) VALUES (" +
                                        "'" + getUuid().toString() + "', " +
                                        "'" + visibility + "', " +
                                        "'" + stacker + "', " +
                                        "'" + chat + "', " +
                                        "'" + vanish + "', " +
                                        "'" + fly + "', " +
                                        "'" + speed + "', " +
                                        "'" + jump + "', " +
                                        "'" + radio + "', " +
                                        "'" + doubleJump + "')");

                        sql.execute();
                        if (ConfigurationManager.getConfig().getBoolean("Debug")) {
                            PlayerSettings.getInstance().log("addPlayer: UUID '" + getUuid().toString() + "' added to the database:");
                            PlayerSettings.getInstance().log("addPlayer: Visibility: " + visibility + ", Stacker: " + stacker + ", Chat: " + chat + ", Vanish: " + vanish + ", Fly: " + fly + ", Speed: " + speed + ", Jump: " + jump + ", Radio: " + radio + ", DoubleJump: " + doubleJump);
                        }
                        sql.close();

                    } catch (SQLException exception) {
                        if (ConfigurationManager.getConfig().getBoolean("Debug"))
                            PlayerSettings.getInstance().log("addPlayer: SQLException, doing nothing");
                        exception.printStackTrace();
                    }
                }
            });
        } else if (ConfigurationManager.getConfig().getBoolean("Debug"))
            PlayerSettings.getInstance().log("addPlayer: MySQL is Disabled, doing nothing");
    }

    public void loadSettings() {
        if (PlayerSettings.getInstance().getConfig().getBoolean("MySQL.enable")) {
            if (PlayerSettings.getMySqlConnection().checkTable()) {

                this.visibility = getBoolean("Visibility");
                this.stacker = getBoolean("Stacker");
                this.chat = getBoolean("Chat");
                this.vanish = getBoolean("Vanish");
                this.fly = !getBoolean("DoubleJump") && getBoolean("Fly");
                this.speed = getBoolean("Speed");
                this.jump = getBoolean("Jump");
                this.radio = (Utilities.hasRadioPlugin() && getBoolean("Radio"));
                this.doubleJump = !getBoolean("Fly") && getBoolean("DoubleJump");

            } else if (ConfigurationManager.getConfig().getBoolean("Debug"))
                PlayerSettings.getInstance().log("loadSettings: checkTable returned false, table is not existent");
        } else if (ConfigurationManager.getConfig().getBoolean("Debug"))
            PlayerSettings.getInstance().log("loadSettings: MySQL is Disabled, doing nothing");
    }

    public void saveSettingsSync() {
        if (PlayerSettings.getInstance().getConfig().getBoolean("MySQL.enable")) {

            final int visibility = (hasVisibility()) ? 1 : 0,
                    stacker = (hasStacker()) ? 1 : 0,
                    chat = (hasChat()) ? 1 : 0,
                    vanish = (hasVanish()) ? 1 : 0,
                    fly = (hasFly()) ? 1 : 0,
                    speed = (hasSpeed()) ? 1 : 0,
                    jump = (hasJump()) ? 1 : 0,
                    radio = ((Utilities.hasRadioPlugin() && hasRadio()) ? 1 : 0),
                    doubleJump = (hasDoubleJump()) ? 1 : 0;

            try {

                Statement statement = PlayerSettings.getMySqlConnection().getCurrentConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                statement.addBatch("UPDATE `PlayerSettings` SET `Visibility` = " + visibility + " WHERE UUID = '" + getUuid().toString() + "'");
                statement.addBatch("UPDATE `PlayerSettings` SET `Stacker` = " + stacker + " WHERE UUID = '" + getUuid().toString() + "'");
                statement.addBatch("UPDATE `PlayerSettings` SET `Chat` = " + chat + " WHERE UUID = '" + getUuid().toString() + "'");
                statement.addBatch("UPDATE `PlayerSettings` SET `Vanish` = " + vanish + " WHERE UUID = '" + getUuid().toString() + "'");
                statement.addBatch("UPDATE `PlayerSettings` SET `Fly` = " + fly + " WHERE UUID = '" + getUuid().toString() + "'");
                statement.addBatch("UPDATE `PlayerSettings` SET `Speed` = " + speed + " WHERE UUID = '" + getUuid().toString() + "'");
                statement.addBatch("UPDATE `PlayerSettings` SET `Jump` = " + jump + " WHERE UUID = '" + getUuid().toString() + "'");
                statement.addBatch("UPDATE `PlayerSettings` SET `Radio` = " + radio + " WHERE UUID = '" + getUuid().toString() + "'");
                statement.addBatch("UPDATE `PlayerSettings` SET `DoubleJump` = " + doubleJump + " WHERE UUID = '" + getUuid().toString() + "'");

                statement.executeBatch();
                if (ConfigurationManager.getConfig().getBoolean("Debug")) {
                    PlayerSettings.getInstance().log("saveSettings: UUID '" + getUuid().toString() + "' settigns updated in the database:");
                    PlayerSettings.getInstance().log("saveSettings: Visibility: " + visibility + ", Stacker: " + stacker + ", Chat: " + chat + ", Vanish: " + vanish + ", Fly: " + fly + ", Speed: " + speed + ", Jump: " + jump + ", Radio: " + radio + ", DoubleJump: " + doubleJump);
                }
                statement.close();

            } catch (SQLException exception) {
                if (ConfigurationManager.getConfig().getBoolean("Debug"))
                    PlayerSettings.getInstance().log("saveSettings: SQLException, doing nothing");
                exception.printStackTrace();
            }
        } else if (ConfigurationManager.getConfig().getBoolean("Debug"))
            PlayerSettings.getInstance().log("saveSettings: MySQL is Disabled, doing nothing");
    }

    public void saveSettingsAsync() {
        if (PlayerSettings.getInstance().getConfig().getBoolean("MySQL.enable")) {
            Bukkit.getScheduler().runTaskAsynchronously(PlayerSettings.getInstance(), new Runnable() {
                @Override
                public void run() {
                    try {

                        Statement statement = PlayerSettings.getMySqlConnection().getCurrentConnection().createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

                        statement.addBatch("UPDATE `PlayerSettings` SET `Visibility` = " + visibility + " WHERE UUID = '" + getUuid().toString() + "'");
                        statement.addBatch("UPDATE `PlayerSettings` SET `Stacker` = " + stacker + " WHERE UUID = '" + getUuid().toString() + "'");
                        statement.addBatch("UPDATE `PlayerSettings` SET `Chat` = " + chat + " WHERE UUID = '" + getUuid().toString() + "'");
                        statement.addBatch("UPDATE `PlayerSettings` SET `Vanish` = " + vanish + " WHERE UUID = '" + getUuid().toString() + "'");
                        statement.addBatch("UPDATE `PlayerSettings` SET `Fly` = " + fly + " WHERE UUID = '" + getUuid().toString() + "'");
                        statement.addBatch("UPDATE `PlayerSettings` SET `Speed` = " + speed + " WHERE UUID = '" + getUuid().toString() + "'");
                        statement.addBatch("UPDATE `PlayerSettings` SET `Jump` = " + jump + " WHERE UUID = '" + getUuid().toString() + "'");
                        statement.addBatch("UPDATE `PlayerSettings` SET `Radio` = " + radio + " WHERE UUID = '" + getUuid().toString() + "'");
                        statement.addBatch("UPDATE `PlayerSettings` SET `DoubleJump` = " + doubleJump + " WHERE UUID = '" + getUuid().toString() + "'");

                        statement.executeBatch();
                        if (ConfigurationManager.getConfig().getBoolean("Debug")) {
                            PlayerSettings.getInstance().log("saveSettings: UUID '" + getUuid().toString() + "' settigns updated in the database:");
                            PlayerSettings.getInstance().log("saveSettings: Visibility: " + visibility + ", Stacker: " + stacker + ", Chat: " + chat + ", Vanish: " + vanish + ", Fly: " + fly + ", Speed: " + speed + ", Jump: " + jump + ", Radio: " + radio + ", DoubleJump: " + doubleJump);
                        }
                        statement.close();

                    } catch (SQLException exception) {
                        if (ConfigurationManager.getConfig().getBoolean("Debug"))
                            PlayerSettings.getInstance().log("saveSettings: SQLException, doing nothing");
                        exception.printStackTrace();
                    }

                }
            });
        }
    }

    private boolean getBoolean(String str) {
        try {
            ResultSet rs = PlayerSettings.getMySqlConnection().getCurrentConnection().createStatement().executeQuery(
                    "SELECT `" + str + "` FROM `PlayerSettings` WHERE `UUID` = '" + getUuid().toString() + "'");

            if (rs.next()) {
                if (rs.wasNull()) {
                    if (ConfigurationManager.getConfig().getBoolean("Debug"))
                        PlayerSettings.getInstance().log("getBoolean: Value '" + str + "' was null, getting default");
                    return ConfigurationManager.getDefault().getBoolean("Default." + str);
                } else {
                    if (ConfigurationManager.getConfig().getBoolean("Debug"))
                        PlayerSettings.getInstance().log("getBoolean: Value '" + str + "', returning " + rs.getBoolean(1));
                    return rs.getBoolean(1);
                }
            }

            rs.close();

        } catch (SQLException e) {
            if (ConfigurationManager.getConfig().getBoolean("Debug"))
                PlayerSettings.getInstance().log("getBoolean: SQLException, returning false");
            e.printStackTrace();
            return false;
        }
        if (ConfigurationManager.getConfig().getBoolean("Debug"))
            PlayerSettings.getInstance().log("getBoolean: Value '" + str + "' not found, returning false");
        return false;
    }

    public void setVisibility(final boolean visibility) {
        this.visibility = visibility;
    }

    public boolean hasVisibility() {
        return visibility;
    }

    public void setStacker(final boolean stacker) {
        this.stacker = stacker;
    }

    public boolean hasStacker() {
        return stacker;
    }

    public void setChat(final boolean chat) {
        this.chat = chat;
    }

    public boolean hasChat() {
        return chat;
    }

    public void setVanish(final boolean vanish) {
        this.vanish = vanish;
    }

    public boolean hasVanish() {
        return vanish;
    }

    public void setFly(final boolean fly) {
        this.fly = fly;
    }

    public boolean hasFly() {
        return fly;
    }

    public void setSpeed(final boolean speed) {
        this.speed = speed;
    }

    public boolean hasSpeed() {
        return speed;
    }

    public void setJump(final boolean jump) {
        this.jump = jump;
    }

    public boolean hasJump() {
        return jump;
    }

    public void setRadio(final boolean radio) {
        this.radio = radio;
    }

    public boolean hasRadio() {
        return radio;
    }

    public void setDoubleJump(final boolean doubleJump) {
        this.doubleJump = doubleJump;
    }

    public boolean hasDoubleJump() {
        return doubleJump;
    }
}