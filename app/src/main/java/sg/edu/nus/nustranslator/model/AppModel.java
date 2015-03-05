package sg.edu.nus.nustranslator.model;

/**
 * Created by Storm on 3/5/2015.
 */
public class AppModel {

    States appState;

    //constructor
    public AppModel() {
        this.appState = States.INACTIVE;
    }

    //public methods
    public States getAppState() {
        return this.appState;
    }

    public void setAppState(States state) {
        this.appState = state;
    }
}
