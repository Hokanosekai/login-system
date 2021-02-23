package fr.hokanosekai.commands;import fr.hokanosekai.Main;import fr.hokanosekai.db.DbConnection;import org.bukkit.command.Command;import org.bukkit.command.CommandExecutor;import org.bukkit.command.CommandSender;import org.bukkit.entity.Player;import org.bukkit.potion.PotionEffectType;import java.sql.*;import java.util.UUID;public class Register implements CommandExecutor {    Main main;    public Register(Main main){        this.main = main;    }    @Override    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {        if (label.equalsIgnoreCase("register")){            if (sender instanceof Player){                Player player = (Player) sender;                UUID uuid = player.getUniqueId();                if (args.length == 2){                    if (args[0].equals(args[1])){                        final DbConnection loginConnection = main.getDbManager().getLoginConnection();                        try {                            final Connection connection = loginConnection.getConnection();                            final PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM players WHERE uuid = ?");                            preparedStatement.setString(1, uuid.toString());                            final ResultSet resultSet = preparedStatement.executeQuery();                            if (resultSet.next()){                                player.sendMessage("§cYou're already registered use /login");                                return true;                            } else {                                registerPlayer(connection, uuid, args[0]);                                player.sendMessage("§eBienvenue sur le serveur !");                                player.removePotionEffect(PotionEffectType.BLINDNESS);                                return true;                            }                        } catch (SQLException throwables) {                            throwables.printStackTrace();                        }                    } else {                        player.sendMessage("§cPlease type same passwords");                        return true;                    }                } else {                    player.sendMessage("§cUsage : /register <password> <password>");                    return true;                }            }            return true;        }        return false;    }    public void registerPlayer(Connection connection, UUID uuid, String password){        try {            final PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO players (uuid, passwd, create_at) VALUES (?,?,?)");            final long time = System.currentTimeMillis();            preparedStatement.setString(1, uuid.toString());            preparedStatement.setString(2, password);            preparedStatement.setTimestamp(3, new Timestamp(time));            preparedStatement.executeUpdate();            main.getLoginManager().getPlayerLogged().put(uuid, password);        } catch (SQLException throwables) {            throwables.printStackTrace();        }    }}