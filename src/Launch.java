public class Launch {
    public static void main(String[] args) {
        new Thread(() -> new ServerSQL().main (args)).start();
        new Thread(() -> new ServerDetector(ChatDetPrivateKey.SERVER_DETECTOR_PORT).startServer("детектора ChatDet")).start();
        new Thread(() -> new ServerDataTransfer(ChatDetPrivateKey.SERVER_FORWARDING_PORT).startServer("пересылки данных ChatDet")).start();
        new Thread(() -> new ServerPicture(ChatDetPrivateKey.SERVER_PICTURE_PORT).startServer("обработки изображений ChatDet")).start();
        new Thread(() -> new ServerSound(ChatDetPrivateKey.SERVER_SOUND_PORT).startServer("обработки голосовых сообщений ChatDet")).start();
        new Thread(() -> new ServerStatus(ChatDetPrivateKey.SERVER_STATUS_PORT).startServer("статусов ChatDet")).start();
        new Thread(() -> new ServerTextMessage(ChatDetPrivateKey.SERVER_SMS_PORT).startServer("обработки текстовых сообщений ChatDet")).start();
        new Thread(() -> new ServerUsername(ChatDetPrivateKey.SERVER_USERNAMES_PORT).startServer("ников ChatDet")).start();

    }
}
