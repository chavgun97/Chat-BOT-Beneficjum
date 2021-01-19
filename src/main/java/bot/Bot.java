package bot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import model.Auth;
import model.SavedData;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import utils.Utils;

public class Bot extends TelegramLongPollingBot {
  private Bot(){}
  private static Bot instance;

  public static Bot getInstance(){
    if(instance == null){
      instance = new Bot();
    }
    return instance;
  }

  Auth auth = Auth.getInstance();
  SavedData savedData = SavedData.getInstance();

  private String state = "", login ="", psw ="";



  @Override
  public String getBotUsername() {
    return "EIWMykolaBot";
  }

  @Override
  public String getBotToken() {
    return "1554141199:AAHtkM7Of4LjhJXkhpKMaSW0-1qUcKuu15Y";
  }

  @Override
  public void onUpdateReceived(Update update) {

    //check auth
    if(isSingleChat(update)){
      adminInterfaceLogic(update);
    }else {
      chatLogic(update);
    }
  }

  private boolean isSingleChat(Update update) {
    boolean b =  update.hasMessage()?
        update.getMessage().getChat().isUserChat()
        : (update.hasCallbackQuery() ?
        update.getCallbackQuery().getMessage().getChat().isUserChat()
        : false);
    return b;
  }

  private void adminInterfaceLogic(Update update) {
    int userId = update.hasMessage()?
        update.getMessage().getFrom().getId():
        (update.hasCallbackQuery()? update.getCallbackQuery().getFrom().getId(): 0 );

    //если пользователь авторизирован и обоновелние имеет или сообщение или запрос переходим к главной логике
    //в главной логике обработаны только запросы и сообщения, по этому другие варианты не будут обрабаьываться
    if(auth.isAlreadyAuth(userId)
        && (update.hasMessage() || update.hasCallbackQuery())
    ){
      try {
        mainInterfaceLogic(update);
      } catch (TelegramApiException e) {
        System.out.println(e.getMessage());
        e.printStackTrace();
      }
    }else if(update.hasMessage()) {
      String chatId = update.getMessage().getChatId().toString();

      if(state.isEmpty()) {
        Utils.sendMessage(this, chatId,
            "У вас нету доступа к управлению ботом. Для получения  доступа введите данные для входа");
        Utils.sendMessage(this, chatId,
            "Введите логин:");
        state = "Ожидаеться логин";
        return;
      }

      if(state.equals("Ожидаеться логин")) {
        login = update.getMessage().getText();

        Utils.sendMessage(this, chatId,
            "Введите пароль:");
        state  = "Ожидаеться пароль";
        return;
      }

      if(state.equals("Ожидаеться пароль")) {
        psw = update.getMessage().getText();

        if(auth.auth(update.getMessage().getFrom().getId(),update.getMessage().getChatId(), login, psw)) {
          Utils.sendMessage(this, chatId,
              "Вы авторизировались и имете дступ к системе.");
          try {
            mainInterfaceLogic(update);
          } catch (TelegramApiException e) {
            System.out.println(e.getMessage());
            e.printStackTrace();
          }
        }else {
          Utils.sendMessage(this, chatId,
              "Логин или пароль неправильный");
        }
        state = "";
      }

    }
  }

