import com.branow.parser.TegParser;
import com.branow.parser.Teg;
import com.branow.parser.TegWalker;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static com.branow.parser.TegWalker.TegField;
import static com.branow.parser.TegWalker.Condition;

public class TegWalkerTest {

    private static final String
            HTML_PAGES_PATH = "src/test/java/files/html-pages/html-src-[NUM].txt",
            HTML_SRC_PATH = "src/test/java/files/html-for-walker/src[NUM].txt",
            NUM = "[NUM]";

    @Test
    public void test() {
        for (int i=1; i<=2; i++) {
            TegWalker walker = new TegWalker(TegParser.parseHTML(getDataSrc(i)));
            checkWalker(walker, i);
        }
    }

    private String getDataSrc(int num) {
        String path = HTML_PAGES_PATH.replace(NUM, num + "");
        return new FileText(path).read();
    }

    private void checkWalker(TegWalker walker, int n) {
        switch (n) {
            case 1 -> checkWalker1(walker);
            case 2 -> checkWalker2(walker);
        }
    }

    private void checkWalker1(TegWalker walker) {
        String scr = getDataExpected(1);
        Teg expected = TegParser.parseHTML(scr);

        Teg find = new Teg(expected.getName());
        Condition condition = Condition.EXACT;
        condition.setFields(TegField.NAME);
        check(walker, expected, find, condition);

        find = new Teg(expected.getName(), new ArrayList<>(), expected.getInnerText());
        condition.setFields(TegField.NAME, TegField.INNER_TEXT);
        checkOrderly(walker, expected, find, condition);

        find = new Teg(expected.getName(), expected.getProperties(), expected.getInnerText(), new Teg(""),
                expected.getChildren());
        condition.setFields(TegField.NAME, TegField.INNER_TEXT, TegField.PROPERTIES, TegField.CHILDREN);
        checkOrderly(walker, expected, find, condition);

        condition.setFields(TegField.values());
        Teg actual4 = walker.find(find, condition);
        Assertions.assertNull(actual4);
    }

    private void checkWalker2(TegWalker walker) {
        String scr = getDataExpected(2);
        List<Teg> expected = TegParser.parseHTML(scr).getChildren();
        Teg teg = expected.get(0);

        Teg find = new Teg("", new ArrayList<>(), teg.getInnerText(), teg.getParent());
        Condition condition = Condition.PARTIAL;
        condition.setFields(TegField.PARENT);
        check(walker, expected, find, condition);

        find = new Teg(teg.getName(), new ArrayList<>(), teg.getInnerText(), null, teg.getChildren());
        condition.setFields(TegField.CHILDREN);
        checkOrderly(walker, expected.subList(0, 1), find, condition);

        condition = Condition.FULL;

        find = new Teg(teg.getName(), teg.getProperties().subList(0, 2), teg.getInnerText(), null, new ArrayList<>());
        condition.setFields(TegField.INNER_TEXT, TegField.PROPERTIES);
        check(walker, expected, find, condition);

        find = new Teg(teg.getName(), new ArrayList<>(), null, teg.getParent());
        condition.setFields(TegField.NAME, TegField.PARENT);
        checkOrderly(walker, expected, find, condition);

        find = new Teg(null, new ArrayList<>(), null, null, teg.getChildren().subList(0, 1));
        condition.setFields(TegField.PROPERTIES, TegField.CHILDREN);
        checkOrderly(walker, expected.subList(0, 1), find, condition);
    }

    private void check(TegWalker walker, Teg expected, Teg find, Condition condition) {
        check(expected, walker.find(find, condition));
    }

    private void checkOrderly(TegWalker walker, Teg expected, Teg find, Condition condition) {
        check(expected, walker.find(find, condition));
    }

    private void check(Teg expected, Teg actual) {
        Assertions.assertEquals(expected, actual);
    }

    private void check(TegWalker walker, List<Teg> expected, Teg find, Condition condition) {
        check(expected, walker.findAll(find, condition));
    }

    private void checkOrderly(TegWalker walker, List<Teg> expected, Teg find, Condition condition) {
        check(expected, walker.findAllOrderly(find, condition));
    }

    private void check(List<Teg> expected, List<Teg> actual) {
        Assertions.assertEquals(expected, actual);
    }

    private String getDataExpected(int num) {
        String path = HTML_SRC_PATH.replace(NUM, num + "");
        return new FileText(path).read();
    }
}
