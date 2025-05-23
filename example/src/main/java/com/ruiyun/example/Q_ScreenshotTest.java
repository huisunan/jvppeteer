package com.ruiyun.example;

import com.ruiyun.jvppeteer.api.core.Browser;
import com.ruiyun.jvppeteer.api.core.ElementHandle;
import com.ruiyun.jvppeteer.api.core.JSHandle;
import com.ruiyun.jvppeteer.api.core.Page;
import com.ruiyun.jvppeteer.api.events.PageEvents;
import com.ruiyun.jvppeteer.cdp.core.Puppeteer;
import com.ruiyun.jvppeteer.cdp.entities.ConsoleMessage;
import com.ruiyun.jvppeteer.cdp.entities.ElementScreenshotOptions;
import com.ruiyun.jvppeteer.cdp.entities.FrameAddStyleTagOptions;
import com.ruiyun.jvppeteer.cdp.entities.GoToOptions;
import com.ruiyun.jvppeteer.cdp.entities.ImageType;
import com.ruiyun.jvppeteer.cdp.entities.ScreenshotOptions;
import com.ruiyun.jvppeteer.cdp.entities.WaitForOptions;
import com.ruiyun.jvppeteer.cdp.entities.WaitForSelectorOptions;
import com.ruiyun.jvppeteer.common.PuppeteerLifeCycle;
import java.util.Collections;
import java.util.function.Consumer;
import org.junit.Test;


import static com.ruiyun.example.A_LaunchTest.LAUNCHOPTIONS;

public class Q_ScreenshotTest {

    @Test
    public void test3() throws Exception {

        Browser browser = Puppeteer.launch(LAUNCHOPTIONS);
        //打开一个页面
        Page page = browser.newPage();
        page.goTo("https://www.baidu.com/?tn=68018901_16_pg");
        ScreenshotOptions screenshotOptions = new ScreenshotOptions();
        screenshotOptions.setPath("baidu.png");
        //webdriver bidi 不支持该参数
//        screenshotOptions.setOmitBackground(true);
        //全屏截图
        screenshotOptions.setFullPage(true);
        //截图的更多
        screenshotOptions.setCaptureBeyondViewport(true);
        page.screenshot(screenshotOptions);
        browser.close();
    }

    @Test
    public void test4() throws Exception {
        Browser browser = Puppeteer.launch(LAUNCHOPTIONS);
        //打开一个页面
        Page page = browser.newPage();
        page.goTo("https://www.baidu.com/?tn=68018901_16_pg");
        ScreenshotOptions screenshotOptions = new ScreenshotOptions();
        screenshotOptions.setPath("baidu2.png");
        //指定图片类型，path指定的名称中的后缀便不起作用了
        screenshotOptions.setType(ImageType.JPEG);
        //jpg可以设置这个选项
        screenshotOptions.setQuality(80.00);
        //全屏截图
        screenshotOptions.setFullPage(true);

        page.screenshot(screenshotOptions);
        browser.close();
    }

    @Test
    public void test5() throws Exception {
        Browser cdpBrowser = Puppeteer.launch(LAUNCHOPTIONS);
        //打开一个页面
        Page page = cdpBrowser.newPage();
        page.goTo("https://www.baidu.com/?tn=68018901_16_pg");
        ScreenshotOptions screenshotOptions = new ScreenshotOptions();
        screenshotOptions.setPath("baidu3.jpeg");
        //指定图片类型，path指定的名称中的后缀便不起作用了
        screenshotOptions.setType(ImageType.WEBP);
        //jpg可以设置这个选项
        screenshotOptions.setQuality(80.00);
        //全屏截图
        screenshotOptions.setFullPage(true);

        page.screenshot(screenshotOptions);
        cdpBrowser.close();
    }

    //某个元素截图
    @Test
    public void test6() throws Exception {

        Browser cdpBrowser = Puppeteer.launch(LAUNCHOPTIONS);
        //打开一个页面
        Page page = cdpBrowser.newPage();
        page.goTo("https://www.baidu.com/?tn=68018901_16_pg");
        FrameAddStyleTagOptions options = new FrameAddStyleTagOptions();
        //修改一下百度一下按钮的颜色
        options.setContent("#head_wrapper .s_btn{cursor:pointer;width:108px;height:44px;line-height:45px;line-height:44px\\9;padding:0;background:0 0;background-color:#b75014;border-radius:0 10px 10px 0;font-size:17px;color:#fff;box-shadow:none;font-weight:400;border:none;outline:0}");
        page.addStyleTag(options);
        ScreenshotOptions screenshotOptions = new ScreenshotOptions();
        screenshotOptions.setPath("baidu.png");
        screenshotOptions.setFullPage(true);
        page.screenshot(screenshotOptions);
        page.$("#su").screenshot("baidu4.png");
        cdpBrowser.close();
    }


