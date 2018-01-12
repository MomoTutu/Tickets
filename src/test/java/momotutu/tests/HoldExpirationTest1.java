package momotutu.tests;
import static org.junit.Assert.*;


import momotutu.SeatHold;
import momotutu.SeatReservationService;

import org.junit.Test;


public class HoldExpirationTest1 {

	@Test
	public void test() throws InterruptedException {
		SeatReservationService a = new SeatReservationService(5, 9, 5000, SeatReservationService.DEFAULT_BEST_RATIO);
		a.printNum();
		a.printChar();
		
		System.out.println("available seats: " + a.numSeatsAvailable());
		
		
		SeatHold h1 = a.findAndHoldSeats(3, "x");
		System.out.println("hold id: " + h1.seatHoldId);
		System.out.println("available seats: " + a.numSeatsAvailable());
		//a.printNum();
		//a.printChar();
		
		SeatHold h2 = a.findAndHoldSeats(3, "y");
		System.out.println("hold id: " + h2.seatHoldId);
		System.out.println("available seats: " + a.numSeatsAvailable());
		//a.printNum();
		//a.printChar();
		
		System.out.print("Sleeping 5 seconds...");
		Thread.sleep(5500); // sleep 5.5 sec
		System.out.println(" done");
		
		
		String r1 = a.reserveSeats(h1.seatHoldId, "x");
		System.out.println("reservation: " + r1);
		System.out.println("available seats: " + a.numSeatsAvailable());
		
		if (r1 == null)
		{
			SeatHold h3 = a.findAndHoldSeats(3, "x");
			System.out.println("hold id: " + h3.seatHoldId);
			System.out.println("available seats: " + a.numSeatsAvailable());
			
			String r2 = a.reserveSeats(h3.seatHoldId, "x");
			System.out.println("available seats: " + a.numSeatsAvailable());
			
			SeatHold h4 = a.findAndHoldSeats(10, "x");
			System.out.println("available seats: " + a.numSeatsAvailable());
			
			a.printChar();
			
			if( r2 == null || h4 == null)
				fail("reservation expiration failed");
		} else {
			fail("successfully reserved when it should not.");
		}
	}
}
