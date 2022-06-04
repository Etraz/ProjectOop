package Menu;

import DataManagement.Main;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class ControllerOfChartWindow {
    ChartWindow chartWindow;
    @FXML
    public BarChart barChart;
    @FXML
    public LineChart lineChart;
    @FXML
    Button addYseriesButton;
    @FXML
    RadioButton barChartButton, lineCharButton;
    @FXML
    public VBox ySeriesSettings;
    @FXML
    ComboBox<String> addYseriesComboBox;

    private List<String> availableYseries = new ArrayList<>();


    @FXML
    public void initialize(){
        lineChart.setVisible(false);
        barChart.setVisible(true);
        barChartButton.setSelected(true);

//        addYseriesComboBox.focusedProperty().addListener((obs, oldVal, newVal) ->
//        {
//            addYseriesComboBox.setVisible(newVal);
//            addYseriesComboBox.setManaged(newVal);
//        });
//        addYseriesComboBox.setOnMouseClicked(event -> {
//            String File = addYseriesComboBox.getSelectionModel().getSelectedItem();
//            addYseriesField.setText(File);
//            addYseriesComboBox.getSelectionModel().clearSelection();
//        });

        // using data location
        // searching for available xColumns and yColumns
        var filePaths = new ArrayList<String>();
        filePaths.add("Test1.csv");
        ControllerOfChartSetUpWindow.fillXYColumns(new ArrayList<>(), availableYseries, filePaths);
        addYseriesComboBox.getItems().addAll(availableYseries);
    }

    public void ComboBoxTypingIn(KeyEvent k) {
        addYseriesComboBox.getItems().clear();
        addYseriesComboBox.getItems().addAll(ControllerOfChartSetUpWindow.searchList(addYseriesComboBox.getEditor().getText(),availableYseries));
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
        // using data location
        ArrayList<String> filePaths = new ArrayList<>();
        filePaths.add("Test1.csv");
        for(var path : filePaths) {
            addYseriesStatic(ySeriesSettings, barChart, lineChart, path, addYseriesComboBox.getEditor().getText().split("\\|")[1], chartWindow.toSave);
        }
    }

    public static void addYseriesStatic(VBox ySeriesSettings, BarChart barChart, LineChart lineChart, String path, String columnName, StringBuilder toSave) {
        //check if already contains this series
        for(var tmp : UsefulFunctions.loopOverSceneGraph(ySeriesSettings, Label.class)) {
            if(tmp.getText().equals(columnName)) return;
        }
        path = "Test1.csv"; // should it be deduced from columnName?
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
        int seriesNumber = ySeriesSettings.getChildren().size()+1;
        for(var tmp2 : oneSeriesSettings.getChildren()) {
            if(tmp2 instanceof Label) ((Label)tmp2).setText(columnName);
            if(tmp2 instanceof ColorPicker) {
                ((ColorPicker)tmp2).valueProperty().addListener(new ChangeListener<Color>() {
                    @Override
                    public void changed(ObservableValue<? extends Color> observableValue, Color color, Color t1) {
                            barChart.setStyle("CHART_COLOR_"+seriesNumber+": "+colorFormat(((ColorPicker)tmp2).getValue())+";");
                            lineChart.setStyle("CHART_COLOR_"+seriesNumber+": "+colorFormat(((ColorPicker)tmp2).getValue())+";");
                    }
                });
            }
        }
        ySeriesSettings.getChildren().add(oneSeriesSettings);

        toSave.append(path).append(";").append(columnName).append("\n");
    }


    public void handleSaveButtonPressed(ActionEvent event){
        Stage newStage = new Stage();
        newStage.initModality(Modality.APPLICATION_MODAL);
        SaveWindow saveWindow = new SaveWindow(chartWindow);
        saveWindow.display(newStage);
        newStage.showAndWait();
    }

    public static String colorFormat(Color c) {
        int r = (int) (255 * c.getRed()) ;
        int g = (int) (255 * c.getGreen()) ;
        int b = (int) (255 * c.getBlue()) ;

        return String.format("#%02x%02x%02x", r, g, b);
    }
}
