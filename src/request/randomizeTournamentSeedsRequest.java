package request;
/**
 * @author Nicholas Hauschild
 *         Date: 5/16/13
 *         Time: 11:27 PM
 */
public class randomizeTournamentSeedsRequest {
    public final String tournamentUrlPath;

    public randomizeTournamentSeedsRequest(final String tournamentUrlPath) {
        this.tournamentUrlPath = tournamentUrlPath;
    }

    public String getTournamentUrlPath() {
        return tournamentUrlPath;
    }
}
