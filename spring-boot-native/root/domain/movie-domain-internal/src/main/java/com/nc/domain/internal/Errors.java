package com.nc.domain.internal;

import com.nc.domain.base.FrontEndTranslatedError;

public interface Errors {

	static class Impl implements Errors {
		private static final Impl instance = new Impl();

		static DomainException make(String msg) {
			return new DomainException(msg, new FrontEndTranslatedError(msg));
		}

		private Impl() {

		}

		@Override
		public DomainException cantFinishRoundNotStarted() {
			var msg = "Attempted to finish round that has not started";

			return make(msg);
		}

		@Override
		public DomainException invalidConstraintValues(int maxErrors, int maxRounds) {
			var msg = String.format("Invalid Constraint Values (%d,%d)", maxErrors, maxRounds);

			return make(msg);
		}

		@Override
		public DomainException invalidPair(Movie first) {
			var msg = String.format("Attempted to add pair of identical movies (id:%d,title:%s)", first.getId(), first.getTitle());

			return make(msg);
		}

		@Override
		public DomainException invalidTopPlayerRequest(int max, int page) {
			var msg = String.format("Invalid parameters to fetch top players (%d,%d)", max, page);

			return make(msg);
		}

		@Override
		public DomainException matchAlreadyFinished() {
			var msg = "Match is already finished";

			return make(msg);
		}

		@Override
		public DomainException maxRoundsReached(int max) {
			var msg = String.format("Max rounds for match (%d) already reached", max);

			return make(msg);
		}

		@Override
		public DomainException movieNotFound(int id) {
			var msg = String.format("No movie with id", id);

			return make(msg);
		}

		@Override
		public DomainException multipleActiveMatches(AppUser u, int size) {
			var msg = String.format("Multiple active matches (%d) for %s", size, u.getName());

			return make(msg);
		}

		@Override
		public DomainException multiplePendingRounds(Match match, int size) {
			var msg = String.format("Multiple pending rounds (%d) for match %d", size, match.getId());

			return make(msg);
		}

		@Override
		public DomainException notEnoughMovies(long count, int minRounds) {
			var msg = String.format("Found %d movies in database. Not enough to start game with %d rounds", count, minRounds);

			return make(msg);
		}

		@Override
		public DomainException pendingTail(Match match, Round tail) {
			var msg = String.format("Match %d has pending round (id:%d,started:%s)", match.getId(), tail.getId(), tail.getStarted());

			return make(msg);
		}

		@Override
		public DomainException roundAlreadyFinished() {
			var msg = "Attempted to finish round that is already finished";

			return make(msg);
		}

		@Override
		public DomainException roundFinishingWithInvalidSelection(int selected) {
			var msg = String.format("Round cannot be finished with selection %d", selected);

			return make(msg);
		}

		@Override
		public DomainException seenHasPair(Match m, Pair pair) {
			var msg = String.format("Match (%d) has already seen Pair (%d,%d)", m.getId(), pair.first(), pair.second());

			return make(msg);
		}

		@Override
		public DomainException unableToGenerateSample() {
			var msg = "Unable to generate sample";

			return make(msg);
		}

		@Override
		public DomainException unsupportedCategory(String category) {
			var msg = String.format("At the moment only %s supported. We might add %s in the future", "movie", category);

			return make(msg);
		}

		@Override
		public DomainException userHasAlreadyFinishedMatch(AppUser user, Match active) {
			var msg = String.format("User %d has already finished match %d", user.getId(), active.getId());

			return make(msg);
		}

		@Override
		public DomainException userHasNotStartedMatch(AppUser user) {
			var msg = String.format("User %d has not started a match yet", user.getId());

			return make(msg);
		}

		@Override
		public DomainException userNotBound() {
			var msg = "User not bound. Should authenticate first";
			return make(msg);
		}

		@Override
		public DomainException userNotFinishedMatch(AppUser user, Match active) {
			var msg = String.format("User (id:%d,name:%s) has a pending match (%d)", user.getId(), user.getName(), active.getId());

			return make(msg);
		}
	}

	public static Errors get() {
		return Impl.instance;
	}

	DomainException cantFinishRoundNotStarted();

	DomainException invalidConstraintValues(int maxErrors, int maxRounds);

	DomainException invalidPair(Movie first);

	DomainException invalidTopPlayerRequest(int max, int page);

	DomainException matchAlreadyFinished();

	DomainException maxRoundsReached(int max);

	DomainException movieNotFound(int id);

	DomainException multipleActiveMatches(AppUser u, int size);

	DomainException multiplePendingRounds(Match match, int size);

	DomainException notEnoughMovies(long count, int minRounds);

	DomainException pendingTail(Match match, Round tail);

	DomainException roundAlreadyFinished();

	DomainException roundFinishingWithInvalidSelection(int selected);

	DomainException seenHasPair(Match m, Pair pair);

	DomainException unableToGenerateSample();

	DomainException unsupportedCategory(String category);

	DomainException userHasAlreadyFinishedMatch(AppUser user, Match active);

	DomainException userHasNotStartedMatch(AppUser user);

	DomainException userNotBound();

	DomainException userNotFinishedMatch(AppUser user, Match active);
}