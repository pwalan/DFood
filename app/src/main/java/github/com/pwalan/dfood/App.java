package github.com.pwalan.dfood;

import android.app.Application;

public class App extends Application{
    private boolean isLogin;
    private String username;
    private int uid;
    private String headurl;
    private String server;

    @Override
    public void onCreate() {
        //对两个值进行初始化
        isLogin = false;
        username="";
        uid =0;
        headurl="";
        server="http://192.168.0.109:8080/AndroidServer/";
        super.onCreate();
    }

    public boolean isLogin() {
        return isLogin;
    }

    public void setIsLogin(boolean isLogin) {
        this.isLogin = isLogin;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getUid() {
        return uid;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public String getServer() {
        return server;
    }

    public void setServer(String server) {
        this.server = server;
    }

    public String getHeadurl() {
        return headurl;
    }

    public void setHeadurl(String headurl) {
        this.headurl = headurl;
    }
}
