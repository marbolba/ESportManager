package sample;

import java.sql.*;
import java.util.Random;

public class PostgreSQLJDBC
{
    Connection c = null;
    Statement stmt = null;

    public void getConnection()
    {
        try {
            Class.forName("org.postgresql.Driver");
            c = DriverManager
                    .getConnection("jdbc:postgresql://127.0.0.1:5432/u4bolba",
                            "u4bolba", "4bolba");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("Opened database successfully");
    }
    public boolean insertTeam(int id_g,String name)
    {
        boolean utworzono=false;
        try {
            stmt = c.createStatement();
            Statement stmt2 = c.createStatement();

            ResultSet rs = stmt.executeQuery( "select count(*) from druzyna where id_gracz="+id_g+" and nazwa_druzyny='"+name+"';");
            while( rs.next() )
            {
                if(rs.getInt("count")==0)
                {
                    stmt2.executeUpdate("insert into druzyna(id_gracz,ocena,nazwa_druzyny) values ("+id_g+",0,'"+name+"');");
                    utworzono=true;
                }
                else
                    utworzono=false;

            }
            stmt2.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return utworzono;
    }
    public boolean insertPlayer(String name,String pass)
    {
        int ile=0;
        try {
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery( "select count(*) from gracz where nazwa_gracza='"+name+"';");

            while( rs.next() )
            {
                ile=rs.getInt("count");
            }
            if(ile==0)
            stmt.executeUpdate("insert into gracz(nazwa_gracza,haslo,budzet) values ('"+name+"','"+pass+"',17000);");


            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        if(ile==0)
            return true;
        else
            return false;

    }
    public int getPlayersSize()
    {
        int size=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select count(*) from zawodnik;");

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("ilosc graczy: "+size);
        return size;
    }
    public int getQuerriedPlayersSize(String cL,String cH,String oL,String oH,String nick,String team)
    {
        int size=0;
        String query="select count(*) from zawodnik where ";
        int count=0;
        if(!cL.isEmpty())
        {
            if((count++)>0)
                query+="and";
            query+=(" cena>="+cL+" ");
        }
        if(!cH.isEmpty())
        {
            if((count++)>0)
                query+="and";
            query+=(" cena<="+cH+" ");
        }
        if(!oL.isEmpty())
        {
            if((count++)>0)
                query+="and";
            query+=(" ocena>="+oL+" ");
        }
        if(!oH.isEmpty())
        {
            if((count++)>0)
                query+="and";
            query+=(" ocena<="+oH+" ");
        }
        if(!nick.isEmpty())
        {
            if((count++)>0)
                query+="and";
            query+=(" LOWER(nazwa)LIKE LOWER('%"+nick+"%') ");
        }
        if(!team.isEmpty())
        {
            if((count++)>0)
                query+="and";
            query+=(" LOWER(org_druzyna) LIKE LOWER('%"+team+"%') ");
        }

        query+=";";
        //System.out.println(query);
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( query);

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        //System.out.println("ilosc wyszukan: "+size);
        return size;
    }
    public int getSeller(int id_k)
    {
        int id_gracz=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select * from karta_zawodnika where id_karta="+id_k+";");

            while( rs.next() )
            {
                id_gracz=rs.getInt("id_gracz");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return id_gracz;
    }
    public int getBenchedSize(int me_id)
    {
        int size=0;
        try {
            stmt = c.createStatement();

            ResultSet rs = stmt.executeQuery( "select count(*) from karta_zawodnika k left join rynek r on k.id_karta=r.id_karta where id_gracz="+me_id+" and id_druzyna is null and r.id_oferta is null;");

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("ilosc rezerwowych: "+size);
        return size;
    }
    public int getMarketSize(int me_id)
    {
        int size=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select count(*) from rynek r left join karta_zawodnika k on r.id_karta=k.id_karta where k.id_gracz!="+me_id+";");

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return size;
    }
    public int getMyMarketSize(int me_id)
    {
        int size=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select count(*) from rynek r left join karta_zawodnika k on r.id_karta=k.id_karta where k.id_gracz="+me_id+";");

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return size;
    }
    public int getTournamentsSize()
    {
        int size=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select count(*) from turniej4;");

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return size;

    }
    public void addTeamToTournament(int id_d,int id_t,int pos)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("update turniej4 set id_druzyna"+pos+"="+id_d+" where id_turniej="+id_t+";");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        if(pos==4)
        {
            createTournamentTerm(id_t);
        }

    }
    public Mecz[] getMyMatches(int me_id)
    {
        int size=getMyMatchesSize(me_id);
        int i=0;
        Mecz[] tablicaMeczy=new Mecz[size];
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select * from mecz m,druzyna d where id_gracz="+me_id+" and (id_druzyna1=id_druzyna or id_druzyna2=id_druzyna);");

            while( rs.next() )
            {
                tablicaMeczy[i]=new Mecz(rs.getInt("id_mecz"),
                        rs.getInt("id_druzyna1"),getTeamName(rs.getInt("id_druzyna1")),getTeamRating(rs.getInt("id_druzyna1")),
                        rs.getInt("id_druzyna2"),getTeamName(rs.getInt("id_druzyna2")),getTeamRating(rs.getInt("id_druzyna2")),rs.getString("wynik"));
                i++;
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return tablicaMeczy;
    }
    public Mecz getThisMatchData(int m_id)
    {
        Mecz mecz=null;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select * from mecz m,druzyna d where id_mecz="+m_id+" and (id_druzyna1=id_druzyna or id_druzyna2=id_druzyna);");

            while( rs.next() )
            {
                mecz=new Mecz(rs.getInt("id_mecz"),
                        rs.getInt("id_druzyna1"),getTeamName(rs.getInt("id_druzyna1")),getTeamRating(rs.getInt("id_druzyna1")),
                        rs.getInt("id_druzyna2"),getTeamName(rs.getInt("id_druzyna2")),getTeamRating(rs.getInt("id_druzyna2")),rs.getString("wynik"));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return mecz;
    }
    public int getMyMatchesSize(int me_id)
    {
        int size=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select count(*) from mecz m,druzyna d where id_gracz="+me_id+" and (id_druzyna1=id_druzyna or id_druzyna2=id_druzyna);");

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return size;
    }
    public int getTeamManager(int id_d)
    {
        int id_g=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select * from druzyna where id_druzyna="+id_d+";");

            while( rs.next() )
            {
                id_g=rs.getInt("id_gracz");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return id_g;
    }
    public int[] getTournamentMatches(int id_t)
    {
        int id_terminarz=0;
        int[] mecze=new int[6];
        //tutaj klepaj
        try {
        stmt = c.createStatement();
        ResultSet rs = stmt.executeQuery( "select id_terminarz from turniej4 where id_turniej="+id_t+";");
            while( rs.next() )
            {
                id_terminarz=rs.getInt("id_terminarz");
            }
            //System.out.println("id_term:"+id_terminarz);
            rs.close();
            ResultSet rs2 = stmt.executeQuery( "select * from terminarz4 where id_terminarz="+id_terminarz+";");
            while( rs2.next() )
            {
                for(int i=0;i<6;i++)
                {
                    mecze[i]=rs2.getInt("id_mecz"+(i+1));
                    //System.out.println("meczid"+(i+1)+": "+mecze[i]);
                }
            }
            //move to function
            rs2.close();

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return mecze;
    }

    public void createTournamentTerm(int id_t)
    {
        Statement stmt2 = null;
        Statement stmt3 = null;
        int numer=0;
        int nr_term=0;
        int nr_meczu=0;
        try {
            stmt = c.createStatement();
            stmt2 = c.createStatement();
            stmt3 = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select * from turniej4 where id_turniej="+id_t+";");

            while( rs.next() )
            {
                for(int i=1;i<4;i++)
                    for(int j=i+1;j<=4;j++)
                    {
                        nr_meczu++;
                        //System.out.println("insert into mecz (id_druzyna1,id_druzyna2) values ("+rs.getInt("id_druzyna"+i)+","+rs.getInt("id_druzyna"+j)+");");
                        stmt2.executeUpdate("insert into mecz (id_druzyna1,id_druzyna2) values ("+rs.getInt("id_druzyna"+i)+","+rs.getInt("id_druzyna"+j)+");");
                        ResultSet nr= stmt3.executeQuery("select * from mecz_id_mecz_seq;");
                        nr.next();
                        numer=nr.getInt("last_value");
                        if(i==1&&j==2)  //tylko w pierwszym przypadku
                        {
                            stmt2.executeUpdate("insert into terminarz4 (id_mecz1) values ("+numer+");");
                            nr= stmt3.executeQuery("select * from terminarz4_id_terminarz_seq;");
                            nr.next();
                            nr_term=nr.getInt("last_value");
                            stmt2.executeUpdate("update turniej4 set id_terminarz="+nr_term+" where id_turniej="+id_t+";");
                        }
                        else
                            stmt2.executeUpdate("update terminarz4 set id_mecz"+nr_meczu+"="+numer+" where id_terminarz="+nr_term+";");
                    }

            }

            rs.close();
            stmt3.close();
            stmt2.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public String getTeamName(int id_d)
    {
        String name="";
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM druzyna where id_druzyna="+id_d+";");

            while( rs.next() )
            {
                name=rs.getString("nazwa_druzyny");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return name;
    }
    public int getTeamRating(int id_d)
    {
        int rating=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM druzyna where id_druzyna="+id_d+";");

            while( rs.next() )
            {
                rating=rs.getInt("ocena");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return rating;
    }
    public Turniej4[] getAllTournaments()
    {
        int size=getTournamentsSize();
        int i=0;
        Turniej4[] tablicaTurniejow=new Turniej4[size];
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM turniej4;");

            while( rs.next() )
            {
                //int ileTeamow=0;
                tablicaTurniejow[i]=new Turniej4(rs.getInt("id_turniej"),rs.getString("nazwa"),rs.getInt("nagroda"));
                if(rs.getInt("id_druzyna1")>0)
                {
                    tablicaTurniejow[i].addTeam(rs.getInt("id_druzyna1"),getTeamName(rs.getInt("id_druzyna1")));
                }
                if(rs.getInt("id_druzyna2")>0)
                {
                    tablicaTurniejow[i].addTeam(rs.getInt("id_druzyna2"),getTeamName(rs.getInt("id_druzyna2")));
                }
                if(rs.getInt("id_druzyna3")>0)
                {
                    tablicaTurniejow[i].addTeam(rs.getInt("id_druzyna3"),getTeamName(rs.getInt("id_druzyna3")));
                }
                if(rs.getInt("id_druzyna4")>0)
                {
                    tablicaTurniejow[i].addTeam(rs.getInt("id_druzyna4"),getTeamName(rs.getInt("id_druzyna4")));
                }

                i++;
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return tablicaTurniejow;
    }
    public Zawodnik[] getAllPlayers()
    {
        int size=getPlayersSize();
        int i=0;
        Zawodnik[] tablicaGraczy=new Zawodnik[size];
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM zawodnik;");

            while( rs.next() )
            {
                //convert...
                double formaNorm = (Math.round(rs.getDouble("forma") * 100.0) / 100.0)*500-500;
                //next
                tablicaGraczy[i]=new Zawodnik(rs.getInt("id_zawodnik"),rs.getString("nazwa"),rs.getInt("ocena"),rs.getInt("cena"),rs.getInt("wiek"),rs.getString("org_druzyna"),formaNorm);
                i++;
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return tablicaGraczy;
    }
    public Zawodnik[] getQueriedPlayers(String cL,String cH,String oL,String oH,String nick,String team)
    {
        int size=0;
        if(!cL.isEmpty()||!cH.isEmpty()||!oL.isEmpty()||!oH.isEmpty()||!nick.isEmpty()||!team.isEmpty())
            size=getQuerriedPlayersSize(cL,cH,oL,oH,nick,team);
        else
            size=getPlayersSize();

        int i=0;
        String query="select * from zawodnik ";
        int count=0;
        if(!cL.isEmpty())
        {
            if((count++)>0)
                query+="and";
            else
                query+="where ";
            query+=(" cena>="+cL+" ");
        }
        if(!cH.isEmpty())
        {
            if((count++)>0)
                query+="and";
            else
                query+="where ";
            query+=(" cena<="+cH+" ");
        }
        if(!oL.isEmpty())
        {
            if((count++)>0)
                query+="and";
            else
                query+="where ";
            query+=(" ocena>="+oL+" ");
        }
        if(!oH.isEmpty())
        {
            if((count++)>0)
                query+="and";
            else
                query+="where ";
            query+=(" ocena<="+oH+" ");
        }
        if(!nick.isEmpty())
        {
            if((count++)>0)
                query+="and";
            else
                query+="where ";
            query+=(" LOWER(nazwa)LIKE LOWER('%"+nick+"%') ");
        }
        if(!team.isEmpty())
        {
            if((count++)>0)
                query+="and";
            else
                query+="where ";
            query+=(" LOWER(org_druzyna) LIKE LOWER('%"+team+"%') ");
        }
        query+=";";

        Zawodnik[] tablicaGraczy=new Zawodnik[size];
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while( rs.next() )
            {
                //convert...
                double formaNorm = (Math.round(rs.getDouble("forma") * 100.0) / 100.0)*500-500;
                tablicaGraczy[i]=new Zawodnik(rs.getInt("id_zawodnik"),rs.getString("nazwa"),rs.getInt("ocena"),rs.getInt("cena"),rs.getInt("wiek"),rs.getString("org_druzyna"),formaNorm);
                i++;
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return tablicaGraczy;
    }
    public Zawodnik[] getMarketOffers(int me_id)
    {
        int size=getMarketSize(me_id);
        int i=0;
        Zawodnik[] tablicaGraczy=new Zawodnik[size];
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select k.id_karta,k.id_zawodnik,z.nazwa,z.ocena,r.cena,z.wiek,g.nazwa_gracza from rynek r,karta_zawodnika k,zawodnik z,gracz g where r.id_karta=k.id_karta and k.id_zawodnik=z.id_zawodnik and g.id_gracz=k.id_gracz and k.id_gracz!="+me_id+";");

            while( rs.next() )
            {
                tablicaGraczy[i]=new Zawodnik(rs.getInt("id_karta"),rs.getInt("id_zawodnik"),rs.getString("nazwa"),rs.getInt("ocena"),rs.getInt("cena"),rs.getInt("wiek"),rs.getString("nazwa_gracza"));
                i++;
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return tablicaGraczy;
    }
    public Zawodnik[] getMyMarketOffers(int me_id)
    {
        int size=getMyMarketSize(me_id);
        int i=0;
        Zawodnik[] tablicaGraczy=new Zawodnik[size];
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select k.id_karta,k.id_zawodnik,z.nazwa,z.ocena,r.cena,z.wiek,g.nazwa_gracza from rynek r,karta_zawodnika k,zawodnik z,gracz g where r.id_karta=k.id_karta and k.id_zawodnik=z.id_zawodnik and g.id_gracz=k.id_gracz and k.id_gracz="+me_id+";");

            while( rs.next() )
            {
                tablicaGraczy[i]=new Zawodnik(rs.getInt("id_karta"),rs.getInt("id_zawodnik"),rs.getString("nazwa"),rs.getInt("ocena"),rs.getInt("cena"),rs.getInt("wiek"),rs.getString("nazwa_gracza"));
                i++;
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return tablicaGraczy;
    }
    public Zawodnik[] getBenchedPlayers(int me_id)
    {
        int size=getBenchedSize(me_id);
        int i=0;
        int id_k=0;
        Zawodnik[] tablicaGraczy=new Zawodnik[size];
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select k.id_karta,k.id_zawodnik,z.nazwa,z.ocena,z.cena,z.forma,z.org_druzyna from karta_zawodnika k left join rynek r on k.id_karta=r.id_karta right join zawodnik z on z.id_zawodnik=k.id_zawodnik where id_gracz="+me_id+" and id_druzyna is null and r.id_oferta is null;");

            while( rs.next() )
            {
                //convert...
                double formaNorm = (Math.round(rs.getDouble("forma") * 100.0) / 100.0)*500-500;
                tablicaGraczy[i]=new Zawodnik(rs.getInt("id_karta"),rs.getInt("id_zawodnik"),rs.getString("nazwa"),rs.getInt("ocena"),rs.getInt("cena"),formaNorm,rs.getString("org_druzyna"));
                i++;
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return tablicaGraczy;
    }
    public void addPlayer(int id_z,int me_id,int budzet)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("insert into karta_zawodnika(id_gracz,id_zawodnik) values ("+me_id+","+id_z+");");
            stmt.executeUpdate("update gracz set budzet="+budzet+"where id_gracz="+me_id+";");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public int chceckIsPlaysInTournament(int id_t,int id_g)
    {
        System.out.println("DANE: "+id_t+", "+id_g);
        int size=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select count(*) from turniej4 t,druzyna d where id_turniej="+id_t+" and id_gracz="+id_g+" and " +
                    "(t.id_druzyna1=d.id_druzyna or t.id_druzyna2=d.id_druzyna or t.id_druzyna3=d.id_druzyna or t.id_druzyna4=d.id_druzyna);");

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return size;
    }
    public void acceptBuyOffer(int id_k,int me_id,int budzet,int wartosc)
    {
        int id_seller=getSeller(id_k);
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("begin;");
            stmt.executeUpdate("update gracz set budzet=budzet+"+wartosc+" where id_gracz="+id_seller+";");
            stmt.executeUpdate("update karta_zawodnika set id_gracz="+me_id+",id_druzyna=NULL where id_karta="+id_k+";");
            stmt.executeUpdate("update gracz set budzet="+budzet+"where id_gracz="+me_id+";");
            stmt.executeUpdate("delete from rynek where id_karta="+id_k+";");
            stmt.executeUpdate("commit;");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public void addPlayerToTeam(int id_k,int id_d)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("update karta_zawodnika set id_druzyna="+id_d+"where id_karta="+id_k+";");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public void addTournament(String name,int price)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("insert into turniej4 (nazwa,nagroda) values ('"+name+"',"+price+");");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public void removeTeam(int id_d)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("delete from druzyna where id_druzyna="+id_d+";");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public void removeOffer(int id_k)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("delete from rynek where id_karta="+id_k+";");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public boolean checkPlayers(int id_d,int id_z)
    {
        int ilosc=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select count(*) from karta_zawodnika where id_druzyna="+id_d+" and id_zawodnik="+id_z+";");

            while( rs.next() )
            {
                ilosc=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        if(ilosc==0)
            return true;
        else
            return false;
    }
    public int getTeamSize(int id_d)
    {
        int size=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select count(*) from karta_zawodnika where id_druzyna="+id_d+";");

            while( rs.next() )
            {
                size=rs.getInt("count");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        System.out.println("ilosc graczy: "+size);
        return size;
    }
    public void removePlayerFromTeam(int id_k)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("update karta_zawodnika set id_druzyna=NULL where id_karta="+id_k+";");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public void replacePlayers(Druzyna d)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("update karta_zawodnika set id_druzyna=NULL where id_druzyna="+d.id_druzyna+";");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public void quickSellPlayer(int me_id,int id_k,int budzet)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("update gracz set budzet="+budzet+"where id_gracz="+me_id+";");
            stmt.executeUpdate("delete from karta_zawodnika where id_karta="+id_k+";");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public void addOffer(int id_k,int cena)
    {
        try {
            stmt = c.createStatement();
            stmt.executeUpdate("insert into rynek(id_karta,cena) values ("+id_k+","+cena+");");

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
    public void addScore(int id_d,int id_m,String score,int nagroda)
    {
        int currMoney=0;
        int id_gracz=0;
        Statement stmt2 = null;
        try {
            stmt = c.createStatement();
            stmt2 = c.createStatement();
            stmt.executeUpdate("update mecz set wynik='"+score+"', nagroda="+nagroda+" where id_mecz="+id_m+";");

            ResultSet rs = stmt.executeQuery( "select id_gracz from druzyna where id_druzyna="+id_d+";");

            while( rs.next() )
            {
                id_gracz=rs.getInt("id_gracz");
            }
            rs.close();

            rs = stmt.executeQuery("select * from gracz where id_gracz="+id_gracz+";");
            while( rs.next() )
            {

                currMoney=rs.getInt("budzet");
                currMoney+=nagroda;
                stmt2.executeUpdate("update gracz set budzet="+currMoney+"where id_gracz="+id_gracz+";");
            }
            rs.close();

            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        //return nagroda;
    }
    public Player loginPlayer(String name,String pass)
    {
        Player p=null;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM gracz where nazwa_gracza='"+name+"'and haslo='"+pass+"';" );

            if ( rs.next() )
            {
                p=new Player(name,pass,rs.getInt("budzet"),rs.getInt("id_gracz"));
            }

            rs.close();
            stmt.close();


        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return p;
    }
    public Zawodnik[] getTeamPlayers(int id_d)
    {
        int size=getTeamSize(id_d);
        int i=0;
        Zawodnik[] tablicaGraczy=new Zawodnik[size];
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select k.id_karta,k.id_zawodnik,z.nazwa,z.ocena,z.cena,z.forma,z.org_druzyna from karta_zawodnika k left join rynek r on k.id_karta=r.id_karta right join zawodnik z on z.id_zawodnik=k.id_zawodnik where k.id_druzyna="+id_d+";");

            while( rs.next() )
            {
                //convert...
                double formaNorm = (Math.round(rs.getDouble("forma") * 100.0) / 100.0)*500-500;
                tablicaGraczy[i]=new Zawodnik(rs.getInt("id_karta"),rs.getInt("id_zawodnik"),rs.getString("nazwa"),rs.getInt("ocena"),rs.getInt("cena"),formaNorm,rs.getString("org_druzyna"));
                i++;
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return tablicaGraczy;
    }
    public Druzyna getTeamInfo(String name,int id_me)                       ///to delete !!!
    {
        Druzyna d=new Druzyna();
        try {
            stmt = c.createStatement();
            int first=0;
            ResultSet rs = stmt.executeQuery( "select d.id_druzyna,id_karta,z.id_zawodnik,z.forma,d.id_gracz,d.ocena as ocenaDruzyny,z.ocena as ocenaZawodnika,cena,wiek,org_druzyna,nazwa from karta_zawodnika k,druzyna d,zawodnik z where d.id_gracz="+id_me+"and d.nazwa_druzyny='"+name+"'and k.id_druzyna=d.id_druzyna and k.id_zawodnik=z.id_zawodnik;");
            // select * from karta_zawodnika k,druzyna d,zawodnik z where d.nazwa_druzyny='Janusze Marcina' and k.id_druzyna=d.id_druzyna and k.id_zawodnik=z.id_zawodnik;
            while( rs.next() )
            {
                int id_druzyna=rs.getInt("id_druzyna");
                int id_karta=rs.getInt("id_karta");
                int id_zawodnik=rs.getInt("id_zawodnik");
                int id_gracz=rs.getInt("id_gracz");
                int ocenaD=rs.getInt("ocenaDruzyny");
                int ocenaZ=rs.getInt("ocenaZawodnika");
                int cenaZ=rs.getInt("cena");
                //convert...
                double formaNorm = (Math.round(rs.getDouble("forma") * 100.0) / 100.0)*500-500;
                String org_druzyna=rs.getString("org_druzyna");
                String nazwaZ=rs.getString("nazwa");
                System.out.println(id_druzyna+" "+id_karta+" "+id_zawodnik+" "+id_gracz+" "+ocenaD+" "+ocenaZ);

                if((first++)==0)
                    d.setAll(id_druzyna,id_gracz,ocenaD,name);
                d.dodajZawodnika(new Zawodnik(id_karta,id_zawodnik,nazwaZ,ocenaZ,cenaZ,formaNorm,org_druzyna));
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return d;
    }
    public String getTeams(int id_gracz)
    {
        String data="";
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "SELECT * FROM druzyna where id_gracz='"+id_gracz+"';");

            while( rs.next() )
            {
                data+=rs.getString("nazwa_druzyny")+","+rs.getInt("ocena")+","+rs.getInt("id_druzyna")+",";
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return data;
    }
    public int getMyMoney(int me_id)
    {
        int money=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select budzet from gracz where id_gracz="+me_id+";");

            while( rs.next() )
            {
                money=rs.getInt("budzet");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return money;
    }
    public int getTournamentTerm(int id_t)
    {
        int id_term=0;
        try {
            stmt = c.createStatement();
            ResultSet rs = stmt.executeQuery( "select * from turniej4 where id_turniej="+id_t+";");

            while( rs.next() )
            {
                id_term=rs.getInt("id_terminarz");
            }

            rs.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return id_term;
    }
    public Scoreboard[] calculateScoreboard(int id_t)
    {
        int id_term=getTournamentTerm(id_t);
        Scoreboard sb[]=new Scoreboard[4];

        try {
            stmt = c.createStatement();
            Statement stmt2 = c.createStatement();
            Statement stmt3 = c.createStatement();
            Statement stmt4 = c.createStatement();
            ResultSet rs=null;
            ResultSet rs2=null;
            ResultSet rs3=null;
            ResultSet rs4=null;
            int i=0;
            int id_d_tab[]=new int[4];
            int prize=0;
            rs = stmt.executeQuery( "select * from turniej4 where id_turniej="+id_t+";");
            while( rs.next() )
            {
                prize=rs.getInt("nagroda");
                sb[i++]=new Scoreboard(getTeamName(rs.getInt("id_druzyna1")),rs.getInt("id_druzyna1"));
                sb[i++]=new Scoreboard(getTeamName(rs.getInt("id_druzyna2")),rs.getInt("id_druzyna2"));
                sb[i++]=new Scoreboard(getTeamName(rs.getInt("id_druzyna3")),rs.getInt("id_druzyna3"));
                sb[i++]=new Scoreboard(getTeamName(rs.getInt("id_druzyna4")),rs.getInt("id_druzyna4"));
            }
            rs.close();

            String Sscore[]=new String[2];
            int score[]=new int[2];
            int tmp=0;
            int ile=0;
            rs3 = stmt3.executeQuery("select * from terminarz4 where id_terminarz=" + id_term + ";");
            try{
            while( rs3.next() )
            {
                for(i=1;i<7;i++)
                {
                    rs2=stmt2.executeQuery("select * from mecz where id_mecz="+rs3.getInt("id_mecz"+i)+";");
                    rs2.next();
                    if(rs2.getString("wynik")!=null)
                    {
                        ile++;
                        Sscore=rs2.getString("wynik").split(" : ");
                        score[0]=Integer.parseInt(Sscore[0]);
                        score[1]=Integer.parseInt(Sscore[1]);
                        System.out.println(score[0]+" vs "+score[1]);
                        //dodawanie
                        if(score[0]>score[1])
                        {
                            //win
                            tmp=rs2.getInt("id_druzyna1");
                            for(int j=0;j<4;j++)
                            {
                                if(sb[j].id_d==tmp)
                                {
                                    sb[j].m+=1;
                                    sb[j].pkt+=3;
                                    sb[j].w+=1;
                                    break;
                                }
                            }
                            //lose
                            tmp=rs2.getInt("id_druzyna2");
                            for(int j=0;j<4;j++)
                            {
                                if(sb[j].id_d==tmp)
                                {
                                    sb[j].m+=1;
                                    sb[j].l+=1;
                                    break;
                                }
                            }
                        }
                        else
                        {
                            //win
                            tmp=rs2.getInt("id_druzyna2");
                            for(int j=0;j<4;j++)
                            {
                                if(sb[j].id_d==tmp)
                                {
                                    sb[j].m+=1;
                                    sb[j].pkt+=3;
                                    sb[j].w+=1;
                                    break;
                                }
                            }
                            //lose
                            tmp=rs2.getInt("id_druzyna1");
                            for(int j=0;j<4;j++)
                            {
                                if(sb[j].id_d==tmp)
                                {
                                    sb[j].m+=1;
                                    sb[j].l+=1;
                                    break;
                                }
                            }
                        }
                    }
                }
                System.out.println("ile:"+ile);
                if(ile==6)
                {
                    int winner=0;
                    for(i=1;i<4;i++)
                        if(sb[winner].pkt<sb[i].pkt)
                            winner=i;
                    System.out.println("wygrywa "+sb[winner].name);

                    int currMoney=0;
                    int teamManager=getTeamManager(sb[winner].id_d);
                    rs4 = stmt3.executeQuery("select * from gracz where id_gracz="+teamManager+";");
                    while( rs4.next() )
                    {

                        currMoney=rs4.getInt("budzet");
                        currMoney+=prize;
                        stmt4.executeUpdate("update gracz set budzet="+currMoney+"where id_gracz="+teamManager+";");
                        stmt4.executeUpdate("update turniej4 set nagroda=0 where id_turniej="+id_t+";");
                    }
                    rs4.close();
                }
            }
            }catch(Exception ex)
            {
                System.out.println("cos");
            }
            rs2.close();
            rs3.close();
            stmt.close();
            stmt2.close();
            stmt3.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        return sb;
    }
    public void updatePlayer(String name,String team,double rating,double form)
    {
        Random generator = new Random();
        int rand = generator.nextInt(300)-150;      //[-150 : 150]

        double inGameRating=rating*70/0.94;
        double price=100000*Math.log10(rating+0.12)+rand;

        try {
            stmt = c.createStatement();
            stmt.executeUpdate("update zawodnik set ocena="+inGameRating+", cena="+price+",org_druzyna='"+team+"',forma="+form+" where nazwa='"+name+"';");
            //do tworzenia jest to //stmt.executeUpdate("insert into zawodnik (ocena,cena,org_druzyna,nazwa) values ("+inGameRating+","+price+",'"+team+"','"+name+"');");
            stmt.close();
            /*
            * Na wypadek pojawienia sie nowego gracza w druzynie
            *
            * IF EXISTS (SELECT * FROM Table1 WHERE Column1='SomeValue')
            UPDATE Table1 SET (...) WHERE Column1='SomeValue'
            ELSE
            INSERT INTO Table1 VALUES (...)
            *
            * */
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
        //System.out.println("insert into zawodnik (ocena,cena,org_druzyna,nazwa) values ("+inGameRating+","+price+",'"+team+"','"+name+"');");
    }
    public void dc()
    {
        try {
            c.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println(e.getClass().getName()+": "+e.getMessage());
            System.exit(0);
        }
    }
}
