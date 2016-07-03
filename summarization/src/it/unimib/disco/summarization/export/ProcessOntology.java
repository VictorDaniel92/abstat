package it.unimib.disco.summarization.export;

import it.unimib.disco.summarization.dataset.FileDataSupport;
import it.unimib.disco.summarization.ontology.ConceptExtractor;
import it.unimib.disco.summarization.ontology.Concepts;
import it.unimib.disco.summarization.ontology.EqConceptExtractor;
import it.unimib.disco.summarization.ontology.EquivalentConcepts;
import it.unimib.disco.summarization.ontology.Model;
import it.unimib.disco.summarization.ontology.OntologySubclassOfExtractor;
import it.unimib.disco.summarization.ontology.Properties;
import it.unimib.disco.summarization.ontology.PropertyExtractor;
import it.unimib.disco.summarization.ontology.SubClassOf;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.semanticweb.HermiT.Reasoner;
import org.semanticweb.owlapi.apibinding.OWLManager;
import org.semanticweb.owlapi.io.RDFXMLOntologyFormat;
import org.semanticweb.owlapi.model.IRI;
import org.semanticweb.owlapi.model.OWLAxiom;
import org.semanticweb.owlapi.model.OWLOntology;
import org.semanticweb.owlapi.model.OWLOntologyManager;
import org.semanticweb.owlapi.reasoner.InferenceType;
import org.semanticweb.owlapi.reasoner.OWLReasoner;
import org.semanticweb.owlapi.reasoner.OWLReasonerFactory;
import org.semanticweb.owlapi.util.InferredAxiomGenerator;
import org.semanticweb.owlapi.util.InferredEquivalentClassAxiomGenerator;
import org.semanticweb.owlapi.util.InferredOntologyGenerator;
import org.semanticweb.owlapi.util.InferredSubClassAxiomGenerator;

import com.hp.hpl.jena.ontology.OntModel;

public class ProcessOntology {

	public static void main(String[] args) throws Exception {

		OWLReasonerFactory reasonerFactory = new Reasoner.ReasonerFactory();
		OWLOntologyManager man = OWLManager.createOWLOntologyManager();
		File file = new File("/schema-summaries/data/datasets/system-test/ontology/dbpedia_2014.owl");
		OWLOntology ont = man.loadOntologyFromOntologyDocument(file);
		OWLReasoner reasoner = reasonerFactory.createNonBufferingReasoner(ont);
		reasoner.precomputeInferences(InferenceType.CLASS_HIERARCHY);
		List<InferredAxiomGenerator<? extends OWLAxiom>> gens = new ArrayList<>();
		gens.add(new InferredEquivalentClassAxiomGenerator());
		gens.add(new InferredSubClassAxiomGenerator());
		OWLOntology infOnt = man.createOntology();
		InferredOntologyGenerator iog = new InferredOntologyGenerator(reasoner, gens);
		iog.fillOntology(man, infOnt);
		man.saveOntology(infOnt, new RDFXMLOntologyFormat(),
				IRI.create("file:/schema-summaries/data/datasets/system-test/ontology/dbpedia_2014.owl"));

		Events.summarization();

		String owlBaseFileArg = null;
		String datasetSupportFileDirectory = null;

		owlBaseFileArg = args[0];
		datasetSupportFileDirectory = args[1];

		File folder = new File(owlBaseFileArg);
		Collection<File> listOfFiles = FileUtils.listFiles(folder, new String[] { "owl" }, false);
		String fileName = listOfFiles.iterator().next().getName();

		String owlBaseFile = "file://" + owlBaseFileArg + "/" + fileName;

		// Model
		OntModel ontologyModel = new Model(owlBaseFile, "RDF/XML").getOntologyModel();

		// Extract Property from Ontology Model
		PropertyExtractor pExtract = new PropertyExtractor();
		pExtract.setProperty(ontologyModel);

		Properties properties = new Properties();
		properties.setProperty(pExtract.getProperty());
		properties.setExtractedProperty(pExtract.getExtractedProperty());
		properties.setCounter(pExtract.getCounter());

		// Extract Concept from Ontology Model
		ConceptExtractor cExtract = new ConceptExtractor();
		cExtract.setConcepts(ontologyModel);

		Concepts concepts = new Concepts();
		concepts.setConcepts(cExtract.getConcepts());
		concepts.setExtractedConcepts(cExtract.getExtractedConcepts());
		concepts.setObtainedBy(cExtract.getObtainedBy());

		// Extract SubClassOf Relation from OntologyModel
		OntologySubclassOfExtractor SbExtractor = new OntologySubclassOfExtractor();
		// The Set of Concepts will be Updated if Superclasses are not in It
		SbExtractor.setConceptsSubclassOf(concepts, ontologyModel);
		SubClassOf SubClassOfRelation = SbExtractor.getConceptsSubclassOf();

		// Extract EquivalentClass from Ontology Model - Qui per considerare
		// tutti i concetti
		EqConceptExtractor equConcepts = new EqConceptExtractor();
		equConcepts.setEquConcept(concepts, ontologyModel);

		EquivalentConcepts equConcept = new EquivalentConcepts();
		equConcept.setExtractedEquConcept(equConcepts.getExtractedEquConcept());
		equConcept.setEquConcept(equConcepts.getEquConcept());

		try {
			FileOutputStream fos = new FileOutputStream(new File(
					"/schema-summaries/data/summaries/system-test/reports/tmp-data-for-computation/equivalenti.txt"));
			ArrayList<String> equivalenti = equConcept.getEquConcept();
			for (int i = 0; i < equivalenti.size(); i++) {

				fos.write((equivalenti.get(i) + "##").getBytes());
			}
			fos.close();
		} catch (Exception e) {
			throw new RuntimeException("Qualcosa non  andato con stampaConcettiEquivalenti " + e);
		}

		concepts.deleteThing();
		SubClassOfRelation.deleteThing();

		FileDataSupport writeFileSupp = new FileDataSupport(SubClassOfRelation,
				datasetSupportFileDirectory + "SubclassOf.txt", datasetSupportFileDirectory + "Concepts.txt");

		writeFileSupp.writeSubclass(equConcept);
		writeFileSupp.writeConcept(concepts);

	}

}
