package com.ruiyun.jvppeteer.cdp.events;

import com.ruiyun.jvppeteer.cdp.entities.LogEntry;

/**
 * Issued when new message was logged.
 */
public class EntryAddedEvent {

    /**
     * The entry.
     */
    private LogEntry entry;

    public LogEntry getEntry() {
        return entry;
    }

    public void setEntry(LogEntry entry) {
        this.entry = entry;
    }

    @Override
    public String toString() {
        return "EntryAddedPayload{" +
                "entry=" + entry +
                '}';
    }
}
