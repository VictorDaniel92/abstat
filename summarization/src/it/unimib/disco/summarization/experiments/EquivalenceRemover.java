package it.unimib.disco.summarization.experiments;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.ArrayList;

public class EquivalenceRemover {

	public static ArrayList<String> prova = new ArrayList<String>();
	public static ArrayList<Pattern> AKPS = new ArrayList<Pattern>();
	public static ArrayList<Pattern> AKPS2 = new ArrayList<Pattern>();

	public static void readTriplesAKPsEq(String aKPsFile) {

		String[] stringAKPs = null;

		try {
			BufferedReader br = new BufferedReader(
					new FileReader(
							new File(
									"/schema-summaries/data/summaries/system-test/patterns/datatype-akp.txt")));
			String line;
			while ((line = br.readLine()) != null) {
				if (!line.equals("")) {
					line = line.substring(0, line.length());
					stringAKPs = line.split("##");
						String[] splitted = new String[4];
						splitted[0] = stringAKPs[0];
						splitted[1] = stringAKPs[1];
						splitted[2] = stringAKPs[2];
						splitted[3] = stringAKPs[3];
						AKPS.add(new Pattern(new Concept(splitted[0]),
								splitted[1], new Concept(splitted[2]),
								splitted[3]));
						AKPS2.add(new Pattern(new Concept(splitted[0]),
								splitted[1], new Concept(splitted[2]),
								splitted[3]));
				}
			}
			br.close();
			for (int u = 0; u < AKPS.size(); u++) {
				for (int c = 0; c < AKPS2.size(); c++) {
					if (AKPS.get(u).getObj().getURI()
							.equals(AKPS2.get(c).getObj().getURI())
							&& AKPS.get(u).getPred()
									.equals(AKPS2.get(c).getPred())) {
						checkEqu(AKPS.get(u).getSubj().getURI(), AKPS2.get(c)
								.getSubj().getURI(), AKPS.get(u).getFreqTri(),
								AKPS2.get(c).getFreqTri(), u, c);
					}
					if (AKPS.get(u).getSubj().getURI()
							.equals(AKPS2.get(c).getSubj().getURI())
							&& AKPS.get(u).getPred()
									.equals(AKPS2.get(c).getPred())) {
						checkEqu(AKPS.get(u).getObj().getURI(), AKPS2.get(c)
								.getObj().getURI(), AKPS.get(u).getFreqTri(),
								AKPS2.get(c).getFreqTri(), u, c);
					}
				}
			}
			// TODO vedere se mantenere o meno
			// findSubjectObjectTriple(stringAKPs, aKPs);
			stampa(prova);
		} catch (Exception e) {
			throw new RuntimeException("Qualcosa non  andato con stampaSuFile "
					+ e);
		}
	}

	private static void findSubjectObjectTriple(String[] stringAKPs,
			Pattern[] aKPs) {

		boolean tmp = false;
		System.out.println("sto cercando so triple");
		for (int i = 0; i < stringAKPs.length - 1; i++) {
			if (checkEqu(aKPs[i].getSubj(), aKPs[i].getObj(), tmp)) {
				aKPs[i].setDelete(true);
				System.out.println("metto delete");
			}
		}
	}

	public static void checkEqu(String concept, String concept2, String freq,
			String freq2, int index, int index2) {

		boolean tmp2 = false;

		try (BufferedReader br = new BufferedReader(
				new FileReader(
						new File(
								"/schema-summaries/data/summaries/system-test/reports/tmp-data-for-computation/equivalenti.txt")))) {
			String line;
			while ((line = br.readLine()) != null) {
				if (!"".equals(line)) {
					line = line.substring(0, line.length());
					String[] stringAKPs = line.split("#######");
					for (int k = 0; k < stringAKPs.length - 1; k++) {
						String[] sottoStringa = stringAKPs[k].split("##");
							String[] equi = new String[2];
							equi[0] = sottoStringa[0];
							equi[1] = sottoStringa[1];
							if ((concept.equals(equi[0]) && concept2
									.equals(equi[1]))
									|| (concept.equals(equi[1]) && concept2
											.equals(equi[0]))) {
								tmp2 = true;
							}
							if (tmp2 == true) {
								if (Integer.parseInt(freq) < Integer
										.parseInt(freq2)) {
									if (!prova.contains(concept2)) {
										prova.add(concept2);
									}
								}
								if (Integer.parseInt(freq) > Integer
										.parseInt(freq2)) {
									if (!prova.contains(concept)) {
										prova.add(concept);
									}
								}
								if (Integer.parseInt(freq) == Integer
										.parseInt(freq2)) {
									conta(concept, concept2);
								}
							}
					}
				}
			}
			br.close();

		} catch (Exception e) {
			System.out.println(e);
		}
	}

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
		if (count1 < count2) {
			if (!prova.contains(concept2)) {
				prova.add(concept2);
			}
		}
		if (count1 > count2) {
			if (!prova.contains(concept)) {
				prova.add(concept);
			}		}
		if (count1 == count2) {
			if (!prova.contains(concept)) {
				prova.add(concept);
			}
		}
		count1 = 0;
		count2 = 0;
	}

	public static boolean checkEqu(Concept concept, Concept concept2,
			boolean tmp) {

		boolean tmp2 = tmp;

		try (BufferedReader br = new BufferedReader(
				new FileReader(
						new File(
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
					if (prova1 == true && prova2 == true) {
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

	public static void stampa(ArrayList<String> blaciao) {
		try {
			FileOutputStream fos = new FileOutputStream(
					new File(
							"/schema-summaries/data/summaries/system-test/patterns/nuovo3.txt"));
			for (int i = 0; i < blaciao.size(); i++) {
				fos.write((blaciao.get(i) + "\n").getBytes());
			}
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException("Qualcosa non  andato con stampaSuFile "
					+ e);
		}
	}

}