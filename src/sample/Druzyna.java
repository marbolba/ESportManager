package sample;

public class Druzyna {
    int id_druzyna;
    int id_gracz;
    int ocena;
    String nazwa;
    Zawodnik[] zawodnicy;
    int zawodnikCounter;

    Druzyna()
    {
        zawodnicy=new Zawodnik[5];
        zawodnikCounter=0;
    }
    Druzyna(String name,int oce,int idd_druzyna) {
        nazwa = name;
        ocena = oce;
        id_druzyna = idd_druzyna;
        //System.out.println("constr:"+nazwa+" "+ocena+" id:"+id_druzyna);
    }
    public void setAll(int idd,int idg,int ocenaa,String nazwaa)
    {
        id_druzyna=idd;
        id_gracz=idg;
        ocena=ocenaa;
        nazwa=nazwaa;
        //System.out.println(nazwaa+" "+ocenaa+" "+idg+" "+idd);
    }
    void dodajZawodnika(Zawodnik a)
    {
        zawodnicy[zawodnikCounter]=a;
        zawodnikCounter++;
    }
    String getAliveList()
    {
        String aliveList="";
        for(int i=0;i<5;i++)
        {
            if(zawodnicy[i].alive)
                aliveList+="1";
            else
                aliveList+="0";
            if(i!=4)
                aliveList+=",";
        }
        return aliveList;
    }

    public int getId_druzyna() {
        return id_druzyna;
    }

    public void setId_druzyna(int id_druzyna) {
        this.id_druzyna = id_druzyna;
    }

    public int getId_gracz() {
        return id_gracz;
    }

    public void setId_gracz(int id_gracz) {
        this.id_gracz = id_gracz;
    }

    public int getOcena() {
        return ocena;
    }

    public void setOcena(int ocena) {
        this.ocena = ocena;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public Zawodnik[] getZawodnicy() {
        return zawodnicy;
    }

    public void setZawodnicy(Zawodnik[] zawodnicy) {
        this.zawodnicy = zawodnicy;
    }

    public int getZawodnikCounter() {
        return zawodnikCounter;
    }

    public void setZawodnikCounter(int zawodnikCounter) {
        this.zawodnikCounter = zawodnikCounter;
    }
}
