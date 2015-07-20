package it.unimib.disco.summarization.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class InternalExternalDatatypeAkp 
{
	public static void main(String[] args) throws FileNotFoundException, IOException
	{
		String pathFile = args[0];
		String dataset = args[1];
		String payLevelDomain = args[2];
		
		datatypeAkpInternalExternal(pathFile,dataset,payLevelDomain);
	}
	
	private static void datatypeAkpInternalExternal(String pathFile, String dataset, String payLevelDomain) throws FileNotFoundException, IOException
	{
		/*Per leggere da file .txt l'input.*/
		String fileDatatypeAkpsPath = pathFile;
		BufferedReader brDatatypeAkps = new BufferedReader(new FileReader(fileDatatypeAkpsPath));
		
		/*Per scrivere su file .txt l'output.*/
		FileWriter fwDatatypeAkps = new FileWriter("../data/summaries/"+dataset+"/patterns/datatype-akp-new.txt");
		BufferedWriter bwDatatypeAkps = new BufferedWriter(fwDatatypeAkps);
		
		/*Cuore dell'Algoritmo.*/
		boolean trovatoPrimoDoppioCancelletto = false;
		boolean trovatoSecondoDoppioCancelletto = false;
		boolean trovatoTerzoDoppioCancelletto = false;
		String lineRead = null;
		String subjectDatatypeAkp = "";
		String propertyDatatypeAkp = "";
		String objectDatatypeAkp = "";
		String numberOfInstances = "";
		String typeOfDatatypeAkp = "";
		
		lineRead = brDatatypeAkps.readLine();
		
		while (lineRead != null)
		{
			for (int i = 0; i < lineRead.length()-4; i++)
			{
				if (trovatoPrimoDoppioCancelletto == false) 
				{
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) != '#'))
					{
						subjectDatatypeAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) != '#') && (lineRead.charAt(i+1) == '#'))
					{
						subjectDatatypeAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) != '#'))
					{
						subjectDatatypeAkp += lineRead.charAt(i);
					}
					if ((lineRead.charAt(i) == '#') && (lineRead.charAt(i+1) == '#'))
					{
						trovatoPrimoDoppioCancelletto = true;
					}
				}
				
				if (trovatoPrimoDoppioCancelletto == true)
				{
					if (trovatoSecondoDoppioCancelletto == false)
					{
						if ((lineRead.charAt(i+2) != '#') && (lineRead.charAt(i+3) != '#'))
						{
							propertyDatatypeAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) != '#') && (lineRead.charAt(i+3) == '#'))
						{
							propertyDatatypeAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) == '#') && (lineRead.charAt(i+3) != '#'))
						{
							propertyDatatypeAkp += lineRead.charAt(i+2);
						}
						if ((lineRead.charAt(i+2) == '#') && (lineRead.charAt(i+3) == '#'))
						{
							trovatoSecondoDoppioCancelletto = true;
						}
					}
					if (trovatoSecondoDoppioCancelletto == true)
					{
						if (trovatoTerzoDoppioCancelletto == false)
						{
							if ((lineRead.charAt(i+4) != '#') && (lineRead.charAt(i+5) != '#'))
							{
								objectDatatypeAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) != '#') && (lineRead.charAt(i+5) == '#'))
							{
								objectDatatypeAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) == '#') && (lineRead.charAt(i+5) != '#'))
							{
								objectDatatypeAkp += lineRead.charAt(i+4);
							}
							if ((lineRead.charAt(i+4) == '#') && (lineRead.charAt(i+5) == '#'))
							{
								trovatoTerzoDoppioCancelletto = true;
							}
						}
						if (trovatoTerzoDoppioCancelletto == true)
						{
							if (lineRead.charAt(i+4) != '#')
							{
								numberOfInstances += lineRead.charAt(i+4);
							}
						}
					}
				}
			}
			
			/*Un akp è definito 'interno' se sia il soggetto sia l'oggetto dell'akp provengono da "http://dbpedia.org". Altrimenti è definito 'esterno'.*/
			if ((subjectDatatypeAkp.contains(payLevelDomain)) && (objectDatatypeAkp.contains(payLevelDomain)))
			{
				typeOfDatatypeAkp = "internalDatatypeAkp";
			}
			else
			{
				typeOfDatatypeAkp = "externalDatatypeAkp";
			}
			
			bwDatatypeAkps.write(subjectDatatypeAkp);
			bwDatatypeAkps.write("##");
			bwDatatypeAkps.write(propertyDatatypeAkp);
			bwDatatypeAkps.write("##");
			bwDatatypeAkps.write(objectDatatypeAkp);
			bwDatatypeAkps.write("##");
			bwDatatypeAkps.write(numberOfInstances);
			bwDatatypeAkps.write("##");
			bwDatatypeAkps.write(typeOfDatatypeAkp);
			bwDatatypeAkps.write("\n");
			
			subjectDatatypeAkp = "";
			propertyDatatypeAkp = "";
			objectDatatypeAkp = "";
			numberOfInstances = "";
			typeOfDatatypeAkp = "";
			trovatoPrimoDoppioCancelletto = false;
			trovatoSecondoDoppioCancelletto = false;
			trovatoTerzoDoppioCancelletto = false;
			
			lineRead = brDatatypeAkps.readLine();
		}
		
		/*Chiudo le connessioni con i file.*/
		brDatatypeAkps.close();
		bwDatatypeAkps.close();
		fwDatatypeAkps.close();
	}
}