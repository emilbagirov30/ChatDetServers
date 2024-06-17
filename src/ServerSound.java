public class ServerSound extends AbstractServer {
    public ServerSound(int port) {
        super(port);
    }

    @Override
    protected String processMessage(String message) {
        return message;
    }


}
