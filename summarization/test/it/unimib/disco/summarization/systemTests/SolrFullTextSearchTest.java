package it.unimib.disco.summarization.systemTests;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.*;

import org.junit.Test;

public class SolrFullTextSearchTest {

	@Test
	public void shouldSupportQueriesOnProperties() throws Exception {
		
		httpAssert().body("select?q=fullTextSearchField:place&fq=type:objectProperty", containsString("http://dbpedia.org/ontology/birthPlace"));
	}

	@Test
	public void shouldSupportQueriesOnConcepts() throws Exception {
		
		httpAssert().body("select?q=fullTextSearchField:place&fq=type:concept", containsString("http://dbpedia.org/ontology/PopulatedPlace"));
	}
	
	@Test
	public void shouldSupportQueriesOnAKPs() throws Exception {
		
		httpAssert().body("select?q=fullTextSearchField:place&fq=type:objectAkp", containsString("http://dbpedia.org/ontology/capital"));
	}
	
	@Test
	public void shouldSupportQueriesOnCrossTypologies() throws Exception {
		
		httpAssert().body("select?q=fullTextSearchField:place", allOf(containsString("concept"),
																	  containsString("Property"),
																	  containsString("Akp")));
	}
	
	@Test
	public void shouldSupportTheSelectionOfTheDataset() throws Exception {
		httpAssert().body("select?q=*:*&fq=dataset:system-test", containsString("numFound=\"213\""));
	}
	
	private HttpAssert httpAssert() {
		return new HttpAssert("http://localhost/solr/indexing");
	}
}
