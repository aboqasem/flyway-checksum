import java.io.StringReader;
import java.util.stream.Stream;
import me.zouabi.FlywayChecksumCalculator;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FlywayChecksumCalculatorTest {

  @ParameterizedTest
  @MethodSource("stringsSource")
  void testCalculateChecksumWithText(String text, int expectedChecksum) {
    final var calculator = new FlywayChecksumCalculator();

    final var reader = new StringReader(text);
    final var checksum = calculator.calculateChecksum(reader);
    reader.close();

    assertEquals(expectedChecksum, checksum);
  }

  private static Stream<Arguments> stringsSource() {
    return Stream.of(
        Arguments.of("SELECT * FROM table", -367416687),
        Arguments.of("SELECT * FROM table\n", -367416687),
        Arguments.of("SELECT * FROM table\n  ", -1318449887),
        Arguments.of("SELECT * FROM table\n  -- comment", 1275075960),
        Arguments.of("SELECT * FROM table\n  -- comment\n  ", 1809040818),
        Arguments.of("\uFEFFSELECT * FROM table", -367416687),
        Arguments.of("\uFEFFSELECT * FROM table\n", -367416687),
        Arguments.of("\uFEFFSELECT * FROM table\n  ", -1318449887),
        Arguments.of("\uFEFFSELECT * FROM table\n  -- comment", 1275075960),
        Arguments.of("\uFEFFSELECT * FROM table\n  -- comment\n  ", 1809040818)
    );
  }

}
