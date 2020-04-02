package test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import junit.framework.Assert;
import junit.framework.TestCase;
import pl.noriSoftware.vendingMachine.VendingMachine;

public class VendingMachineTest extends TestCase {

	final protected Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	@Before
	public void setUp() throws Exception {
		System.out.println("setUp");
	}

	@Test
	public void test_Cola1() throws UnsupportedEncodingException {
		String config = "target\\test-classes\\cola1.config";
		String operations = "1 1 1 q";
		System.out.println("test_Cola1");

		SortedMap<Integer, Integer> sortedCoinBag1 = new TreeMap<Integer, Integer>();
		readCoinBagByConfig(config, sortedCoinBag1);
		// System.out.println("sortedCoinBag1="+sortedCoinBag1);

		VendingMachine vm = new VendingMachine(3, config, new ByteArrayInputStream(operations.getBytes("UTF-8")));
		vm.run();
		SortedMap<Integer, Integer> sortedCoinBag2 = new TreeMap<Integer, Integer>();
		readCoinBagByConfig(config, sortedCoinBag2);
		Assert.assertTrue(sortedCoinBag2.get(1).equals(sortedCoinBag1.get(1) + 3));

		operations = "4 4 4";
		vm = new VendingMachine(3, config, new ByteArrayInputStream(operations.getBytes("UTF-8")));
		vm.run();

		sortedCoinBag2 = new TreeMap<Integer, Integer>();
		readCoinBagByConfig(config, sortedCoinBag2);
		Assert.assertTrue(sortedCoinBag2.get(1).equals(sortedCoinBag1.get(1)));

		operations = "4 4 4 4 4 4 4 4 4 4 4 4 4 4 4";
		vm = new VendingMachine(3, config, new ByteArrayInputStream(operations.getBytes("UTF-8")));
		vm.run();

		sortedCoinBag2 = new TreeMap<Integer, Integer>();
		readCoinBagByConfig(config, sortedCoinBag2);

		// System.out.println("sortedCoinBag3="+sortedCoinBag2);

		Assert.assertTrue(sortedCoinBag2.get(1) == null);
		Assert.assertTrue(sortedCoinBag2.get(4).equals(13));

		operations = "1000";
		vm = new VendingMachine(3, config, new ByteArrayInputStream(operations.getBytes("UTF-8")));
		vm.run();

		sortedCoinBag2 = new TreeMap<Integer, Integer>();
		readCoinBagByConfig(config, sortedCoinBag2);

		Assert.assertTrue(sortedCoinBag2.get(1000) == null);
		Assert.assertTrue(sortedCoinBag2.get(1) == null);
		Assert.assertTrue(sortedCoinBag2.get(4).equals(13));
		Assert.assertTrue(sortedCoinBag2.get(5).equals(10));
	}

	private void readCoinBagByConfig(String fileName, SortedMap<Integer, Integer> sortedCoinBag) {
		Integer nominal = null;
		int tmp;
		try {
			Scanner sc = new Scanner(new File(fileName));
			while (sc.hasNextInt()) {
				if ((tmp = sc.nextInt()) < 1) {
					System.out.println("Unable to read nominal nor count less then one");
					nominal = null;
					continue;
				}
				if (nominal == null) {
					nominal = new Integer(tmp);
					continue;
				}
				VendingMachine.putCoins(nominal, tmp, sortedCoinBag);
				nominal = null;
			}
			sc.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
			ex.printStackTrace();
		}
	}
}
