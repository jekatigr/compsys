/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.controller;
import comparative_system.CompSys;
import comparative_system.Preferences;
import comparative_system.Proccessor;
import comparative_system.gui.CodeEditor;
import comparative_system.model.Algorithm;
import comparative_system.model.Code;
import comparative_system.model.DataGenerator;
import comparative_system.model.Project;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SingleSelectionModel;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;

/**
 *
 * @author TireX
 */
public class FXMLguiController implements Initializable {
    private static Stage primaryStage;
    
    public static Parent algPanel;
    public static Parent dataPanel;
    public static Parent testsPanel;
    
    @FXML private static ToggleGroup toggles;
    @FXML private static ToggleButton algButton;
    @FXML private static ToggleButton dataButton;
    @FXML private static ToggleButton testsButton;
    @FXML private static AnchorPane mainPanel;
    @FXML private static ListView algList;
    @FXML private static TextField algNameTextField;
    
    @FXML private static TabPane codesOfAlgorithmsTabPane;
    private static int currentAlgCodeTab;
    @FXML private static ComboBox algMethodsComboBox;
    @FXML private static CheckBox showCountersCheckBox;
    @FXML private static Button reloadMethodsListButton;
    private static boolean addingNewAlg = false;
    private static boolean hasChanges = false;
    

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
    
    @FXML private void handleAddTabButtonClicked() {
        hasChanges = true;
        
        Tab tab = new Tab();
            tab.setText("Класс (" + (codesOfAlgorithmsTabPane.getTabs().size() + 1) + ")");
                CodeEditor ce = new CodeEditor("/*\n   Вставьте сюда код класса\n   или перетащите файл java\n   в окно программы и нажмите\n   кнопку \"Сохранить\"...     */\n\n\n\n");
                ce.setId("ce");
                AnchorPane.setTopAnchor(ce, -5.0);
                AnchorPane.setRightAnchor(ce, 0.0);
                AnchorPane.setBottomAnchor(ce, -5.0);
                AnchorPane.setLeftAnchor(ce, -5.0);
            tab.setContent(ce);
            tab.setClosable(true);
        codesOfAlgorithmsTabPane.getTabs().add(tab);
        codesOfAlgorithmsTabPane.getSelectionModel().select(tab);
    }
    
