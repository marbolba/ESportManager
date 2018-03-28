package sample;

public class Zawodnik
{
    int id_karta,id_zawodnik,ocena,cena,wiek;
    double forma;
    double formaPasek;
    String nazwa,org_druzyna;
    int k,d;
    boolean alive;
    Zawodnik(int id_kartaa,int id_zawodnikk,String nazwaa,int ocenaa,int cenaa,double form,String org_druzynaa)
    {
        id_karta=id_kartaa;
        id_zawodnik=id_zawodnikk;
        ocena=ocenaa;
        cena=cenaa;
        forma=form;
        formaPasek=forma/100+0.5;
        nazwa=nazwaa;
        org_druzyna=org_druzynaa;
        k=0;
        d=0;
        alive=true;
        //System.out.println(nazwaa+" "+org_druzynaa+" "+wiekk+" "+cena+" "+ocenaa+" "+id_karta+" "+id_zawodnik);
    }
    Zawodnik(int id_zawodnikk,String nazwaa,int ocenaa,int cenaa,int wiekk,String org_druzynaa,double form)
    {
        //potrzebny do banku
        id_zawodnik=id_zawodnikk;
        ocena=ocenaa;
        cena=cenaa;
        wiek=wiekk;
        forma=form;
        formaPasek=forma/100+0.5;
        nazwa=nazwaa;
        org_druzyna=org_druzynaa;
    }
    public void setKD(int kk,int dd)
    {
        k=kk;
        d=dd;
    }

    public int getK() {
        return k;
    }

    public int getD() {
        return d;
    }

    public int getId_karta() {
        return id_karta;
    }

    public void setId_karta(int id_karta) {
        this.id_karta = id_karta;
    }

    public int getId_zawodnik() {
        return id_zawodnik;
    }

    public void setId_zawodnik(int id_zawodnik) {
        this.id_zawodnik = id_zawodnik;
    }

    public int getOcena() {
        return ocena;
    }

    public void setOcena(int ocena) {
        this.ocena = ocena;
    }

    public int getCena() {
        return cena;
    }

    public void setCena(int cena) {
        this.cena = cena;
    }

    public int getWiek() {
        return wiek;
    }

    public double getForma() {
        return forma;
    }

    public double getFormaPasek(){return formaPasek;}

    public void setWiek(int wiek) {
        this.wiek = wiek;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getOrg_druzyna() {
        return org_druzyna;
    }

    public void setOrg_druzyna(String org_druzyna) {
        this.org_druzyna = org_druzyna;
    }
}
