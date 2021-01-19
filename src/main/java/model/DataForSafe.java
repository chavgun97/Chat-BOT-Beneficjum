package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;

public class DataForSafe implements Serializable {

  private Map<Chat, List<UserActivitesControl>> chats = new HashMap<>();
  private List<Message> messages = new ArrayList<>();

  private HashSet<Admin> admins = new HashSet<>();

  public DataForSafe(
      Map<Chat, List<UserActivitesControl>> chats,
      List<Message> messages, HashSet<Admin> admins) {
    this.chats = chats;
    this.messages = messages;
    this.admins = admins;
  }

  public Map<Chat, List<UserActivitesControl>> getChats() {
    return chats;
  }

  public void setChats(
      Map<Chat, List<UserActivitesControl>> chats) {
    this.chats = chats;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public HashSet<Admin> getAdmins() {
    return admins;
  }

  public void setAdmins(HashSet<Admin> admins) {
    this.admins = admins;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DataForSafe)) {
      return false;
    }
    DataForSafe that = (DataForSafe) o;
    return Objects.equals(getChats(), that.getChats()) &&
        Objects.equals(getMessages(), that.getMessages()) &&
        Objects.equals(getAdmins(), that.getAdmins());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getChats(), getMessages(), getAdmins());
  }
}
