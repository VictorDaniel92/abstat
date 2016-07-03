package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class EquivalenceRemover {

	private static final List<String> ConcettiEtichettati = new ArrayList<>();
	private static final List<Pattern> AKPS = new ArrayList<>();
	private static final List<Pattern> AKPS2 = new ArrayList<>();

	private EquivalenceRemover() {
	}

	/*
	 * Si apre un buffer in cui si memorizzano gli AKP dal file in input. Si
	 * Inizializza l'oggetto line che conterrà ad ogni iterazione l'AKP
	 * successivo. Nell'oggetto stringAKPs si inseriscono le 4 stringhe in
	 * stringAKPs che contengono soggetto, predicato, oggetto e frequenza
	 * dell'AKP. Adesso si salva nei parametri globali AKPS e AKPS2 di volta in
	 * volta l'AKP letto. Chiuso il buffer viene richiamato il metodo
	 * findPossibleEquivalenceAkp per iniziare a cercare gli AKP che contengono
	 * concetti equivalenti. Trovati tutti i concetti preferiti si stampano
	 * usando il metodo stampa.
	 */
	public static void readTriplesAKPsEq() {

		String[] stringAKPs = null;

		try {
			BufferedReader br = new BufferedReader(
					new FileReader(new File("/schema-summaries/data/summaries/system-test/patterns/datatype-akp.txt")));
			String line;
			while ((line = br.readLine()) != null) {
				if (!"".equals(line)) {
					line = line.substring(0, line.length());
					stringAKPs = line.split("##");
					String[] splitted = new String[4];
					splitted[0] = stringAKPs[0];
					splitted[1] = stringAKPs[1];
					splitted[2] = stringAKPs[2];
					splitted[3] = stringAKPs[3];
					AKPS.add(new Pattern(new Concept(splitted[0]), splitted[1], new Concept(splitted[2]), splitted[3]));
					AKPS2.add(
							new Pattern(new Concept(splitted[0]), splitted[1], new Concept(splitted[2]), splitted[3]));
				}
			}
			br.close();
			findPossibleEquivalenceAkp();
			// TODO vedere se mantenere o meno
			// findSubjectObjectTriple(stringAKPs, aKPs);
			stampa(ConcettiEtichettati);
		} catch (Exception e) {
			throw new RuntimeException("Qualcosa non  andato con stampaSuFile " + e);
		}
	}

	/*
	 * Si cicla su tutte gli AKP per vedere se ce ne sono di AKP che
	 * rispecchiano i parametri per essere controllati dal resto dell'algoritmo.
	 * Se si trovano due AKP che hanno predicato ed oggetto uguali fra loro o
	 * predicato e soggetto uguali fra loro si richiama il metodo checkEqu per
	 * controllare che il soggetto o l'oggetto rispettivamente siano equivalenti
	 */
	private static void findPossibleEquivalenceAkp() {
		for (int u = 0; u < AKPS.size(); u++) {
			for (int c = 0; c < AKPS2.size(); c++) {
				if (AKPS.get(u).getObj().getURI().equals(AKPS2.get(c).getObj().getURI())
						&& AKPS.get(u).getPred().equals(AKPS2.get(c).getPred())) {
					checkEqu(AKPS.get(u).getSubj().getURI(), AKPS2.get(c).getSubj().getURI(), AKPS.get(u).getFreqTri(),
							AKPS2.get(c).getFreqTri());
				}
				if (AKPS.get(u).getSubj().getURI().equals(AKPS2.get(c).getSubj().getURI())
						&& AKPS.get(u).getPred().equals(AKPS2.get(c).getPred())) {
					checkEqu(AKPS.get(u).getObj().getURI(), AKPS2.get(c).getObj().getURI(), AKPS.get(u).getFreqTri(),
							AKPS2.get(c).getFreqTri());
				}
			}
		}
	}

	private static void findSubjectObjectTriple(String[] stringAKPs, Pattern[] aKPs) {

		boolean tmp = false;
		for (int i = 0; i < stringAKPs.length - 1; i++) {
			if (checkEqu(aKPs[i].getSubj(), aKPs[i].getObj(), tmp)) {
				aKPs[i].setDelete(true);
			}
		}
	}

	/*
	 * Si inizializza una variabile locale booleana a false. Si apre un altro
	 * buffer in cui questa volta saranno salvati tutti i concetti equivalenti
	 * fra loro. L'oggetto in cui verrano salvati questi concetti sarà line che
	 * andranno poi inseriti in stringAKPs divisi a due a due. per finire verrà
	 * chiamato il metodo checkEquWhileReading in cui verrà effettuato il
	 * controllo fra i due AKP sospetti ed i concetti equivalenti appena
	 * salvati.
	 */
	public static void checkEqu(String concept, String concept2, String freq, String freq2) {

		boolean tmp2 = false;

		try (BufferedReader br = new BufferedReader(new FileReader(new File(
				"/schema-summaries/data/summaries/system-test/reports/tmp-data-for-computation/equivalenti.txt")))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!"".equals(line)) {
					line = line.substring(0, line.length());
					String[] stringAKPs = line.split("#######");
					tmp2 = checkEquWhileReading(concept, concept2, freq, freq2, tmp2, stringAKPs);
				}
			}
			br.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

	/*
	 * Si cicla sui concetti per vedere se il soggetto o l'oggetto sospettato di
	 * essere equivalenti lo è realmente. Dopo aver salvato due concetti in
	 * relazione di equivalenza alla volta si controlla se i concetti sospettati
	 * siano nella stessa relazione con i soggetti equivalenti. Se lo sono si
	 * vede a questo punto quale AKP ha la frequenza maggiore con il metodo
	 * checkFreq
	 */
	private static boolean checkEquWhileReading(String concept, String concept2, String freq, String freq2,
			boolean tmp2, String[] stringAKPs) {
		boolean tmp = tmp2;
		for (int k = 0; k < stringAKPs.length - 1; k++) {
			String[] sottoStringa = stringAKPs[k].split("##");
			String[] equi = new String[2];
			equi[0] = sottoStringa[0];
			equi[1] = sottoStringa[1];
			if ((concept.equals(equi[0]) && concept2.equals(equi[1]))
					|| (concept.equals(equi[1]) && concept2.equals(equi[0]))) {
				tmp = true;
			}
			checkFreq(concept, concept2, freq, freq2, tmp);
		}
		return tmp;
	}

	/*
	 * Non si controlla altro quale AKP ha la frequenza maggiore. Se uno dei due
	 * AKP ha la frequenza maggiore dell'altro si aggiunge all'oggetto
	 * ConcettiEtichettati il concetto che appartiene a quell'AKP. Se le
	 * frequenze sono identiche si usa il metodo conta per vedere quale dei due
	 * concetti equivalenti compare più volte negli AKP.
	 */
	private static void checkFreq(String concept, String concept2, String freq, String freq2, boolean tmp2) {
		if (tmp2) {
			if ((Integer.parseInt(freq) < Integer.parseInt(freq2)) && (!ConcettiEtichettati.contains(concept2))) {
				ConcettiEtichettati.add(concept2);
			}
			if ((Integer.parseInt(freq) > Integer.parseInt(freq2)) && (!ConcettiEtichettati.contains(concept))) {
				ConcettiEtichettati.add(concept);
			}
			if (Integer.parseInt(freq) == Integer.parseInt(freq2)) {
				conta(concept, concept2);
			}
		}
	}

	/*
	 * Si inizializzano due contatori prima di tutto che conterranno
	 * rispettivamente il numero di volte che i due concetti campaiono negli
	 * AKP. Dopo aver effettuato il conteggio si vede quale concetto appare.
	 * Come prima si preferisce il concetto che appare più spesso e appena
	 * individuato si aggiunge akk'oggetto ConcettiEtichettati. In caso di
	 * pareggio si aggiungono entrambi.
	 */
	public static void conta(String concept, String concept2) {

		int count1 = 0;
		int count2 = 0;
		int u = 0;

		for (; u < AKPS.size(); u++) {
			if (AKPS.get(u).getSubj().getURI().equals(concept)) {
				count1++;
			} else if (AKPS.get(u).getSubj().getURI().equals(concept2)) {
				count2++;
			}
		}
		if ((count1 < count2) && (!ConcettiEtichettati.contains(concept2))) {
			ConcettiEtichettati.add(concept2);
		}
		if ((count1 > count2) && (!ConcettiEtichettati.contains(concept))) {
			ConcettiEtichettati.add(concept);
		}
		if ((count1 == count2) && (!ConcettiEtichettati.contains(concept))) {
			ConcettiEtichettati.add(concept);
		}
	}

	public static boolean checkEqu(Concept concept, Concept concept2, boolean tmp) {

		boolean tmp2 = tmp;

		try (BufferedReader br = new BufferedReader(new FileReader(new File(
				"/schema-summaries/data/summaries/system-test/reports/tmp-data-for-computation/equivalenti.txt")))) {
			boolean prova1 = false;
			boolean prova2 = false;
			String line;
			while ((line = br.readLine()) != null) {
				if (!"".equals(line)) {
					line = line.substring(0, line.length());
					String[] stringAKPs = line.split("##");
					for (int i = 0; i < stringAKPs.length - 1; i++) {
						if (concept.getURI().equals(stringAKPs[i]))
							prova1 = true;
						if (concept2.getURI().equals(stringAKPs[i]))
							prova2 = true;
					}
					if (prova1 && prova2) {
						tmp2 = true;
						return tmp2;
					}
				}
			}
		} catch (Exception e) {
			System.out.println(e);
		}
		return tmp2;
	}

	/*
	 * Si apre un buffer di Output per stampare nel file ConcettiPreferiti tutti
	 * i concetti salvati in ConcettiEtichettati.
	 */
	public static void stampa(List<String> concettiPreferiti) {
		try {
			FileOutputStream fos = new FileOutputStream(
					new File("/schema-summaries/data/summaries/system-test/patterns/ConcettiPreferiti.txt"));
			for (int i = 0; i < concettiPreferiti.size(); i++) {
				fos.write((concettiPreferiti.get(i) + "\n").getBytes());
			}
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException("Qualcosa non  andato con stampaSuFile " + e);
		}
	}
}