package sample;

public class Scoreboard
{
    String name;
    int id_d;
    int m;
    int w;
    int l;
    int pkt;

    Scoreboard(String n1,int id1)
    {
        name=n1;
        id_d=id1;
        m=0;
        w=0;
        l=0;
        pkt=0;
        System.out.println("sb:dodaje team:"+name);
    }

    public int getM() {
        return m;
    }

    public void setM(int m) {
        this.m = m;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId_d() {
        return id_d;
    }

    public void setId_d(int id_d) {
        this.id_d = id_d;
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public int getL() {
        return l;
    }

    public void setL(int l) {
        this.l = l;
    }

    public int getPkt() {
        return pkt;
    }

    public void setPkt(int pkt) {
        this.pkt = pkt;
    }
}
