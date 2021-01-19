import bot.Bot;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import model.Admin;
import model.Auth;
import model.DataForSafe;
import model.SavedData;
import model.UserActivitesControl;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;


public class TelegramBotApplication {
  public static void main(String[] args) {
    System.out.println("Я заработал");
    Bot bot = Bot.getInstance();
    SavedData savedData = SavedData.getInstance();
    try {
      serialiseLogic();
    } catch (Exception e) {
      System.out.println(e.getMessage());
     // e.printStackTrace();
    }
    try {
      new TelegramBotsApi(DefaultBotSession.class).registerBot(bot);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }

    Thread loopDellMesages = new Thread(new Runnable() {
      @Override
      public void run() {

        while (true){
          try {
            Thread.sleep(1000 * 60*  5);
            //Thread.sleep(1000 * 3);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }

          savedData.getAllMessagesOlderThenDay().forEach(x ->{
            var method = new DeleteMessage(String.valueOf(x.getChatId()), x.getMessageId());
            try {
              bot.execute(method);
              savedData.getMessages().remove(x);
            } catch (TelegramApiException e) {
              System.out.println(e.getMessage());
            }
          });

        }
      }
    });
    loopDellMesages.start();
  }

  private static void serialiseLogic() throws IOException, ClassNotFoundException {

    new Thread(new Runnable() {
      @Override
      public void run() {
        while (true) {
          try {
            Thread.sleep(1000 * 60);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }
          SavedData savedData = SavedData.getInstance();
          Auth auth = Auth.getInstance();
          DataForSafe dump = new DataForSafe(savedData.getChats(), savedData.getMessages(), auth.getAdmins());
          File filedump = new File("save");
          try {
            FileOutputStream fileOutputStream = new FileOutputStream(filedump, false);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
            objectOutputStream.writeObject(dump);
            objectOutputStream.close();

          } catch (Exception e) {
            System.out.println(e.getMessage());
          }
        }

      }
    }).start();


    File sevedFile = new File("save");

    if(sevedFile.exists()){
      DataForSafe dataFromFileSave;
      //logic deser
      FileInputStream fileInputStream = new FileInputStream(sevedFile);
      ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
      dataFromFileSave = (DataForSafe) objectInputStream.readObject();
      var chats = dataFromFileSave.getChats();
      var messages = dataFromFileSave.getMessages();
      var admins = dataFromFileSave.getAdmins();
      SavedData savedData = SavedData.getInstance();
      Auth auth = Auth.getInstance();
      savedData.setChats(chats);
      savedData.setMessages(messages);
      auth.setAdmins(admins);
      //возможно не сохраняться
      fileInputStream.close();
    }


  }
}

