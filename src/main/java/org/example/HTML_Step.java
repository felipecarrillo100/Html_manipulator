package org.example;

public class HTML_Step implements Comparable
{
    String NAME;
    String ACTION;
    String HTML_TARGET_MATCH;
    String HTML_REPLACEMENT;
    String SUBSTR;
    String ATTR;
    String VALUE;
    String WHICH;

    public HTML_Step(final String NAME, final String ACTION, final String HTML_TARGET_MATCH, final String HTML_REPLACEMENT, final String SUBSTR, final String ATTR, final String VALUE, final String WHICH) {
        this.NAME = NAME;
        this.ACTION = ACTION;
        this.HTML_TARGET_MATCH = HTML_TARGET_MATCH;
        this.HTML_REPLACEMENT = HTML_REPLACEMENT;
        this.ATTR = ATTR;
        this.VALUE = VALUE;
        this.WHICH = WHICH;
        this.SUBSTR = SUBSTR;
    }

    public int compareTo(final Object o) {
        return this.NAME.compareTo(((HTML_Step)o).NAME);
    }
}
