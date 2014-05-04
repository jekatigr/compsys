package comparative_system.controller;

import comparative_system.CompSys;
import comparative_system.Preferences;
import comparative_system.Proccessor;
import comparative_system.gui.CodeEditor;
import comparative_system.model.Algorithm;
import comparative_system.model.Code;
import comparative_system.model.Data;
import comparative_system.model.DataGenerator;
import comparative_system.model.Project;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.ResourceBundle;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Modifier;

/**
 * Класс, отвечающий за поведение GUI.
 * @author Gromov Evg.
 */
public class FXMLguiController implements Initializable {
    /** Форма главного окна. */
    private static Stage primaryStage;
    
    /** Панель алгоритмов. */
    public static Parent algPanel;
    /** Панель генераторов исходных данных. */
    public static Parent dataPanel;
    /** Панель результатов тестов. */
    public static Parent testsPanel;
    
    /** Кнопка для показа панели алгоритов. */
    @FXML private static ToggleButton algButton;
    /** Кнопка для показа панели генераторов. */
    @FXML private static ToggleButton dataButton;
    /** Кнопка для показа панели результатов. */
    @FXML private static ToggleButton testsButton;
    /** Панель, накоторой будет располагаться одна из панелей: алгоритмы, генераторы или результаты. */
    @FXML private static AnchorPane mainPanel;
    /** Элемент список с именами алгоритов. */
    @FXML private static ListView algList;
    /** Слушатель события изменения выбранного алгоритма в списке. */
    private static ChangeListener selectAlgInListListener;
    /** Текстовое поле для имени алгоритма. */
    @FXML private static TextField algNameTextField;
    
    /** Панель вкладок с кодами классов алгоритма. */
    @FXML private static TabPane codesOfAlgorithmsTabPane;
    /** Выпадающий список с методами классов алгоритма. */
    @FXML private static ComboBox algMethodsComboBox;
    /** Чек-бокс для выбора режима показа счетчиков. */
    @FXML private static CheckBox showCountersCheckBox;
    /** Кнопка обновления списка методов в выпадающем списке. */
    @FXML private static Button reloadMethodsListButton;
    /** Флаг для режима добавления нового алгоритма. */
    private static boolean addingNewAlg = false;
    //private static boolean hasChanges = false;
    
    /** Панель для кода и значений генератора исходных данных. */
    @FXML private static AnchorPane dataGeneratorCodePanel;
    /** Элемент список с именами генераторов. */
    @FXML private static ListView genList;
    /** Слушатель события изменения выбранного генератора в списке. */
    private static ChangeListener selectGenInListListener;
    /** Текстовое поле для имени генератора. */
    @FXML private static TextField genNameTextField;
    /** Флаг для режима добавления нового генератора. */
    private static boolean addingNewGen = false;
    /** Контейнер для кнопок выбора режима промотра генератора: код-сгенерированные данные. */
    @FXML private static HBox dataGeneratorToggleGroupHBox;
    /** Надпись с инструкцией к генератору. */
    @FXML private static Label apiInstructionLabel;
    /** Панель с сгенерированными данными генератора. */
    @FXML private static AnchorPane dataGeneratorValuesPanel;
    
    @FXML private static ListView genListForTests;
    @FXML private static ListView algListForTests;
    @FXML private static Button startTestsButton;
    
//    /** Таблица с сгенерированными данными генератора. */
//    @FXML private static TableView tableForDateGeneratorValues;
//
//    public static void reloadTableValuesForCurrentDataGenerator() {
//        DataGenerator gen = CompSys.getProject().getDataGenerator(Project.getCurrentGuiGen());
//        
//        ObservableList<Data> data = FXCollections.observableArrayList(gen.getValues());
//        for(int i = 0; i < DataGenerator.getCountOfMethodsParams(); i++) {
//            TableColumn tc = new TableColumn(""+DataGenerator.getParam(i).getName());
//            tc.setCellValueFactory(new PropertyValueFactory<Data, Object>("list["+i+"]"));
//            tableForDateGeneratorValues.getColumns().add(tc);
//        }
//        tableForDateGeneratorValues.setItems(data);
//        
//    }
   
    /** Обработка нажания кнопки "Сохранить проект". */
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

    /** Обработка нажания кнопки "Открыть проект". */
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

