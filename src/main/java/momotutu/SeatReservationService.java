package momotutu;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Xiaofan Feng's implementation of TicketService. Attempts to find and hold the
 * best seats in the venue. The default best seat is the middle of a row, and
 * 2/3rds from the stage. Seats are grouped when possible, i.e., friends and
 * acquaintances would want to seat together.
 */
public class SeatReservationService implements TicketService {

	// held seat value will be negative of the original weight of the seat
	private final int RESERVED = 0;

	/**
	 * Number of rows in default venue size, which is 9.
	 */
	public final static int DEFAULT_ROWS = 9;
	/**
	 * Number of columns in default venue size, which is 33.
	 */
	public final static int DEFAULT_COLUMNS = 33;

	/**
	 * Default expiration time of seat holds. The default is 5 minutes.
	 */
	public final static long DEFAULT_EXPIRATION = 5 * 60 * 1000;

	/**
	 * Default best-ratio row position, used for seating. This is 2/3rds.
	 */
	public final static float DEFAULT_BEST_RATIO = (float) 2 / (float) 3;

	private int nRows;
	private int nColumns;
	private long expirationTimeInMillis;
	private float bestRatio;

	private int[][] seats;
	private int numSeatsAvailable;

	// key is the index for seat, value is the time the seat is held;
	private Map<Integer, String> customerLookUp = new Hashtable<Integer, String>();
	private Map<Integer, SeatHold> lookUpSeatHold = new Hashtable<Integer, SeatHold>();
	private Map<Integer, Long> seatHoldExpiration = new Hashtable<Integer, Long>();

	private class Pair {
		public int a;
		public int b;

		public Pair(int a, int b) {
			this.a = a;
			this.b = b;
		}
	}

	public SeatReservationService(int rows, int columns,
			long expirationTimeInMillis, float bestRatio) {

		this.nRows = rows;
		this.nColumns = columns;
		this.expirationTimeInMillis = expirationTimeInMillis;
		this.bestRatio = bestRatio;

		int rowMedian = nColumns / 2;
		int columnBest = Math.round((float) nRows * this.bestRatio);

		seats = new int[nRows][nColumns];
		numSeatsAvailable = nRows * nColumns;

		for (int i = 0; i < rowMedian; i++) {
			seats[0][i] = i + 1;
			seats[0][nColumns - i - 1] = i + 1;
		}
		if (nColumns % 2 == 1) {
			seats[0][rowMedian] = rowMedian + 1;
		}
		for (int i = 1; i < columnBest; i++) {
			for (int j = 0; j < nColumns; j++) {
				seats[i][j] = seats[0][j] * (i + 1);
			}
		}
		for (int i = columnBest; i < nRows; i++) {
			for (int j = 0; j < nColumns; j++) {
				seats[i][j] = seats[0][j] * (columnBest - (i - columnBest) - 1);
			}
		}
	}

	/**
	 * Construct with default venue settings.
	 */
	public SeatReservationService() {
		this(DEFAULT_ROWS, DEFAULT_COLUMNS, DEFAULT_EXPIRATION,
				DEFAULT_BEST_RATIO);
	}

	/**
	 * Find and hold the best available seats for a customer.
	 * 
	 * @param numSeats
	 *            the number of seats to find and hold
	 * @param customerEmail
	 *            unique identifier for the customer
	 * @return a SeatHold object identifying the specific seats and related
	 *         information. Returns null in all cases where seat cannot be
	 *         reserved. Does not throw exceptions since the provided interface
	 *         does not support exceptions.
	 */
	public synchronized SeatHold findAndHoldSeats(int numSeats,
			String customerEmail) {
		releaseExpiredSeats(0, customerEmail);

		if (numSeats > 0 && numSeats <= numSeatsAvailable) {

			int[] flattenSeats = new int[nRows * nColumns];
			for (int i = 0; i < nRows; i++) {
				if (i % 2 == 0) {
					for (int j = 0; j < nColumns; j++) {
						flattenSeats[i * nColumns + j] = seats[i][j];
					}
				} else {
					for (int j = 0; j < nColumns; j++) {
						flattenSeats[i * nColumns + j] = seats[i][nColumns - j
						                                          - 1];
					}
				}
			}

			SortedMap<Integer, Pair> sm = new TreeMap<Integer, Pair>();
			int breakCounter = 0;
			int tempSum = 0;
			int k = 0;
			for (int i = 0; i < flattenSeats.length; i++) {
				if (flattenSeats[i] < 1)
					breakCounter += 1;
				else {
					tempSum += flattenSeats[i];
					k++;
					if (k == numSeats) {
						while (flattenSeats[i - numSeats - breakCounter + 1] < 1) {
							breakCounter--;
						}
						int availableStart = i - numSeats - breakCounter + 1;
						if (sm.containsKey(breakCounter)) {
							// because of the comparison is greater and EQUAL,
							// the group of seats found
							// will prefer the back seats more when different
							// groups are of the same value
							if (tempSum >= sm.get(breakCounter).b) {
								sm.get(breakCounter).b = tempSum;
								sm.get(breakCounter).a = availableStart;
							}
						} else {
							sm.put(breakCounter, new Pair(availableStart,
									tempSum));
						}
						tempSum -= flattenSeats[availableStart];
						k--;
					}
				}
			}
			int startIndexSeatsFound = sm.get(sm.firstKey()).a;
			// Pair[] seatsFound = new Pair[numSeats];
			
			int seatHoldId = generateSeatHoldID(customerEmail);
			
			int [][] seatsHeld = new int[numSeats][2];
			// row, column pair
			int i = 0;
			while (i < numSeats) {
				if (flattenSeats[startIndexSeatsFound + i] > 0) {
					// convert this index
					int rowIndex = (startIndexSeatsFound + i) / nColumns;
					int columnIndex = (startIndexSeatsFound + i) % nColumns;

					if ((startIndexSeatsFound + i) / nColumns % 2 == 1) {
						columnIndex = nColumns - columnIndex - 1;
					}

					seatsHeld[i][0] = rowIndex;
					seatsHeld[i][1] = columnIndex;
					seats[rowIndex][columnIndex] *= -1;
					i++;
				} else {
					startIndexSeatsFound++;
				}
			}
			
			SeatHold sh = new SeatHold(seatHoldId, seatsHeld);
			
			lookUpSeatHold.put(sh.seatHoldId, sh);
			customerLookUp.put(sh.seatHoldId, customerEmail);
			seatHoldExpiration.put(sh.seatHoldId, System.currentTimeMillis());
			numSeatsAvailable -= numSeats;
			return sh;
		}
		return null;
	}

