/**
 * 
 */
package ch.hesge.csim2.engine;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.SimpleSelector;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

/**
 * Utility class related to concept analysis.
 * 
 * Copyright HEG Geneva 2014, Switzerland
 * 
 * @author Eric Harth
 *
 */
public class OntologyLoaderUtils {

	/**
	 * Retrieve an iterator on type class statements
	 */
	public static StmtIterator getClassIterator(Model model) {

		SimpleSelector selector = new SimpleSelector(null, null, (RDFNode)null) {
		    public boolean selects(Statement s) { 
		    	return s.getPredicate().getLocalName().equals("type") && 
		    		s.getObject().toString().endsWith("#Class") && 
		    		!s.getSubject().getLocalName().equals("Tuple") &&
		    		!s.getSubject().getLocalName().equals("Relation");
		    }
		};

		return model.listStatements(selector);
	}

	/**
	 * Retrieve an iterator over type attribute statements
	 */
	public static StmtIterator getAttributeIterator(Model model) {

		SimpleSelector selector = new SimpleSelector(null, null, (RDFNode)null) {
		    public boolean selects(Statement s) { 
		    	return s.getPredicate().getLocalName().equals("type") && 
		    		s.getObject().toString().endsWith("#attribute");
		    }
		};

		return model.listStatements(selector);
	}

	/**
	 * Retrieve an iterator over type tuple (relation between concepts) statements
	 */
	public static StmtIterator getTupleIterator(Model model) {

		SimpleSelector selector = new SimpleSelector(null, null, (RDFNode)null) {
		    public boolean selects(Statement s) { 
		    	return s.getPredicate().getLocalName().equals("type") && 
		    		s.getObject().toString().endsWith("#Tuple");
		    }
		};

		return model.listStatements(selector);
	}

	/**
	 * Retrieve an iterator over type attribute statements
	 */
	public static StmtIterator getOntoTermIterator(Model model) {

		SimpleSelector selector = new SimpleSelector(null, null, (RDFNode)null) {
		    public boolean selects(Statement s) { 
		    	return s.getPredicate().getLocalName().equals("type") && 
		    		s.getObject().toString().endsWith("#ontoterm");
		    }
		};

		return model.listStatements(selector);
	}

}
