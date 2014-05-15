package comparative_system.controller;

import comparative_system.CompSys;
import comparative_system.Counter;
import comparative_system.Preferences;
import comparative_system.Proccessor;
import comparative_system.gui.CodeEditor;
import comparative_system.model.Algorithm;
import comparative_system.model.Code;
import comparative_system.model.Data;
import comparative_system.model.DataGenerator;
import comparative_system.model.Project;
import comparative_system.model.Result;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Dialogs.DialogOptions;
import javafx.scene.control.Dialogs.DialogResponse;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.FlowPane;
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
    
    /** Режим: 0 для панели алгоритмов, 1 для генераторов и 2 для результатов. */
    private static int mode;
    
    @FXML private static MenuItem openProjectMenuItem;
    @FXML private static MenuItem newProjectMenuItem;
    
    /** Панель алгоритмов. */
    public static Parent algPanel;
    /** Панель генераторов исходных данных. */
    public static Parent dataPanel;
    /** Панель результатов тестов. */
    public static Parent testsPanel;
    
    /** Панель для GUI без открытого проекта. */
    @FXML private static FlowPane projectNotOpenedPanel;
    /** Панель для GUI генераторов без алгоритмов в проекте. */
    @FXML private static FlowPane withoutAnyAlgorithmsPanel;
    /** Панель для GUI тестов без генераторов в проекте. */
    @FXML private static FlowPane withoutAnyDataGeneratorsPanel;
    
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
    /** Кнопка для удаления алгоритма. */
    @FXML private static Button removeAlgButton;
    
    /** Панель вкладок с кодами классов алгоритма. */
    @FXML private static TabPane codesOfAlgorithmsTabPane;
    /** Выпадающий список с методами классов алгоритма. */
    @FXML private static ComboBox algMethodsComboBox;
    /** Чек-бокс для выбора режима показа счетчиков. */
    @FXML private static CheckBox showCountersCheckBox;
    /** Кнопка обновления списка методов в выпадающем списке. */
    @FXML private static Button reloadMethodsListButton;
    /** Кнопка для показа ошибок в алгоритме. */
    @FXML private static Button showErrorDescriptionButton;
    /** Флаг для режима добавления нового алгоритма. */
    private static boolean addingNewAlg = false;
    
    /** Панель для кода генератора исходных данных. */
    @FXML private static AnchorPane dataGeneratorCodePanel;
    /** Элемент список с именами генераторов. */
    @FXML private static ListView genList;
    /** Слушатель события изменения выбранного генератора в списке. */
    private static ChangeListener selectGenInListListener;
    /** Текстовое поле для имени генератора. */
    @FXML private static TextField genNameTextField;
    /** Флаг для режима добавления нового генератора. */
    @FXML private static Button removeGenButton;
    /** Флаг для добавления нового генератора данных. */
    private static boolean addingNewGen = false;
    /** Надпись с инструкцией к генератору. */
    @FXML private static Label apiInstructionLabel;
    
    /** Выпадающий список генераторов данных для проведения вычислений трудоемкости. */
    @FXML private static ComboBox genListForTests;
    /** Выпадающий список алгоритмов для проведения вычислений трудоемкости. */
    @FXML private static ComboBox algListForTests;
    /** Кнопка для начала тестов. */
    @FXML private static Button startTestsButton;
    /** График результатов. */
    @FXML private static LineChart testGraph;
    
    /** Панель для отображения текущей задачи. */
    @FXML private static FlowPane taskPerformingPanel;
    /** Имя текущей задачи. */
    @FXML private static Label currentTaskTitle;
    /** Описание текущей задачи. */
    @FXML private static Label currentTaskDescription;
    /** Прогрессбар для отображения прогресса выполнения текущей задачи. */
    @FXML private static ProgressBar taskProgressBar;
    /** Кнопка отмены текущей задачи. */
    @FXML private static Button cancelTaskButton;
    /** Флаг для остановки текущей задачи. */
    public static boolean stopTask = false;
    /** Список всех результатов, которые в данный момент выведены на график.  */
    private static HashMap<String, XYChart.Series<Number,Number>> onGraph = new HashMap<>();

       
    /** Обработка нажания кнопки "Новый проект...". */
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
            CompSys.closeProject();
            Project.createNewProject(new_proj_file);
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
            CompSys.closeProject();
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
        ArrayList<MethodDeclaration> methods = Proccessor.getAllMethodsFromCodesStrings(codes);
        for(MethodDeclaration method : methods) {
            algMethodsComboBox.getItems().add(method.getName());
        }
        
        algMethodsComboBox.setDisable(false);
    }
    
    /** Обработка нажания кнопки "Сохранить" при редактировании алгоритма. */
    @FXML private void handleSaveAlgButtonClicked() {
        final ArrayList<Code> codes;
        if(addingNewAlg == false && CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).getShowCounters()) {
            codes = CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).getCodes();
        } else {
            codes = new ArrayList<>();
            for(Tab tab : codesOfAlgorithmsTabPane.getTabs()) {
                String code = ((CodeEditor)tab.getContent().lookup("#ce")).getCode();
                codes.add(Proccessor.putCounters(code));
            }
        }
        
        final String algName = algNameTextField.getText();
        final int methodIndex = algMethodsComboBox.getSelectionModel().selectedIndexProperty().getValue();
        
        final Task<Void> t = new Task<Void>() {
            @Override
            protected Void call() {
                this.updateTitle("Проверка алгоритма:");
                if (algName.length() > 0) {//проверка имени
                    if (methodIndex >= 0) {
                        this.updateMessage("Проверка метода вызова алгоритма и его параметров...");
                        this.updateProgress(1, 3);
                        ArrayList<MethodDeclaration> methods = Proccessor.getAllMethodsFromCodes(codes);
                        final MethodDeclaration method = methods.get(methodIndex);
                        if (Modifier.isPublic(method.getModifiers()) && Modifier.isStatic(method.getModifiers())) {//проверка метода на модификаторы public & static
                            final List parameters = method.parameters();
                            if (parameters.size() > 0) {//количество параметров должно быть > 0 
                                final String mName = method.getName().getFullyQualifiedName();
                                if (Project.methodParamsAreCompatible(parameters)) {//проверка на совместимость параметров методов.
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (addingNewAlg == true) {
                                                CompSys.addNewAlgorithm(new Algorithm(algName, mName, codes));
                                            } else {
                                                CompSys.saveAlgorithm(Project.getCurrentGuiAlg(), algName, mName, codes);
                                            }   
                                        }
                                    });
                                } else {//запрос на перезапись или исправление переметров методов
                                    Platform.runLater(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (Dialogs.showConfirmDialog(primaryStage, "Параметры метода вызова алгоритма не совпадают с уже сохраненными!\nДругие алгоритмы или генераторы данных могут потребовать соответствующих исправлений.", "Перезаписать параметры?", "Параметры метода вызова...", Dialogs.DialogOptions.YES_NO) == Dialogs.DialogResponse.YES) {
                                                //перезапись
                                                DataGenerator.saveNewMainMethodParams(parameters);
                                                if (addingNewAlg == true) {
                                                    CompSys.addNewAlgorithm(new Algorithm(algName, mName, codes));
                                                } else {
                                                    CompSys.saveAlgorithm(Project.getCurrentGuiAlg(), algName, mName, codes);
                                                }
                                            }
                                        }
                                    });
                                }
                            } else {
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        Dialogs.showWarningDialog(primaryStage, "Метод вызова алгоритма должен содержать один или несколько параметров для передачи данных при последующих вычислениях трудоемкости.", "Ошибка при сохранении алгоритма.", "Параметры метода...");
                                    }
                                });
                            }
                        } else {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    Dialogs.showWarningDialog(primaryStage, "Метод вызова алгоритма должен обладать модификаторами public и static.", "Ошибка при сохранении алгоритма.", "Метод вызова алгоритма...");
                                }
                            });
                        }                    
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                Dialogs.showWarningDialog(primaryStage, "Необходимо выбрать метод вызова алгоритма.", "Ошибка при сохранении алгоритма.", "Метод вызова алгоритма...");
                            }
                        });
                    }                  
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Dialogs.showWarningDialog(primaryStage, "Вы не ввели название алгоритма.", "Ошибка при сохранении алгоритма.", "Название алгоритма...");
                        }
                    });
                }  
                return null;
            }
        };
        
        startPerformingTask(false);
        
        final Thread th = new Thread(t);
        th.start();
        
        Task<Void> daemon = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    while(th.isAlive()) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                FXMLguiController.refreshPerformingTaskPanel(t.getTitle(), t.getMessage(), t.getProgress());
                            }
                        });
                        th.join(100);
                    }
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            FXMLguiController.stopPerformingTask();
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(CompSys.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        Thread upd = new Thread(daemon);
        upd.start();
    }
    
    /** Обработка нажания кнопки "Сохранить и сгенерировать данные" при редактировании генератора. */
    @FXML private void handleSaveGenAndLoadDataButtonClicked() {
        final String code = Proccessor.setPackageGenerator(((CodeEditor) dataGeneratorCodePanel.lookup("#ceDataGen")).getCode());
        
        final Task<Void> t = new Task<Void>() {
            @Override
            protected Void call() {
                this.updateTitle("Сохранение генератора исходных данных...");
                this.updateMessage("Проверка параметров...");
                this.updateProgress(1, 4);
                String name = genNameTextField.getText().trim();
                if (name.length() > 0) {//проверка имени
                    this.updateMessage("Компиляция...");
                    this.updateProgress(2, 4);
                    final String compileRes = Proccessor.checkGeneratorCompilable(code);
                    if (compileRes.length() == 0) {//проверка на компилируемость
                        //отделяем импорты и код generate
                        this.updateMessage("Генерация данных...");
                        this.updateProgress(3, 4);
                        String imports = Proccessor.getUserDefinedImports(code, CompSys.getProject().getAllAlgotithmsAsImportsMap());
                        String generateImplementation = Proccessor.getGenerateImplementation(code);
                        if (addingNewGen) {
                            CompSys.addNewDataGenerator(name, imports, generateImplementation);
                            try {
                                CompSys.getProject().getDataGenerator(CompSys.getProject().getCountOfDataGenerators() - 1).generateData(CompSys.getProject().getAllAlgotithmsAsImportsMap());
                            } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                                String stackTr = "";
                                for (Object o : ex.getStackTrace()) {
                                    stackTr += o + "\n";
                                }
                                final String  exMess = ex.getMessage() + "\n\n" + ex.toString() + "\n" + stackTr;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        AnchorPane pane = new AnchorPane();
                                        TextArea t = new TextArea();
                                        t.setText(exMess);
                                        AnchorPane.setTopAnchor(t, -1.0);
                                        AnchorPane.setRightAnchor(t, -1.0);
                                        AnchorPane.setBottomAnchor(t, -1.0);
                                        AnchorPane.setLeftAnchor(t, -1.0);
                                        pane.getChildren().add(t);
                                        Dialogs.showCustomDialog(FXMLguiController.getPrimaryStage(), pane, "Исключение при расчетах трудоемкости.\n\nИнформация об исключении:", "Исключение при расчетах...", Dialogs.DialogOptions.OK, null);
                                    }
                                });
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    FXMLguiController.reloadGenList();
                                    FXMLguiController.reloadGenAndAlgListsForTests();
                                    testGraph.getData().clear();
                                }
                            });
                        } else {
                            CompSys.saveDataGenerator(Project.getCurrentGuiGen(), name, imports, generateImplementation);
                            try {
                                CompSys.getProject().getDataGenerator(Project.getCurrentGuiGen()).generateData(CompSys.getProject().getAllAlgotithmsAsImportsMap());
                            } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException ex) {
                                String stackTr = "";
                                for (Object o : ex.getStackTrace()) {
                                    stackTr += o + "\n";
                                }
                                final String  exMess = ex.getMessage() + "\n\n" + ex.toString() + "\n" + stackTr;
                                Platform.runLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        AnchorPane pane = new AnchorPane();
                                        TextArea t = new TextArea();
                                        t.setText(exMess);
                                        AnchorPane.setTopAnchor(t, -1.0);
                                        AnchorPane.setRightAnchor(t, -1.0);
                                        AnchorPane.setBottomAnchor(t, -1.0);
                                        AnchorPane.setLeftAnchor(t, -1.0);
                                        pane.getChildren().add(t);
                                        Dialogs.showCustomDialog(FXMLguiController.getPrimaryStage(), pane, "Исключение при расчетах трудоемкости.\n\nИнформация об исключении:", "Исключение при расчетах...", Dialogs.DialogOptions.OK, null);
                                    }
                                });
                            }
                            for(int i = 0; i < CompSys.getProject().getCountOfAlgorithms(); i++) {
                                CompSys.getProject().getAlgorithm(i).removeResults(Project.getCurrentGuiGen());
                            }
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    FXMLguiController.reloadGenList();
                                    FXMLguiController.reloadGenAndAlgListsForTests();
                                    FXMLguiController.loadDataGeneratorView(Project.getCurrentGuiGen());
                                    testGraph.getData().clear();
                                }
                            });
                        }
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                AnchorPane pane = new AnchorPane();
                                TextArea t = new TextArea();
                                t.setText(compileRes);
                                AnchorPane.setTopAnchor(t, -1.0);
                                AnchorPane.setRightAnchor(t, -1.0);
                                AnchorPane.setBottomAnchor(t, -1.0);
                                AnchorPane.setLeftAnchor(t, -1.0);
                                pane.getChildren().add(t);
                                Dialogs.showCustomDialog(primaryStage, pane, "Ошибка при компиляции кода генератора.\n\nСообщение компилятора:", "Ошибки в алгоритме...", Dialogs.DialogOptions.OK, null);
                            }
                        });
                    }
                } else {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            Dialogs.showWarningDialog(primaryStage, "Вы не ввели название генератора.", "Ошибка при сохранении генератора.", "Название генератора...");
                        }
                    });
                }
                return null;
            }
        };
        
        startPerformingTask(false);
        
        final Thread th = new Thread(t);
        th.start();
        
        Task<Void> daemon = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    while(th.isAlive()) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                FXMLguiController.refreshPerformingTaskPanel(t.getTitle(), t.getMessage(), t.getProgress());
                            }
                        });
                        th.join(100);
                    }
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            FXMLguiController.stopPerformingTask();
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(CompSys.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        Thread upd = new Thread(daemon);
        upd.start();
    }
        
    /**
     * Обработка нажатия кнопки "Расчитать трудоемкость".
     */
    @FXML private void handleStartTestsButtonClicked() {
        final int gen_index = genListForTests.getSelectionModel().getSelectedIndex();
        final int alg_index = algListForTests.getSelectionModel().getSelectedIndex();
        
        if (CompSys.getProject().getAlgorithm(alg_index).hasErrors()) {
            Dialogs.showErrorDialog(primaryStage, "Исправьте ошибки для проведения вычислений.", "В выбранном алгоритме есть ошибки.", "Ошибки в алгоритме...");
            return;
        }
        
        final ArrayList<Data> data = CompSys.getProject().getDataGenerator(gen_index).getValues();
        if (data.isEmpty()) {
            Dialogs.showErrorDialog(primaryStage, "Для выбранного генератора данных не "
                    + "было создано наборов для тестов.\nПопробуйте исправить реализацию генератора для добавления наборов данных.", "Генератор данных реализован не верно!", "Нет данных для тестов...");
            return;
        }
        
        
        final Algorithm alg = CompSys.getProject().getAlgorithm(alg_index);
        final Class c = alg.getClassWithMainMethod();
        final Class[] classes_of_params = DataGenerator.getMethodsParamsAsArrayOfClasses();

        final ArrayList<Result> res = new ArrayList<>();
        final XYChart.Series<Number,Number> series = new XYChart.Series<>();
        series.setName(alg.getName()+ " - " +CompSys.getProject().getDataGenerator(gen_index).getName());

        FXMLguiController.startPerformingTask(true);

        final Task<Void> t = new Task<Void>() {
            @Override
            protected Void call() {
                if (alg.getResults(gen_index) == null || alg.getResults(gen_index).isEmpty()) {
                    if (c != null) {
                        this.updateTitle("Вычисление трудоемкости...");
                        this.updateMessage("Обработано: 0 из " + data.size() + " наборов данных...");
                        this.updateProgress(0, data.size());
                        try {
                            for (int i = 0; i < data.size() && !stopTask; i++) {
                                Counter.resetCountResult();
                                Method method = c.getMethod(alg.getMainMethod(), classes_of_params);
                                method.invoke(null, data.get(i).getValues());
                                res.add(new Result(Counter.getCountResult(), data.get(i).getSecondParam()));
                                this.updateMessage("Обработано: "+ i +" из " + data.size() + " наборов данных...");
                                this.updateProgress(i, data.size());
                            }
                        } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException ex) {
                            String stackTr = "";
                            for (Object o : ex.getStackTrace()) {
                                stackTr += o + "\n";
                            }
                            final String  exMess = ex.getMessage() + "\n\n" + ex.toString() + "\n" + stackTr;
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    AnchorPane pane = new AnchorPane();
                                    TextArea t = new TextArea();
                                    t.setText(exMess);
                                    AnchorPane.setTopAnchor(t, -1.0);
                                    AnchorPane.setRightAnchor(t, -1.0);
                                    AnchorPane.setBottomAnchor(t, -1.0);
                                    AnchorPane.setLeftAnchor(t, -1.0);
                                    pane.getChildren().add(t);
                                    Dialogs.showCustomDialog(primaryStage, pane, "Исключение при расчетах трудоемкости.\n\nИнформация об исключении:", "Исключение при расчетах...", Dialogs.DialogOptions.OK, null);
                                }
                            });
                        }
                    }
                    CompSys.getProject().getAlgorithm(alg_index).setResults(gen_index, res);
                }
                return null;
            }
        };
        
        final Thread th = new Thread(t);
        th.start();

        Task<Void> daemon = new Task<Void>() {
            @Override
            protected Void call() {
                try {
                    while(th.isAlive()) {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                FXMLguiController.refreshPerformingTaskPanel(t.getTitle(), t.getMessage(), t.getProgress());
                            }
                        });
                        th.join(100);
                    }
                    
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            ArrayList<Result> res_values = alg.getResults(gen_index);
                            for (Result r : res_values) {
                                series.getData().add(new XYChart.Data<Number,Number>(r.getSecondParam(), r.getCountOfOperations()));
                            } 
                            if (onGraph.containsKey(series.getName())) {
                                testGraph.getData().remove(onGraph.get(series.getName()));
                            }
                            testGraph.getData().add(series);       
                            onGraph.put(series.getName(), series);

                            series.nodeProperty().get().setStyle("-fx-stroke-width: 2px;");
                            
                            FXMLguiController.stopPerformingTask();
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(CompSys.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        Thread upd = new Thread(daemon);
        upd.start();
    }
    
    /**
     * Обработка изменения текста в редакторе кода.
     */
    public static void handleKeyTypedInCodeEditor() {
        if (mode == 0) {
            algMethodsComboBox.getItems().clear();
            algMethodsComboBox.setDisable(true);
        } 
    }
    
    /**
     * Обработка нажания кнопки "показать ошибки".
     */
    @FXML private static void handleShowErrorInAlgButtonClicked() {
        AnchorPane pane = new AnchorPane();
        TextArea t = new TextArea();
        ArrayList<Code> codes = CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).getCodes();
        String error = "";
        for(Code c : codes) {
            if (c.getSourceCodeErrors().isEmpty()) {
                t.setText(c.getGeneratedCodeErrors());
                error = "Ошибка компиляции кода с вставленными счетчиками.\nПопробуйте изменить алгоритм.\n\nСообщение компилятора:";
            } else {
                t.setText(c.getSourceCodeErrors());
                error = "Ошибка компиляции исходного кода.\n\nСообщение компилятора:";
            }
        }
        AnchorPane.setTopAnchor(t, -1.0);
        AnchorPane.setRightAnchor(t, -1.0);
        AnchorPane.setBottomAnchor(t, -1.0);
        AnchorPane.setLeftAnchor(t, -1.0);
        pane.getChildren().add(t);
        Dialogs.showCustomDialog(primaryStage, pane, error, "Ошибки в алгоритме...", Dialogs.DialogOptions.OK, null);
    }
    
    /**
     * Обработка нажатия кнопки "Очистить график".
     */
    @FXML private void handleClearTestsGraphButtonClicked() {
            testGraph.getData().clear();
            onGraph.clear();
    }
    
    /**
     * Обработка нажатия кнопки удаления алгоритма.
     */
    @FXML private void handleRemoveAlgButtonClicked() {
        if (Project.getCurrentGuiAlg() != -1 && DialogResponse.YES == 
            Dialogs.showConfirmDialog(primaryStage, "", "Удалить алгоритм \""+ CompSys.getProject().getAlgorithm(Project.getCurrentGuiAlg()).getName() +"\" и все связанные данные?", "Удаление алгоритма...", Dialogs.DialogOptions.YES_NO)) {
            CompSys.removeAlgorithm(Project.getCurrentGuiAlg());
            FXMLguiController.reloadAlgList();
            FXMLguiController.loadAlgorithmView(0);
            reloadGenAndAlgListsForTests();
            testGraph.getData().clear();
        }
    }
    
    /**
     * Обработка нажатия кнопки удаления генератора данных.
     */
    @FXML private void handleRemoveGenButtonClicked() {
        if (Project.getCurrentGuiGen() != -1 && DialogResponse.YES == 
            Dialogs.showConfirmDialog(primaryStage, "", "Удалить генератор \""+ CompSys.getProject().getDataGenerator(Project.getCurrentGuiGen()).getName() +"\" и все связанные данные?", "Удаление генератора...", Dialogs.DialogOptions.YES_NO)) {
            CompSys.removeDataGenerator(Project.getCurrentGuiGen());
            FXMLguiController.reloadGenList();
            FXMLguiController.loadDataGeneratorView(0);
            reloadGenAndAlgListsForTests();
            testGraph.getData().clear();
        }
    }
    
    /**
     * Метод для обработки нажатия кнопки "Отменить задачу".
     */
    @FXML private void handleCancelTaskButtonClicked() {
        stopTask = true;
    }
    
    /**
     * Метод для обработки нажатия кнопки "Добавить" для добавления генератора..
     */
    @FXML private void handleAddDataLinkClicked() {
        dataButton.setSelected(true);
        switchShowMode(1);
    }
    
    /**
     * Метод для обработки нажатия кнопки "Добавить" для добавления алгоритма.
     */
    @FXML private void handleAddAlgLinkClicked() {
        algButton.setSelected(true);
        switchShowMode(0);
    }
    
    /**
     * Метод для обработки нажатия кнопки "Папка с jdk" в меню программы.
     */
    @FXML private void handleMenuItemJDKPathClicked() {
        CompSys.setNewJDKPath();
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
        
        removeAlgButton.setGraphic(new ImageView(new Image(CompSys.class.getResourceAsStream("remove.png"))));
        removeAlgButton.setMinSize(45, 25);
        removeGenButton.setGraphic(new ImageView(new Image(CompSys.class.getResourceAsStream("remove.png"))));
        removeGenButton.setMinSize(45, 25);
        
        reloadMethodsListButton.setGraphic(new ImageView(new Image(CompSys.class.getResourceAsStream("reload.png"))));
        reloadMethodsListButton.setMinSize(30, 25);
        reloadMethodsListButton.tooltipProperty().setValue(new Tooltip("Обновить список"));
        
        algMethodsComboBox.setDisable(true);
        showErrorDescriptionButton.setVisible(false);
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
        
        apiInstructionLabel.setText("Реализуйте функцию generate(). Для сохранения набора данных воспользуйтесь функцией addData(long second_parameter, ...");
        dataGeneratorCodePanel.visibleProperty().setValue(true);
        
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
        
        testGraph.setCreateSymbols(true);
        testGraph.setAnimated(false);
        
        taskPerformingPanel.setVisible(false);
        withoutAnyAlgorithmsPanel.setVisible(false);
        withoutAnyDataGeneratorsPanel.setVisible(false);
    }

    /**
     * Метод возвращает форму главного окна.
     * @return Форма главного окна.
     */ 
    public static Stage getPrimaryStage() {
        return primaryStage;
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
     * @param m Режим: 0 для панели алгоритмов, 1 для генераторов и 2 для результатов.
     */
    public static void switchShowMode(int m) {
        mode = m;
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
                withoutAnyAlgorithmsPanel.setVisible(CompSys.getProject().getCountOfAlgorithms() == 0);
                mainPanel.getChildren().clear();
                AnchorPane.setTopAnchor(dataPanel, 0.0);
                AnchorPane.setRightAnchor(dataPanel, 0.0);
                AnchorPane.setBottomAnchor(dataPanel, 0.0);
                AnchorPane.setLeftAnchor(dataPanel, 0.0);
                mainPanel.getChildren().add(dataPanel);
                apiInstructionLabel.setText("Реализуйте функцию generate(). Для сохранения набора данных воспользуйтесь функцией\naddData(long second_parameter, "+ DataGenerator.getMethodsParamsAsString(true) +");");
                break;
            }
            case 2: {
                withoutAnyDataGeneratorsPanel.setVisible(CompSys.getProject().getCountOfDataGenerators() == 0);
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
        projectNotOpenedPanel.setVisible(false);
        primaryStage.setTitle("Вычисление трудоемкости алгоритмов - " + project.getProjectFilePath());
        reloadAlgList();
        reloadGenList();
        algList.getSelectionModel().select(0);
        genList.getSelectionModel().select(0);
        apiInstructionLabel.setText("Реализуйте функцию generate(). Для сохранения набора данных воспользуйтесь функцией\naddData(long second_parameter, "+ DataGenerator.getMethodsParamsAsString(true) +");");
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
                   CodeEditor ce = new CodeEditor("/*\n   Вставьте сюда код класса, \n   выберите метод вызова алгоритма \n   и нажмите   кнопку \"Сохранить\"...   */\n\n\n\n");
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
            removeAlgButton.visibleProperty().set(false);
            showErrorDescriptionButton.setVisible(false);
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
                         ce = new CodeEditor("");                   
                         ce.setCode(c.getGeneratedCode(), 0, 0, 0, c.getGeneratedCode().split("\n").length);                   
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
            removeAlgButton.visibleProperty().set(true);
            showCountersCheckBox.visibleProperty().set(true);
            
            showErrorDescriptionButton.setVisible(alg.hasErrors());
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
        if (index == genList.getItems().size() - 1 || index == -1) {//добавление нового
            addingNewGen = true;
            Project.setCurrentGuiGen(-1);
            genNameTextField.setText("Генератор данных ("+ (CompSys.getProject().getCountOfDataGenerators() + 1) +")");
            String header = DataGenerator.getHeaderInDataGeneratorCode(CompSys.getProject().getAllAlgotithmsAsImportsMap());
            String footer = DataGenerator.getFooterInDataGeneratorCode();
                        //если алгоритмов нет, то не даем добавить генератор.
            
            int fb = 3, fe = header.split("\n").length + fb, sb = fe + 3, se = sb + footer.split("\n").length;
            
            ((CodeEditor)dataGeneratorCodePanel.lookup("#ceDataGen")).setCode("//Добавьте необходимые классы здесь...\n\n\n"+ header + "\n        \n        \n        \n"+footer, fb, fe, sb, se);
            removeGenButton.visibleProperty().set(false);
        } else {//отображение сохраненного
            addingNewGen = false;
            DataGenerator gen = CompSys.getProject().getDataGenerator(index);
            Project.setCurrentGuiGen(index);

            genNameTextField.setText(gen.getName());

            String header = DataGenerator.getHeaderInDataGeneratorCode(CompSys.getProject().getAllAlgotithmsAsImportsMap());
            String footer = DataGenerator.getFooterInDataGeneratorCode();

            String imports = gen.getImports() + " \n";
            String genImp = gen.getGenerateImplementation();

            int fb = 3 + imports.split("\n").length, fe = fb + header.split("\n").length, sb = fe + genImp.split("\n").length, se = sb + footer.split("\n").length + 2;

            ((CodeEditor)dataGeneratorCodePanel.lookup("#ceDataGen")).setCode("//Добавьте необходимые классы здесь...\n\n\n"+ imports + header + genImp + footer, fb, fe, sb, se);
            removeGenButton.visibleProperty().set(true);           
        }
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
    
    /**
     * Метод для изменения интерфейса при выборе генератора в выпадающем списке на панели тестов.
     * @param index Индекс выбранного генератора.
     */
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

    /**
     * Метод для изменения интерфейса при выборе алгоритма в выпадающем списке на панели тестов.
     * @param index Индекс выбранного алгоритма.
     */
    private static void selectAlgInListForTests(int index) {
        startTestsButton.setDisable(false);
    }
    
    /**
     * Метод для получения панели для списка алгоритмов в красивом виде.
     * @param index Индекс алгоритма в списке проекта.
     * @return Панель для списка алгоритмов.
     */
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
                Tooltip t = new Tooltip("В алгоритме возникли ошибки. Кликните на \"Подробнее...\" для просмотра информации.");
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
                AnchorPane.setRightAnchor(l2, 37.0);
            } else {
                AnchorPane.setRightAnchor(l, 2.0);
                AnchorPane.setRightAnchor(l2, 2.0);
            }
            AnchorPane.setLeftAnchor(l2, 20.0);
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
    
    /**
     * Метод для получения панели для списка генераторов данных в красивом виде.
     * @param index Индекс генератора в списке проекта.
     * @return Панель для списка генераторов.
     */
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
            AnchorPane.setRightAnchor(l2, 2.0);
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

    /**
     * Метод для переключения интерфейса в режим выполнения задачи.
     * @param withCancel флаг отображения кнопки отмены.
     */
    public static void startPerformingTask(boolean withCancel){
        taskPerformingPanel.visibleProperty().setValue(true);
        stopTask = false;
        cancelTaskButton.visibleProperty().setValue(withCancel);
    }
    
    /**
     * Метод для обновления интерфейса в режиме выполнения задачи.
     * @param title Имя задачи.
     * @param message Описание задачи.
     * @param progress Прогресс выполнения задачи.
     */
    public static void refreshPerformingTaskPanel(String title, String message, double progress){
        currentTaskTitle.setText(title);
        currentTaskDescription.setText(message);
        taskProgressBar.setProgress(progress);
    }
    
    /**
     * Метод для переключения интерфейса из режима выполнения задачи.
     */
    public static void stopPerformingTask(){
        taskPerformingPanel.visibleProperty().setValue(false);
    }
    
    /**
     * Метод для установки интерфейса в первородное состояние.
     */
    public static void closeProject() {
        mode = 0;
        algButton.setSelected(true);
        switchShowMode(0);
        setAllEnabled(false);
        addingNewAlg = false;
        addingNewGen = false;
        testGraph.getData().clear();
        currentTaskTitle.setText("");
        currentTaskDescription.setText("");
        onGraph = new HashMap<>();
        algMethodsComboBox.setDisable(true);
        showErrorDescriptionButton.setVisible(false);
        apiInstructionLabel.setText("Реализуйте функцию generate(). Для сохранения набора данных воспользуйтесь функцией addData(long second_parameter, ...");
        startTestsButton.setDisable(true);
        algListForTests.setDisable(true);
    }
    
    /**
     * Метод для задания интерфейса в режим без открытого проекта.
     */
    public static void setWithoutProject() {
        projectNotOpenedPanel.setVisible(true);
        setAllEnabled(false);
    }
    
    /**
     * Метод для задания интерфейса в режим без доступа к компилятору.
     */
    public static void setWithoutCompiler() {
        openProjectMenuItem.setDisable(true);
        newProjectMenuItem.setDisable(true);
    }
    
    /**
     * Метод для задания интерфейса в режим с доступом к компилятору.
     */
    public static void setWithCompiler() {
        openProjectMenuItem.setDisable(false);
        newProjectMenuItem.setDisable(false);
    }
}


































