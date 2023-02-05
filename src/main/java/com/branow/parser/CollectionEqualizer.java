package com.branow.parser;

import java.util.List;

class CollectionEqualizer {

    public static<E> boolean equalsListUnordered(List<E> l1, List<E> l2) {
        return contains(l1, l2) && contains(l2, l1);
    }

    public static<E> boolean contains(List<E> l1, List<E> container) {
        for (E e: l1)
            if (!container.contains(e)) return false;
        return true;
    }

    public static<E> boolean equalsPartially(List<E> l1, List<E> l2) {
        for (E e: l1)
            if (l2.contains(e)) return true;
        return false;
    }

}
