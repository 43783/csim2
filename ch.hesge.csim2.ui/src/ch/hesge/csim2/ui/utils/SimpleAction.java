package ch.hesge.csim2.ui.utils;

/**
 * Simple action execute by SwingUtils.
 * 
 * @author Eric Harth
 *
 * @param <T>
 */
public interface SimpleAction<T> {

    public void run(T param);
}
