package model;


import java.io.Serializable;
import java.util.HashSet;

public class Auth{

  private Auth(){}
  private static Auth instance;

  public static Auth getInstance(){
    if(instance == null){
      instance = new Auth();
    }
    return instance;
  }

  private String login = "someLogin";
  private String psw = "somePsw";

  private HashSet<Admin> admins = new HashSet<>();

  public HashSet<Admin> getAdmins() {
    return admins;
  }

  public void setAdmins(HashSet<Admin> admins) {
    this.admins = admins;
  }

  public boolean auth(int adminId, long chatId, String login, String psw){
    if(this.login.equals(login) & this.psw.equals(psw) ){

      return this.admins.add( new Admin(adminId, chatId));
    }
    return false;
  }

  public boolean isAlreadyAuth(Integer userId){
    return admins.contains(new Admin(userId));
  }
}

