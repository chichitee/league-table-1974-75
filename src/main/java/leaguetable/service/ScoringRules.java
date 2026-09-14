package leaguetable.service;

/**
 * The points awarded for each match outcome. Different eras and competitions
 * award different points for a win — English football used 2 points for a
 * win until the 1981/82 season, when it moved to today's familiar 3.
 */
public interface ScoringRules {

    int pointsForWin();

    int pointsForDraw();

    int pointsForLoss();

    /**
     * The rules in force in the English Football League First Division for
     * the 1974/75 season: two points for a win, one for a draw, none for a
     * defeat.
     */
    static ScoringRules englishFirstDivision1974_75() {
        return new ScoringRules() {
            @Override
            public int pointsForWin() {
                return 2;
            }

            @Override
            public int pointsForDraw() {
                return 1;
            }

            @Override
            public int pointsForLoss() {
                return 0;
            }
        };
    }

    static ScoringRules threePointsForWin() {
        // The modern (post-1981) three-points-for-a-win rule, kept for completeness/tests.
        return new ScoringRules() {
            @Override
            public int pointsForWin() {
                return 3;
            }

            @Override
            public int pointsForDraw() {
                return 1;
            }

            @Override
            public int pointsForLoss() {
                return 0;
            }
        };
    }
}
