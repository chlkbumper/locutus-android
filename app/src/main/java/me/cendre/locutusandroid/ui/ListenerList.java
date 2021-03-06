package me.cendre.locutusandroid.ui;

import java.util.ArrayList;
import java.util.List;


/**
 * ListenerList helps dispatching events with typed objects easily
 *
 * @param <L> The type of listener subclass handling typed objects
 */
public class ListenerList<L> {
    private List<L> listenerList = new ArrayList<L>();

    public void add(L listener) {
        listenerList.add(listener);
    }

    public void fireEvent(FireHandler<L> fireHandler) {
        List<L> copy = new ArrayList<L>(listenerList);
        for (L l : copy) {
            fireHandler.fireEvent(l);
        }
    }

    public void remove(L listener) {
        listenerList.remove(listener);
    }

    public List<L> getListenerList() {
        return listenerList;
    }

    public interface FireHandler<L> {
        void fireEvent(L listener);
    }
}
