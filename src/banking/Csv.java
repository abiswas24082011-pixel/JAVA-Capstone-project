package banking;

import java.util.ArrayList;
import java.util.List;

public final class Csv {
    private Csv() {
    }

    public static String join(String... fields) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < fields.length; i++) {
            if (i > 0) {
                builder.append(',');
            }
            builder.append(escape(fields[i]));
        }
        return builder.toString();
    }

    public static String[] parse(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;

        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        current.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    current.append(c);
                }
            } else if (c == ',') {
                values.add(current.toString());
                current.setLength(0);
            } else if (c == '"') {
                inQuotes = true;
            } else {
                current.append(c);
            }
        }

        values.add(current.toString());
        return values.toArray(new String[0]);
    }

    private static String escape(String value) {
        String safe = value == null ? "" : value;
        boolean needsQuotes = safe.contains(",") || safe.contains("\"") || safe.contains("\n") || safe.contains("\r");
        if (safe.contains("\"")) {
            safe = safe.replace("\"", "\"\"");
        }
        return needsQuotes ? "\"" + safe + "\"" : safe;
    }
}

