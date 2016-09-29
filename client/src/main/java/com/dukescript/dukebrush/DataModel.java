package com.dukescript.dukebrush;

import net.java.html.json.ComputedProperty;
import net.java.html.json.Model;
import net.java.html.json.Property;
import net.java.html.BrwsrCtx;
import net.java.html.json.Function;

@Model(className = "Data", targetId = "", properties = {
    @Property(name = "time", type = long.class),
    @Property(name = "sector", type = int.class),
    @Property(name = "battery", type = float.class),
    @Property(name = "mode", type = int.class),
    @Property(name = "state", type = int.class),
    @Property(name = "running", type = boolean.class),
    @Property(name = "highPressure", type = boolean.class)
})
public final class DataModel {

    @ComputedProperty
    public static long timeAsSeconds(long time) {
        return time / 1000;
    }

    @Function static void appear(Data ui){
        ui.setRunning(!ui.isRunning());
    }
    
    /**
     * Called when the page is ready.
     */
    static void onPageLoad() throws Exception {
        ui = new Data();
        ui.applyBindings();
        ui.setRunning(true);
    }

    static Data ui;
    static BrwsrCtx defaultContext;

}
