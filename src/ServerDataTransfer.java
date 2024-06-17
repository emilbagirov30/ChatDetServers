public class ServerDataTransfer extends AbstractServer {
    public ServerDataTransfer(int port) {
        super(port);
    }

    @Override
    protected String processMessage(String message) {
        return message;
    }


}