    /** Обработка нажания кнопки "Выход". */
    @FXML private void handleExit(ActionEvent event) {
        //CompSys.saveAllBeforeClose();
        Platform.exit();
    }

    /** Обработка нажания кнопки "Алгоритмы" для выбора панели. */
    @FXML private void handleAlgClicked() {
        switchShowMode(0);
    }

    /** Обработка нажания кнопки "Исходные данные" для выбора панели. */
    @FXML private void handleDataClicked() {
        switchShowMode(1);
    }

    /** Обработка нажания кнопки "Результаты" для выбора панели. */
    @FXML private void handleTestsClicked() {
        switchShowMode(2);
    }
    
    /** Обработка нажания кнопки "Добавить класс" при редактировании алгоритма. */
    @FXML private void handleAddTabButtonClicked() {
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
    
    /** Обработка нажания чек-бокса "показать счетчики". */
    @FXML private void handleShowCountersCheckBoxClicked() {
        if (showCountersCheckBox.selectedProperty().get()) {
            CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).setShowCounters(true);
        } else {
            CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).setShowCounters(false);
        }
        FXMLguiController.loadAlgorithmView(Project.getCurrentGuiAlg());
    }
    
    /** Обработка нажания кнопки "Обновить методы в выпадающем списке". */
    @FXML private void handleReloadMethodsListButtonClicked() {
        ArrayList<String> codes = new ArrayList<>();
        for(Tab tab : codesOfAlgorithmsTabPane.getTabs()) {
            codes.add(((CodeEditor)tab.getContent().lookup("#ce")).getCode());
        }
        
        algMethodsComboBox.getItems().clear();
        ArrayList<MethodDeclaration> methods = Proccessor.getAllMethodsFromCodes(codes);
        for(MethodDeclaration method : methods) {
            algMethodsComboBox.getItems().add(method.getName());
        }
    }
    
    /** Обработка нажания кнопки "Сохранить" при редактировании алгоритма. */
    @FXML private void handleSaveAlgButtonClicked() {
        ArrayList<String> codes = new ArrayList<>();
        for(Tab tab : codesOfAlgorithmsTabPane.getTabs()) {
            codes.add(((CodeEditor)tab.getContent().lookup("#ce")).getCode());
        }

        if (algNameTextField.getText().length() > 0) {//проверка имени
            String compileRes = Proccessor.checkClassesCompilableInTabs(codes);
            if (compileRes.length() == 0) {//проверка public-классов в вкладках на компилируемость
                int methodIndex = algMethodsComboBox.getSelectionModel().selectedIndexProperty().getValue();
                if (methodIndex >= 0) {
                    ArrayList<MethodDeclaration> methods = Proccessor.getAllMethodsFromCodes(codes);
                    MethodDeclaration method = methods.get(methodIndex);
                    if (checkMainMethodModificators(method)) {//проверка метода на модификаторы public & static
                        List parameters = method.parameters();
                        if (parameters.size() > 0) {//количество параметров должно быть > 0 
                            if (Project.methodParamsAreCompatible(parameters)) {//проверка на совместимость параметров методов.
                                if (addingNewAlg == true) {
                                    CompSys.addNewAlgorithm(algNameTextField.getText(), method.getName().getFullyQualifiedName(), codes);
                                } else {
                                    CompSys.saveAlgorithm(Project.getCurrentGuiAlg(), algNameTextField.getText(), method.getName().getFullyQualifiedName(), codes);
                                }
                            } else {//запрос на перезапись или исправление переметров методов
                                if (Dialogs.showConfirmDialog(primaryStage, "Перезаписать параметры?", "Параметры метода вызова алгоритма не совпадают с уже сохраненными! Параметры для всех алгоритмов должны совпадать.", "Параметры метода вызова...", Dialogs.DialogOptions.YES_NO) == Dialogs.DialogResponse.YES) {
                                    //перезапись
                                    DataGenerator.saveNewMainMethodParams(parameters);
                                    if (addingNewAlg == true) {
                                        CompSys.addNewAlgorithm(algNameTextField.getText(), method.getName().getFullyQualifiedName(), codes);
                                    } else {
                                        CompSys.saveAlgorithm(Project.getCurrentGuiAlg(), algNameTextField.getText(), method.getName().getFullyQualifiedName(), codes);
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
    
    @FXML private void handleDataCodeButtonClicked() {
        dataGeneratorValuesPanel.visibleProperty().setValue(false);        
        dataGeneratorCodePanel.visibleProperty().setValue(true);        
    }
    
    @FXML private void handleDataValuesButtonClicked() {
        dataGeneratorCodePanel.visibleProperty().setValue(false);        
        dataGeneratorValuesPanel.visibleProperty().setValue(true);
    }
    
    /** Обработка нажания кнопки "Сохранить" при редактировании генератора. */
    @FXML private void handleSaveGenButtonClicked() {
        String code = ((CodeEditor) dataGeneratorCodePanel.lookup("#ceDataGen")).getCode();
        code = Proccessor.setPackageGenerator(code);
        String name = genNameTextField.getText().trim();
        if (name.length() > 0) {//проверка имени
            String compileRes = Proccessor.checkGeneratorCompilable(code, CompSys.getProject().getAlgorithms());
            if (compileRes.length() == 0) {//проверка на компилируемость
                //отделяем импорты и код generate
                String imports = Proccessor.getUserDefinedImports(code, CompSys.getProject().getAllAlgotithmsAsImportsMap());
                String generateImplementation = Proccessor.getGenerateImplementation(code);
                if (addingNewGen) {
                    CompSys.addNewDataGenerator(name, imports, generateImplementation);
                } else {
                    CompSys.saveDataGenerator(Project.getCurrentGuiGen(), name, imports, generateImplementation);
                }
            } else {
                Dialogs.showWarningDialog(primaryStage, "Ошибка компиляции исходного кода.\nСообщение компилятора:\n\n" + compileRes, "Ошибка при компиляции исходного кода генератора.", "Исходные коды генератора...");
            }
        } else {
            Dialogs.showWarningDialog(primaryStage, "Вы не ввели название генератора.", "Ошибка при сохранении генератора.", "Название генератора...");
        }
    }
    
    /** Обработка нажания кнопки "Сохранить и сгенерировать данные" при редактировании генератора. */
    @FXML private void handleSaveGenAndLoadDataButtonClicked() {
        String code = ((CodeEditor) dataGeneratorCodePanel.lookup("#ceDataGen")).getCode();
        code = Proccessor.setPackageGenerator(code);
        String name = genNameTextField.getText().trim();
        if (name.length() > 0) {//проверка имени
            String compileRes = Proccessor.checkGeneratorCompilable(code, CompSys.getProject().getAlgorithms());
            if (compileRes.length() == 0) {//проверка на компилируемость
                //отделяем импорты и код generate
                String imports = Proccessor.getUserDefinedImports(code, CompSys.getProject().getAllAlgotithmsAsImportsMap());
                String generateImplementation = Proccessor.getGenerateImplementation(code);
                if (addingNewGen) {
                    CompSys.addNewDataGenerator(name, imports, generateImplementation);
                    CompSys.getProject().getDataGenerator(CompSys.getProject().getCountOfDataGenerators() - 1).generateData();
                } else {
                    CompSys.saveDataGenerator(Project.getCurrentGuiGen(), name, imports, generateImplementation);
                    CompSys.getProject().getDataGenerator(Project.getCurrentGuiGen()).generateData();
                }
            } else {
                Dialogs.showWarningDialog(primaryStage, "Ошибка компиляции исходного кода.\nСообщение компилятора:\n\n" + compileRes, "Ошибка при компиляции исходного кода генератора.", "Исходные коды генератора...");
            }
        } else {
            Dialogs.showWarningDialog(primaryStage, "Вы не ввели название генератора.", "Ошибка при сохранении генератора.", "Название генератора...");
        }
    }
        
    @FXML private void handleStartTestsButtonClicked() {
        int gen = genListForTests.getSelectionModel().getSelectedIndex();
        int alg = algListForTests.getSelectionModel().getSelectedIndex();
        
        if (CompSys.getProject().getDataGenerator(gen).getValues().isEmpty()) {
            Dialogs.showErrorDialog(primaryStage, "Для выбранного генератора данных не "
                    + "было создано наборов для тестов.\nПопробуйте исправить реализацию генератора для добавления наборов данных.", "Генератор данных реализован не верно!", "Нет данных для тестов...");
        }
        
        Class c = CompSys.getProject().getAlgorithm(alg).getClassWithMainMethod();
        if (c != null) {
            Dialogs.showErrorDialog(primaryStage, "hihihihihih");
        }
    }
    
    
    /**
     * Метод для инициализации.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        selectAlgInListListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                FXMLguiController.loadAlgorithmView(algList.getSelectionModel().getSelectedIndex());
            }
        };
        
        selectGenInListListener = new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                FXMLguiController.loadDataGeneratorView(genList.getSelectionModel().getSelectedIndex());
            }
        };
    }
    
    /**
     * Метод для инициализации интерфейса при запуске программы.
     */
    public static void initialize() {//TODO: при созании нового проекта при первом сохранении алгоритма не показываются счетчики.
        algList.getSelectionModel().selectedIndexProperty().addListener(selectAlgInListListener);
        genList.getSelectionModel().selectedIndexProperty().addListener(selectGenInListListener);
        
        reloadMethodsListButton.setGraphic(new ImageView(new Image(CompSys.class.getResourceAsStream("reload.png"))));
        reloadMethodsListButton.setMinSize(30, 25);
        reloadMethodsListButton.tooltipProperty().setValue(new Tooltip("Обновить список"));
        
        //при первом открытии отключаем все элементы и ставим вкладку алгоритмов.
        FXMLguiController.setAllEnabled(false);
        FXMLguiController.switchShowMode(0);
        
        CodeEditor ce = new CodeEditor("");
            ce.setId("ceDataGen");
            AnchorPane.setTopAnchor(ce, -5.0);
            AnchorPane.setRightAnchor(ce, 240.0);
            AnchorPane.setBottomAnchor(ce, 0.0);
            AnchorPane.setLeftAnchor(ce, -5.0);
        dataGeneratorCodePanel.getChildren().add(ce);
        
        apiInstructionLabel.setText("Реализуйте функцию generate(). Для сохранения набора данных воспользуйтесь функцией addData("+ DataGenerator.getMethodsParamsAsString(true) +");");
        dataGeneratorCodePanel.visibleProperty().setValue(true);
        dataGeneratorValuesPanel.visibleProperty().setValue(false);
        
        genListForTests.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                FXMLguiController.selectGenInListForTests(newValue.intValue());
            }
        });
        algListForTests.getSelectionModel().selectedIndexProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
                FXMLguiController.selectAlgInListForTests(newValue.intValue());
            }
        });
        
        startTestsButton.setDisable(true);
        algListForTests.setDisable(true);
    }

    /**
     * Метод для активации интерфейса.
     * @param enabled Флаг активации, {@code true} для включения, {@code false} для отключения.
     */
    public static void setAllEnabled(boolean enabled) {
        mainPanel.setDisable(!enabled);
        algButton.setDisable(!enabled);
        dataButton.setDisable(!enabled);
        testsButton.setDisable(!enabled);
    }

    /**
     * Метод для переключения панелей.
     * @param mode Режим: 0 для панели алгоритмов, 1 для генераторов и 2 для результатов.
     */
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

    /**
     * Метод для отображения проекта в GUI.
     * @param project Проект для отображения.
     */
    public static void openProject(Project project) {
        reloadAlgList();
        reloadGenList();
        algList.getSelectionModel().select(0);
        genList.getSelectionModel().select(0);
        apiInstructionLabel.setText("Реализуйте функцию generate(). Для сохранения набора данных воспользуйтесь функцией\naddData("+ DataGenerator.getMethodsParamsAsString(true) +");");
        reloadGenAndAlgListsForTests();
        setAllEnabled(true);
    }
    
    /**
     * Метод задает форму главного окна. Необходимо для показа модальных предупреждений.
     * @param stage Форма окна.
     */
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Метод для обновления списка алгоритмов в ListView.
     */
    public static void reloadAlgList() {
        algList.getSelectionModel().selectedIndexProperty().removeListener(selectAlgInListListener);
        
        algList.getItems().clear();
        int algCount = CompSys.getProject().getCountOfAlgorithms();
        for (int i = 0; i < algCount; i++) {
            algList.getItems().add(getAlgViewInList(i));//CompSys.getProject().getAlgorithm(i).getName());                
        }
        algList.getItems().add(getAlgViewInList(-1));
        
        algList.getSelectionModel().selectedIndexProperty().addListener(selectAlgInListListener);
    }
    
    /**
     * Метод для отображения алгоритма в главном окне.
     * @param index Индекс алгоритма в списке проекта, либо -1 для добавления нового алгоритма.
     */
    public static void loadAlgorithmView(int index) {        
        if (index == algList.getItems().size() - 1) {//добавление нового
            addingNewAlg = true;
            Project.setCurrentGuiAlg(-1);
            algNameTextField.setText("Алгоритм ("+ (CompSys.getProject().getCountOfAlgorithms() + 1) +")");
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
                 tab.setText(c.getClassName());
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
            ArrayList<MethodDeclaration> methods = new ArrayList<>();
            for (Code c : alg.getCodes()) {
                methods.addAll(Proccessor.getAllMethodsFromCode(c.getSourceCode()));
            }
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
            showCountersCheckBox.visibleProperty().set(true);
        }
    }
    
    /**
     * Метод для обновления списка генераторов в ListView.
     */
    public static void reloadGenList() {
        genList.getSelectionModel().selectedIndexProperty().removeListener(selectGenInListListener);
        
        genList.getItems().clear();
        int genCount = CompSys.getProject().getCountOfDataGenerators();
        for (int i = 0; i < genCount; i++) {
            genList.getItems().add(getGenViewInList(i));                
        }
        genList.getItems().add(getGenViewInList(-1));
        
        genList.getSelectionModel().selectedIndexProperty().addListener(selectGenInListListener);
    }
    
    /**
     * Метод для отображения генератора исходных данных в главном окне.
     * @param index Индекс генератора в списке проекта, либо -1 для добавления нового генератора.
     */
    public static void loadDataGeneratorView(int index) {        
        if (index == genList.getItems().size() - 1) {//добавление нового
            addingNewGen = true;
            Project.setCurrentGuiGen(-1);
            genNameTextField.setText("Генератор данных ("+ (CompSys.getProject().getCountOfDataGenerators() + 1) +")");
            String header = DataGenerator.getHeaderInDataGeneratorCode();
            String footer = DataGenerator.getFooterInDataGeneratorCode();
                        //если алгоритмов нет, то не даем добавить генератор.
            
            int fb = 3, fe = header.split("\n").length + fb, sb = fe + 3, se = sb + footer.split("\n").length;
            
            ((CodeEditor)dataGeneratorCodePanel.lookup("#ceDataGen")).setCode("//Добавьте необходимые классы здесь...\n\n\n"+ header + "\n        \n        \n        \n"+footer, fb, fe, sb, se);
            dataGeneratorToggleGroupHBox.visibleProperty().setValue(false);
        } else {//отображение сохраненного
            addingNewGen = false;
            DataGenerator gen = CompSys.getProject().getDataGenerator(index);
            Project.setCurrentGuiGen(index);

            genNameTextField.setText(gen.getName());

            if(gen.getIsCodeOpened()) {//отображаем исходный код
                String header = DataGenerator.getHeaderInDataGeneratorCode();
                String footer = DataGenerator.getFooterInDataGeneratorCode();

                String imports = gen.getImports() + " \n";
                String genImp = gen.getGenerateImplementation();
                
                int fb = 3 + imports.split("\n").length, fe = fb + header.split("\n").length, sb = fe + genImp.split("\n").length, se = sb + footer.split("\n").length + 2;
                
                ((CodeEditor)dataGeneratorCodePanel.lookup("#ceDataGen")).setCode("//Добавьте необходимые классы здесь...\n\n\n"+ imports + header + genImp + footer, fb, fe, sb, se);
                dataGeneratorToggleGroupHBox.visibleProperty().setValue(true);
            } else {//отображаем сгенерированные данные
                //TODO: отображаем сгенерированные данные
            }
        }
    }
    
    /**
     * Метод для проверки модификаторов метода.
     * @param m Метод для проверки.
     * @return {@code true}, если есть модификаторы {@code public} и {@code static}, {@code false} иначе.
     */
    private static boolean checkMainMethodModificators(MethodDeclaration m) {
        return (Modifier.isPublic(m.getModifiers()) && Modifier.isStatic(m.getModifiers()));
    }
    
    /**
     *  Метод для перезагрузки списков генераторов и алгоритмов на панели результатов.
     */
    public static void reloadGenAndAlgListsForTests() {
        genListForTests.getItems().clear();
        for(DataGenerator gen : CompSys.getProject().getDataGenerators()) {
            genListForTests.getItems().add(gen.getName());
        }
        algListForTests.getItems().clear();
        for(Algorithm alg : CompSys.getProject().getAlgorithms()) {
            algListForTests.getItems().add(alg.getName());
        }
    }
    
    private static void selectGenInListForTests(int index) {
        if (index != -1 && CompSys.getProject().getDataGenerator(index) != null) {
            algListForTests.setDisable(false);
            if (algListForTests.getSelectionModel().getSelectedIndex() != -1) {
                startTestsButton.setDisable(false);
            } else {
                startTestsButton.setDisable(true);
            }
        } else {
            startTestsButton.setDisable(true);
            algListForTests.setDisable(true);
        }
    }

    private static void selectAlgInListForTests(int index) {
        startTestsButton.setDisable(false);
    }
    
    private static Pane getAlgViewInList(int index) {
        if (index != -1) {
            AnchorPane p = new AnchorPane();
            p.setPrefSize(100, 50);
            Algorithm alg = CompSys.getProject().getAlgorithm(index);
            if (alg.hasErrors()) {
                ImageView iw = new ImageView();
                iw.setImage(new Image(CompSys.class.getResourceAsStream("warn.png")));
                iw.setFitHeight(35);
                iw.setFitWidth(35);
                Tooltip t = new Tooltip("В алгоритме возникли ошибки компиляции.");
                Tooltip.install(iw, t);
                AnchorPane.setRightAnchor(iw, 0.0);
                AnchorPane.setTopAnchor(iw, 7.0);
                AnchorPane.setBottomAnchor(iw, 7.0);
                p.getChildren().add(iw);
            }
            Label l = new Label("\"" + alg.getName() + "\"");
            l.setFont(Font.font("Colibri", 16.0));
            Label l2 = new Label("Классов: " + alg.getCodes().size());
            l2.setFont(Font.font("Colibri", 12.0));
            l2.setUnderline(true);
            AnchorPane.setLeftAnchor(l, 10.0);
            if (alg.hasErrors()) {
                AnchorPane.setRightAnchor(l, 37.0);
            } else {
                AnchorPane.setRightAnchor(l, 2.0);
            }
            AnchorPane.setLeftAnchor(l2, 20.0);
            AnchorPane.setRightAnchor(l2, 37.0);
            AnchorPane.setBottomAnchor(l2, 5.0);
            p.getChildren().add(l);
            p.getChildren().add(l2);
            return p;
        } else {
            VBox v = new VBox();
            Label l = new Label("Добавить");
            l.setFont(Font.font("Colibri", FontWeight.EXTRA_BOLD, 16.0));
            Label l2 = new Label("новый алгоритм...");
            v.setPrefSize(100, 50);
            v.setAlignment(Pos.CENTER);
            v.getChildren().add(l);
            v.getChildren().add(l2);
            return v;
        }
    }
    
    private static Pane getGenViewInList(int index) {
        if (index != -1) {
            AnchorPane p = new AnchorPane();
            p.setPrefSize(100, 50);
            DataGenerator gen = CompSys.getProject().getDataGenerator(index);
            Label l = new Label("\"" + gen.getName() + "\"");
            l.setFont(Font.font("Colibri", 16.0));
            Label l2 = new Label("Наборов данных: " + gen.getValues().size());
            l2.setFont(Font.font("Colibri", 12.0));
            l2.setUnderline(true);
            AnchorPane.setLeftAnchor(l, 10.0);
            AnchorPane.setRightAnchor(l, 2.0);
            AnchorPane.setLeftAnchor(l2, 20.0);
            AnchorPane.setRightAnchor(l2, 37.0);
            AnchorPane.setBottomAnchor(l2, 5.0);
            p.getChildren().add(l);
            p.getChildren().add(l2);
            return p;
        } else {
            VBox v = new VBox();
            Label l = new Label("Добавить");
            l.setFont(Font.font("Colibri", FontWeight.EXTRA_BOLD, 16.0));
            Label l2 = new Label("генератор данных...");
            v.setPrefSize(100, 50);
            v.setAlignment(Pos.CENTER);
            v.getChildren().add(l);
            v.getChildren().add(l2);
            return v;
        }
    }
}


































