package momotutu;

/**
 * Class for holding seats in a venue.
 * Stores the hold id, and the seats tied to the hold id.
 *
 */
public class SeatHold {
	/**
	 * Seat hold id guaranteeing you seats to a venue until expiration or a new seat search.
	 */
	public final int seatHoldId;
	
	/**
	 * Seats being held for reservation corresponding to the seat hold id.
	 */
	public final int[][] seatsHeld;

	SeatHold(int seatHoldId, int [][] seatsHeld) {
		this.seatHoldId = seatHoldId;
		this.seatsHeld = seatsHeld;
	}
}