    //测试 captureBeyondViewport
    @Test
    public void test7() throws Exception {

        Browser browser = Puppeteer.launch(LAUNCHOPTIONS);
        //打开一个页面
        Page page = browser.newPage();
        page.setContent("<!DOCTYPE html>\n" +
                "<html>\n" +
                "    <body>\n" +
                "        <div\n" +
                "            class=\"beyond-viewport-element\"\n" +
                "            style=\"height: 110vh; background-color: blue; border: 10px solid red\"></div>\n" +
                "        <div\n" +
                "            class=\"next-element\"\n" +
                "            style=\"height: 100px; background-color: green; border: 10px solid yellow\"></div>\n" +
                "    </body>\n" +
                "</html>");
        ElementScreenshotOptions screenshotOptions = new ElementScreenshotOptions();
        screenshotOptions.setPath("false.png");
        screenshotOptions.setCaptureBeyondViewport(false);
        ElementHandle $ = page.$(".beyond-viewport-element");
        $.screenshot(screenshotOptions);
        screenshotOptions.setPath("true.png");
        screenshotOptions.setCaptureBeyondViewport(true);
        $.screenshot(screenshotOptions);
        browser.close();
    }

    //测试 长截图
    @Test
    public void test8() throws Exception {

        Browser browser = Puppeteer.launch(LAUNCHOPTIONS);
        //打开一个页面
        Page page = browser.newPage();
        GoToOptions options = new GoToOptions();
        options.setWaitUntil(Collections.singletonList(PuppeteerLifeCycle.networkIdle));
        page.goTo("https://www.ruanyifeng.com/blog/2025/01/weekly-issue-332.html", options);
        ElementScreenshotOptions screenshotOptions = new ElementScreenshotOptions();
        screenshotOptions.setFromSurface(true);
        screenshotOptions.setPath("long.png");
        screenshotOptions.setFullPage(true);
        page.screenshot(screenshotOptions);
        browser.close();
    }


    //等待图片加载完毕
    @Test
    public void test9() throws Exception {
        LAUNCHOPTIONS.setDevtools(true);
        Browser browser = Puppeteer.launch(LAUNCHOPTIONS);
        //打开一个页面
        Page page = browser.newPage();
        page.on(PageEvents.Console, (Consumer<ConsoleMessage>) message -> System.out.println("console: " + message.text()));
        WaitForOptions waitForOptions = new WaitForOptions();
        waitForOptions.setWaitUntil(Collections.singletonList(PuppeteerLifeCycle.networkIdle));

        page.setContent(" <!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Icon Home Example</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Home Icon Example</h1>\n" +
                "    <img id=\"icon-home\" src='https://cdn-icons-png.flaticon.com/512/3222/3222434.png'></img>\n" +
                "</body>\n" +
                "</html>", waitForOptions);
        WaitForSelectorOptions options = new WaitForSelectorOptions();
        options.setVisible(true);
        ElementHandle elementHandle = page.waitForSelector("#icon-home", options);
        while (true) {
            Object complete = elementHandle.evaluate("(element) => {\n" +
                    "    return element.complete\n" +
                    "}");
            if ((boolean) complete) {
                break;
            }
        }

        ElementScreenshotOptions screenshotOptions = new ElementScreenshotOptions();
        screenshotOptions.setFromSurface(true);
        screenshotOptions.setPath("图片.png");
        screenshotOptions.setFullPage(true);
        page.screenshot(screenshotOptions);
        browser.close();
    }

    //等待图片加载完毕
    @Test
    public void test10() throws Exception {
        LAUNCHOPTIONS.setDevtools(true);
        Browser browser = Puppeteer.launch(LAUNCHOPTIONS);
        //打开一个页面
        Page page = browser.newPage();
        page.on(PageEvents.Console, (Consumer<ConsoleMessage>) message -> System.out.println("console: " + message.text()));

        page.setContent(" <!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Icon Home Example</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>Home Icon Example</h1>\n" +
                "    <img id=\"icon-home\" src='https://cdn-icons-png.flaticon.com/512/3222/3222434.png'></img>\n" +
                "</body>\n" +
                "</html>");
        WaitForSelectorOptions options = new WaitForSelectorOptions();
        options.setVisible(true);
        ElementHandle elementHandle = page.waitForSelector("#icon-home", options);
        JSHandle jsHandle = page.waitForFunction("(element) => {\n" +
                "    return element.complete;\n" +
                "}", elementHandle);
        System.out.println("图片加载完毕" + jsHandle);
        ElementScreenshotOptions screenshotOptions = new ElementScreenshotOptions();
        screenshotOptions.setFromSurface(true);
        screenshotOptions.setPath("图片.png");
        screenshotOptions.setFullPage(true);
        page.screenshot(screenshotOptions);
        browser.close();
    }
}
