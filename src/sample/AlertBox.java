package sample;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.event.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.*;

public class AlertBox
{
    public static void alert(String title,String message)
    {
        Stage alert =new Stage();

        alert.initModality(Modality.APPLICATION_MODAL);//okienko pierwszego priorytetu
        alert.setTitle(title);
        alert.setMinWidth(250);
        alert.setMinHeight(100);

        Label mes=new Label(message);
        Button b=new Button("Ok");
            b.setOnAction(e -> alert.close());
        VBox label1=new VBox(10);
        label1.getChildren().addAll(mes,b);
        label1.setAlignment(Pos.CENTER);

        Scene s=new Scene(label1);

        alert.setScene(s);
        alert.showAndWait();
    }
}
