package com.branow.parser;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.branow.parser.StringConstructor.*;
import static com.branow.parser.StringConstructor.deleteSpaceBeforePunctuationMark;

public class Teg {

    private String name;
    private final List<Property> properties;

    private List<String> innerText;
    private Teg parent;
    private List<Teg> children;

    public Teg(String name) {
        this(name, new ArrayList<>(), new ArrayList<>(), null, new ArrayList<>());
    }

    public Teg(String name, List<Property> properties) {
        this(name, properties, new ArrayList<>(), null, new ArrayList<>());
    }

    public Teg(String name, List<Property> properties, List<String> innerText) {
        this(name, properties, innerText, null, new ArrayList<>());
    }

    public Teg(String name, List<Property> properties, List<String> innerText, Teg parent) {
        this(name, properties, innerText, parent, new ArrayList<>());
    }

    public Teg(String name, List<Property> properties, List<String> innerText, Teg parent, List<Teg> children) {
        this.name = name;
        this.innerText = innerText;
        this.parent = parent;
        this.children = children;
        this.properties = properties;
    }

    public Property getProperty(String name) {
        return properties.stream().filter(e -> e.getName().equals(name)).findAny().get();
    }

    public List<Property> getProperties() {
        return properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInnerTextSting() {
        return String.join("", innerText);
    }

    public String getInnerTextStingAll() {
        StringBuilder sb = new StringBuilder();
        for (int i=0; i<children.size(); i++) {
            sb.append(addSpaceAtEndOfWord(innerText.get(i)));
            sb.append(addSpaceAtEndOfWord(children.get(i).getInnerTextStingAll()));
        }
        sb.append(innerText.get(innerText.size() - 1));
        return prepareString(sb.toString());
    }

    public List<String> getInnerText() {
        return innerText;
    }

    public void setInnerText(List<String> innerText) {
        this.innerText = innerText;
    }

    public Teg getParent() {
        return parent;
    }

    public void setParent(Teg parent) {
        this.parent = parent;
    }

    public List<Teg> getChildren() {
        return children;
    }

    public void setChildren(List<Teg> children) {
        this.children = children;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Teg teg = (Teg) o;
        return Objects.equals(name, teg.name) && Objects.equals(properties, teg.properties);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, properties);
    }

    @Override
    public String toString() {
        return "<" + name + getPropertyString(properties) + ">" +
                String.join("", (innerText != null ? innerText : new ArrayList<>())) +
                "</" + name + ">";
    }

    public String toStringHTML() {
        return toStringTeg(new StringBuilder(), this, 0);
    }

    private String toStringTeg(StringBuilder sb, Teg teg, int deep) {
        String tab = "\t".repeat(deep);
        if (!tab.isEmpty()) sb.append(tab);
        if (teg.children.isEmpty()) {
            sb.append(teg).append("\n");
        } else {
            sb.append("<").append(teg.name).append(getPropertyString(teg.properties)).append(">").append("\n");
            if (!teg.innerText.isEmpty()) {
                for (int i=0; i<teg.children.size(); i++) {
                    if (!teg.innerText.get(i).isEmpty())
                        sb.append(toStringInnerText(teg.innerText.get(i), tab));
                    toStringTeg(sb, teg.children.get(i), deep + 1);
                }
                if (!teg.innerText.get(teg.innerText.size() - 1).isEmpty())
                    sb.append(toStringInnerText(teg.innerText.get(teg.innerText.size() - 1), tab));
            }
            sb.append(tab).append("</").append(teg.name).append(">").append("\n");
        }
        return sb.toString();
    }

    private String toStringInnerText(String text, String tab) {
        tab += "\t";
        return tab + text.replaceAll("\n", "\n" + tab) + "\n";
    }

    private String getPropertyString(List<Property> properties) {
        String prop = properties.stream().map(Property::toString).collect(Collectors.joining(" "));
        return prop.isEmpty() ? prop : " " + prop;
    }

    public static class Property {

        private String name;
        private String value;

        public Property(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Property property = (Property) o;
            return Objects.equals(name, property.name) && Objects.equals(value, property.value);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name, value);
        }

        @Override
        public String toString() {
            return name + "=\"" + value + "\"";
        }
    }

}
