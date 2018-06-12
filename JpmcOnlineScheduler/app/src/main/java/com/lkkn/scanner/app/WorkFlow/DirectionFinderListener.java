package com.lkkn.scanner.app.WorkFlow;

/**
 * Created by RANJITH on 04-03-2018.
 */

import java.util.List;



public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
