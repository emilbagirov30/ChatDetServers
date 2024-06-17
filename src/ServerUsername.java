public class ServerUsername extends AbstractServer {
    public ServerUsername(int port) {
        super(port);
    }

    @Override
    protected String processMessage(String message) {
        return message;
    }


}
