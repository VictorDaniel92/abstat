package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.OverallDatatypeRelationsCounting;
import it.unimib.disco.summarization.dataset.ParallelProcessing;
import it.unimib.disco.summarization.experiments.Pattern;
import it.unimib.disco.summarization.experiments.Concept;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class ProcessDatatypeRelationAssertions {

	static boolean tmp = false;
	static int count1 = 0;
	static int count2 = 0;

	public static void main(String[] args) throws Exception {

		Events.summarization();

		File sourceDirectory = new File(args[0]);
		File minimalTypesDirectory = new File(args[1]);
		File datatypes = new File(new File(args[2]), "count-datatype.txt");
		File properties = new File(new File(args[2]), "count-datatype-properties.txt");
		File akps = new File(new File(args[2]), "datatype-akp.txt");

		OverallDatatypeRelationsCounting counts = new OverallDatatypeRelationsCounting(datatypes, properties, akps,
				minimalTypesDirectory);

		new ParallelProcessing(sourceDirectory, "_dt_properties.nt").process(counts);

		counts.endProcessing();
		readTriplesAKPsEq("datatype-akp.txt");
		stampaSuFile("datatype-akp.txt");
	}

	// leggo il file
	public static void readTriplesAKPsEq(String AKPsFile) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(AKPsFile)));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					line = line.substring(1, line.length() - 1);
					String[] stringAKPs = line.split(", ");
					Pattern[] AKPs = new Pattern[stringAKPs.length];

					// creo l'array con le triple
					for (int i = 0; i < stringAKPs.length; i++) {
						String[] splitted = stringAKPs[i].split("##");
						String s = splitted[0];
						String p = splitted[1];
						String o = splitted[2];
						String f = splitted[3];
						// TODO aggiungere frequence a pattern;
						AKPs[i] = new Pattern(new Concept(s), p, new Concept(o), f);
					}
					// creo una copia dell'array di prima
					Pattern[] AKPs2 = new Pattern[AKPs.length];
					System.arraycopy(AKPs, 0, AKPs2, 0, AKPs.length);

					// ciclo sui due array per trovare pattern uguali: se trovo
					// triple che hanno predicato ed oggetto uguali controllo se
					// il soggetto
					// è equivalente al soggetto della seconda tripla
					for (int i = 0; i < AKPs.length; i++) {
						for (int j = 0; i < AKPs2.length; j++) {
							// soggetti equivalenti
							if (AKPs[i].getObj() == AKPs2[j].getObj() && AKPs[i].getPred() == AKPs2[j].getPred()) {
								tmp = false;
								checkEqu(AKPs[i].getSubj(), AKPs[j].getSubj(), tmp);
							} // se le condizioni appena descritte si verificano
								// cancello la tripla con la frequenza più bassa
							if (tmp = true) {
								if (Integer.parseInt(AKPs[i].getFreqTri()) > Integer.parseInt(AKPs2[j].getFreqTri())) {
									AKPs2[j].setDelete(true);
								} else {
									AKPs[i].setDelete(true);
								} // se la frequenza dei pattern è uguale allora
									// si conta il numero di volte che i
									// concetti equivalenti compaiono nelle
									// triple e si cancella la tripla con il
									// concetto che appare meno volte
								if (Integer.parseInt(AKPs[i].getFreqTri()) == Integer.parseInt(AKPs2[j].getFreqTri())) {
									for (int z = 0; i < stringAKPs.length; z++)
										if (AKPs[z].getSubj() == AKPs[i].getSubj()) {
											count1++;
										} else if (AKPs[z].getSubj() == AKPs2[j].getSubj()) {
											count2++;
										}
								}
								if (count1 > count2) {
									AKPs2[j].setDelete(true);
								} else {
									AKPs[i].setDelete(true);
								}
							}
							// oggetti equivalenti
							if (AKPs[i].getSubj() == AKPs2[j].getSubj() && AKPs[i].getPred() == AKPs2[j].getPred()) {
								tmp = false;
								checkEqu(AKPs[i].getObj(), AKPs[j].getObj(), tmp);
							} // se le condizioni appena descritte si verificano
								// cancello la tripla con la frequenza più bassa
							if (tmp = true) {
								if (Integer.parseInt(AKPs[i].getFreqTri()) > Integer.parseInt(AKPs2[j].getFreqTri())) {
									AKPs2[j].setDelete(true);
								} else {
									AKPs[i].setDelete(true);
								} // se la frequenza dei pattern è uguale allora
									// si conta il numero di volte che i
									// concetti equivalenti compaiono nelle
									// triple e si cancella la tripla con il
									// concetto che appare meno volte
								if (Integer.parseInt(AKPs[i].getFreqTri()) == Integer.parseInt(AKPs2[j].getFreqTri())) {
									for (int z = 0; i < stringAKPs.length; z++)
										if (AKPs[z].getSubj() == AKPs[i].getSubj()) {
											count1++;
										} else if (AKPs[z].getSubj() == AKPs2[j].getSubj()) {
											count2++;
										}
								}
								if (count1 > count2) {
									AKPs2[j].setDelete(true);
								} else {
									AKPs[i].setDelete(true);
								}
							}
						}
					} // se il soggetto e l’oggetto sono concetti equivalenti
						// cancello il pattern
					for (int i = 0; i < stringAKPs.length; i++) {
						if (checkEqu(AKPs[i].getSubj(), AKPs[i].getObj(), tmp)) {
							AKPs[i].setDelete(true);
						}
						br.close();
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	// metodo ausiliario per il controllo dell'equivalenza: creo un array con
	// tutte le classi di equivalenza leggendo il file subClassOf.txt
	public static boolean checkEqu(Concept concept, Concept concept2, boolean tmp) {
		try {
			String Equivalence = null;
			BufferedReader br = new BufferedReader(new FileReader(new File(Equivalence)));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					line = line.substring(1, line.length() - 1);
					String[] stringAKPs = line.split(", ");
					Pattern[] EqConcepts = new Pattern[stringAKPs.length];

					for (int i = 0; i < stringAKPs.length; i++) {
						String[] splitted = stringAKPs[i].split("##");
						String a = splitted[0];
						String b = splitted[1];
						EqConcepts[i] = new Pattern(new Concept(a), new Concept(b));
					}

					// se il soggetto del primo pattern compare come primo
					// elemento e il
					// soggetto del secondo elemento compare come secondo
					// elemento o
					// viceversa nell'array
					// EqConcepts vuol dire che sono equivalenti e quindi
					// ritorno true
					// altrimenti falso
					for (int i = 0; i < EqConcepts.length; i++) {
						if (concept == EqConcepts[i].getSubj() && concept2 == EqConcepts[i].getObj()
								|| concept == EqConcepts[i].getObj() && concept2 == EqConcepts[i].getSubj()) {
							return true;
						} else
							return false;
					}
					br.close();
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return false;
	}
	
	public static void stampaSuFile(String nomeFile){
		try{
			FileOutputStream fos = new FileOutputStream(new File(nomeFile));
			Set<Pattern> patterns = new HashSet<Pattern>();
			for (Pattern pattern : patterns)    
				fos.write( (pattern.getSubj().getURI()+"##"+pattern.getPred()+"##"+pattern.getObj().getURI()+"##"+ pattern.getFreqTri()+"\n").getBytes()  );
			fos.close();
		}
		catch(Exception e){
			System.out.println("Eccezione stampaSuFile");
		}
	}
}