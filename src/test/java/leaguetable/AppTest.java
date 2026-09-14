package leaguetable;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** End-to-end tests driving {@link App} the same way a user would from a shell. */
class AppTest {

    private static final String SAMPLE_CSV =
            "Date,HomeTeam,AwayTeam,HomeGoals,AwayGoals\n"
            + "1974-08-17,Stoke City,Leeds United,3,0\n"
            + "1974-08-17,Everton,Derby County,0,0\n";

    @Test
    void readsFromStdinAndWritesToStdout() {
        ByteArrayInputStream stdin = new ByteArrayInputStream(SAMPLE_CSV.getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream stdoutBytes = new ByteArrayOutputStream();
        ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();

        int exitCode = App.run(new String[0], stdin, new PrintStream(stdoutBytes), new PrintStream(stderrBytes));

        assertEquals(App.EXIT_OK, exitCode);
        String output = stdoutBytes.toString(StandardCharsets.UTF_8);
        assertTrue(output.startsWith("Position,Team,Played,Won,Drawn,Lost,GoalsFor,GoalsAgainst,GoalAverage,Points"));
        assertTrue(output.contains("Stoke City"));
        assertTrue(output.contains("Everton"));
    }

    @Test
    void readsFromFileAndWritesToFile(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Path output = tempDir.resolve("output.csv");
        Files.writeString(input, SAMPLE_CSV);

        ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();
        int exitCode = App.run(
                new String[] {input.toString(), output.toString()},
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream()),
                new PrintStream(stderrBytes));

        assertEquals(App.EXIT_OK, exitCode);
        String output1 = Files.readString(output);
        assertTrue(output1.contains("Stoke City"));
        assertTrue(output1.contains("Everton"));
    }

    @Test
    void readsFromFileAndWritesToStdoutWhenOnlyOneArgumentGiven(@TempDir Path tempDir) throws IOException {
        Path input = tempDir.resolve("input.csv");
        Files.writeString(input, SAMPLE_CSV);

        ByteArrayOutputStream stdoutBytes = new ByteArrayOutputStream();
        int exitCode = App.run(
                new String[] {input.toString()},
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(stdoutBytes),
                new PrintStream(new ByteArrayOutputStream()));

        assertEquals(App.EXIT_OK, exitCode);
        assertTrue(stdoutBytes.toString(StandardCharsets.UTF_8).contains("Stoke City"));
    }

    @Test
    void exitsWithUsageErrorForTooManyArguments() {
        ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();
        int exitCode = App.run(
                new String[] {"a", "b", "c"},
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream()),
                new PrintStream(stderrBytes));

        assertEquals(App.EXIT_USAGE_ERROR, exitCode);
        assertTrue(stderrBytes.toString(StandardCharsets.UTF_8).contains("Usage"));
    }

    @Test
    void exitsWithUsageErrorForMissingInputFile() {
        ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();
        int exitCode = App.run(
                new String[] {"/no/such/file.csv"},
                new ByteArrayInputStream(new byte[0]),
                new PrintStream(new ByteArrayOutputStream()),
                new PrintStream(stderrBytes));

        assertEquals(App.EXIT_USAGE_ERROR, exitCode);
        assertTrue(stderrBytes.toString(StandardCharsets.UTF_8).contains("Cannot read input file"));
    }

    @Test
    void exitsWithDataErrorForMalformedCsv() {
        ByteArrayInputStream stdin = new ByteArrayInputStream(
                "not,a,valid,header".getBytes(StandardCharsets.UTF_8));
        ByteArrayOutputStream stderrBytes = new ByteArrayOutputStream();

        int exitCode = App.run(new String[0], stdin, new PrintStream(new ByteArrayOutputStream()),
                new PrintStream(stderrBytes));

        assertEquals(App.EXIT_DATA_ERROR, exitCode);
        assertTrue(stderrBytes.toString(StandardCharsets.UTF_8).contains("Error"));
    }
}
