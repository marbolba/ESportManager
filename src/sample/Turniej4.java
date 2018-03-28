package sample;

import javafx.scene.control.Button;

public class Turniej4
{
    int id_turniej;
    int id_terminarz;
    int nagroda;
    String nazwa;
    String druzyna1;
    int id_druzyna1;
    String druzyna2;
    int id_druzyna2;
    String druzyna3;
    int id_druzyna3;
    String druzyna4;
    int id_druzyna4;
    public boolean active;


    int druzynaCount;

    Turniej4(int id_t, String name, int winnings)
    {
        id_turniej=id_t;
        nazwa=name;
        nagroda=winnings;
        druzynaCount=0;

        druzyna1="";
        druzyna2="";
        druzyna3="";

        druzyna4="";
        id_druzyna1=0;
        id_druzyna2=0;
        id_druzyna3=0;
        id_druzyna4=0;

        active=false;
    }
    public void calculateScoreboard()
    {

    }
    public boolean zapiszTeam(int id_d,int ile)
    {
        if(druzynaCount<4 && ile==0)
            return true;
        else
            return false;
    }
    public boolean checkIfRegisterOpened()
    {
        if(druzynaCount<4)
            return true;
        else
            return false;
    }
    public void addTeam(int id_d,String nazwaD)
    {
        switch (druzynaCount)
        {
            case 0:
                druzyna1=nazwaD;
                id_druzyna1=id_d;
                System.out.println("dodaje druzyne1");
                break;

            case 1:
                druzyna2=nazwaD;
                id_druzyna2=id_d;
                System.out.println("dodaje druzyne2");
                break;

            case 2:
                druzyna3=nazwaD;
                id_druzyna3=id_d;
                System.out.println("dodaje druzyne3");
                break;

            case 3:
                druzyna4=nazwaD;
                id_druzyna4=id_d;
                System.out.println("dodaje druzyne4, start");
                break;
        }
        druzynaCount++;
    }
    public int getId_turniej() {
        return id_turniej;
    }

    public void setId_turniej(int id_turniej) {
        this.id_turniej = id_turniej;
    }

    public int getId_terminarz() {
        return id_terminarz;
    }

    public void setId_terminarz(int id_terminarz) {
        this.id_terminarz = id_terminarz;
    }

    public int getNagroda() {
        return nagroda;
    }

    public void setNagroda(int nagroda) {
        this.nagroda = nagroda;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getDruzyna1() {
        return druzyna1;
    }

    public void setDruzyna1(String druzyna1) {
        this.druzyna1 = druzyna1;
    }

    public int getId_druzyna1() {
        return id_druzyna1;
    }

    public void setId_druzyna1(int id_druzyna1) {
        this.id_druzyna1 = id_druzyna1;
    }

    public String getDruzyna2() {
        return druzyna2;
    }

    public void setDruzyna2(String druzyna2) {
        this.druzyna2 = druzyna2;
    }

    public int getId_druzyna2() {
        return id_druzyna2;
    }

    public void setId_druzyna2(int id_druzyna2) {
        this.id_druzyna2 = id_druzyna2;
    }

    public String getDruzyna3() {
        return druzyna3;
    }

    public void setDruzyna3(String druzyna3) {
        this.druzyna3 = druzyna3;
    }

    public int getId_druzyna3() {
        return id_druzyna3;
    }

    public void setId_druzyna3(int id_druzyna3) {
        this.id_druzyna3 = id_druzyna3;
    }

    public String getDruzyna4() {
        return druzyna4;
    }

    public void setDruzyna4(String druzyna4) {
        this.druzyna4 = druzyna4;
    }

    public int getId_druzyna4() {
        return id_druzyna4;
    }

    public void setId_druzyna4(int id_druzyna4) {
        this.id_druzyna4 = id_druzyna4;
    }

    public int getDruzynaCount() {
        return druzynaCount;
    }

    public void setDruzynaCount(int druzynaCount) {
        this.druzynaCount = druzynaCount;
    }

}
