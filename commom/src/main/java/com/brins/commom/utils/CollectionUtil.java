package com.brins.commom.utils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class CollectionUtil {

    public static <T> void sortDesc(List<T> list, Comparator<? super T> comparator){
        Collections.sort(list,new DescComparatorWrapper<T>(comparator));
    }


    private static class DescComparatorWrapper<T> implements Comparator<T>{

        private Comparator<? super T> comparator;

        public DescComparatorWrapper(Comparator<? super T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(T lhs, T rhs) {
            return -comparator.compare(lhs,rhs);
        }
    }

    public static <T> List<List<T>> splitList(List<T> all, int maxSubListSize) {
        if (all == null) {
            return null;
        }

        int size = all.size();
        List<List<T>> lists = new ArrayList<>();
        if (size <= maxSubListSize) {
            lists.add(all);
            return lists;
        }

        int start = 0;
        while (start <= size) {
            int end = Math.min(start + maxSubListSize, size);
            lists.add(all.subList(start, end));
            start += maxSubListSize;
        }

        return lists;
    }

    public static <T> boolean deleteItem(List<T> list, T data) {
        if (!CheckUtils.isAvailable(list) || data == null) return false;
        Iterator<T> iterator = list.iterator();
        while (iterator.hasNext()) {
            T item = iterator.next();
            if (item == null) continue;
            if (data.equals(item)) {
                iterator.remove();
                return true;
            }
        }
        return false;
    }

    public static <T> void swapItems(List<T> list, int oldPosition, int newPosition) {
        KGAssert.assertNotNull(list);
        T tempElement = list.get(oldPosition);
        if (oldPosition < newPosition) {
            for (int i = oldPosition; i < newPosition; i++) {
                list.set(i, list.get(i + 1));
            }
            list.set(newPosition, tempElement);
        }
        if (oldPosition > newPosition) {
            for (int i = oldPosition; i > newPosition; i--) {
                list.set(i, list.get(i - 1));
            }
            list.set(newPosition, tempElement);
        }
    }

    public static boolean isEmpty(Collection collection) {
        return collection == null || collection.size() == 0;
    }
}
