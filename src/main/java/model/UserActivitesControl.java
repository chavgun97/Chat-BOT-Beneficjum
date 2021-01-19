package model;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import org.telegram.telegrambots.meta.api.objects.User;

public class UserActivitesControl implements Serializable {
  private User user;
  private Date lastMessageDate;

  public UserActivitesControl(User user, Date lastMessageDate) {
    this.user = user;
    this.lastMessageDate = lastMessageDate;
  }

  public User getUser() {
    return user;
  }

  public void setUser(User user) {
    this.user = user;
  }

  public Date getLastMessageDate() {
    return lastMessageDate;
  }

  public void setLastMessageDate(Date lastMessageDate) {
    this.lastMessageDate = lastMessageDate;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof UserActivitesControl)) {
      return false;
    }
    UserActivitesControl that = (UserActivitesControl) o;
    return getUser().equals(that.getUser());
  }

  @Override
  public int hashCode() {
    return Objects.hash(getUser());
  }
}
