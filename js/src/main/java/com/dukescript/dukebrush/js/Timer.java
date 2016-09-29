package com.dukescript.dukebrush.js;

import net.java.html.js.JavaScriptBody;
import net.java.html.js.JavaScriptResource;

/**
 * Use {@link JavaScriptBody} annotation on methods to directly interact with
 * JavaScript. See
 * http://bits.netbeans.org/html+java/1.2/net/java/html/js/package-summary.html
 * to understand how.
 */
@JavaScriptResource("timer.min.js")
public final class Timer {

    Object delegate;

    private Timer(Object delegate) {
        this.delegate = delegate;
    }

    public static Timer create(String id, float radius, int seconds, double strokeWidth,
            String fillStyle, String strokeStyle, float fontSize, String fontColor) {
        return new Timer(create_impl(id, radius, seconds,
                strokeWidth, fillStyle, strokeStyle, fontSize, fontColor));
    }

    public void start() {
        start_impl(delegate);
    }

    public void stop() {
        stop_impl(delegate);
    }

    @JavaScriptBody(args = {"timer"}, body = "timer.start();")
    public static native void start_impl(Object timer);

    public void highPressure(boolean high) {
        if (high) {
            high_impl(delegate);
        } else {
            low_impl(delegate);
        }
    }

    @JavaScriptBody(args = {"timer"}, body = "timer.highPressure();")
    public static native void high_impl(Object timer);

    @JavaScriptBody(args = {"timer"}, body = "timer.lowPressure();")
    public static native void low_impl(Object timer);

    @JavaScriptBody(args = {"timer"}, body = "timer.stop();")
    public static native void stop_impl(Object timer);

    @JavaScriptBody(args = {"id", "radius", "seconds", "strokeWidth", "fillStyle",
        "strokeStyle", "fontSize", "fontColor"},
            body = "return $(id).countdown360({\n"
            + "  radius      : radius,\n"
            + "  seconds     : seconds,\n"
            + "  strokeWidth : strokeWidth,\n"
            + "  fillStyle   : fillStyle,\n"
            + "  strokeStyle : strokeStyle,\n"
            + "  fontSize    : fontSize,\n"
            + "  fontColor   : fontColor,\n"
            + "  autostart: false,\n"
            + "  onComplete  : function () { console.log('completed') }\n"
            + "});"
    )
    public static native Object create_impl(String id, float radius, int seconds, double strokeWidth,
            String fillStyle, String strokeStyle, float fontSize, String fontColor);

    public static void init() {
        JQuery.init_impl();
        init_impl();
    }

    @JavaScriptBody(args = {}, body = "")
    public static native void init_impl();

    @JavaScriptResource("jquery.js")
    public static class JQuery {

        @JavaScriptBody(args = {}, body = "")
        static native void init_impl();
    }

}
