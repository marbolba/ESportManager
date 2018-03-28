package sample;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.PrintWriter;

public class WebScrapper
{
    //zasubskrybowane teamy
    static String teams[]={"SK","FaZe","Cloud9","mousesports","fnatic","Liquid","Natus Vincere","Astralis","G2","NiP","Heroic","North","Gambit","HellRaisers","Space Soldiers","Virtus.pro","AGO"};
    public String topTeams[];
    //public static void main(String[] args){}
    WebScrapper(String linkToTeamsRanking)
    {
        getPlayerRating(550);
        if(!linkToTeamsRanking.isEmpty())
            getTeamsRating(20,linkToTeamsRanking);
    }
    static void getPlayerRating(int size)
    {
        Document doc;
        String name;
        String team;
        double rating;

        try{
            doc= Jsoup.connect("https://www.hltv.org/stats/players?startDate=2018-02-13&endDate=2018-03-13").get();
            PrintWriter out = new PrintWriter("zawodnikRating.txt");
            for(int i=1;i<size;i++)
            {
                team=doc.select(".stats-table > tbody:nth-child(2) > tr:nth-child("+i+") > td:nth-child(2)").text();    //team
                if(checkTeam(team))             //sprawdza czy jest to subskrybowana druzyna
                {
                    name=doc.select(".stats-table > tbody:nth-child(2) > tr:nth-child("+i+") > td:nth-child(1)").text();    //imie
                    rating=Double.parseDouble(doc.select(".stats-table > tbody:nth-child(2) > tr:nth-child("+i+") > td:nth-child(6)").text());  //rating
                    System.out.println("n:"+name+" t:"+team+" r:"+rating);

                    //save to file
                    out.println(name+","+team+","+rating);
                }
            }
            out.close();

        }catch (Exception e)
        {
            System.out.println("BLAD danych");
        }
    }
    void getTeamsRating(int size,String linkToTeamsRanking)
    {
        Document doc;
        String name;
        int pos;
        topTeams=new String[size];

        try{
            doc= Jsoup.connect(linkToTeamsRanking).get();
            //PrintWriter out = new PrintWriter("teamForm.txt");
            for(int i=4;i<size+4;i++)     //4 to 1
            {
                pos=Integer.parseInt(doc.select("div.ranked-team:nth-child("+i+") > div:nth-child(1) > div:nth-child(1) > span:nth-child(1)").text().substring(1));    //team
                name=doc.select("div.ranked-team:nth-child("+i+") > div:nth-child(1) > div:nth-child(1) > span:nth-child(3)").text();
                topTeams[i-4]=name;
                //System.out.println(pos+" - "+topTeams[i-4]);
            }
            //out.close();
        }catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    static boolean checkTeam(String team)
    {
        for(String t:teams)
        {
            if(team.equals(t))
                return true;
        }
        return false;
    }
    String[] getTopTeams()
    {
        return topTeams;
    }
}
