package momotutu.tests;

import momotutu.SeatReservationService;

import org.junit.Test;


public class HoldTest2 {

	@Test
	public void test() {
		SeatReservationService a = new SeatReservationService();
		a.findAndHoldSeats(100, "x");
		a.printNum();
		a.printChar();
		a.findAndHoldSeats(133, "x");
		
		a.findAndHoldSeats(2, "x");
		a.printNum();
		a.printChar(); 
	}

}
