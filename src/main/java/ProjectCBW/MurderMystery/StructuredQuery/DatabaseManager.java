package ProjectCBW.MurderMystery.StructuredQuery;

import ProjectCBW.MurderMystery.Main;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class DatabaseManager {

    private static final MySQL SQL = Main.getSQL();
    private static final Connection connection = SQL.getConnection();

    public static void createUser(Player player) {
        try {
            UUID uuid = player.getUniqueId();
            if (exists("users", new String[]{"uuid", uuid.toString()})) return;
            String name = player.getName();
            String sqlQuery = ("INSERT INTO users (name, uuid, data) VALUES(?,?,?)");
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, name);
            ps.setString(2, uuid.toString());
            ps.setString(3, UUID.randomUUID().toString());
            ps.executeUpdate();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    public static void createData(UUID uuid) {
        try {
            if (exists("data", new String[]{"uuid", uuid.toString()})) return;
            String sqlQuery = ("INSERT INTO data (uuid) VALUES(?)");
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, uuid.toString());
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public static void createWorld(String worldName) {
        try {
            if (exists("worlds", new String[]{"name", worldName})) return;
            String sqlQuery = ("INSERT INTO worlds (name, locations) VALUES(?,?)");
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, worldName);
            ps.setString(2, UUID.randomUUID().toString());
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    public static void addLocation(String worldName, int index, Location location) {
        try {
            List<String[]> primaryKeys = new ArrayList<>();
            String locationUUIDString = get("locations", "worlds", new String[]{"name", worldName}).toString();
            String[] primaryKey = new String[]{"uuid", locationUUIDString};
            primaryKeys.add(new String[]{"index", String.valueOf(index)});
            primaryKeys.add(primaryKey);
            String sqlQuery;
            PreparedStatement ps;
            if (exists("locations", primaryKeys)) {
                sqlQuery = ("UPDATE locations SET x=?, y=?, z=?, yaw=?, pitch=? WHERE (uuid=? AND `index`=?)");
                ps = connection.prepareStatement(sqlQuery);
                ps.setString(1, String.valueOf(location.getX()));
                ps.setString(2, String.valueOf(location.getY()));
                ps.setString(3, String.valueOf(location.getZ()));
                ps.setString(4, String.valueOf(location.getYaw()));
                ps.setString(5, String.valueOf(location.getPitch()));
                ps.setString(6, locationUUIDString);
                ps.setString(7, String.valueOf(index));
            } else {
                sqlQuery = ("INSERT INTO locations (uuid, `index`, x, y, z, yaw, pitch) VALUES(?,?,?,?,?,?,?)");
                ps = connection.prepareStatement(sqlQuery);
                ps.setString(1, locationUUIDString);
                ps.setString(2, String.valueOf(index));
                ps.setString(3, String.valueOf(location.getX()));
                ps.setString(4, String.valueOf(location.getY()));
                ps.setString(5, String.valueOf(location.getZ()));
                ps.setString(6, String.valueOf(location.getYaw()));
                ps.setString(7, String.valueOf(location.getPitch()));
            }
            ps.executeUpdate();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
    }

    public static List<String> getAll(String columnName, String tableName) {
        String sqlQuery = ("SELECT " + columnName + " FROM " + tableName);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            List<String> result = new ArrayList<>();
            while (rs.next()) {
                result.add(rs.getString(columnName));
            }
            return result;
        } catch (SQLException ignored) {
        }
        return null;
    }

    public static Object getRandomValue(String columnName, String tableName) {
        Random random = new Random();
        int valueCount = countTable(tableName);
        int r = random.nextInt(valueCount);
        String sqlQuery = String.format("SELECT `%s` FROM `%s` LIMIT %s,1", columnName, tableName, r);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getObject(columnName);
        } catch (SQLException ignored) { ignored.printStackTrace(); }
        return 0;
    }

    // データを昇順にソートしてから上位に位置する値をfor文の最大回転数個取得して返します。
    public static Map<Object, Object> getRanking(String columnName) {
        String sqlQuery = ("SELECT name, " + columnName + ", ROW_NUMBER() OVER(ORDER BY " + columnName + " DESC) FROM users ORDER BY users." + columnName + " DESC");
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            Map<Object, Object> results = new HashMap<>();
            for (int i = 0; i < 10; i++) {
                rs.next();
                UUID id = UUID.fromString(rs.getString("uuid"));
                Player player = Bukkit.getOfflinePlayer(id).getPlayer();
                results.put(i, new Object[]{player, rs.getObject(columnName)});
            }
            return results;
        } catch (SQLException ignored) {
        }
        return new HashMap<>();
    }

    // 引数により指定されたカラムのデータを取得して返します。
    public static Object get(String columnName, String tableName, String[] primaryKey) {
        String sqlQuery = ("SELECT " + columnName + " FROM " + tableName + " WHERE " + primaryKey[0] + "=?");
        Object result = 0;
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, primaryKey[1]);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return 0;
            result = rs.getObject(columnName);
        } catch (SQLException ignored) { }
        return result;
    }

    // List形式の引数によって指定された２つのプライマリキーを元に、指定されたカラムの位置からデータを取得して返します。
    public static Object get(String columnName, String tableName, List<String[]> primaryKeys) {
        String sqlQuery = String.format("SELECT `%s` FROM `%s` WHERE (`%s`=? AND `%s`=?)", columnName, tableName, primaryKeys.get(0)[0], primaryKeys.get(1)[0]);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, primaryKeys.get(0)[1]);
            ps.setString(2, primaryKeys.get(1)[1]);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return 0;
            return rs.getObject(columnName);
        } catch (SQLException ignored) { }
        return 0;
    }

    // テーブルに存在するデータの数を取得して返します。
    public static int countTable(String tableName) {
        String sqlQuery = ("SELECT COUNT(*) FROM " + tableName);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("COUNT(*)");
        } catch (SQLException ignored) { }
        return 0;
    }

    // テーブル内のデータからプライマリーキーにて指定された条件と一致するデータの数を取得して返します。
    public static int countTable(String tableName, String[] primaryKey) {
        String sqlQuery = String.format("SELECT COUNT(*) FROM `%s` WHERE `%s`=?", tableName, primaryKey[0]);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, primaryKey[1]);
            ResultSet rs = ps.executeQuery();
            rs.next();
            return rs.getInt("COUNT(*)");
        } catch (SQLException ignored) {
        }
        return 0;
    }

    // データが存在するかどうかを判定して結果を（ 存在すれば true / 存在しなければ false ）で返します。
    public static boolean exists(String tableName, String[] primaryKey) {
        try {
            String sqlQuery = ("SELECT * FROM " + tableName + " WHERE " + primaryKey[0] + "=?");
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, primaryKey[1]);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return false;
    }

    // データが存在するかどうかを判定して結果を（ 存在すれば true / 存在しなければ false ）で返します。
    public static boolean exists(String tableName, List<String[]> primaryKeys) {
        try {
            String sqlQuery = String.format("SELECT * FROM `%s` WHERE `%s`=? AND `%s`=?", tableName, primaryKeys.get(0)[0], primaryKeys.get(1)[0]);
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setString(1, primaryKeys.get(0)[1]);
            ps.setString(2, primaryKeys.get(1)[1]);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ignored) {
            ignored.printStackTrace();
        }
        return false;
    }

    // 指定されたプライマリーキーを元にテーブル内のデータを書き換えます。
    public static void set(String columnName, String tableName, Object newValue, String[] primaryKey) {
        try {
            String sqlQuery = ("UPDATE " + tableName + " SET " + columnName + "=? WHERE " + primaryKey[0] + "=?");
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.setObject(1, newValue);
            ps.setString(2, primaryKey[1]);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }

    // 指定されたプライマリーキーを元にテーブル内のデータを書き換えます。
    public static void set(String columnName, String tableName, Object newValue, List<String[]> primaryKeys) {
        try {
            String sqlQuery = String.format("UPDATE %s SET %s=%s WHERE %s=%s AND %s=%s", tableName, columnName, newValue, primaryKeys.get(0)[0], primaryKeys.get(0)[1], primaryKeys.get(1)[0], primaryKeys.get(1)[1]);
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.executeUpdate();
        } catch (SQLException ignored) {
        }
    }
}
