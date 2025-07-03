module com.blackjack.mtg {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    

    opens com.mtg.blackjack.View to javafx.fxml;
    opens com.mtg.blackjack.Model to javafx.fxml;
    opens com.mtg.blackjack.Controller to javafx.fxml;
    exports com.mtg.blackjack;

}