/**
 * 
 */
package pl.noriSoftware.vendingMachine;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author karol
 */
public class ColaTest {

	private static final Class<ColaTest> thisClass = ColaTest.class;
	protected static final Logger logger = LoggerFactory.getLogger(thisClass.getName());

	/**
	 * @param args
	 */
	public static void main(final String[] args) {
		VendingMachine vm = new VendingMachine(3, "cola.config", System.in);
		vm.run();
	}

}