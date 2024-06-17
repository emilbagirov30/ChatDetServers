public class ServerDetector extends AbstractServer {
    public ServerDetector(int port) {
        super(port);
    }

    @Override
    protected String processMessage(String message) {
        return ChatDetDetector.analysis(message);
    }


}
