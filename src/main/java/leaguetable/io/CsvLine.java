package leaguetable.io;

import java.util.ArrayList;
import java.util.List;

/**
 * A tiny, dependency-free CSV line splitter supporting just enough of
 * RFC 4180 for this project's needs: comma-separated fields, optionally
 * wrapped in double quotes when a field itself contains a comma, with
 * {@code ""} as an escaped literal quote.
 */
final class CsvLine {

    private CsvLine() {
    }

    /** Splits a CSV line into fields, handling quoted fields and escaped quotes. */
    static List<String> split(String line) {
        List<String> fields = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);

            if (inQuotes) {
                if (c == '"') {
                    boolean nextIsQuote = i + 1 < line.length() && line.charAt(i + 1) == '"';
                    if (nextIsQuote) {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    fields.add(current.toString());
                    current.setLength(0);
                } else {
                    current.append(c);
                }
            }
        }
        fields.add(current.toString());
        return fields;
    }
}