    @FXML private void handleShowCountersCheckBoxClicked() {
        if (showCountersCheckBox.selectedProperty().get()) {
            CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).setShowCounters(true);
        } else {
            CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).setShowCounters(false);
        }
        FXMLguiController.loadAlgorithmView(Project.getCurrentGuiAlg());
    }
    
    @FXML private void handleReloadMethodsListButtonClicked() {
        ArrayList<String> codes = new ArrayList<>();
        for(Tab tab : codesOfAlgorithmsTabPane.getTabs()) {
            codes.add(((CodeEditor)tab.getContent().lookup("#ce")).getCodeAndSnapshot());
        }
        
        algMethodsComboBox.getItems().clear();
        if (addingNewAlg == false) {
            ArrayList<MethodDeclaration> methods = Proccessor.getAllMethodsFromCodes(codes);
            CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).setMethodsList(methods);
            if (methods.size() > 0) {
                for(MethodDeclaration method : methods) {
                    algMethodsComboBox.getItems().add(method.getName());
                }
            }
        } else {
            ArrayList<MethodDeclaration> methods = Proccessor.getAllMethodsFromCodes(codes);
            if (methods.size() > 0) {
                for(MethodDeclaration method : methods) {
                    algMethodsComboBox.getItems().add(method.getName());
                }
            }
        }
    }
    
    @FXML private void handleSaveAlgButtonClicked() {
        ArrayList<String> codes = new ArrayList<>();
        for(Tab tab : codesOfAlgorithmsTabPane.getTabs()) {
            codes.add(((CodeEditor)tab.getContent().lookup("#ce")).getCodeAndSnapshot());
        }

        //CompSys.getProject().getAlgorithm(CompSys.getProject().getCurrentGuiAlg()).setMethodsList(Proccessor.getAllMethodsFromCodes(codes));
        
        if (algNameTextField.getText().length() > 0) {//проверка имени
            String compileRes = Proccessor.checkClassesCompilableInTabs(codes);
            if (compileRes.length() == 0) {//проверка public-классов в вкладках на компилируемость
                int methodIndex = algMethodsComboBox.getSelectionModel().selectedIndexProperty().getValue();
                if (methodIndex >= 0) {
                    ArrayList<MethodDeclaration> methods;
                    if (!addingNewAlg) {
                        methods = CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).getMethodsList();
                    } else {
                        methods = Proccessor.getAllMethodsFromCodes(codes);
                    }
                    MethodDeclaration method = methods.get(methodIndex);
                    if (checkMainMethodModificators(method)) {//проверка метода на модификаторы public & static
                        List parameters = method.parameters();
                        if (parameters.size() > 0) {//количество параметров должно быть > 0 
                            if (Project.methodParamsAreCompatible(parameters)) {//проверка на совместимость параметров методов.
                                if (addingNewAlg == true) {
                                    CompSys.addAlgorithm(algNameTextField.getText(), method.getName().getFullyQualifiedName(), codes);
                                } else {
                                    CompSys.saveAlgorithm(Project.getCurrentGuiAlg(), algNameTextField.getText(), method.getName().getFullyQualifiedName(), codes);//TODO: сделать изменение имени алгоритма в списке
                                }
                            } else {//запрос на перезапись или исправление переметров методов
                                if (Dialogs.showConfirmDialog(primaryStage, "Перезаписать параметры?", "Параметры метода вызова алгоритма не совпадают с уже сохраненными! Параметры для всех алгоритмов должны совпадать.", "Параметры метода вызова...", Dialogs.DialogOptions.YES_NO) == Dialogs.DialogResponse.YES) {
                                    //перезапись
                                    DataGenerator.saveNewMainMethodParams(parameters);
                                    if (addingNewAlg == true) {
                                        CompSys.addAlgorithm(algNameTextField.getText(), method.getName().getFullyQualifiedName(), codes);
                                    } else {
                                        CompSys.saveAlgorithm(Project.getCurrentGuiAlg(), algNameTextField.getText(), method.getName().getFullyQualifiedName(), codes);//TODO: сделать изменение имени алгоритма в списке
                                    }
                                }
                            }
                        } else {
                            Dialogs.showWarningDialog(primaryStage, "Метод вызова алгоритма должен содержать один или несколько параметров для передачи данных при последующих вычислениях трудоемкости.", "Ошибка при сохранении алгоритма.", "Параметры метода...");
                        }
                    } else {
                        Dialogs.showWarningDialog(primaryStage, "Метод вызова алгоритма должен обладать модификаторами public и static.", "Ошибка при сохранении алгоритма.", "Метод вызова алгоритма...");
                    }                    
                } else {
                    Dialogs.showWarningDialog(primaryStage, "Необходимо выбрать метод вызова алгоритма.", "Ошибка при сохранении алгоритма.", "Метод вызова алгоритма...");
                }                    
            } else {
                Dialogs.showWarningDialog(primaryStage, "Ошибка компиляции исходного кода.\nСообщение компилятора:\n\n" + compileRes, "Ошибка при компиляции алгоритма.", "Классы алгоритма...");
            }                    
        } else {
            Dialogs.showWarningDialog(primaryStage, "Вы не ввели название алгоритма.", "Ошибка при сохранении алгоритма.", "Название алгоритма...");
        }
    }
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }
    
    /**
     * Метод для инициализации интерфейса при запуске программы.
     */
    public static void initialize() {
        algList.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
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
        
        reloadMethodsListButton.setGraphic(new ImageView(new Image(CompSys.class.getResourceAsStream("reload.png"))));
        reloadMethodsListButton.setMinSize(30, 25);
        reloadMethodsListButton.tooltipProperty().setValue(new Tooltip("Обновить список"));
        
        //при первом открытии отключаем все элементы и ставим вкладку алгоритмов.
        FXMLguiController.setAllEnabled(false);
        FXMLguiController.switchShowMode(0);
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
        reloadAlgList();
        algList.getSelectionModel().select(0);
        setAllEnabled(true);
    }
    
    private static void showInfoMessage(String title, String header, String info) {
        Dialogs.showInformationDialog(primaryStage, info, header, title);
    }
    
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Метод для обновления списка алгоритмов в ListView.
     */
    public static void reloadAlgList() {
        algList.getItems().clear();
        int algCount = CompSys.getCountOfAlgorithms();
        for (int i = 0; i < algCount; i++) {
            algList.getItems().add(CompSys.getProject().getAlgorithm(i).getName());                
        }
        Label l = new Label("Добавить...");
        l.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, Font.getDefault().getSize() + 2));
        algList.getItems().add(l);
    }
    
    /**
     * Метод для одображения алгоритма в главном окне.
     * @param index Индекс алгоритма в списке проекта, либо -1 для добавления нового алгоритма.
     */
    public static void loadAlgorithmView(int index) {        
        if (index == algList.getItems().size() - 1) {//добавление нового
            addingNewAlg = true;
            Project.setCurrentGuiAlg(-1);
            algNameTextField.setText("Новый алгоритм...");
            codesOfAlgorithmsTabPane.getTabs().clear();
            
            Tab tab = new Tab();
               tab.setText("Класс (1)");
                   CodeEditor ce = new CodeEditor("/*\n   Вставьте сюда код класса\n   или перетащите файл java\n   в окно программы и нажмите\n   кнопку \"Сохранить\"...     */\n\n\n\n");
                   ce.setId("ce");
                   AnchorPane.setTopAnchor(ce, -5.0);
                   AnchorPane.setRightAnchor(ce, 0.0);
                   AnchorPane.setBottomAnchor(ce, -5.0);
                   AnchorPane.setLeftAnchor(ce, -5.0);
               tab.setContent(ce);
               tab.setClosable(true);
            codesOfAlgorithmsTabPane.getTabs().add(tab);
            algMethodsComboBox.getItems().clear();                    
            showCountersCheckBox.visibleProperty().set(false);
        } else {//отображение сохраненного
            addingNewAlg = false;
            Algorithm alg = CompSys.getProject().getAlgorithm(index);
            Project.setCurrentGuiAlg(index);

            algNameTextField.setText(alg.getName());

            codesOfAlgorithmsTabPane.getTabs().clear();
            for (int i = 0; i < alg.getCodes().size(); i++) {
                 Tab tab = new Tab();
                 Code c = alg.getCodes().get(i);
                 tab.setText(alg.getClassTabName(i));
                     CodeEditor ce = null;
                     if (!alg.getShowCounters()) {
                         ce = new CodeEditor(c.getSourceCode());
                     } else {
                         ce = new CodeEditor(c.getGeneratedCode());                   
                     }
                     ce.setId("ce");
                     AnchorPane.setTopAnchor(ce, -5.0);
                     AnchorPane.setRightAnchor(ce, 0.0);
                     AnchorPane.setBottomAnchor(ce, -5.0);
                     AnchorPane.setLeftAnchor(ce, -5.0);
                 tab.setContent(ce);
                 codesOfAlgorithmsTabPane.getTabs().add(tab);
            }

            algMethodsComboBox.getItems().clear();
            int selectedIndex = 0;
            ArrayList<MethodDeclaration> methods = alg.getMethodsList();
            for (int i = 0; i < methods.size(); i++) {
                algMethodsComboBox.getItems().add(methods.get(i).getName());
                if (methods.get(i).getName().toString().equals(alg.getMainMethod())) {
                    selectedIndex = i;
                }
            }
            algMethodsComboBox.getSelectionModel().select(selectedIndex);

            if (!alg.getShowCounters()) {
                showCountersCheckBox.selectedProperty().set(false);
            } else {                              
                showCountersCheckBox.selectedProperty().set(true);
            }  
        }
    }

    private boolean checkMainMethodModificators(MethodDeclaration m) {
        return (Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()));
    }
}



