	public int numSeatsAvailable() {
		/*
		 * Does not need to be locked.
		 */

		releaseExpiredSeats(0, "");
		return numSeatsAvailable;
	}

	public String reserveSeats(int seatHoldId, String customerEmail) {
		/*
		 * Does not need to be locked.
		 */

		releaseExpiredSeats(seatHoldId, customerEmail);
		SeatHold x = lookUpSeatHold.get(seatHoldId);
		if (x != null && customerLookUp.get(seatHoldId).equals(customerEmail)) {
			for (int i = 0; i < x.seatsHeld.length; i++) {
				int row = x.seatsHeld[i][0];
				int column = x.seatsHeld[i][1];
				seats[row][column] = RESERVED;
			}

			seatHoldExpiration.remove(seatHoldId);
			return String.format("%08X", seatHoldId);

		}
		return null;
	}

	private int generateSeatHoldID(String customerEmail) {
		/*
		 * Does not need to be locked.
		 */
		int seatHoldID;
		do {
			String timeSalt = Long.toString(System.currentTimeMillis());
			seatHoldID = (customerEmail + timeSalt).hashCode();
		} while (lookUpSeatHold.get(seatHoldID) != null);
		return seatHoldID;
	}

	private synchronized void releaseExpiredSeats(int holdId,
			String customerEmail) {
		final long currentTime = System.currentTimeMillis();
		List<Integer> holdIDtoRemove = new ArrayList<Integer>();

		for (Integer seatHoldId : seatHoldExpiration.keySet()) {
			boolean expireFlag = false;
			String email = customerLookUp.get(seatHoldId);

			if (email != null && email.equals(customerEmail)
					&& holdId != seatHoldId) {
				expireFlag = true;
			} else if (currentTime - seatHoldExpiration.get(seatHoldId) > expirationTimeInMillis) {
				expireFlag = true;
			}

			if (expireFlag) {
				int[][] seatsToRemove = lookUpSeatHold.get(seatHoldId).seatsHeld;
				for (int seat = 0; seat < seatsToRemove.length; seat++) {
					seats[seatsToRemove[seat][0]][seatsToRemove[seat][1]] *= -1;
				}
				numSeatsAvailable += seatsToRemove.length;
				customerLookUp.remove(seatHoldId);
				lookUpSeatHold.remove(seatHoldId);
				holdIDtoRemove.add(seatHoldId);
			}
		}

		for (Integer seatHoldId : holdIDtoRemove) {
			seatHoldExpiration.remove(seatHoldId);
		}
	}

	/**
	 * Print the theatre-seats with the assigned seat values. A positive value
	 * indicates how good a seat is. 0 indicates that the seat is reserved. A
	 * negative value indicates the seat is currently on hold.
	 */
	public void printNum() {
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nColumns; j++) {
				System.out.printf("%3d ", seats[i][j]);
			}
			System.out.println();
		}
	}

	/**
	 * Print the theatre-seats, where H indicates seat held, X indicates seat is
	 * reserved, and . indicates seat is available.
	 */
	public void printChar() {
		for (int i = 0; i < nRows; i++) {
			for (int j = 0; j < nColumns; j++) {
				if (seats[i][j] == 0)
					System.out.print("X  ");
				else if (seats[i][j] < 0)
					System.out.print("H  ");
				else
					System.out.print(".  ");
			}
			System.out.println();
		}
	}

	/**
	 * Example entry-point driver for demonstration.
	 * 
	 * @param args
	 *            Unused
	 * @throws InterruptedException
	 */
	public static void main(String... args) throws InterruptedException {
		System.out.println("Seat Reservation Service");
		System.out
		.println("This system attempts to reserve the best seats in the venue.");
		System.out.println();
		System.out.println();
		System.out
		.println("Demonstrating an example with 9 rows and 17 columns.");

		SeatReservationService a = new SeatReservationService(9, 17, 5000,
				SeatReservationService.DEFAULT_BEST_RATIO);
		System.out.println("Seats prior to reservations or holds.");
		a.printChar();

		System.out.println();
		System.out.println("Holding 3 seats; displaying layout.");
		SeatHold sh = a.findAndHoldSeats(3, "x");
		a.printChar();

		System.out.println();
		System.out
		.println("Holding 3 more seats for new email; displaying layout.");
		a.findAndHoldSeats(3, "y"); // NOTE: not reserving these.
		a.printChar();

		System.out.println();
		System.out
		.println("Reserving the first 3 seats and displaying layout.");
		a.reserveSeats(sh.seatHoldId, "x");
		a.printChar();

		System.out.println();
		System.out.println("Done.");

	}
}
