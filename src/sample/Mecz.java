package sample;

import java.util.Random;

public class Mecz
{
    int id_mecz;
    public Druzyna A;
    public Druzyna B;
    String druzyna1;
    int id_druzyna1;
    int rating1;
    String druzyna2;
    int id_druzyna2;
    int id_gracz_1;
    int id_gracz_2;
    int rating2;
    int nagroda;

    int runda;
    int score1;
    int score2;
    int przesuniecie;
    int[] alive1tab;
    int[] alive2tab;
    int pamiec1;
    int pamiec2;
    String endScore;

    public String getEndScore() {
        return endScore;
    }

    Mecz(int id_m, int id_d1, String name_1, int r1, int id_d2, String name_2, int r2,String wynik)
    {
        id_mecz=id_m;
        id_druzyna1=id_d1;
        id_druzyna2=id_d2;
        druzyna1=name_1;
        druzyna2=name_2;
        rating1=r1;
        rating2=r2;

        runda=0;
        score1=0;
        score2=0;

        alive1tab=new int[5];
        alive2tab=new int[5];
        przesuniecie=0;
        pamiec1 = 0;
        pamiec2 = 0;
        if(wynik!=null)
            endScore=wynik;
        else
            endScore="nie rozegrano";
    }
    public void rozegrajRunde()
    {
        Random luck = new Random();
        float l1;
        float l2;
        //ozywiam wszystkich zawodnikow
            runda++;

            for (int i = 0; i < 5; i++) {
                A.zawodnicy[i].alive = true;
                B.zawodnicy[i].alive = true;
            }
            //potrzebne zmienne

            int x = 0;
            //pierwsze pojedynki
            setAliveTab();
            /*for (int i = 0; i < alive1tab.length; i++)
                System.out.println(alive1tab[i] + " vs " + alive2tab[i]);*/


            //lecimy
            while (x < 30)
            {
                //setting luck values
                do {
                    l1 = luck.nextFloat() / 2;//0-1
                    l2 = luck.nextFloat() / 2;
                }while(l1<0.3 || l2<0.3);
                System.out.println("l1:"+l1+" l2:"+l2);

                x++;
                //System.out.println("\n\nkolejny pojedynek "+runda);
                for (int i = pamiec1; i < pamiec1 + 5; i++)
                {
                    /*for (int j = 0; j < 5; j++)
                        System.out.println(j+": " + alive1tab[j] + " vs " + alive2tab[j]);*/
                    if (alive1tab[i % 5] == 1) {
                        //jesli jest zywy
                        pamiec1 = i % 5;
                        //System.out.println("Znaleziono1 " + i % 5);
                        break;

                    }
                }
                for (int i = pamiec2; i < pamiec2 + 5; i++)
                {
                    if (alive2tab[i % 5] == 1) {
                        //jesli jest zywy
                        pamiec2 = i % 5;
                        //System.out.println("Znaleziono2 " + i % 5);
                        break;

                    }
                }
                System.out.println("POJEDYNEK: "+pamiec1+" vs "+pamiec2);
                System.out.println(A.zawodnicy[pamiec1].ocena+" ["+l1+"]"+" vs "+B.zawodnicy[pamiec2].ocena+" ["+l2+"]");
                System.out.println(A.zawodnicy[pamiec1].ocena*l1 +"v"+ B.zawodnicy[pamiec2].ocena*l2);
                //setting modificators

                if (A.zawodnicy[pamiec1].ocena*l1 >= B.zawodnicy[pamiec2].ocena*l2)
                {
                    System.out.println("wygral " + A.zawodnicy[pamiec1].nazwa);
                    A.zawodnicy[pamiec1].k++;
                    B.zawodnicy[pamiec2].alive = false;
                    B.zawodnicy[pamiec2].d++;
                    alive2tab[pamiec2] = 0;

                    pamiec1++;
                } else
                {
                    System.out.println("wygral " + B.zawodnicy[pamiec2].nazwa);
                    B.zawodnicy[pamiec2].k++;
                    A.zawodnicy[pamiec1].alive = false;
                    A.zawodnicy[pamiec1].d++;
                    alive1tab[pamiec1] = 0;

                    pamiec2++;
                }
                System.out.println(sum(alive1tab) + "-" + sum(alive2tab));


                if (sum(alive1tab) == 0) {
                    System.out.println("wygrywa2");
                    score2++;
                    break;

                }
                if (sum(alive2tab) == 0) {
                    System.out.println("wygrywa1");
                    score1++;
                    break;
                }
            }
        System.out.println("r:"+runda+"---WYNIK "+score1+":"+score2);

    }
    void setAliveTab()
    {
        String alive1=A.getAliveList();
        String alive2=B.getAliveList();
        String [] t1;
        String [] t2;
        t1=alive1.split(",");
        t2=alive2.split(",");
        for(int i=0;i<t1.length;i++)
        {
            alive1tab[i]=Integer.parseInt(t1[i]);
            alive2tab[i]=Integer.parseInt(t2[i]);
            System.out.println(","+i+": "+alive1tab[i]+" vs "+alive2tab[i]);
        }
    }
    int sum(int[] d)
    {
        int suma=0;
        for(int i=0;i<d.length;i++)
            suma+=d[i];
        return suma;
    }
    void setIdGraczy(int id_g1,int id_g2)
    {
        id_gracz_1=id_g1;
        id_gracz_2=id_g2;
    }

    public int getId_mecz() {
        return id_mecz;
    }

    public void setId_mecz(int id_mecz) {
        this.id_mecz = id_mecz;
    }

    public Druzyna getA() {
        return A;
    }

    public void setA(Druzyna a) {
        A = a;
    }

    public Druzyna getB() {
        return B;
    }

    public void setB(Druzyna b) {
        B = b;
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

    public int getNagroda() {
        return nagroda;
    }

    public void setNagroda(int nagroda) {
        this.nagroda = nagroda;
    }
}
