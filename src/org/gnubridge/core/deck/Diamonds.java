package org.gnubridge.core.deck;


public class Diamonds extends Color {

	private static Diamonds instance;

	private Diamonds() {
		super();
	}
	
	public static Diamonds i() {
		if (instance == null) {
			instance = new Diamonds();
		}
		return instance ;
	}

	@Override
	public String toString() {
		return "DIAMONDS";
	}

}
