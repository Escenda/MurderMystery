package ProjectCBW.MurderMystery.StructuredQuery;

import ProjectCBW.MurderMystery.Main;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class DatabaseManager {

    private static final MySQL SQL = Main.getSQL();
    private static final Connection connection = SQL.getConnection();

    public static Object getRandomValue(String columnName, String tableName) {
        String sqlQuery = String.format("SELECT %s FROM %s", columnName, tableName);
        ResultSet rs;
        Random random = new Random();
        int valueCount = (int) countTable(tableName);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            rs = ps.executeQuery();
            for (int i = 0; i != random.nextInt(valueCount); i++) { rs.next(); }
            return rs.getObject(columnName);
        } catch (SQLException ignored) { }
        return 0;
    }

    // データを昇順にソートしてから上位に位置する値をfor文の最大回転数個取得して返します。
    public static Map<Object, Object> getRanking(String columnName) throws SQLException {
        String sqlQuery = String.format("SELECT NAME, %s, ROW_NUMBER() OVER(ORDER BY %s DESC) FROM USERS ORDER BY 'USERS'.'%s' DESC", columnName, columnName, columnName);
        PreparedStatement ps = connection.prepareStatement(sqlQuery);
        ResultSet rs = ps.executeQuery();
        Map<Object, Object> results = new HashMap<>();
        for (int i = 0; i < 10; i++) {
            rs.next();
            results.put(rs.getString("NAME"), rs.getObject(columnName));
        }
        return results;
    }

    // 引数により指定されたカラムのデータを取得して返します。
    public static Object getData(String columnName, String tableName, String[] primaryKey) {
        String sqlQuery = String.format("SELECT %s FROM %s WHERE %s=%s", columnName, tableName, primaryKey[0], primaryKey[1]);
        Object result = 0;
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return 0;
            result = rs.getObject(columnName);
        } catch (SQLException ignored) { }
        return result;
    }

    // List形式の引数によって指定された２つのプライマリキーを元に、指定されたカラムの位置からデータを取得して返します。
    public static Object getData(String columnName, String tableName, List<String[]> primaryKeys) {
        String sqlQuery = String.format("SELECT %s FROM %s WHERE %s=%s AND %s=%s", columnName, tableName, primaryKeys.get(0)[0], primaryKeys.get(0)[1], primaryKeys.get(1)[0], primaryKeys.get(1)[1]);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            if (!rs.next()) return 0;
            return rs.getObject(columnName);
        } catch (SQLException ignored) { }
        return 0;
    }

    // テーブルに存在するデータの数を取得して返します。
    public static long countTable(String tableName) {
        String sqlQuery = String.format("SELECT COUNT(*) FROM %s", tableName);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            return rs.getLong("count(*)");
        } catch (SQLException ignored) { }
        return 0;
    }

    // テーブル内のデータからプライマリーキーにて指定された条件と一致するデータの数を取得して返します。
    public static long countTable(String tableName, String[] primaryKey) {
        String sqlQuery = String.format("SELECT COUNT(*) FROM %s WHERE %s=%s", tableName, primaryKey[0], primaryKey[1]);
        try {
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            return rs.getLong("count(*)");
        } catch (SQLException ignored) { }
        return 0;
    }

    // データが存在するかどうかを判定して結果を（ 存在すれば true / 存在しなければ false ）で返します。
    public static boolean exists(String tableName, String[] primaryKey) {
        try {
            String sqlQuery = String.format("SELECT * FROM %s WHERE %s=%s", tableName, primaryKey[0], primaryKey[1]);
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException ignored) { }
        return false;
    }

    // 指定されたプライマリーキーを元にテーブル内のデータを書き換えます。
    public static void set(String columnName, String tableName, Object newValue, String[] primaryKey) {
        try {
            String sqlQuery = String.format("UPDATE %s SET %s=%s WHERE %s=%s", tableName, columnName, newValue, primaryKey[0], primaryKey[1]);
            PreparedStatement ps = connection.prepareStatement(sqlQuery);
            ps.executeUpdate();
        } catch (SQLException ignored) { }
    }

}
