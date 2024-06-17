public class ServerPicture extends AbstractServer {
    public ServerPicture(int port) {
        super(port);
    }

    @Override
    protected String processMessage(String message) {
        return message;
    }


}
