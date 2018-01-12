package momotutu.tests;
import static org.junit.Assert.*;

import momotutu.SeatHold;
import momotutu.SeatReservationService;

import org.junit.Test;


public class ReservationTest1 {

	@Test
	public void test() {
		SeatReservationService a = new SeatReservationService();
		//a.printNum();
		//a.printChar();
		
		SeatHold h1 = a.findAndHoldSeats(3, "x");
		System.out.println("hold id: " + h1.seatHoldId);
		//a.printNum();
		//a.printChar();
		
		SeatHold h2 = a.findAndHoldSeats(3, "y");
		System.out.println("hold id: " + h2.seatHoldId);
		//a.printNum();
		//a.printChar();
		
		
		String r1 = a.reserveSeats(h1.seatHoldId, "x");
		System.out.println("reservation: " + r1);
		
		a.printChar();
		
		if (r1 == null)
			fail("Reservation failed");
	}
}
