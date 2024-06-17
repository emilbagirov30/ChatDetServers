import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.*;
import java.util.Base64;

public class ServerSQL {
    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(ChatDetPrivateKey.SERVER_SQL_PORT)) {
            System.out.println("Сервер обработки SQL-запросов ChatDet запущен");
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("#Пользователь подключился [" + clientSocket.getInetAddress()+ "]");

                new Thread(() -> {
                    try (BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                         BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(clientSocket.getOutputStream()));

                         Connection connection = DriverManager.getConnection(ChatDetPrivateKey.SQL_URL, ChatDetPrivateKey.SQL_USERNAME, ChatDetPrivateKey.SQL_PASSWORD))
                    {
                        String key = reader.readLine();
                        switch (key) {
                            case ChatDetPrivateKey.REGISTRATION_APP_KEY -> {
                                String username = reader.readLine();
                                if  (checkUserExist(username, connection)) {
                                    sendMessage(writer,ChatDetPrivateKey.USER_EXIST);
                                }else {
                                    sendMessage(writer,ChatDetPrivateKey.USER_NOT_EXIST);
                                    String password = reader.readLine();
                                    String codeWord = reader.readLine();
                                    String query = "INSERT INTO chatdet_data (username, password, code_word) VALUES (?, ?, ?)";
                                    PreparedStatement statement = connection.prepareStatement(query);
                                    statement.setString(1, username);
                                    statement.setString(2, password);
                                    statement.setString(3, codeWord);
                                    statement.executeUpdate();
                                    statement.close();
                                }
                            }
                            case ChatDetPrivateKey.REGISTRATION_APP_KEY_AVATAR -> {
                                String username = reader.readLine();
                                if(checkUserExist(username, connection))  {
                                    sendMessage(writer,ChatDetPrivateKey.USER_EXIST);
                                }else {
                                    sendMessage(writer,ChatDetPrivateKey.USER_NOT_EXIST);
                                    String password = reader.readLine();
                                    String codeWord = reader.readLine();
                                    String avatar = reader.readLine();
                                    byte[] data =  Base64.getDecoder().decode(avatar);
                                    String query = "INSERT INTO chatdet_data (username, password, code_word,avatar) VALUES (?, ?, ?,?)";
                                    PreparedStatement statement = connection.prepareStatement(query);
                                    statement.setString(1, username);
                                    statement.setString(2, password);
                                    statement.setString(3, codeWord);
                                    statement.setBytes(4, data);
                                    statement.executeUpdate();
                                    statement.close();

                                }
                            }
                            case ChatDetPrivateKey.REPLACE_PASSWORD -> {
                                String username = reader.readLine();
                                String codeWord = reader.readLine();
                                String query = "SELECT * FROM chatdet_data WHERE username = ? AND code_word = ?";
                                PreparedStatement statement = connection.prepareStatement(query);
                                statement.setString(1, username);
                                statement.setString(2, codeWord);
                                ResultSet resultSet = statement.executeQuery();
                                if (resultSet.next()) {
                                    sendMessage(writer,ChatDetPrivateKey.USER_EXIST);
                                    String newPassword = reader.readLine();
                                    String updatePassword = "UPDATE chatdet_data SET password = ? WHERE username = ?";
                                    PreparedStatement updateStatement = connection.prepareStatement(updatePassword);
                                    updateStatement.setString(1, newPassword);
                                    updateStatement.setString(2, username);
                                    int rowsAffected = updateStatement.executeUpdate();
                                    updateStatement.close();
                                    if (rowsAffected > 0) {
                                        sendMessage(writer,ChatDetPrivateKey.REPLACE_PASSWORD_SUCCESSFUL);
                                    } else {
                                        sendMessage(writer,ChatDetPrivateKey.REPLACE_PASSWORD_ERROR);
                                    }
                                    updateStatement.close();
                                }else{
                                    sendMessage(writer,ChatDetPrivateKey.USER_NOT_EXIST);
                                }
                            }  case ChatDetPrivateKey.LOG_IN -> {
                                String username = reader.readLine();
                                String password = reader.readLine();
                                String query = "SELECT * FROM chatdet_data WHERE username = ? AND password = ?";
                                PreparedStatement statement = connection.prepareStatement(query);
                                statement.setString(1, username);
                                statement.setString(2, password);
                                ResultSet resultSet = statement.executeQuery();
                                if (resultSet.next()) {
                                    sendMessage(writer,ChatDetPrivateKey.CORRECT_DATA);
                                    String id = String.valueOf(resultSet.getInt("id"));
                                    sendMessage(writer,id);

                                }else sendMessage(writer,ChatDetPrivateKey.INCORRECT_DATA);

                                statement.close();
                            }case ChatDetPrivateKey.GET_ALL_USER_DATA -> {
                                String id = reader.readLine();
                                String query = "SELECT * FROM chatdet_data WHERE id = ?";
                                PreparedStatement statement = connection.prepareStatement(query);
                                statement.setInt(1, Integer.valueOf(id));
                                ResultSet resultSet = statement.executeQuery();
                                if (resultSet.next()) {
                                    sendMessage(writer,resultSet.getString("username"));
                                    sendMessage(writer,resultSet.getString("password"));
                                    sendMessage(writer,resultSet.getString("code_word"));
                                    byte [] avatar = resultSet.getBytes("avatar");
                                    if (avatar == null){
                                        sendMessage(writer,"NULL");
                                    }else{
                                        String avatarString = Base64.getEncoder().encodeToString(avatar);
                                        sendMessage(writer,avatarString);
                                    }
                                    statement.close();
                                }
                            }case ChatDetPrivateKey.CHANGE_USER_DATA_NA -> {
                                String id = reader.readLine();
                                String username = reader.readLine();
                                String password = reader.readLine();
                                String codeWord = reader.readLine();
                                String updateQuery = "UPDATE chatdet_data SET username = ?, password = ?, code_word = ? WHERE id = ?";
                                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                                preparedStatement.setString(1, username);
                                preparedStatement.setString(2, password);
                                preparedStatement.setString(3, codeWord);
                                preparedStatement.setInt(4, Integer.valueOf(id));
                                int rowsUpdated = preparedStatement.executeUpdate();
                                if (rowsUpdated > 0) {
                                    sendMessage(writer,ChatDetPrivateKey.CHANGE_DATA_SUCCESSFUL);

                                } else {
                                    sendMessage(writer,ChatDetPrivateKey.CHANGE_DATA_ERROR);
                                }
                            }case ChatDetPrivateKey.CHANGE_USER_DATA -> {
                                String avatar = reader.readLine();
                                byte[] data =  Base64.getDecoder().decode(avatar);
                                String id = reader.readLine();
                                String username = reader.readLine();
                                String password = reader.readLine();
                                String codeWord = reader.readLine();
                                String updateQuery = "UPDATE chatdet_data SET username = ?, password = ?, code_word = ?,avatar = ? WHERE id = ?";
                                PreparedStatement preparedStatement = connection.prepareStatement(updateQuery);
                                preparedStatement.setString(1, username);
                                preparedStatement.setString(2, password);
                                preparedStatement.setString(3, codeWord);
                                preparedStatement.setBytes(4, data);
                                preparedStatement.setInt(5, Integer.valueOf(id));
                                int rowsUpdated = preparedStatement.executeUpdate();
                                if (rowsUpdated > 0) {
                                    sendMessage(writer,ChatDetPrivateKey.CHANGE_DATA_SUCCESSFUL);
                                } else {
                                    sendMessage(writer,ChatDetPrivateKey.CHANGE_DATA_ERROR);
                                }
                                preparedStatement.close();
                            }  case ChatDetPrivateKey.GET_AVATAR-> {
                                String id = reader.readLine();
                                String query = "SELECT * FROM chatdet_data WHERE id = ?";
                                PreparedStatement preparedStatement = connection.prepareStatement(query);
                                preparedStatement.setInt(1, Integer.valueOf(id));
                                ResultSet resultSet = preparedStatement.executeQuery();

                                if (resultSet.next()) {
                                    byte [] avatar = resultSet.getBytes("avatar");
                                    if (avatar == null){
                                        sendMessage(writer,"NULL");
                                    }else{
                                        String avatarString = Base64.getEncoder().encodeToString(avatar);
                                        sendMessage(writer,avatarString);
                                    }
                                }
                                preparedStatement.close();
                            }
                        }

                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                }).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private static boolean checkUserExist(String username, Connection connection) throws SQLException {
        String checkExist = "SELECT * FROM chatdet_data WHERE username = ?";
        PreparedStatement check = connection.prepareStatement(checkExist);
        check.setString(1, username);
        ResultSet resultSet = check.executeQuery();
        return resultSet.next();
    }

    private static void sendMessage (BufferedWriter writer,String message) throws IOException {
        writer.write(message);
        writer.newLine();
        writer.flush();
    }


}
