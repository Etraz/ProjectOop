package Menu;

import DataManagement.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.Chart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class ControllerOfChartWindow {
    @FXML
    BarChart barChart;
    @FXML
    LineChart lineChart;
    @FXML
    TextField addYseriesField;
    @FXML
    Button addYseriesButton;
    @FXML
    RadioButton barChartButton, lineCharButton;
    @FXML
    VBox ySeriesSettings;

    @FXML
    public void initialize(){
        lineChart.setVisible(false);
        barChart.setVisible(true);
        barChartButton.setSelected(true);
    }

    public void bar_LineSwitch(ActionEvent e) {
        if(barChartButton.isSelected()) {
            barChart.setVisible(true);
            lineChart.setVisible(false);
        }
        else {
            barChart.setVisible(false);
            lineChart.setVisible(true);
        }
    }

    public void addYseries(ActionEvent e) {
        String columnName = addYseriesField.getText();
        //check if already contains this series
        for(var tmp : UsefulFunctions.loopOverSceneGraph(ySeriesSettings, Label.class)) {
            if(tmp.getText().equals(columnName)) return;
        }
        String path = "Test1.csv"; // should it be deduced from columnName?
        int columnIndex = UsefulFunctions.getColumnIndex(path, columnName);
        if(columnIndex < 0) return; // check if specified column exists

        var s = Main.getSeries(path, columnIndex);
        var s2 = Main.getSeries(path, columnIndex);
        s[0].setName(columnName); s2[0].setName(columnName);
        barChart.getData().addAll(s);
        lineChart.getData().addAll(s2);

        // adding HBox with this series settings controls
        HBox oneSeriesSettings = null;
        try{
            oneSeriesSettings = FXMLLoader.load(ChartSetUpWindow.class.getResource("oneSeriesSettings.fxml"));
        } catch(Exception ex) {
            ex.printStackTrace();
        }
        for(var tmp2 : oneSeriesSettings.getChildren()) {
            if(tmp2 instanceof Label) ((Label)tmp2).setText(columnName);
            if(tmp2 instanceof ColorPicker) {
                ((ColorPicker)tmp2).setValue(Color.RED);
                ((ColorPicker)tmp2).valueProperty().addListener(new ChangeListener<Color>() {
                    @Override
                    public void changed(ObservableValue<? extends Color> observableValue, Color color, Color t1) {
                            barChart.setStyle("CHART_COLOR_"+(columnIndex+1)+": "+colorFormat(((ColorPicker)tmp2).getValue())+";");
                            lineChart.setStyle("CHART_COLOR_"+(columnIndex+1)+": "+colorFormat(((ColorPicker)tmp2).getValue())+";");
                    }
                });
            }
        }
        ySeriesSettings.getChildren().add(oneSeriesSettings);
    }

    public static String colorFormat(Color c) {
        int r = (int) (255 * c.getRed()) ;
        int g = (int) (255 * c.getGreen()) ;
        int b = (int) (255 * c.getBlue()) ;

        return String.format("#%02x%02x%02x", r, g, b);
    }
}
