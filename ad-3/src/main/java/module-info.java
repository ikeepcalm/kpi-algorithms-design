module dev.ua.ikeepcalm.ad {
    requires javafx.fxml;
    requires atlantafx.base;

    opens dev.ua.ikeepcalm.ad to javafx.fxml;
    opens dev.ua.ikeepcalm.ad.controllers to javafx.fxml;
    opens dev.ua.ikeepcalm.ad.entities to javafx.base;

    exports dev.ua.ikeepcalm.ad;
}