package com.branow.parser;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringConstructor {

    public static String prepareString(String string) {
        return deleteSpaceBeforePunctuationMark(deleteRepeatedSpace(string.strip()));
    }

    public static String addSpaceAtEndOfWord(String word) {
        Pattern pattern = Pattern.compile("(\n|.)*[\\w.,!?]+");
        return pattern.matcher(word).matches() ? word + " " : word;
    }

    public static String deleteRepeatedSpace(String string) {
        return string.replaceAll(" {2}", " ");
    }

    public static String deleteSpaceBeforePunctuationMark(String string) {
        StringBuilder sb = new StringBuilder(string);
        Pattern pattern = Pattern.compile(" [.:,?!/]");
        Matcher matcher = pattern.matcher(sb);
        try {
            while (matcher.find()) {
                int index = matcher.start();
                sb.deleteCharAt(index);
            }
        } finally {
            return sb.toString();
        }
    }

    public static String addDotAtTheEnd(String string) {
        return addAtTheEnd(".", string);
    }

    public static String addAtTheEnd(String end, String string) {
        Pattern pattern = Pattern.compile("(\n|.)*\\w+");
        return pattern.matcher(string).matches() ? string + end : string;
    }

}
