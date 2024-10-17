package com.ruiyun.jvppeteer.entities;

public class ElementScreenshotOptions extends ScreenshotOptions{
    private boolean scrollIntoView = true;

    public boolean getScrollIntoView() {
        return scrollIntoView;
    }

    public ElementScreenshotOptions setScrollIntoView(boolean scrollIntoView) {
        this.scrollIntoView = scrollIntoView;
        return this;
    }
}