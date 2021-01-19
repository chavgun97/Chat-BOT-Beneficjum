package utils;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class Utils {

  public static void sendMessage(TelegramLongPollingBot bot, String chatId, String message) {
    SendMessage send = new SendMessage();
    send.setReplyMarkup(new ReplyKeyboardRemove(true));
    send.setChatId(chatId);
    send.setText(message);

    try {
      bot.execute(send);
    } catch (TelegramApiException e) {
      e.printStackTrace();
    }

  }
}
