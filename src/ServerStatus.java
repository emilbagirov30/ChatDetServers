public class ServerStatus extends AbstractServer {
    public ServerStatus(int port) {
        super(port);
    }

    @Override
    protected String processMessage(String message) {
        return message;
    }


}