  private void mainInterfaceLogic(Update update) throws TelegramApiException {
    SendMessage send = new SendMessage();
    send.setChatId(update.getMessage().getChatId().toString());
    if(update.hasMessage()) {
      if (update.getMessage().hasText()) {
        String requestText = update.getMessage().getText();
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> rowList = new ArrayList<>();


        var chatIntrface = savedData.getAllChatsWithBot().stream().filter(chat ->
            chat.getTitle().equals(requestText)).findFirst().orElse(null);

        if(Objects.nonNull(chatIntrface)){
          //интерфейс управления группой
          var row = new KeyboardRow();
          row.add("Удалить все сообщения группы: " + chatIntrface.getTitle());
          rowList.add(row);

          var row2 = new KeyboardRow();
          row2.add("Вернуться");
          rowList.add(row2);
          keyboard.setKeyboard(rowList);

          send.setReplyMarkup(keyboard);
          send.setText("Список доступных действий с группой:");
          execute(send);
          return;
        }
        if(requestText.contains("Удалить все сообщения группы: ")){
          String chatTitle = requestText.substring(30);
          Chat currentChat = savedData.getAllChatsWithBot().stream().filter(ch ->
            ch.getTitle().equals(chatTitle)
          ).findAny().orElse(null);

         boolean isDone = dellAllMessageFromChat(currentChat.getId());
          if(isDone){
            send.setText("Все сообщения с чата " + currentChat.getTitle() + " были удалены");
          }else{
            send.setText("Сообщения с чата " + currentChat.getTitle() + " не были удалены. " +
                "Это могло произойти потому что бот не имеет прав администратора в группе. " +
                "Проверьте пожалуйста являеться ли бот администратором управляемой группы. Если после" +
                "этого ничего не изменилось обратитесь к Коле");

          }
          execute(send);
          return;
        }

          savedData.getAllChatsWithBot().forEach(chat ->
          {
            var row = new KeyboardRow();
            row.add(chat.getTitle());
            rowList.add(row);
          });

          keyboard.setKeyboard(rowList);


          send.setChatId(update.getMessage().getChatId().toString());
          if(rowList.size()>0) {
            send.setText("Список доступных для управления групп:");
            send.setReplyMarkup(keyboard);
          } else {
            send.setText("Пока нет доступных групп для управления. Добавьте бота в участники " +
                "группы и предоставьте права администратора");
            send.setReplyMarkup(new ReplyKeyboardRemove(true));
          }
          //добавление кнопки в сообшения
          execute(send);
          return;




        //Создание панели кнопок снизу


//        KeyboardRow row1 = new KeyboardRow();
//        KeyboardRow row2 = new KeyboardRow();
//        row1.add(new KeyboardButton("test Button"));
//        row1.add(new KeyboardButton("test Button2"));
//        row1.add(new KeyboardButton("test Button3"));
//        row2.add("testRow");


//        if (requestText.equals("testRow")) {
//          InlineKeyboardMarkup inlineKayboard = new InlineKeyboardMarkup();
//
//          List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();
//          InlineKeyboardButton inlineButton = new InlineKeyboardButton("test Button");
//          inlineButton.setCallbackData("Inline Test");
//          inlineKeyboardButtons.add(inlineButton);
//
//          List<List<InlineKeyboardButton>> rowsList = new ArrayList<>();
//          rowsList.add(inlineKeyboardButtons);
//
//
//          inlineKayboard.setKeyboard(rowsList);
//          send.setReplyMarkup(inlineKayboard);
//
//          send.setChatId(update.getMessage().getChatId().toString());
//          send.setText("панель сверху");
//        }
//
//        if (update.hasCallbackQuery() && update.getCallbackQuery().getData().equals("Inline Test")) {
//          send.setChatId(update.getMessage().getChatId().toString());
//          send.setText("обработка запроса кнопки сверху завершена");
//        }
//        execute(send);

      }
      return;
    }

    if(update.hasCallbackQuery()){
      if (update.getCallbackQuery().getData().equals("Inline Test")) {
        send.setChatId(update.getCallbackQuery().getMessage().getChatId().toString());
        send.setText("обработка запроса кнопки сверху завершена");
      }
      execute(send);
      return;
    }
  }


  private void chatLogic(Update update){
    try {
      savedData.setData(update);
    } catch (Exception e) {
      System.out.println(e.getMessage());
      e.printStackTrace();
    }


  }

  private boolean dellAllMessageFromChat(long idChat){
    boolean isDone = true;
    List<Message> messagesOfChat = savedData.getAllMessagesByChatId(idChat);
    List<Message> messagesToRemove = new ArrayList<>();
    for (Message x : messagesOfChat) {
      var dellMethod = new DeleteMessage(String.valueOf(idChat), x.getMessageId());
      try {
        execute(dellMethod);
        isDone = true;
      } catch (TelegramApiException e) {
        isDone = false;
        e.printStackTrace();
      }
      if(isDone)
        messagesToRemove.add(x);
    }

    savedData.getMessages().removeAll(messagesToRemove);
    return isDone;
  }

}
