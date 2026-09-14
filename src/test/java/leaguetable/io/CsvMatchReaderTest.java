package leaguetable.io;

import leaguetable.model.Match;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CsvMatchReaderTest {

    private static final String HEADER = "Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals\n";

    private final CsvMatchReader reader = new CsvMatchReader();

    @Test
    void parsesValidCsv() {
        String csv = HEADER
                + "1974-08-17,Birmingham City,Middlesbrough,0,3\n"
                + "1974-08-17,Stoke City,Leeds United,3,0\n";

        List<Match> matches = reader.read(new StringReader(csv));

        assertEquals(2, matches.size());
        assertEquals(new Match(LocalDate.of(1974, 8, 17), "Birmingham City", "Middlesbrough", 0, 3),
                matches.get(0));
        assertEquals(new Match(LocalDate.of(1974, 8, 17), "Stoke City", "Leeds United", 3, 0),
                matches.get(1));
    }

    @Test
    void ignoresBlankLines() {
        String csv = HEADER
                + "\n"
                + "1974-08-17,Birmingham City,Middlesbrough,0,3\n"
                + "\n";

        assertEquals(1, reader.read(new StringReader(csv)).size());
    }

    @Test
    void headerIsCaseAndWhitespaceInsensitive() {
        String csv = "date, hometeam ,awayteam,HomeGoals,AWAYGOALS\n"
                + "1974-08-17,Birmingham City,Middlesbrough,0,3\n";

        assertEquals(1, reader.read(new StringReader(csv)).size());
    }

    @Test
    void supportsQuotedTeamNamesContainingCommas() {
        String csv = HEADER
                + "1974-08-17,\"Town, The\",Middlesbrough,0,3\n";

        List<Match> matches = reader.read(new StringReader(csv));
        assertEquals("Town, The", matches.get(0).homeTeam());
    }

    @Test
    void rejectsEmptyInput() {
        CsvParseException ex = assertThrows(CsvParseException.class,
                () -> reader.read(new StringReader("")));
        assertTrue(ex.getMessage().contains("empty"));
    }

    @Test
    void rejectsWrongHeader() {
        String csv = "Wrong,Header,Here\n1974-08-17,Birmingham,Middlesbrough,0,3\n";
        assertThrows(CsvParseException.class, () -> reader.read(new StringReader(csv)));
    }

    @Test
    void rejectsWrongFieldCount() {
        String csv = HEADER
                + "1974-08-17,Birmingham City,Middlesbrough,0\n";
        CsvParseException ex = assertThrows(CsvParseException.class,
                () -> reader.read(new StringReader(csv)));
        assertTrue(ex.getMessage().contains("5 fields"));
    }

    @Test
    void rejectsInvalidDate() {
        String csv = HEADER
                + "17-08-1974,Birmingham City,Middlesbrough,0,3\n";
        assertThrows(CsvParseException.class, () -> reader.read(new StringReader(csv)));
    }

    @Test
    void rejectsNonNumericGoals() {
        String csv = HEADER
                + "1974-08-17,Birmingham City,Middlesbrough,nil,3\n";
        assertThrows(CsvParseException.class, () -> reader.read(new StringReader(csv)));
    }

    @Test
    void rejectsNegativeGoals() {
        String csv = HEADER
                + "1974-08-17,Birmingham City,Middlesbrough,-1,3\n";
        assertThrows(CsvParseException.class, () -> reader.read(new StringReader(csv)));
    }

    @Test
    void errorMessageIncludesLineNumber() {
        String csv = HEADER
                + "1974-08-17,Birmingham City,Middlesbrough,0,3\n"
                + "1974-08-17,Stoke City,Leeds United,x,0\n";
        CsvParseException ex = assertThrows(CsvParseException.class,
                () -> reader.read(new StringReader(csv)));
        assertTrue(ex.getMessage().contains("Line 3"));
    }
}
