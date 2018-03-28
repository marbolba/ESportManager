package sample;

public class Player
{
    String name;
    int budzet;
    private String password;
    int id;
    Druzyna currTeam;
    String currTeamName;

    Player(String n,String pass,int b,int idd)
    {
        name=n;
        password=pass;
        budzet=b;
        id=idd;
        currTeam=null;
        currTeamName=null;
    }
}
