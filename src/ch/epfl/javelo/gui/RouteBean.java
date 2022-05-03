package ch.epfl.javelo.gui;

import ch.epfl.javelo.routing.ElevationProfile;
import ch.epfl.javelo.routing.Route;
import ch.epfl.javelo.routing.RouteComputer;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;

import java.util.List;

public final class RouteBean {
    private final ObjectProperty<RouteComputer> rc;
    private final ObservableList<Waypoint> waypointList;
    //en readOnly ?
    private final ReadOnlyObjectProperty<Route> route;
    private final ReadOnlyObjectProperty<ElevationProfile> elevationProfile;
    private final DoubleProperty highlightedPosition;


    public RouteBean(RouteComputer routeComputer) {

    }

    //get comtemu ATTENTION imuable
    public List<Waypoint> getWaypointList() {
        return ;
    }

    public double getHighlightedPosition() {
        return highlightedPosition.get();
    }

 /*   public Route getRoute() {
        return ;
    }

    public ElevationProfile getElevationProfile() {
        return ;
    }

    public RouteComputer getRc() {
        return ;
    }*/

    //Get property ATtention vue / read only?
    public ObservableList<Waypoint> WaypointList() {
        return ;
    }

    public DoublePropertyouble HighlightedPosition() {
        return highlightedPosition.get();
    }

    public ReadOnlyObjectProperty<Route> Route() {
        return;
    }

    public ReadOnlyObjectProperty<ElevationProfile> ElevationProfile() {
        return ;
    }

    public ReadOnlyObjectProperty<RouteComputer> Rc() {
        return ;
    }

    //méthode setable
    public void setWaypointList() {
        ...
    }

    public void setHighlightedPosition() {
        ...
    }
}
