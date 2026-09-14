package leaguetable;

import leaguetable.io.CsvMatchReader;
import leaguetable.io.CsvStandingsWriter;
import leaguetable.model.Match;
import leaguetable.model.TeamStanding;
import leaguetable.service.StandingsCalculator;

import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Command-line entry point.
 *
 * <p>Usage:
 * <pre>
 *   java -jar league-table.jar                      # stdin  -&gt; stdout
 *   java -jar league-table.jar results.csv          # file   -&gt; stdout
 *   java -jar league-table.jar results.csv table.csv # file   -&gt; file
 * </pre>
 *
 * <p>Exits with status 0 on success, 1 on a usage error, and 2 if the input
 * could not be parsed or read.
 */
public final class App {

    static final int EXIT_OK = 0;
    static final int EXIT_USAGE_ERROR = 1;
    static final int EXIT_DATA_ERROR = 2;

    public static void main(String[] args) {
        int exitCode = run(args, System.in, System.out, System.err);
        if (exitCode != EXIT_OK) {
            System.exit(exitCode);
        }
    }

    /**
     * Runs the CLI against the given streams. Package-visible and side-effect
     * free with respect to {@code System.exit} so it can be exercised
     * end-to-end from tests.
     */
    static int run(String[] args, java.io.InputStream stdin, java.io.PrintStream stdout, java.io.PrintStream stderr) {
        if (args.length > 2) {
            printUsage(stderr);
            return EXIT_USAGE_ERROR;
        }

        Path inputPath = args.length >= 1 ? Path.of(args[0]) : null;
        Path outputPath = args.length == 2 ? Path.of(args[1]) : null;

        if (inputPath != null && !Files.isReadable(inputPath)) {
            stderr.println("Cannot read input file: " + inputPath);
            return EXIT_USAGE_ERROR;
        }

        try (Reader reader = openReader(inputPath, stdin);
             Writer writer = openWriter(outputPath, stdout)) {

            List<Match> matches = new CsvMatchReader().read(reader);
            List<TeamStanding> table = StandingsCalculator.forEnglishFirstDivision1974_75()
                    .calculate(matches);
            new CsvStandingsWriter().write(table, writer);
            return EXIT_OK;

        } catch (UncheckedIOException | IOException e) {
            stderr.println("Error: " + e.getMessage());
            return EXIT_DATA_ERROR;
        } catch (RuntimeException e) {
            stderr.println("Error: " + e.getMessage());
            return EXIT_DATA_ERROR;
        }
    }

    private static Reader openReader(Path inputPath, java.io.InputStream stdin) throws IOException {
        if (inputPath == null) {
            return new InputStreamReader(stdin, StandardCharsets.UTF_8);
        }
        return new FileReader(inputPath.toFile(), StandardCharsets.UTF_8);
    }

    private static Writer openWriter(Path outputPath, java.io.PrintStream stdout) throws IOException {
        if (outputPath == null) {
            return new OutputStreamWriter(stdout, StandardCharsets.UTF_8);
        }
        return new BufferedWriter(new FileWriter(outputPath.toFile(), StandardCharsets.UTF_8));
    }

    private static void printUsage(java.io.PrintStream out) {
        out.println("Usage: league-table [input.csv] [output.csv]");
        out.println("  No arguments:        read match results CSV from stdin, write table CSV to stdout");
        out.println("  One argument:        read match results CSV from the given file, write table CSV to stdout");
        out.println("  Two arguments:       read match results CSV from the first file, write table CSV to the second");
    }

    private App() {
    }
}
