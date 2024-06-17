public class ServerTextMessage extends AbstractServer {
    public ServerTextMessage(int port) {
        super(port);
    }

    @Override
    protected String processMessage(String message) {
        return message;
    }


}
