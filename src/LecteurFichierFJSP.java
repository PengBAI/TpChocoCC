import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

public class LecteurFichierFJSP {

    protected String nomFic;
    public int[] DateDeDebJobs;  
    public int[] DateDeFinJobs;  
    public int[] Ressources;  
    protected int NbJobs;
    protected int NbRessources;
    
    public LecteurFichierFJSP(String nFic) {
        nomFic = nFic;
        DateDeDebJobs = null;
        DateDeFinJobs = null;
        Ressources = null;
        NbJobs=0;
        NbRessources=0;
    }

    public int GetNbJobs() 
    {
    	return NbJobs;
    }

    public int GetNbRessources() 
    {
    	return NbRessources;
    }

    public int[] GetDateDeDebutJobs() 
    {
    	return DateDeDebJobs;
    }

    public int[] GetDateDeFinJobs() 
    {
    	return DateDeFinJobs;
    }

    public int[] GetDateEmbauche() 
    {
    	return Ressources;
    }

    public void convert() {
        try {
            BufferedReader in = new BufferedReader(new FileReader(nomFic)); // ouvre le fichier
            String line;
            line = in.readLine() ;
            if (line != null)
            {
            	NbJobs=Integer.parseInt(line);
//            	System.out.println("NbJobs "+NbJobs+" ");
            	DateDeDebJobs = new int[NbJobs];
            	DateDeFinJobs = new int[NbJobs];
            	
            	for (int j=0; j<NbJobs; j++)
            	{
            		line = in.readLine();
            		StringTokenizer st = new StringTokenizer(line, " ");
            		DateDeDebJobs[j] = Integer.parseInt(st.nextToken());  //renvoie la string sur laquelle on pointe et passe à la suivante
            		DateDeFinJobs[j] = Integer.parseInt(st.nextToken());
            	}
//        		line = in.readLine();
//            	NbRessources=Integer.parseInt(line);
////            	System.out.println("NbRessources "+NbJobs+" ");
//            	Ressources = new int[NbRessources];
//            	
//        		line = in.readLine();
//        		StringTokenizer st = new StringTokenizer(line, " ");
//            	for (int j=0; j<NbRessources; j++)
//            	{
//            		Ressources[j] = Integer.parseInt(st.nextToken());  //renvoie la string sur laquelle on pointe et passe à la suivante
//            	}

            }
            else System.out.println("vide !!!");
            
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erreur lecture fichier dans LecteurFichier2");
        }

 /*       System.out.println("------------------------------------------------");
            for(int j=0; j<NbJobs; j++) System.out.print(DateDeDebJobs[j]+" "+DureeJobs[j]+" ");
            System.out.println();
        for(int j=0; j<NbRessources; j++) System.out.print(Ressources[j]+" ");
            System.out.println();
        

        System.out.println();
        System.out.println("------------------------------------------------");*/


 //       return instance;
    }
}