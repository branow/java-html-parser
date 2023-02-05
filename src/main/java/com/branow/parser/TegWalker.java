package com.branow.parser;

import java.util.*;

public class TegWalker {

    public enum TegField {
        NAME, PROPERTIES, INNER_TEXT, PARENT, CHILDREN
    }

    public enum Condition {
        EXACT(TegField.NAME, TegField.PROPERTIES),
        PARTIAL(TegField.NAME, TegField.PROPERTIES),
        FULL(TegField.NAME, TegField.PROPERTIES);

        private TegWalker.TegField[] fields;

        Condition(TegWalker.TegField... fields) {
            this.fields = fields;
        }

        public TegField[] getFields() {
            return fields;
        }

        public void setFields(TegField... fields) {
            this.fields = fields;
        }
    }

    private Teg root;

    public TegWalker(Teg root) {
        this.root = root;
    }

    public Teg getRoot() {
        return root;
    }

    public void setRoot(Teg root) {
        this.root = root;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TegWalker tegWalker = (TegWalker) o;
        return Objects.equals(root, tegWalker.root);
    }

    @Override
    public int hashCode() {
        return Objects.hash(root);
    }

    @Override
    public String toString() {
        return root.toString();
    }

    public Teg find(Teg base, Condition condition) {
        Queue<Teg> unchecked = new ArrayDeque<>();
        unchecked.add(root);
        while (!unchecked.isEmpty()) {
            Teg temp = unchecked.poll();
            if (isSuitable(temp, base, condition))
                return temp;
            unchecked.addAll(temp.getChildren());
        }
        return null;
    }

    public List<Teg> findAll(Teg base, Condition condition) {
        List<Teg> suitable = new ArrayList<>();
        LinkedList<Teg> unchecked = new LinkedList<>();
        unchecked.add(root);
        while (!unchecked.isEmpty()) {
            Teg temp = unchecked.pollFirst();
            if (isSuitable(temp, base, condition)) {
                suitable.add(temp);
            }
            unchecked.addAll(temp.getChildren());
        }
        return suitable;
    }

    public List<Teg> findAllOrderly(Teg base, Condition condition) {
        List<Teg> res = new ArrayList<>();
        findAllOrderly(root, base, condition, res);
        return res;
    }

    public Teg findOrderly(Teg base, Condition condition) {
        List<Teg> find = new ArrayList<>(3);
        findOrderly(root, base, condition, find);
        return find.isEmpty() ? null : find.get(0);
    }

    private void findAllOrderly(Teg root, Teg base, Condition condition, List<Teg> suitable) {
        if (isSuitable(root, base, condition)) {
            suitable.add(root);
        }
        for (Teg temp: root.getChildren()) {
            findAllOrderly(temp, base, condition, suitable);
        }
    }

    private void findOrderly(Teg root, Teg base, Condition condition, List<Teg> suitable) {
        if (suitable.size() > 1) return;
        if (isSuitable(root, base, condition))
            suitable.add(root);
        for (Teg temp: root.getChildren()) {
            findOrderly(temp, base, condition, suitable);
        }
    }

    public boolean isSuitable(Teg got, Teg base, Condition condition) {
        return switch (condition) {
            case EXACT -> isSuitableExactly(got, base, condition.fields);
            case FULL -> isSuitableFull(got, base, condition.fields);
            case PARTIAL -> isSuitablePartially(got, base, condition.fields);
        };
    }

    private boolean isSuitableExactly(Teg got, Teg base, TegField[] fields) {
        for (TegField field: fields)
            if (!isSuitableExactly(got, base, field)) return false;
        return true;
    }

    private boolean isSuitableExactly(Teg got, Teg base, TegField field) {
        return switch (field) {
            case NAME -> base.getName().equals(got.getName());
            case PROPERTIES -> CollectionEqualizer.equalsListUnordered(got.getProperties(), base.getProperties());
            case INNER_TEXT -> got.getInnerTextSting().equals(base.getInnerTextSting());
            case PARENT -> base.getParent().equals(got.getParent());
            case CHILDREN -> CollectionEqualizer.equalsListUnordered(got.getChildren(), base.getChildren());
        };
    }

    private boolean isSuitableFull(Teg got, Teg base, TegField[] fields) {
        for (TegField field: fields)
            if (!isSuitableFull(got, base, field)) return false;
        return true;
    }

    private boolean isSuitableFull(Teg got, Teg base, TegField field) {
        return switch (field) {
            case NAME -> base.getName().equals(got.getName());
            case PROPERTIES -> CollectionEqualizer.contains(base.getProperties(), got.getProperties());
            case INNER_TEXT -> base.getInnerTextSting().contains(got.getInnerTextSting());
            case PARENT -> base.getParent().equals(got.getParent());
            case CHILDREN -> CollectionEqualizer.contains(base.getChildren(), got.getChildren());
        };
    }

    private boolean isSuitablePartially(Teg got, Teg base, TegField[] fields) {
        for (TegField field: fields)
            if (isSuitablePartially(got, base, field)) return true;
        return false;
    }

    private boolean isSuitablePartially(Teg got, Teg base, TegField field) {
        return switch (field) {
            case NAME -> got.getName().equals(base.getName());
            case PROPERTIES -> CollectionEqualizer.equalsPartially(got.getProperties(), base.getProperties());
            case INNER_TEXT -> got.getInnerTextSting().equals(base.getInnerTextSting());
            case PARENT -> base.getParent().equals(got.getParent());
            case CHILDREN -> CollectionEqualizer.equalsPartially(got.getChildren(), base.getChildren());
        };
    }

}
