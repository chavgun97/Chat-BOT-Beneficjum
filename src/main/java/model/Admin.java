package model;

import java.io.Serializable;
import java.util.Objects;

public class Admin implements Serializable {
  private int userId;
  private long chatId;

  public Admin(int userId) {
    this.userId = userId;
  }


  public Admin(int userId, long chatId) {
    this.userId = userId;
    this.chatId = chatId;
  }

  public long getChatId() {
    return chatId;
  }

  public void setChatId(long chatId) {
    this.chatId = chatId;
  }

  public int getUserId() {
    return userId;
  }

  public void setUserId(int userId) {
    this.userId = userId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Admin)) {
      return false;
    }
    Admin admin = (Admin) o;
    return Objects.equals(getUserId(), admin.getUserId());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUserId());
  }
}
