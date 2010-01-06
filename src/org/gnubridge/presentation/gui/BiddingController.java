package org.gnubridge.presentation.gui;

import org.gnubridge.core.Direction;
import org.gnubridge.core.Deal;
import org.gnubridge.core.Hand;
import org.gnubridge.core.North;
import org.gnubridge.core.Player;
import org.gnubridge.core.South;
import org.gnubridge.core.West;
import org.gnubridge.core.bidding.Auctioneer;
import org.gnubridge.core.bidding.Bid;
import org.gnubridge.core.bidding.BiddingAgent;

public class BiddingController {

	private final BiddingView view;
	private final Deal cardHolder;
	private final Auctioneer auction;
	private final Player human;
	private final MainController parent;

	public BiddingController(BiddingView v, MainController p) {
		view = v;
		view.setController(this);
		view.show();
		parent = p;
		cardHolder = Deal.construct();
		auction = new Auctioneer(West.i());
		view.setAuction(auction);
		human = cardHolder.selectHumanPlayer();
		view.setCards(new Hand(human.getHand()));
		doAutomatedBidding();

		// fake bidding to get to the other page
		//		auction.bid(new Bid(7, NoTrump.i()));
		//		doAutomatedBidding();
		//        playGame();
	}

	public Deal getCardHolder() {
		return cardHolder;
	}

	public Auctioneer getAuction() {
		return auction;
	}

	public Player getHuman() {
		return human;
	}

	private void doAutomatedBidding() {
		while (!auction.biddingFinished() && !auction.getNextToBid().equals(human.getDirection2())) {
			Hand hand = new Hand(cardHolder.getPlayer(auction.getNextToBid().getValue()).getHand());
			BiddingAgent ba = new BiddingAgent(auction, hand);
			auction.bid(ba.getBid());
			view.auctionStateChanged();
		}

	}

	public void placeBid(int bidSize, String trump) {
		if (!auction.biddingFinished()) {
			if (!auction.getNextToBid().equals(human.getDirection2())) {
				view.display("Not your turn to bid");
				return;
			}
			Bid candidate = Bid.makeBid(bidSize, trump);
			if (!auction.isValid(candidate)) {
				view.display("Invalid bid");
				return;
			}
			auction.bid(candidate);
			view.display("Bid placed:" + candidate);
			view.auctionStateChanged();
			doAutomatedBidding();
		}
		if (auction.biddingFinished()) {
			String message = "BIDDING COMPLETE.";
			if (auction.getHighBid() != null) {
				message += " High bid: " + auction.getHighBid();
			} else {
				message += " No contract reached. Cannot play game.";
			}
			view.display(message);
		}
	}

	public void playGame() {
		view.hide();
		parent.playGame();

	}

	public Direction allowHumanToPlayIfDummy() {
		Direction newHuman = auction.getDummyOffsetDirection(getHuman().getDirection2());
		if (North.i().equals(newHuman)) {
			newHuman = South.i();
		}
		return newHuman;
	}

	public void newGame() {
		view.hide();
		parent.newGame();

	}
}
