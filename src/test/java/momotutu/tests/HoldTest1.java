package momotutu.tests;

import momotutu.SeatReservationService;

import org.junit.Test;


public class HoldTest1 {

	@Test
	public void test() {
		SeatReservationService a = new SeatReservationService();
		a.findAndHoldSeats(3, "x");
		a.printNum();
		a.printChar();
		a.findAndHoldSeats(50, "x");
		a.printNum();
		a.printChar();
	}

}
