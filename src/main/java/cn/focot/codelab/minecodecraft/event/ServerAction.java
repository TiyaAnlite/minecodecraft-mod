package cn.focot.codelab.minecodecraft.event;

public class ServerAction extends EventMsg{
    public String action;  // lunch, stop

    protected ServerAction(String msgSubject) {
        super(msgSubject);
    }

    public static ServerAction of(String action) {
        ServerAction e = new ServerAction("serverAction");
        e.action = action;
        return e;
    }
}
