package pl.noriSoftware.vendingMachine;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.SortedMap;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class VendingMachine {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	private final SortedMap<Integer, Integer> sortedCoinBag = new TreeMap<Integer, Integer>(new CoinBagDescComparator());
	private Integer coinBagSum = 0;
	private Integer cost;
	private String fileName;
	private Scanner sc;
	private final List<Integer> readCoins = new ArrayList<Integer>();

	@SuppressWarnings("unused")
	private VendingMachine() {
		new UnsupportedOperationException();
	}

	public VendingMachine(final Integer cost, final String fileName, final InputStream in) {
		this.cost = cost;
		this.fileName = fileName;
		this.sc = new Scanner(in);
	}

	public void loadCoins() {
		Integer nominal = null;
		int tmp;
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File(fileName));
			while (scanner.hasNextInt()) {
				if ((tmp = scanner.nextInt()) < 1) {
					System.out.println("Unable to read nominal nor count less then one");
					nominal = null;
					continue;
				}
				if (nominal == null) {
					nominal = Integer.valueOf(tmp);
					continue;
				}
				putCoins("LoadCoins", nominal, tmp);
				nominal = null;
			}
			printCoinBag("LoadCoins");
		} catch (final FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
			ex.printStackTrace();
		} finally {
			if (scanner != null) {
				scanner.close();
			}
		}
	}

	private void putCoins(final String where, final Integer nominal, final int tmp) {
		coinBagSum += (nominal * tmp);
		putCoins(nominal, tmp, sortedCoinBag);
		if (logger.isDebugEnabled()) {
			logger.debug(new StringBuilder(where).append(" put ").append(nominal).append('(').append(sortedCoinBag.get(nominal)).append(')').toString());
		}
	}

	public static void putCoins(final Integer nominal, int tmp, final SortedMap<Integer, Integer> sortedCoinBag) {
		Integer count;
		if ((count = sortedCoinBag.get(nominal)) != null) {
			tmp += count.intValue();
		}
		sortedCoinBag.put(nominal, Integer.valueOf(tmp));
	}

	void saveCoinBag() {
		BufferedWriter bufferedWriter = null;
		try {
			bufferedWriter = new BufferedWriter(new FileWriter(fileName));
			for (final SortedMap.Entry<Integer, Integer> entry : sortedCoinBag.entrySet()) {
				if (entry.getValue() > 0) {
					bufferedWriter.write(entry.getKey().toString());
					bufferedWriter.write(' ');
					bufferedWriter.write(entry.getValue().toString());
					bufferedWriter.newLine();
				}
			}
		} catch (final IOException ex) {
			System.out.println("Error writing to file '" + fileName + "'");
			ex.printStackTrace();
		} finally {
			if (bufferedWriter != null) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	void printMenu() {
		System.out.println("You can buy Cola for " + cost.toString() + " or exit");
		System.out.println("To buy write coins by space and press enter");
		System.out.println("To exit press q");
	}

	int getServiceCode() {
		int tmp;
		readCoins.clear();
		Integer account = 0;
		while (sc.hasNext()) {
			if (!sc.hasNextInt()) {
				if (sc.next().equals("q")) {
					break;
				}
			} else {
				tmp = sc.nextInt();
				if (tmp < 1) {
					System.out.println("Unable to read coin less then one");
				} else {
					readCoins.add(Integer.valueOf(tmp));
					account += tmp;
					System.out.println("Your account " + account.toString());
					if (account.equals(cost)) {
						return 1;
					} else if (account.compareTo(cost) > 0) {
						if (!isCheckOrBackRest("getServiceCodeCheck", account - cost, true)) {
							return 3;
						}
						account -= cost;
						isCheckOrBackRest("getServiceCode", account, false);
						return 2;
					}
				}
			}
		}
		saveCoinBag();
		sc.close();
		return 0;
	}

	void putCola() {
		System.out.println();
		System.out.println("Get Cola and Thank You");
		System.out.println();
	}

	void backOrGetReadCoins(final boolean isBackOnly) {
		if (isBackOnly && (!readCoins.isEmpty())) {
			System.out.print("Get back Your coin(s):");
		}
		for (Integer coin : readCoins) {
			if (!isBackOnly) {
				putCoins("GetReadCoins", coin, 1);
			} else {
				System.out.print(" " + coin.toString());
			}
		}
		if (isBackOnly && (!readCoins.isEmpty())) {
			System.out.println();
			System.out.println();
		}
		readCoins.clear();
	}

	boolean isCheckOrBackRest(final String where, int account, final boolean checkOnly) {
		if (logger.isDebugEnabled()) {
			logger.debug(new StringBuilder(where).append(checkOnly ? "CheckRest" : "BackRest").append("1 account=").append(account).toString());
		}
		final Collection<Integer> rest = new ArrayList<Integer>();
		Integer nominal, count;
		final SortedMap<Integer, Integer> tmp = checkOnly ? new TreeMap<Integer, Integer>(sortedCoinBag) : sortedCoinBag;
		final Iterator<Integer> iterator = tmp.keySet().iterator();
		while ((account > 0) && iterator.hasNext()) {
			nominal = iterator.next();
			while ((account > 0) && (nominal <= account)) {
				count = tmp.get(nominal);
				if (count == 0) {
					break;
				}
				if (!checkOnly) {
					coinBagSum -= nominal;
				}
				tmp.put(nominal, --count);
				account -= nominal.intValue();
				rest.add(nominal);
			}
		}
		if ((account > 0) || rest.isEmpty()) {
			System.out.println("Sorry, can not give Your change");
			return false;
		}
		if (!checkOnly) {
			backOrGetReadCoins(false);
			System.out.print("Get Your change:");
			for (Integer coin : rest) {
				System.out.print(" " + coin.toString());
			}
			System.out.println();
			printCoinBag("BackRest");
		}
		return true;
	}

	void printCoinBag(final String where) {
		System.out.print(where + " Coin bag " + coinBagSum + ":");
		for (final SortedMap.Entry<Integer, Integer> entry : sortedCoinBag.entrySet()) {
			if (entry.getValue() > 0) {
				System.out.print(" " + entry.getKey() + "(" + entry.getValue() + ")");
			}
		}
		System.out.println("");
	}

	private class CoinBagDescComparator implements Comparator<Integer> {

		@Override
		public int compare(final Integer paramT1, final Integer paramT2) {
			return -paramT1.compareTo(paramT2);
		}
	}

	public void run() {
		loadCoins();
		printMenu();
		int serviceCode;
		while ((serviceCode = getServiceCode()) != 0) {
			switch (serviceCode) {
			case 1:
			case 2:
				backOrGetReadCoins(false);
				putCola();
				break;
			case 3:
				backOrGetReadCoins(true);
				break;
			}
			printMenu();
		}
	}
}