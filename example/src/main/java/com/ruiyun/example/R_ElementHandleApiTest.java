package com.ruiyun.example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.ruiyun.jvppeteer.core.Browser;
import com.ruiyun.jvppeteer.core.ElementHandle;
import com.ruiyun.jvppeteer.core.Frame;
import com.ruiyun.jvppeteer.core.Page;
import com.ruiyun.jvppeteer.core.Puppeteer;
import com.ruiyun.jvppeteer.entities.AutofillData;
import com.ruiyun.jvppeteer.entities.BoundingBox;
import com.ruiyun.jvppeteer.entities.BoxModel;
import com.ruiyun.jvppeteer.entities.ConsoleMessage;
import com.ruiyun.jvppeteer.entities.CreditCard;
import com.ruiyun.jvppeteer.entities.DragData;
import com.ruiyun.jvppeteer.entities.Point;
import com.ruiyun.jvppeteer.entities.Viewport;
import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class R_ElementHandleApiTest extends A_LaunchTest {
    /**
     * 获取元素信息
     */
    @Test
    public void test3() throws Exception {

        Browser browser = Puppeteer.launch(launchOptions);
        //打开一个页面
        Page page = browser.newPage();

        //方式1 waitForSelector
        //page.goTo("https://www.bookstack.cn/read/HTTP-Status-codes/websocket.md",false);
        // ElementHandle elementHandle = page.waitForSelector("#page-content");

        //方式2 waitForSelector
        new Thread(() -> {
            try {
                ElementHandle elementHandle = page.waitForSelector("#page-content");
                BoxModel boxModel = elementHandle.boxModel();
                elementHandle.dispose();//手动释放，防止内存泄露
                double height = boxModel.getHeight();
                double width = boxModel.getWidth();
                System.out.println(height + ":" + width);
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
        }).start();
        page.goTo("https://www.bookstack.cn/read/HTTP-Status-codes/websocket.md");
        browser.close();
    }

    /**
     * 判断元素是否在可视区域
     */
    @Test
    public void test4() throws Exception {

        Browser browser = Puppeteer.launch(launchOptions);
        //打开一个页面
        Page page = browser.newPage();
        page.goTo("https://pptr.nodejs.cn/api/puppeteer.elementhandle.isvisible");

        page.click("#__docusaurus > nav > div.navbar__inner > div.navbar__items.navbar__items--right > div.navbarSearchContainer_Bca1 > button");
        //等待输入框出现
        ElementHandle elementHandle = page.waitForSelector("#docsearch-input");
        elementHandle.type("jvppeteer测试");

        boolean intersectingViewport = elementHandle.isIntersectingViewport();
        System.out.println("intersectingViewport: " + intersectingViewport);
        Thread.sleep(2000);
        boolean visible1 = elementHandle.isVisible();
        System.out.println("visible: " + visible1);
        elementHandle.press("Escape");
        boolean isHidden = elementHandle.isHidden();
        System.out.println("isHidden: " + isHidden);
        page.waitForSelector("#__docusaurus > nav > div.navbar__inner > div.navbar__items.navbar__items--right > div.navbarSearchContainer_Bca1 > button");

        //esc按键释放后，该元素消失，那么isIntersectingViewport便会抛出错误
        try {
            elementHandle.isIntersectingViewport();
        } catch (Exception e) {
            System.out.println("intersectingViewport2: 出错了");
        }
        browser.close();
    }

    /**
     * 获取元素中心位置，边界框等
     */
    @Test
    public void test5() throws Exception {
        Browser browser = Puppeteer.launch(launchOptions);
        //打开一个页面
        Page page = browser.newPage();
        page.goTo("https://pptr.nodejs.cn/api/puppeteer.elementhandle.isvisible");

        //selector可能会变，报错及时更改
        ElementHandle elementHandle = page.$("#__docusaurus_skipToContent_fallback > div > div > main > div > div > div.col.docItemCol_VOVn > div > nav > a.pagination-nav__link.pagination-nav__link--prev");
        try {
            elementHandle.hover();
            //利用 point 和 boundingBox可以用来拖动验证条
            Point point = elementHandle.clickablePoint();
            System.out.println("元素中心位置：" + point);
            BoundingBox boundingBox = elementHandle.boundingBox();
            System.out.println("元素边界框：" + boundingBox);
            Thread.sleep(5000);
        } finally {
            elementHandle.dispose();
        }
        browser.close();
    }

    /**
     * 拖动元素,拖动事件是发生了，貌似页面没变？
     */
    @Test
    public void test6() throws Exception {
        ArrayList<String> args = new ArrayList<>();//添加一些额外的启动参数
        args.add("--no-sandbox");
        launchOptions.setArgs(args);
        Browser browser = Puppeteer.launch(launchOptions);
        //打开一个页面
        Page page = browser.newPage();
        //打开这个。ElementHandle.drag才有返回值，不打开返回null, setDragInterception(true)已过时
        // page.setDragInterception(true);
        page.on(Page.PageEvent.Console, (Consumer<ConsoleMessage>) consoleMessage -> System.out.println("consoleMessage: " + consoleMessage.text()));
        page.setContent("<script>\n" +
                "  function dragstartHandler(ev) {\n" +
                "    // Add the target element's id to the data transfer object\n" +
                "    ev.dataTransfer.setData(\"application/my-app\", ev.target.id);\n" +
                "    ev.dataTransfer.effectAllowed = \"move\";\n" +
                "  }\n" +
                "  function dragoverHandler(ev) {\n" +
                "    ev.preventDefault();\n" +
                "    ev.dataTransfer.dropEffect = \"move\";\n" +
                "    console.log(\"dragoverHandler\")\n" +
                "  }\n" +
                "  function dropHandler(ev) {\n" +
                "    ev.preventDefault();\n" +
                "    // Get the id of the target and add the moved element to the target's DOM\n" +
                "    const data = ev.dataTransfer.getData(\"application/my-app\");\n" +
                "    ev.target.appendChild(document.getElementById(data));\n" +
                "    console.log(\"dropHandler\")\n" +
                "  }\n" +
                "</script>\n" +
                "\n" +
                "<p id=\"p1\" draggable=\"true\" ondragstart=\"dragstartHandler(event)\">\n" +
                "  This element is draggable.\n" +
                "</p>\n" +
                "<div\n" +
                "  id=\"target\"\n" +
                "  ondrop=\"dropHandler(event)\"\n" +
                "  ondragover=\"dragoverHandler(event)\">\n" +
                "  Drop Zone\n" +
                "</div>");

        ElementHandle drop = page.$("#p1");
        ElementHandle target = page.$("#target");
        //鼠标移动到element中心
        try {
            DragData drag = drop.drag(target);
            System.out.println(drag);
        } finally {//不用的时候必须释放
            drop.dispose();
            target.dispose();
        }
        Thread.sleep(50000);
        browser.close();
    }

    /**
     * 信用卡表单填写,目前版本的浏览器不可以用
     * {@link com.ruiyun.jvppeteer.common.Constant#VERSION}
     */
    @Test
    public void test7() throws Exception {
        Browser browser = Puppeteer.launch(launchOptions);
        Page page = browser.newPage();
        page.setContent("<!DOCTYPE html>\n" +
                "<html lang=\"zh\">\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <title>信用卡信息表单</title>\n" +
                "</head>\n" +
                "<body>\n" +
                "    <h1>信用卡信息表单</h1>\n" +
                "    <form action=\"/submit_credit_card_info\" method=\"post\">\n" +
                "        <label for=\"cardNumber\">信用卡号:</label>\n" +
                "        <input type=\"text\" id=\"number\" name=\"cardNumber\" required><br><br>\n" +
                "        \n" +
                "        <label for=\"cardHolderName\">持卡人姓名:</label>\n" +
                "        <input type=\"text\" id=\"name\" name=\"cardHolderName\" required><br><br>\n" +
                "        \n" +
                "        <label for=\"expiryDate\">到期日期 (MM/YY):</label>\n" +
                "        <input type=\"text\" id=\"expiryDate\" name=\"expiryDate\" required><br><br>\n" +
                "        \n" +
                "        <label for=\"cvv\">安全码 (CVV):</label>\n" +
                "        <input type=\"text\" id=\"cvv\" name=\"cvv\" required><br><br>\n" +
                "        \n" +
                "        <input type=\"submit\" value=\"提交\">\n" +
                "    </form>\n" +
                "</body>\n" +
                "</html>\n");
        ElementHandle elementHandle = page.$("body > form");
        elementHandle.autofill(new AutofillData(new CreditCard("12346", "张三", "2023", "01", "123")));
        elementHandle.dispose();
        Thread.sleep(500000);
        browser.close();
    }


    /**
     * hover和focus
     *
     */
    @Test
    public void test8() throws Exception {
        Browser browser = Puppeteer.launch(launchOptions);
        Page page = browser.newPage();
        page.setViewport(new Viewport(1200, 1200));
        page.goTo("https://www.baidu.com/?tn=68018901_16_pg");
        ElementHandle elementHandle = page.$("#hotsearch-content-wrapper > li:nth-child(1) > a > span.title-content-title");
        Frame frame = elementHandle.contentFrame();
        System.out.println("frame:" + frame);
        Thread.sleep(3000);
        System.out.println("elementHandle1 focus");
        elementHandle.focus();
        Thread.sleep(3000);

        ElementHandle elementHandle2 = page.$("#kw");
        Frame frame2 = elementHandle2.contentFrame();
        System.out.println("frame2:" + frame2);
        System.out.println("elementHandle2 focus");
        elementHandle2.focus();
        Thread.sleep(2000);
        System.out.println("elementHandle1 hover");
        elementHandle.hover();
        Thread.sleep(3000);
        elementHandle.dispose();

        System.out.println("elementHandle2 hover");
        elementHandle2.hover();
        elementHandle.dispose();
        elementHandle2.dispose();
        Thread.sleep(5000);
        browser.close();
    }


    /**
     * 测试$eval和$$eval
     */
    @Test
    public void test11() throws InterruptedException, IOException {
        Browser browser = Puppeteer.launch(launchOptions);
        Page page = browser.newPage();
        page.on(Page.PageEvent.Console, (Consumer<ConsoleMessage>) message -> System.out.println(message.text()));
        //设置html页面，有三个按钮，点击后打印出按钮的文本
        page.setContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Button Example</title>\n" +
                "    <style>\n" +
                "        .button-class {\n" +
                "            /* 你可以在这里添加CSS样式 */\n" +
                "            background-color: #4CAF50;\n" +
                "            /* Green */\n" +
                "            border: none;\n" +
                "            color: white;\n" +
                "            padding: 15px 32px;\n" +
                "            text-align: center;\n" +
                "            text-decoration: none;\n" +
                "            display: inline-block;\n" +
                "            font-size: 16px;\n" +
                "            margin: 4px 2px;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"button\">\n" +
                "        <!-- 使用相同的类名 -->\n" +
                "        <button class=\"button-class\">按钮1</button>\n" +
                "        <button class=\"button-class\">按钮2</button>\n" +
                "        <button class=\"button-class3\">按钮3</button>\n" +
                "    </div>\n" +
                "    <script>\n" +
                "        // 通过类名选择所有按钮并添加点击事件监听器\n" +
                "        document.querySelectorAll('.button-class').forEach(function (button) {\n" +
                "            button.addEventListener('click', function () {\n" +
                "                console.log(\"按钮1 或 按钮2被点击了\")\n" +
                "            });\n" +
                "        });\n" +
                "        // 通过类名选择所有按钮并添加点击事件监听器\n" +
                "        document.querySelectorAll('.button-class3').forEach(function (button) {\n" +
                "            button.addEventListener('click', function () {\n" +
                "                console.log(\"按钮3被点击了\")\n" +
                "            });\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        //点击class =button-class3的元素
        ElementHandle button3 = page.$(".button-class3");
        button3.click();
        button3.dispose();
        //查询所有class =button-class的元素，当前的html页面有2个
        List<ElementHandle> button1and2 = page.$$(".button-class");
        for (ElementHandle elementHandle : button1and2) {
            elementHandle.click();
            elementHandle.dispose();
        }
        ElementHandle divElement = page.$(".button");

        Object o1 = divElement.$eval(".button-class3", "element  => element.innerText = element.innerText + \"魔法按钮\"");
        Object o2 = divElement.$$eval(".button-class", "array1 => array1.forEach((element) => element.innerText = element.innerText + \"魔法按钮\")");
        System.out.println("1: " + o1);
        System.out.println("2: " + o2);
        Thread.sleep(5000);
        divElement.dispose();
        browser.close();
    }


    /**
     * 列表多选
     */
    @Test
    public void test12() throws InterruptedException, IOException {
        Browser browser = Puppeteer.launch(launchOptions);
        Page page = browser.newPage();
        page.setViewport(new Viewport(1200, 1200));
        page.setContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Button Example</title>\n" +
                "    <style>\n" +
                "        .button-class {\n" +
                "            /* 你可以在这里添加CSS样式 */\n" +
                "            background-color: #4CAF50;\n" +
                "            /* Green */\n" +
                "            border: none;\n" +
                "            color: white;\n" +
                "            padding: 15px 32px;\n" +
                "            text-align: center;\n" +
                "            text-decoration: none;\n" +
                "            display: inline-block;\n" +
                "            font-size: 16px;\n" +
                "            margin: 4px 2px;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"button\">\n" +
                "        <!-- 使用相同的类名 -->\n" +
                "        <button class=\"button-class\">按钮1</button>\n" +
                "        <button class=\"button-class\">按钮2</button>\n" +
                "        <button class=\"button-class3\">按钮3</button>\n" +
                "        <label for=\"pet-select\">Choose a pet:</label>\n" +
                "        <select name=\"pets\" multiple id=\"pet-select\">\n" +
                "            <option value=\"\">--Please choose an option--</option>\n" +
                "            <option value=\"dog\">dog</option>\n" +
                "            <option value=\"cat\">cat</option>\n" +
                "            <option value=\"hamster\">Hamster</option>\n" +
                "            <option value=\"parrot\">Parrot</option>\n" +
                "            <option value=\"spider\">Spider</option>\n" +
                "            <option value=\"goldfish\">Goldfish</option>\n" +
                "        </select>\n" +
                "    </div>\n" +
                "\n" +
                "</div>\n" +
                "    <script>\n" +
                "        // 通过类名选择所有按钮并添加点击事件监听器\n" +
                "        document.querySelectorAll('.button-class').forEach(function (button) {\n" +
                "            button.addEventListener('click', function () {\n" +
                "                console.log(\"按钮1 或 按钮2被点击了\")\n" +
                "            });\n" +
                "        });\n" +
                "        // 通过类名选择所有按钮并添加点击事件监听器\n" +
                "        document.querySelectorAll('.button-class3').forEach(function (button) {\n" +
                "            button.addEventListener('click', function () {\n" +
                "                console.log(\"按钮3被点击了\")\n" +
                "            });\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        //可以多选的列表
        ElementHandle selectElement = page.$("#pet-select");
        List<String> select = selectElement.select(Arrays.asList("dog", "cat"));
        System.out.println(String.join(",", select));
        selectElement.dispose();
        Thread.sleep(5000);
        browser.close();
    }

    /**
     * tap
     */
    @Test
    public void test13() throws InterruptedException, IOException {
        Browser browser = Puppeteer.launch(launchOptions);
        Page page = browser.newPage();
        page.goTo("https://www.baidu.com/");
        page.setViewport(new Viewport(1200, 1200));
        page.$("#hotsearch-content-wrapper > li:nth-child(6) > a > span.title-content-title").tap();
        Thread.sleep(2000);
        browser.close();
    }

    /**
     * toElement测试
     */
    @Test
    public void test15() throws InterruptedException, IOException {
        Browser browser = Puppeteer.launch(launchOptions);
        Page page = browser.newPage();
        page.setContent("<script>\n" +
                "  function dragstartHandler(ev) {\n" +
                "    // Add the target element's id to the data transfer object\n" +
                "    ev.dataTransfer.setData(\"application/my-app\", ev.target.id);\n" +
                "    ev.dataTransfer.effectAllowed = \"move\";\n" +
                "  }\n" +
                "  function dragoverHandler(ev) {\n" +
                "    ev.preventDefault();\n" +
                "    ev.dataTransfer.dropEffect = \"move\";\n" +
                "  }\n" +
                "  function dropHandler(ev) {\n" +
                "    ev.preventDefault();\n" +
                "    // Get the id of the target and add the moved element to the target's DOM\n" +
                "    const data = ev.dataTransfer.getData(\"application/my-app\");\n" +
                "    ev.target.appendChild(document.getElementById(data));\n" +
                "  }\n" +
                "</script>\n" +
                "\n" +
                "<p id=\"p1\" draggable=\"true\" ondragstart=\"dragstartHandler(event)\">\n" +
                "  This element is draggable.\n" +
                "</p>\n" +
                "<div\n" +
                "  id=\"target\"\n" +
                "  ondrop=\"dropHandler(event)\"\n" +
                "  ondragover=\"dragoverHandler(event)\">\n" +
                "  <a id=\"myAnchor\" href=\"https://developer.mozilla.org/zh-CN/HTMLAnchorElement\">点击</a>\n" +
                "  Drop Zone\n" +
                "</div>\n" +
                "\n");
        page.setViewport(new Viewport(1200, 1200));
        ElementHandle elementHandle = page.$("#myAnchor");
        elementHandle.toElement("a").click();
        //这里会报错，因为页面重新导航了，原来的页面信息已经丢失，释放与否已经无所谓了
        elementHandle.dispose();
        Thread.sleep(10000);
        browser.close();
    }

    @Test
    public void test16() throws InterruptedException, IOException {
        Browser browser = Puppeteer.launch(launchOptions);
        Page page = browser.newPage();
        page.on(Page.PageEvent.Console, (Consumer<ConsoleMessage>) message -> System.out.println(message.text()));
        page.setContent("<!DOCTYPE html>\n" +
                "<html lang=\"en\">\n" +
                "\n" +
                "<head>\n" +
                "    <meta charset=\"UTF-8\">\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n" +
                "    <title>Button Example</title>\n" +
                "    <style>\n" +
                "        .button-class {\n" +
                "            /* 你可以在这里添加CSS样式 */\n" +
                "            background-color: #4CAF50;\n" +
                "            /* Green */\n" +
                "            border: none;\n" +
                "            color: white;\n" +
                "            padding: 15px 32px;\n" +
                "            text-align: center;\n" +
                "            text-decoration: none;\n" +
                "            display: inline-block;\n" +
                "            font-size: 16px;\n" +
                "            margin: 4px 2px;\n" +
                "            cursor: pointer;\n" +
                "        }\n" +
                "    </style>\n" +
                "</head>\n" +
                "\n" +
                "<body>\n" +
                "    <div class=\"button\">\n" +
                "        <!-- 使用相同的类名 -->\n" +
                "        <button  draggable=\"true\" class=\"button-class\">按钮1</button>\n" +
                "        <button class=\"button-class2\">按钮2</button>\n" +
                "        <button class=\"button-class3\">按钮3</button>\n" +
                "    </div>\n" +
                "\n" +
                "</div>\n" +
                "    <script>\n" +
                "        // 通过类名选择所有按钮并添加点击事件监听器\n" +
                "        document.querySelectorAll('.button-class').forEach(function (button) {\n" +
                "            button.addEventListener('touchstart', function () {\n" +
                "                console.log(\"按钮1 touchStart...\")\n" +
                "            });\n" +
                "            button.addEventListener('touchmove',function(){\n" +
                "                console.log('按钮1 touchmove...')\n" +
                "            });\n" +
                "            button.addEventListener('touchend',function(){\n" +
                "                console.log('按钮1 touchend...')\n" +
                "            });\n" +
                "        });\n" +
                "        // 通过类名选择所有按钮并添加点击事件监听器\n" +
                "        document.querySelectorAll('.button-class3').forEach(function (button) {\n" +
                "            button.addEventListener('click', function () {\n" +
                "                console.log(\"按钮3被点击了\")\n" +
                "            });\n" +
                "        });\n" +
                "    </script>\n" +
                "</body>\n" +
                "\n" +
                "</html>");
        page.setViewport(new Viewport(1200, 1200));
        ElementHandle elementHandle = page.$(".button-class");
        elementHandle.touchStart();
        Thread.sleep(2000);
        elementHandle.touchMove();
        Thread.sleep(2000);
        elementHandle.touchEnd();
        Thread.sleep(10000);
        browser.close();
    }
}