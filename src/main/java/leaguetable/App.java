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
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.io.Writer;
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

    /** Maximum positional arguments this CLI accepts: an input path and an output path. */
    private static final int MAX_ARGS = 2;

    /** JVM entry point; delegates to {@link #run} and exits non-zero on failure. */
    public static void main(String[] args) {
        int exitCode = run(args, System.in, System.out, System.err);
        if (exitCode != EXIT_OK) {
            System.exit(exitCode);
        }
    }

    /** Runs the CLI against the given streams; package-visible so tests can call it directly. */
    static int run(String[] args, InputStream stdin, PrintStream stdout, PrintStream stderr) {
        if (args.length > MAX_ARGS) {
            printUsage(stderr);
            return EXIT_USAGE_ERROR;
        }

        Path inputPath = args.length >= 1 ? Path.of(args[0]) : null;
        Path outputPath = args.length == MAX_ARGS ? Path.of(args[1]) : null;

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

    /** Opens the match-results source: {@code stdin} if no input path was given, otherwise the file. */
    private static Reader openReader(Path inputPath, InputStream stdin) throws IOException {
        if (inputPath == null) {
            return new InputStreamReader(stdin, StandardCharsets.UTF_8);
        }
        return new FileReader(inputPath.toFile(), StandardCharsets.UTF_8);
    }

    /** Opens the table destination: {@code stdout} if no output path was given, otherwise the file. */
    private static Writer openWriter(Path outputPath, PrintStream stdout) throws IOException {
        if (outputPath == null) {
            return new OutputStreamWriter(stdout, StandardCharsets.UTF_8);
        }
        return new BufferedWriter(new FileWriter(outputPath.toFile(), StandardCharsets.UTF_8));
    }

    /** Prints the usage message shown when the CLI is called with too many arguments. */
    private static void printUsage(PrintStream out) {
        out.println("Usage: league-table [input.csv] [output.csv]");
        out.println("  No arguments:        read match results CSV from stdin, write table CSV to stdout");
        out.println("  One argument:        read match results CSV from the given file, write table CSV to stdout");
        out.println("  Two arguments:       read match results CSV from the first file, write table CSV to the second");
    }

    private App() {
    }
}