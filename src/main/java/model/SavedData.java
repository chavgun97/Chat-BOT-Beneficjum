package model;

import bot.Bot;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;


//EIWMykolaBot - nameBot;
public class SavedData implements Serializable {
  private SavedData(){
  }


  private static SavedData instance;

  public static SavedData getInstance(){
    if(instance == null){
      instance = new SavedData();
    }
    return  instance;
  }

  private Map<Chat, List<UserActivitesControl>> chats = new HashMap<>();
  private List<Message> messages = new ArrayList<>();

  public Map<Chat, List<UserActivitesControl>> getChats() {
    return chats;
  }

  public void setChats(
      Map<Chat, List<UserActivitesControl>> chats) {
    this.chats = chats;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setData(Update update) throws Exception {
    //если нету сообщения -выход
    if(!update.hasMessage())
      return;

    messages.add(update.getMessage());

    //если есть новые участники
    if(!update.getMessage().getNewChatMembers().isEmpty()){
      var newUsers = update.getMessage().getNewChatMembers();
      //если новый учасник бот, и его имя равняеться текущему боту то добавляем новый чат
      //это значит что бот добавили в чат
      newUsers.stream().forEach( nu ->{
              if(nu.getUserName().equals(Bot.getInstance().getBotUsername())){
                chats.put(update.getMessage().getChat(), new ArrayList<UserActivitesControl>());

      }});



      //если новый пользователь не являеться нашим ботом то добавляем его в список участников группы
      //для отслеживания активности

      var usersOfChat = chats.get(update.getMessage().getChat());
      //вставляем в чат новых пользователей с последней датой активности -сейчас
      newUsers.stream().forEach(nu ->{
        if(!nu.getUserName().equals(Bot.getInstance().getBotUsername())){
          usersOfChat.add(new UserActivitesControl(nu, new Date()));

        }
      });
      return;
    }

    if(update.getMessage().getLeftChatMember() != null){
      var leftUser = update.getMessage().getLeftChatMember();

      //если удаленный пользователь наш бот, то удалем текущую группу с данных
        if(leftUser.getUserName().equals(Bot.getInstance().getBotUsername())){
          chats.remove(update.getMessage().getChat());
          return;
        }

      //если это пользователь, удаляем его из списка группы
      var usersOfChat = chats.get(update.getMessage().getChat());
      if(!usersOfChat.remove(leftUser)){
        System.out.println("Пользователь не был удален.");
      }
      return;
    }


  }

  public List<Chat> getAllChatsWithBot(){

    return new ArrayList<>(this.chats.keySet());
  }



  public List<Message> getAllMessagesByChatId (long chatId){
    var result = messages.stream().filter(m-> m.getChatId() == chatId).collect(Collectors.toList());
    return result;
  }

  public List<Message> getAllMessagesOlderThenDay (){
    List<Message> oldMsg = messages.stream().filter(ms ->{
          Date expectedTime = new Date((long) ms.getDate() * 1000);
          System.out.println(expectedTime.toString());

          Date nowMinus24hours = new Date(System.currentTimeMillis() - 24*60*60*1000);
          //Date nowMinus24hours = new Date(System.currentTimeMillis() - 20*1000);
          return expectedTime.getTime() < nowMinus24hours.getTime();
            }
            ).collect(Collectors.toList());

    return oldMsg;
  }

  /***
   *
   * @param day сколько дней от пользователя не было сообщений
   * @return список пользователе
   */
  public  List<Integer> getAllInactiveUsers (int day){
    List<Integer> chatsId = new ArrayList<>();


    return chatsId;
  }


  /***
   *  Метод неработает из-за ненадобности и сложности реализации
   * @param bot
   * @param item
   * @return
   */
  private boolean checkBotAdmin(TelegramLongPollingBot bot, Chat item) {
//     var method = new GetChatAdministrators(item.getId().toString());
//    List<ChatMember> admins = null;
//    try {
//      admins = bot.execute(method);
//    } catch (TelegramApiException e) {
//      e.printStackTrace();
//    }
//    for (var member:
//         admins) {
//      if(member.getUser().getId() ==)
//
//    }
    return false;
  }
}

