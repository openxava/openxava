package org.openxava.tools;

import java.io.*;
import java.net.*;
import java.nio.charset.*;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

/**
 * Utility to fetch the official MDI Upgrade Guide and update
 * src/main/resources/xava-mdi-icon-mapping.properties with all
 * known renames and removal-with-suggestion pairs.
 *
 * Safe merging policy:
 * - If a key already exists in the mapping file, we KEEP the existing value
 *   and log a WARN if it differs from upstream.
 * - New pairs are appended (alphabetically) under the existing content,
 *   preserving the header.
 *
 * @author Javier Paniza
 * @since 7.6
 */
class GenerateMDIMapping {

    private static final String UPGRADE_URL = "https://pictogrammers.com/docs/library/mdi/releases/upgrade/";
    private static final Path MAPPING_PATH = Paths.get("src/main/resources/xava-mdi-icon-mapping.properties");

    public static void main(String[] args) throws Exception {
        System.out.println("Fetching: " + UPGRADE_URL);
        String html = fetch(UPGRADE_URL);
        Map<String,String> upstream = extractPairs(html);
        System.out.println("Found upstream pairs: " + upstream.size());

        List<String> header = new ArrayList<String>();
        Map<String,String> current = new LinkedHashMap<String,String>();
        if (Files.exists(MAPPING_PATH)) {
            // Read manually to preserve header comments
            List<String> lines = Files.readAllLines(MAPPING_PATH, StandardCharsets.UTF_8);
            boolean inHeader = true;
            for (String line : lines) {
                String t = line.trim();
                if (inHeader && (t.isEmpty() || t.startsWith("#"))) {
                    header.add(line);
                    continue;
                }
                inHeader = false;
                if (t.isEmpty() || t.startsWith("#")) continue;
                int idx = line.indexOf('=');
                if (idx > 0) {
                    String k = line.substring(0, idx).trim();
                    String v = line.substring(idx + 1).trim();
                    if (!k.isEmpty() && !v.isEmpty()) current.put(k, v);
                }
            }
        }

        int added = 0;
        int conflicted = 0;
        for (Map.Entry<String,String> e : upstream.entrySet()) {
            String k = e.getKey();
            String v = e.getValue();
            if (current.containsKey(k)) {
                String old = current.get(k);
                if (!old.equals(v)) {
                    conflicted++;
                    System.out.println("WARN: keeping existing mapping " + k + "=" + old + " (upstream suggests " + v + ")");
                }
            } else {
                current.put(k, v);
                added++;
            }
        }

        // Write back: header + timestamp + sorted mappings
        List<String> out = new ArrayList<String>();
        if (!header.isEmpty()) out.addAll(header);
        else out.add("# xava-mdi-icon-mapping.properties (auto-merged)\n");
        out.add("# Auto-merge at " + new Date());

        List<String> keys = new ArrayList<String>(current.keySet());
        Collections.sort(keys);
        for (String k : keys) {
            out.add(k + "=" + current.get(k));
        }
        Files.write(MAPPING_PATH, out, StandardCharsets.UTF_8,
            StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);

        System.out.println("Added: " + added + ", conflicts (kept ours): " + conflicted);
        System.out.println("Done. Updated: " + MAPPING_PATH.toAbsolutePath());
    }

    private static String fetch(String url) throws IOException {
        HttpURLConnection con = (HttpURLConnection) new URL(url).openConnection();
        con.setConnectTimeout(15000);
        con.setReadTimeout(20000);
        con.setRequestProperty("User-Agent", "OpenXava-MDI-Mapping-Updater");
        try (InputStream in = con.getInputStream(); ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[8192];
            int n;
            while ((n = in.read(buffer)) != -1) {
                bos.write(buffer, 0, n);
            }
            return new String(bos.toByteArray(), StandardCharsets.UTF_8);
        }
    }

    private static Map<String,String> extractPairs(String html) {
        Map<String,String> map = new LinkedHashMap<String,String>();
        String[] lines = html.split("\n");
        for (String raw : lines) {
            String line = stripTags(raw).trim();
            if (line.isEmpty()) continue;

            // Bullet items may have leading '-' or whitespace
            line = line.replaceAll("^[-•\\s]+", "");

            // Case 1: old to new
            int toIdx = line.indexOf(" to ");
            if (toIdx > 0) {
                String left = line.substring(0, toIdx).trim();
                String right = line.substring(toIdx + 4).trim();
                if (isIcon(left) && isIcon(right)) map.put(left, right);
                continue;
            }
            // Case 2: old - Use new instead.
            Matcher m = Pattern.compile("^([a-z0-9\\-]+)\\s+\\-\\s+Use\\s+([a-z0-9\\-]+)\\s+instead\\.", Pattern.CASE_INSENSITIVE)
                               .matcher(line);
            if (m.find()) {
                String left = m.group(1).trim();
                String right = m.group(2).trim();
                if (isIcon(left) && isIcon(right)) map.put(left, right);
            }
        }
        return map;
    }

    private static boolean isIcon(String s) {
        return s.matches("[a-z0-9\\-]+") && s.length() >= 2;
    }

    private static String stripTags(String s) {
        // Remove HTML tags if any
        return s.replaceAll("<[^>]+>", "");
    }
}
