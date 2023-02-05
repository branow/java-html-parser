import com.branow.parser.TegParser;
import com.branow.parser.Teg;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TegParserTest {

    private static final int COUNT_FILES = 4, COUNT_PAGES = 5;
    private static final String
            FILE_PATH = "src/test/java/files/html-text/[TYPE]-html-[NUM].txt",
            HTML_PAGES_PATH = "src/test/java/files/html-pages/html-[TYPE]-[NUM].txt",
            EXPECTED = "expected", SRC = "src", ACTUAL= "actual", TYPE = "[TYPE]", NUM = "[NUM]";

    @Test
    public void testParsingAndTransformationToString() {
        for (int i=1; i<=COUNT_FILES; i++) {
            String expected = getData(i, EXPECTED).replaceAll(" {4}", "\t");
            String actual = TegParser.parseHTML(getData(i, SRC)).toStringHTML();
            Assertions.assertEquals(expected, actual);
        }
    }

    @Test
    public void testParsing() {
        for (int i=1; i<=COUNT_PAGES; i++) {
            Teg teg = TegParser.parseHTML(getDataOfPage(i));
            Assertions.assertNotNull(teg);
            String data = teg.toStringHTML();
            Assertions.assertFalse(data.isEmpty());
            writeDataToPage(i, data);
        }
    }

    private String getData(int num, String type) {
        String path = FILE_PATH.replace(TYPE, type).replace(NUM, num + "");
        return new FileText(path).read();
    }

    private String getDataOfPage(int num) {
        String path = HTML_PAGES_PATH.replace(TYPE, SRC).replace(NUM, num + "");
        return new FileText(path).read();
    }

    private void writeDataToPage(int num, String data) {
        String path = HTML_PAGES_PATH.replace(TYPE, ACTUAL).replace(NUM, num + "");
        new FileText(path).write(data);
    }

}
