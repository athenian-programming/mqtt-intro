package org.athenian;

public class SynchronizationUsage {

    static final Object staticVal = new Object();
    final Object instanceVal = new Object();

    /*
    The synchronized keyword can be used in 4 circumstances:
        Synchronized block in instance methods
        Synchronized instance methods
        Synchronized block in static methods
        Synchronized static methods
     */

    /****** Static methods ******/

    // Synchronized block in static methods
    static void staticMethod() {
        // Some method actions
        synchronized (staticVal) {
            // Synchronized method actions
        }
        // Other method actions
    }

    // Synchronized static methods
    static synchronized void syncStaticMethod() {
        // Method actions
    }

    // Equivalent to syncStaticMethod()
    static void nonsyncStaticMethod() {
        synchronized (SynchronizationUsage.class) {
            // Method actions
        }
    }

    /****** Instance methods ******/

    // Synchronized block in instance methods
    void instanceMethod() {
        // Some method actions
        synchronized (instanceVal) {
            // Synchronized method actions
        }
        // Other method actions
    }

    // Synchronized instance methods
    synchronized void syncInstanceMethod() {
        // Synchronized method actions
    }

    // Equivalent to syncInstanceMethod()
    void nonsyncInstanceMethod() {
        synchronized (this) {
            // Synchronized method actions
        }
    }
}
