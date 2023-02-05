package com.branow.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TegParser {

    private static final Pattern HEADER = Pattern.compile("<[^!<>]+/?>");

    public static Teg parseHTML (String html) {
        return new TegParser(html).parse();
    }

    private String html;

    public TegParser(String html) {
        this.html = html;
    }

    public Teg parse() {
        html = removeStyleAndScriptTegs(html);
        Substring sub = new Substring(0, html.length());
        Substring header = getHeader(sub);
        if (header == null) return null;
        return parse(sub, header);
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    private String removeStyleAndScriptTegs(String html) {
        String[] tegs = {"script", "style"};
        for (String teg: tegs) {
            html = removeTeg(html, teg);
        }
        return html;
    }

    private String removeTeg(String html, String teg) {
        String start = "<" + teg;
        String end = "</" + teg + ">";
        StringBuilder sb = new StringBuilder(html);
        for (int s=0, e=0; s<sb.length() && e<sb.length(); ) {
            sb.delete(s, e);
            s = sb.indexOf(start, e - (e - s));
            e = sb.indexOf(end, s);
            if (s < 0 || e < 0) break;
            e += end.length();
        }
        return sb.toString();
    }

    private Teg parse(Substring sub, Substring header) {
        Teg teg = new Teg(getName(header));
        teg.getProperties().addAll(getProperties(header));

        sub.setStart(header.finish);
        if (!isSingular(header)) {
            parseInnerHTML(sub, teg);
        }
        return teg;
    }

    private void parseInnerHTML(Substring sub, Teg teg) {
        Substring childHeader = getHeader(sub);
        Substring footer = getFooter(teg, sub);
        while (childHeader != null && footer != null && childHeader.start < footer.start) {
            addInnerText(sub.start, childHeader.start, teg);
            sub.setStart(childHeader.start);
            Teg child = parse(sub, childHeader);
            child.setParent(teg);
            teg.getChildren().add(child);

            childHeader = getHeader(sub);
            footer = getFooter(teg, sub);
        }

        if (footer != null) {
            addInnerText(sub.start, footer.start, teg);
            sub.setStart(footer.finish);
        }
    }

    private void addInnerText(int start, int finish, Teg teg) {
        String innerText = html.substring(start, finish);
        teg.getInnerText().add(cleanInnerText(innerText));
    }

    private String cleanInnerText(String text) {
        text = text.replaceAll("[\n\t]", " ");
        text = text.replaceAll(" {2,}", " ");
        text = text.strip();
        return text;
    }

    private Substring getFooter(Teg teg, Substring sub) {
        String footer = getFooterString(teg.getName());
        int start = html.indexOf(footer, sub.start);
        if (start<0) return null;
        int finish = start + footer.length();
        return new Substring(start, finish);
    }

    private String getFooterString(String name) {
        return "</" + name + ">";
    }

    private boolean isSingular(Substring header) {
        return header.get().endsWith("/>");
    }

    private Substring getHeader(Substring sub) {
        Matcher matcher = HEADER.matcher(html);
        boolean find = matcher.find(sub.start);
        if (!find) return null;
        return new Substring(matcher.start(), matcher.end());
    }

    private String cleanHeader(String header) {
        header = header.substring(1, header.length() -1);
        if (header.endsWith("/"))
            header = header.substring(0, header.length() - 1);
        return header;
    }

    private String getName(Substring header) {
        return cleanHeader(header.get()).split(" ")[0];
    }

    private List<Teg.Property> getProperties(Substring header) {
        List<String> data = splitToProperties(cleanHeader(header.get()));
        List<Teg.Property> properties = new ArrayList<>();
        for (int i=1; i<data.size(); i++) {
            Teg.Property property = getProperty(data.get(i));
            if (property == null) continue;
            properties.add(property);
        }
        return properties;
    }

    private List<String> splitToProperties(String head) {
        List<String> prop = new ArrayList<>();
        int last = 0;
        char[] chars = head.toCharArray();
        boolean inQuotation = false;
        for (int i=0; i<chars.length; i++) {
            if (chars[i] == '\"') {
                inQuotation = !inQuotation;
            } else if (chars[i] == ' ' && !inQuotation) {
                prop.add(head.substring(last, i));
                last = i+1;
            }
        }
        prop.add(head.substring(last));
        return prop;
    }

    private Teg.Property getProperty(String property) {
        property = property.replaceAll("\n", "");
        int index = property.indexOf("=");
        if (index < 0) return null;
        String name = property.substring(0, index);
        String value = property.substring(index+1).replaceAll("\"", "");
        return new Teg.Property(name, value);
    }

    private class Substring {

        private int start;
        private int finish;

        public Substring() {
            this(0, html.length());
        }

        public Substring(int start, int finish) {
            this.start = start;
            this.finish = finish;
        }

        public String get() {
            return html.substring(start, finish);
        }

        public int getStart() {
            return start;
        }

        public void setStart(int start) {
            this.start = start;
        }

        public int getFinish() {
            return finish;
        }

        public void setFinish(int finish) {
            this.finish = finish;
        }

    }

}
