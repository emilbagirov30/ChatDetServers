import java.io.*;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public abstract class AbstractServer {
    private volatile ConcurrentHashMap<String, CopyOnWriteArrayList<BufferedWriter>> userMap = new ConcurrentHashMap<>();
    private int port;

    public AbstractServer(int port) {
        this.port = port;
    }

    protected abstract String processMessage(String message);

    public void startServer(String serverName) {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Сервер " + serverName + " запущен на порту " + port);
            while (true) {
                Socket socket = serverSocket.accept();
                System.out.println("#Пользователь подключился [" + socket.getInetAddress() + "]");

                new Thread(() -> handleClient(socket)).start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void handleClient(Socket socket) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()))) {

            String key = reader.readLine();
            userMap.computeIfAbsent(key, k -> new CopyOnWriteArrayList<>()).add(writer);

            String message;
            while ((message = reader.readLine()) != null) {
                String processedMessage = processMessage(message);
                System.out.println(processedMessage.replace(ChatDetPrivateKey.NEW_LINE_KEY, " "));

                for (BufferedWriter userWriter : userMap.get(key)) {
                    if (checkError(userWriter)) {
                        if (port == ChatDetPrivateKey.SERVER_STATUS_PORT){
                            if (userWriter!=writer){
                                userWriter.write(processedMessage);
                                userWriter.newLine();
                                userWriter.flush();
                            }
                        }else{
                            userWriter.write(processedMessage);
                            userWriter.newLine();
                            userWriter.flush();
                        }

                    } else {
                        userMap.values().forEach(writers -> writers.remove(userWriter));
                    }
                }
            }
        } catch (ConnectException c) {
            Thread.currentThread().interrupt();
            throw new RuntimeException(c);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean checkError(BufferedWriter userWriter) {
        try {
            userWriter.write(" ");
            userWriter.newLine();
            userWriter.flush();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
