package momotutu.tests;
import static org.junit.Assert.*;

import java.util.Random;


import momotutu.SeatHold;
import momotutu.SeatReservationService;
import momotutu.TicketService;

import org.junit.Test;

public class MultithreadTest1 {
	
	private static class Person implements Runnable {
		private TicketService ts;
		private Random r;
		private String email;
		private int venueSize;
		private boolean result = false;
		
		public Person(TicketService ts, String emailAddress, Random r, int venueSize) {
			this.ts = ts;
			this.email = emailAddress;
			this.r = r;
			this.venueSize = venueSize;
		}

		@Override
		public void run() {
			int nAvailable = ts.numSeatsAvailable();
			int allocationSize = r.nextInt(venueSize);
			
			System.out.printf("Trying to hold %d seats of %d available.\n", allocationSize, nAvailable);
			SeatHold sh = ts.findAndHoldSeats(allocationSize, email);
			if (sh != null) {
				if (allocationSize <= nAvailable) {
					System.out.printf("PASSED. Holding %d seats (id: %d).\n", allocationSize, sh.seatHoldId);
					result = true;
				} else {
					System.out.println("FAILED.");
				}
			} else {
				nAvailable = ts.numSeatsAvailable();
				if (allocationSize > nAvailable) {
					System.out.printf("PASSED. Could not allocate seats since newly available seat count is: %d\n", nAvailable);
					result = true;
				} else {
					System.out.printf("FAILED. new nAvailable: %d\n", nAvailable);
				}
			}		
		}
		
	}

	
	@Test
	public void test() throws InterruptedException {
		Random r = new Random();
		TicketService tickets = new SeatReservationService();
		int venueSize = tickets.numSeatsAvailable();
		boolean result = true;
		
		Person [] ps = new Person[2];
		Thread [] ts = new Thread[2];
		
		for (int i = 0; i < 2; i++) {
			Person p = new Person(tickets, Integer.toHexString(i) + "@cnbc.com", r, venueSize);
			Thread t = new Thread(p);
			ps[i] = p;
			ts[i] = t;
			
			t.start();
		}
		
		for (int i = 0; i < 2; ++i){
			ts[i].join();
		}
		
		for (int i = 0; i < 2; ++i){
			result &= ps[i].result;			
		}
		
		if (!result){
			fail("Yello");
		}
		
	}

}
