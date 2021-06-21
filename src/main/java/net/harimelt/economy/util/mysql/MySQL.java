/*
 * Copyright (C) 2021 SirOswaldo
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package net.harimelt.economy.util.mysql;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MySQL {

    private Connection connection;
    private final String ip;
    private final String port;
    private final String username;
    private final String password;
    private final String database;
    private final String table;
    private final String[][] values;

    public MySQL(String ip, String port, String username, String password, String database, String table, String[][] values) {
        this.ip = ip;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.table = table;
        this.values = values;
    }

    public boolean startConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection("jdbc:mysql://" + ip + ":" + port + "/" + database, username, password);
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
    public void closeConnection() {
        try {
            if (connection != null) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createTable() {
        PreparedStatement preparedStatement;
        try {
            StringBuilder execution = new StringBuilder();
            execution.append("CREATE TABLE IF NOT EXISTS ").append(table).append(" (");
            String primaryKey = values[0][0];
            for (String[] name_type:values) {
                String name = name_type[0];
                String type = name_type[1];
                execution.append(name);
                execution.append(" ");
                execution.append(type);
                execution.append(",");
            }
            execution.append(" PRIMARY KEY(").append(primaryKey).append("))");

            preparedStatement = connection.prepareStatement(execution.toString());
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createRow(Object[] values) {
        try {
            if (!existsPrimaryKey(values[0])) {
                StringBuilder sqlText = new StringBuilder();
                sqlText.append("INSERT INTO ").append(table).append(" VALUE(?");
                for (int i = 1; i < values.length; i++) {
                    sqlText.append(",?");
                }
                sqlText.append(")");
                PreparedStatement preparedStatement = connection.prepareStatement(sqlText.toString());

                for (int i = 0; i < values.length; i++) {
                    Object value = values[i];
                    switch (value.getClass().getSimpleName()) {
                        case "Boolean":
                            preparedStatement.setBoolean(i + 1, (Boolean) value);
                            break;
                        case "String":
                            preparedStatement.setString(i + 1, (String) value);
                            break;
                        case "Byte":
                            preparedStatement.setByte(i + 1, (Byte) value);
                            break;
                        case "Integer":
                            preparedStatement.setInt(i + 1, (Integer) value);
                            break;
                        case "Double":
                            preparedStatement.setDouble(i + 1, (Double) value);
                            break;
                        case "Float":
                            preparedStatement.setFloat(i + 1, (Float) value);
                            break;
                        case "Long":
                            preparedStatement.setLong(i + 1, (Long) value);
                            break;
                        case "Short":
                            preparedStatement.setShort(i + 1, (Short) value);
                            break;
                        default:
                            preparedStatement.setObject(i + 1, value);
                            break;
                    }
                }

                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean existsPrimaryKey(Object primaryKey) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM " + table + " WHERE " + values[0][0] + "=?");

            switch (primaryKey.getClass().getSimpleName()) {
                case "Boolean":
                    preparedStatement.setBoolean( 1, (Boolean) primaryKey);
                    break;
                case "String":
                    preparedStatement.setString( 1, (String) primaryKey);
                    break;
                case "Byte":
                    preparedStatement.setByte( 1, (Byte) primaryKey);
                    break;
                case "Integer":
                    preparedStatement.setInt( 1, (Integer) primaryKey);
                    break;
                case "Double":
                    preparedStatement.setDouble(1, (Double) primaryKey);
                    break;
                case "Float":
                    preparedStatement.setFloat(1, (Float) primaryKey);
                    break;
                case "Long":
                    preparedStatement.setLong(1, (Long) primaryKey);
                    break;
                case "Short":
                    preparedStatement.setShort(1, (Short) primaryKey);
                    break;
                default:
                    preparedStatement.setObject(1, primaryKey);
                    break;
            }

            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                return true;
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void drop(String primaryKey) {
        String sql = "DELETE FROM `" + table + "` WHERE `" + table + "`.`" + values[0][0] + "` = ?";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            preparedStatement.setString(1, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Boolean getBoolean(String primaryKey, String column) {
        boolean result = false;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getBoolean(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public String getString(String primaryKey, String column) {
        String result = "";
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getString(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public Byte getByte(String primaryKey, String column) {
        byte result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getByte(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public Integer getInt(String primaryKey, String column) {
        int result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getInt(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public Double getDouble(String primaryKey, String column) {
        double result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getDouble(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public Float getFloat(String primaryKey, String column) {
        float result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getFloat(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public Long getLong(String primaryKey, String column) {
        long result = 0L;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getLong(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public Short getShort(String primaryKey, String column) {
        short result = 0;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getShort(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public Object getObject(String primaryKey, String column) {
        Object result = 0L;
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT " + column + " FROM " + table + " WHERE " + values[0][0] + "=?");
            preparedStatement.setString(1, primaryKey);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                result = resultSet.getObject(column);
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return result;
    }
    public void setBoolean(String primaryKey, String column, Boolean value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE " + table + " SET " + column + "=? WHERE " + primaryKey + "=?");
            preparedStatement.setBoolean(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setString(String primaryKey, String column, String value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + table + "` SET `" + column + "` =? WHERE `" + table +"`.`" + values[0][0] + "` = ?");
            preparedStatement.setString(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setByte(String primaryKey, String column, Byte value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + table + "` SET `" + column + "` =? WHERE `" + table +"`.`" + values[0][0] + "` = ?");
            preparedStatement.setByte(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setInt(String primaryKey, String column, Integer value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + table + "` SET `" + column + "` =? WHERE `" + table +"`.`" + values[0][0] + "` = ?");
            preparedStatement.setInt(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setDouble(String primaryKey, String column, Double value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + table + "` SET `" + column + "` =? WHERE `" + table +"`.`" + values[0][0] + "` = ?");
            preparedStatement.setDouble(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setFloat(String primaryKey, String column, Float value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + table + "` SET `" + column + "` =? WHERE `" + table +"`.`" + values[0][0] + "` = ?");
            preparedStatement.setFloat(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setLong(String primaryKey, String column, Long value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + table + "` SET `" + column + "` =? WHERE `" + table +"`.`" + values[0][0] + "` = ?");
            preparedStatement.setLong(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setShort(String primaryKey, String column, Short value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + table + "` SET `" + column + "` =? WHERE `" + table +"`.`" + values[0][0] + "` = ?");
            preparedStatement.setShort(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setObject(String primaryKey, String column, Object value) {
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("UPDATE `" + table + "` SET `" + column + "` =? WHERE `" + table +"`.`" + values[0][0] + "` = ?");
            preparedStatement.setObject(1, value);
            preparedStatement.setString(2, primaryKey);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<String> getAllValues() {
        List<String> list = new ArrayList<>();
        try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + table + "`");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                list.add(resultSet.getString(values[0][0]));
            }
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
}
