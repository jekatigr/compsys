/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.controller;
import comparative_system.CompSys;
import comparative_system.Preferences;
import comparative_system.gui.CodeEditor;
import comparative_system.model.Algorithm;
import comparative_system.model.Code;
import comparative_system.model.Project;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 *
 * @author TireX
 */
public class FXMLguiController implements Initializable {
    private static Stage primaryStage;
    
    @FXML private static ToggleGroup toggles;
    @FXML private static ToggleButton algButton;
    @FXML private static ToggleButton dataButton;
    @FXML private static ToggleButton testsButton;
    @FXML private static AnchorPane mainPanel;
    @FXML private static ListView algList;
    @FXML private static HBox hBoxZeroAlgs;
    @FXML private static TextField algNameTextField;
    
    @FXML private static TabPane codesOfAlgorithmsTabPane;
    private static int currentAlgCodeTab;
    
    

    @FXML private void handleSaveNewProject(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Сохранение нового проекта...");
        String initial_dir_for_filechooser = Preferences.getLastPathForFileChooser();
        if (initial_dir_for_filechooser != null && new File(initial_dir_for_filechooser).exists())
        {
            fileChooser.setInitialDirectory(new File(initial_dir_for_filechooser));
        }
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("project","*.db"), new FileChooser.ExtensionFilter("all files","*.*"));
        fileChooser.setInitialFileName("new_project.db");
        
        
        File new_proj_file = fileChooser.showSaveDialog(primaryStage);
        if (new_proj_file != null) {
            Preferences.updateLastPathForFileChooser(new_proj_file.getParent());
            Project.createNewProject(new_proj_file);//TODO: сделать проверку на успешное создание файла
            CompSys.openProject(new_proj_file);
        }
    }

    @FXML private void handleOpenProject(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Открыть проект...");
        String initial_dir_for_filechooser = Preferences.getLastPathForFileChooser();
        if (initial_dir_for_filechooser != null && new File(initial_dir_for_filechooser).exists())
        {
            fileChooser.setInitialDirectory(new File(initial_dir_for_filechooser));
        }
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("project","*.db"), new FileChooser.ExtensionFilter("all files","*.*"));

        File proj_file = fileChooser.showOpenDialog(primaryStage);
        if (proj_file != null) {
            Preferences.updateLastPathForFileChooser(proj_file.getParent());
            CompSys.openProject(proj_file);
        }
    }

    @FXML private void handleExit(ActionEvent event) {
        //CompSys.saveAllBeforeClose();
        Platform.exit();
    }

    @FXML private void handleAlgClicked() {
        switchShowMode(0);
    }

    @FXML private void handleDataClicked() {
        switchShowMode(1);
    }

    @FXML private void handleTestsClicked() {
        switchShowMode(2);
    }

    @FXML private void handleAddAlgClicked() {
        try {
            Stage stage = new Stage();
            Parent root = FXMLLoader.load(CompSys.class.getResource("gui/addEditAlgWindow.fxml"));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            //AddEditAlgWindowController.setStage(stage);
            stage.show();
        } catch (IOException ex) {
            Logger.getLogger(FXMLguiController.class.getName()).log(Level.SEVERE, null, ex);//TODO: сделать нормалиный вывод ошибок
        }
    }
    
    
    public static Parent algPanel;
    public static Parent dataPanel;
    public static Parent testsPanel;
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    public static void initialize() {
        algList.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                FXMLguiController.loadAlgorithmView(algList.getSelectionModel().getSelectedIndex());
            }
        });
        
        codesOfAlgorithmsTabPane.getSelectionModel().selectedIndexProperty().addListener(
            new ChangeListener<Number>() {
                @Override
                public void changed(ObservableValue<? extends Number> ov, Number t, Number t1) {
                    currentAlgCodeTab = t1.intValue();
                }
            }
        );
    }

    public static void setAllEnabled(boolean enabled) {
        mainPanel.setDisable(!enabled);
        algButton.setDisable(!enabled);
        dataButton.setDisable(!enabled);
        testsButton.setDisable(!enabled);
    }

    public static void switchShowMode(int mode) {
        switch(mode) {
            case 0: {
                mainPanel.getChildren().clear();
                AnchorPane.setTopAnchor(algPanel, 0.0);
                AnchorPane.setRightAnchor(algPanel, 0.0);
                AnchorPane.setBottomAnchor(algPanel, 0.0);
                AnchorPane.setLeftAnchor(algPanel, 0.0);
                mainPanel.getChildren().add(algPanel);
                break;
            }
            case 1: {
                mainPanel.getChildren().clear();
                AnchorPane.setTopAnchor(dataPanel, 0.0);
                AnchorPane.setRightAnchor(dataPanel, 0.0);
                AnchorPane.setBottomAnchor(dataPanel, 0.0);
                AnchorPane.setLeftAnchor(dataPanel, 0.0);
                mainPanel.getChildren().add(dataPanel);
                break;
            }
            case 2: {
                mainPanel.getChildren().clear();
                AnchorPane.setTopAnchor(testsPanel, 0.0);
                AnchorPane.setRightAnchor(testsPanel, 0.0);
                AnchorPane.setBottomAnchor(testsPanel, 0.0);
                AnchorPane.setLeftAnchor(testsPanel, 0.0);
                mainPanel.getChildren().add(testsPanel);
                break;
            }
        }
    }

    public static void openProject(Project project) {
        algList.getItems().clear();
        hBoxZeroAlgs.setVisible(true);
        if (project.getCountOfAlgorithms() > 0) {
            for (int i = 0; i < project.getCountOfAlgorithms(); i++) {
                addAlgInList(i, project.getAlgorithm(i));
            }
        }
        setAllEnabled(true);
    }
    
    private static void showInfoMessage(String title, String header, String info) {
        Dialogs.showInformationDialog(primaryStage, info, header, title);
    }
    
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Метод для добавления алгоритма в ListView.
     * @param index Индекс алгоритма в списке в проекте.
     * @param alg Алгоритм типа {@code Algoritm}.
     */
    public static void addAlgInList(int index, Algorithm alg) {
        hBoxZeroAlgs.setVisible(false);
        algList.getItems().add(alg.getName());
    }
    
    /**
     * Метод для одображения алгоритма в главном окне.
     * @param index Индекс алгоритма в списке проекта, либо -1 для добавления нового алгоритма.
     */
    public static void loadAlgorithmView(int index) {
       if (index == -1) {//добавление нового
           
       } else {//отображение сохраненного
           Algorithm alg = CompSys.getProject().getAlgorithm(index);
           algNameTextField.setText(alg.getName());
      
           codesOfAlgorithmsTabPane.getTabs().clear();
           for (Code c : alg.getCodes()) {
               Tab tab = new Tab();
                tab.setText("Новый класс");
                    CodeEditor ce = new CodeEditor(c.getSourceCode());
                    ce.setId("ce");
                    //sourceCodeTextArea.setPromptText("Исходный код алгоритма (java)...");
                    AnchorPane.setTopAnchor(ce, -5.0);
                    AnchorPane.setRightAnchor(ce, 0.0);
                    AnchorPane.setBottomAnchor(ce, -5.0);
                    AnchorPane.setLeftAnchor(ce, -5.0);
                tab.setContent(ce);
                codesOfAlgorithmsTabPane.getTabs().add(tab);
           }    //codesOfAlgorithmsTabPane.getSelectionModel().select(tab);
           
       }     
    }
}



































