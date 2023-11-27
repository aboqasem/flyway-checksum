package me.zouabi;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.CRC32;
import me.zouabi.exceptions.FlywayChecksumCalculationException;

/**
 * Calculates the checksum of text in a similar way to Flyway.
 *
 * @see <a
 *     href="https://github.com/flyway/flyway/blob/d16fb6670408586664043ccac3f2378cc38d8c97/flyway-core/src/main/java/org/flywaydb/core
 *     /internal/resolver/ChecksumCalculator.java#L31">
 *     ChecksumCalculator
 *     </a>
 */
public class FlywayChecksumCalculator {

  /**
   * Calculates the checksum of the text behind the given reader.
   *
   * @param reader the reader to read the text from
   * @return the checksum of the text
   * @throws FlywayChecksumCalculationException if the checksum could not be calculated
   */
  public int calculateChecksum(Reader reader) {
    final var crc32 = new CRC32();

    try (final var bufferedReader = new BufferedReader(reader, 4096)) {
      String line = bufferedReader.readLine();

      if (line != null) {
        line = filterByteOrderMark(line);
        do {
          crc32.update(line.getBytes(StandardCharsets.UTF_8));
        } while ((line = bufferedReader.readLine()) != null);
      }
    } catch (IOException e) {
      throw new FlywayChecksumCalculationException("Unable to calculate checksum", e);
    }

    return (int) crc32.getValue();
  }

  private static String filterByteOrderMark(String line) {
    if (line != null && !line.isEmpty() && (line.charAt(0) == '\uFEFF')) {
      return line.substring(1);
    }

    return line;
  }

  @SuppressWarnings({"java:S106", "java:S112"})
  public static void main(String[] args) {
    final var calculator = new FlywayChecksumCalculator();

    Reader reader;
    if (args.length > 1) {
      throw new IllegalArgumentException("Expected zero or one arguments");
    } else if (args.length == 1) {
      try {
        reader = new FileReader(args[0]);
        System.out.println("Calculating checksum of file...");
      } catch (FileNotFoundException e) {
        System.out.println("Calculating checksum of string...");
        reader = new StringReader(args[0]);
      }
    } else {
      System.out.println("Calculating checksum of stdin...");
      reader = new InputStreamReader(System.in);
    }

    final int checksum = calculator.calculateChecksum(reader);

    try {
      reader.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    System.out.println("Checksum: " + checksum);
  }

}
