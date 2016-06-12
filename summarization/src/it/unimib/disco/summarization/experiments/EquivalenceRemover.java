package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.HashSet;
import java.util.Set;

public class EquivalenceRemover {

	static boolean tmp = false;
	static int count1 = 0;
	static int count2 = 0;

	private EquivalenceRemover() {
	}

	// leggo il file
	public static void readTriplesAKPsEq(String aKPsFile) {

		try {
			BufferedReader br = new BufferedReader(new FileReader(new File(aKPsFile)));
			String line;
			while ((line = br.readLine()) != null) {
				if (!"".equals(line)) {
					line = line.substring(1, line.length() - 1);
					String[] stringAKPs = line.split(", ");
					Pattern[] aKPs = new Pattern[stringAKPs.length];

					// creo l'array con le triple
					Pattern[] aKPs2 = saveTriple(stringAKPs, aKPs);

					// ciclo sui due array per trovare pattern uguali: se trovo
					// triple che hanno predicato ed oggetto uguali controllo se
					// il soggetto
					// è equivalente al soggetto della seconda tripla
					findEquivalenceTriple(stringAKPs, aKPs, aKPs2); // se il
																	// soggetto
																	// e
																	// l’oggetto
																	// sono
																	// concetti
																	// equivalenti
					// cancello il pattern
					findSubjectObjectTriple(stringAKPs, aKPs);
				}
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	private static void findSubjectObjectTriple(String[] stringAKPs, Pattern[] aKPs) {
		for (int i = 0; i < stringAKPs.length; i++) {
			if (checkEqu(aKPs[i].getSubj(), aKPs[i].getObj(), tmp)) {
				aKPs[i].setDelete(true);
			}
		}
	}

	private static void findEquivalenceTriple(String[] stringAKPs, Pattern[] aKPs, Pattern[] aKPs2) {
		for (int i = 0; i < aKPs.length; i++) {
			for (int j = 0; j < aKPs2.length; j++) {
				// soggetti equivalenti
				if (aKPs[i].getObj() == aKPs2[j].getObj() && aKPs[i].getPred() == aKPs2[j].getPred()) {
					tmp = false;
					checkEqu(aKPs[i].getSubj(), aKPs[j].getSubj(), tmp);
				} // se le condizioni appena descritte si verificano
					// cancello la tripla con la frequenza più bassa
				chooseTriple(stringAKPs, aKPs, aKPs2, i, j);
				// oggetti equivalenti
				if (aKPs[i].getSubj() == aKPs2[j].getSubj() && aKPs[i].getPred() == aKPs2[j].getPred()) {
					tmp = false;
					checkEqu(aKPs[i].getObj(), aKPs[j].getObj(), tmp);
				} // se le condizioni appena descritte si verificano
				chooseTriple(stringAKPs, aKPs, aKPs2, i, j);
			}
		}
	}

	private static Pattern[] saveTriple(String[] stringAKPs, Pattern[] aKPs) {
		for (int i = 0; i < stringAKPs.length; i++) {
			String[] splitted = stringAKPs[i].split("##");
			String s = splitted[0];
			String p = splitted[1];
			String o = splitted[2];
			String f = splitted[3];
			aKPs[i] = new Pattern(new Concept(s), p, new Concept(o), f);
		}
		// creo una copia dell'array di prima
		Pattern[] aKPs2 = new Pattern[aKPs.length];
		System.arraycopy(aKPs, 0, aKPs2, 0, aKPs.length);
		return aKPs2;
	}

	private static void chooseTriple(String[] stringAKPs, Pattern[] aKPs, Pattern[] aKPs2, int i, int j) {
		if (tmp) {
			if (Integer.parseInt(aKPs[i].getFreqTri()) < Integer.parseInt(aKPs2[j].getFreqTri())) {
				aKPs[i].setDelete(true);
			} // se la frequenza dei pattern è uguale allora
				// si conta il numero di volte che i
				// concetti equivalenti compaiono nelle
				// triple e si cancella la tripla con il
				// concetto che appare meno volte
			if (Integer.parseInt(aKPs[i].getFreqTri()) == Integer.parseInt(aKPs2[j].getFreqTri())) {
				for (int z = 0; z < stringAKPs.length; z++)
					if (aKPs[z].getSubj() == aKPs[i].getSubj()) {
						count1++;
					} else if (aKPs[z].getSubj() == aKPs2[j].getSubj()) {
						count2++;
					}
			}
			if (count1 < count2) {
				aKPs[i].setDelete(true);
			}
		}
	}

	// metodo ausiliario per il controllo dell'equivalenza: creo un array con
	// tutte le classi di equivalenza leggendo il file subClassOf.txt
	public static boolean checkEqu(Concept concept, Concept concept2, boolean tmp) {

		boolean tmp2 = tmp;
		String equivalence = null;

		try (BufferedReader br = new BufferedReader(new FileReader(new File(equivalence)))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!"".equals(line)) {
					line = line.substring(1, line.length() - 1);
					String[] stringAKPs = line.split(", ");
					Pattern[] eqConcepts = new Pattern[stringAKPs.length];

					for (int i = 0; i < stringAKPs.length; i++) {
						String[] splitted = stringAKPs[i].split("##");
						String a = splitted[0];
						String b = splitted[1];
						eqConcepts[i] = new Pattern(new Concept(a), new Concept(b));
					}

					// se il soggetto del primo pattern compare come primo
					// elemento e il
					// soggetto del secondo elemento compare come secondo
					// elemento o
					// viceversa nell'array
					// EqConcepts vuol dire che sono equivalenti e quindi
					// ritorno true
					// altrimenti falso
					for (int i = 0; i < eqConcepts.length; i++) {
						if (concept == eqConcepts[i].getSubj() && concept2 == eqConcepts[i].getObj()
								|| concept == eqConcepts[i].getObj() && concept2 == eqConcepts[i].getSubj()) {
							tmp2 = true;
							return tmp2;
						}
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return tmp2;
	}

	public static void stampaSuFile(String nomeFile) {
		try {
			FileOutputStream fos = new FileOutputStream(new File(nomeFile));
			Set<Pattern> patterns = new HashSet<>();
			for (Pattern pattern : patterns) {
				if (!pattern.isDelete()) {
					fos.write((pattern.getSubj().getURI() + "##" + pattern.getPred() + "##" + pattern.getObj().getURI()
							+ "##" + pattern.getFreqTri() + "\n").getBytes());
				}
			}
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException("Qualcosa non è andato con stampaSuFile " + e);
		}
	}
}
