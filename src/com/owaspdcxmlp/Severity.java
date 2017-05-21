package com.owaspdcxmlp;

/**
 * Created by micha on 05/20/2017.
 */
public enum Severity {
    LOW(0), MEDIUM(1), HIGH(2), CRITICAL(3);

    private int sevLevel;

    Severity(int level) {
        sevLevel = level;
    }

    // TODO: Code getters.

}
