package ProjectCBW.MurderMystery.StructuredQuery;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {
    protected final String host = "localhost";
    protected final String port = "3306";
    protected final String database = "mining";
    protected final String username = "root";
    protected final String password = "";
    private Connection connection;

    // SQLに接続します。
    public void connect() throws SQLException {
        if (this.isConnected()) return;
        this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
    }

    // SQLに再接続します。
    public void reconnect() throws SQLException {
        if (this.isConnected()) { this.connection.close(); }
        this.connection = DriverManager.getConnection("jdbc:mysql://" + host + ":" + port + "/" + database + "?useSSL=false", username, password);
    }

    // SQLの接続を切断します。
    public void disconnect() throws SQLException {
        if (this.isConnected()) { this.connection.close(); }
    }

    // SQLに接続されているかどうかを判定して返します。
    public boolean isConnected() {
        return this.connection != null;
    }

    // 取得済みのSQLコネクションを返します。
    public Connection getConnection() {
        return this.connection;
    }
}
