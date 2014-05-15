package comparative_system;

import comparative_system.controller.FXMLguiController;
import comparative_system.gui.CodeEditor;
import comparative_system.model.*;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;

/**
 * Главный класс программы.
 * @author Gromov Evg.
 */
public class CompSys extends Application {
    /** Текущий проект. */
    private static Project project;
    /** Форма окна. */
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        FXMLguiController.setStage(stage);        
        //готовим gui
        Parent root = FXMLLoader.load(getClass().getResource("gui/mainWindow.fxml"));
        FXMLguiController.algPanel = FXMLLoader.load(getClass().getResource("gui/algPanel.fxml"));
        FXMLguiController.dataPanel = FXMLLoader.load(getClass().getResource("gui/dataPanel.fxml"));
        FXMLguiController.testsPanel = FXMLLoader.load(getClass().getResource("gui/testsPanel.fxml"));

        stage.setTitle("Вычисление трудоемкости алгоритмов");
        Scene scene = new Scene(root);
        stage.setScene(scene);

        stage.setOnCloseRequest(new EventHandler() {
            @Override
            public void handle(Event t) {
                Preferences.savePreferences();
                CompSys.closeProject();
                Platform.exit();
                System.exit(0);
            }
        });
        //загружаем файлы библиотек для подсветки кода
        CodeEditor.loadCodeMirrorLibs();
        //--загружаем файлы библиотек для подсветки кода
        //инициализируем интерфейс
        FXMLguiController.initialize();
        //--готовим gui
        //загружаем настройки программы
        Preferences.loadPreferences();
        //--загружаем настройки программы
        if (Preferences.getJdkPath().equals("")) {
            CompSys.setNewJDKPath();
        }
        //проверяем папку с jdk
        if (Proccessor.checkCompilerAvailable()){
            //открываем последний открытый проект, если он есть
            File proj_file = Preferences.getLastOpenedProject();
            if (proj_file != null) {
                openProject(proj_file);
            }
            //--открываем последний проект, если он есть
        } else {
            FXMLguiController.setWithoutProject();
            FXMLguiController.setWithoutCompiler();
            Dialogs.showErrorDialog(primaryStage, "Укажить пать к папке с jdk/bin (Меню -> Настройки -> Папка с jdk...).", "Невозможно вызвать javac компилятор!", "Ошибка...", Dialogs.DialogOptions.OK);
        }
        //--проверяем папку с jdk
        primaryStage = stage;
        stage.show();
    }

    /**
     * The main() method is ignored in correctly deployed JavaFX application. main() serves only as fallback in case the
     * application can not be launched through deployment artifacts, e.g., in IDEs with limited FX support. NetBeans ignores
     * main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Открытие проекта из файла и загрузка в пользовательский интерфейс во втором потоке.
     * @param file Файл проекта.
     */
    public static void openProject(final File file) {        
        FXMLguiController.startPerformingTask(false);
               
        final Task t = Project.openProject(file);
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
                        th.join(250);
                    }
                    project = (Project) t.get();
                    if (project != null) { //все ок, готовим gui
                        Preferences.addLastOpenedProject(file);
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                FXMLguiController.openProject(project);
                                FXMLguiController.stopPerformingTask();
                            }
                        });
                    } else {
                        Platform.runLater(new Runnable() {
                            @Override
                            public void run() {
                                FXMLguiController.setWithoutProject();
                                Dialogs.showErrorDialog(primaryStage, "Ошибка при открытии проекта.", "Попробуйте открыть файл еще раз.", "Ошибка при открытии проекта...");
                                FXMLguiController.stopPerformingTask();
                            }
                        });
                    }             
                } catch (InterruptedException ex) {
                    Logger.getLogger(CompSys.class.getName()).log(Level.SEVERE, null, ex);
                } catch (ExecutionException ex) {
                    Logger.getLogger(CompSys.class.getName()).log(Level.SEVERE, null, ex);
                }
                return null;
            }
        };
        Thread upd = new Thread(daemon);
        upd.start();
    }

    /**
     * Добавление нового алгоритма к проекту с отображением в окне и сохранением в БД.
     * @param alg Алгоритм.
     */
    public static void addNewAlgorithm(Algorithm alg) {
        project.addNewAlgorithm(alg);
        FXMLguiController.reloadAlgList();
        FXMLguiController.reloadGenAndAlgListsForTests();
        FXMLguiController.loadDataGeneratorView(Project.getCurrentGuiGen());
    }
    
    /**
     * Метод для сохранения измененного алгоритма и отображения его в GUI.
     * @param index Индекс алгоритма в списке проекта.
     * @param name Имя алгоритма.
     * @param codes Исходные коды алгоритма.
     * @param method Метод вызова алгоритма. 
     */
    public static void saveAlgorithm(int index, String name, String method, ArrayList<Code> codes) {
        project.saveAlgorithm(index, name, method, codes);
        FXMLguiController.reloadAlgList();
        FXMLguiController.reloadGenAndAlgListsForTests();
        FXMLguiController.loadAlgorithmView(Project.getCurrentGuiAlg());
        FXMLguiController.loadDataGeneratorView(Project.getCurrentGuiGen());
    }
    
    /**
     * Добавление нового генератора исходных данных к проекту с отображением в окне и сохранением в БД.
     * @param name Имя генератора.
     * @param imports Импорты, добваленные пользователем.
     * @param generateImplementation Код генератора.
     */
    public static void addNewDataGenerator(String name, String imports, String generateImplementation) {
        project.addNewDataGenerator(name, imports, generateImplementation);
    }
    
    /**
     * Сохранение измененного генератора исходных данных с отображением в GUI.
     * @param index Индекс генератора в списке проекта.
     * @param name Имя генератора.
     * @param imports Импорты, добваленные пользователем.
     * @param generateImplementation Код генератора.
     */
    public static void saveDataGenerator(int index, String name, String imports, String generateImplementation) {
        project.saveDataGenerator(index, name, imports, generateImplementation);
    }
    
    /**
     * Метод для остановки запущенных проектов и сохранения данных программы перед закрытием.
     */
    public static void saveAllBeforeClose() {
        //this thing is useless
        //if (project != null && Dialogs.showConfirmDialog(primaryStage, "", "Сохранить изменения в проекте перед выходом?", "Сохранение перед выходом...", Dialogs.DialogOptions.YES_NO) == DialogResponse.YES) {
            //TODO: project.save();
        //}
    }

    /**
     * Метод для закрытия приложения в случае фатальной ошибки с показом сообщения.
     * @param info Подробное описание ошибки.
     * @param head Заголовок описания ошибки.
     * @param title Надпись в заголовке всплывающего окна.
     */
    private static void exitWithError(String info, String head, String title) {
        Dialogs.showErrorDialog(primaryStage, info, head, title);
        Platform.exit();
    }
    
    /**
     * Метод возвращает текущий проект.
     * @return Текущий проект.
     */
    public static Project getProject() {
        return project;
    }
    
    /**
     * Метод для удаления алгоритма из проекта с обновлением GUI.
     * @param index Индекс алгоритма в списке.
     */
    public static void removeAlgorithm(int index) {
        project.removeAlgorithm(index);
    }
    
    /**
     * Метод для удаления генератора данных из проекта с обновлением GUI.
     * @param index Индекс генератора в списке проекта.
     */
    public static void removeDataGenerator(int index) {
        project.removeDataGenerator(index);
    }

    /**
     * Метод для очистки программы от текущего проекта.
     */
    public static void closeProject() {
        project = null;
        DataGenerator.removeParams();
        FXMLguiController.setWithoutProject();
        Project.setCurrentGuiAlg(-1);
        Project.setCurrentGuiGen(-1);
        FXMLguiController.closeProject();
        //очищаем временные папки программы
        Proccessor.removeDirectory(new File("source_codes"));
        Proccessor.removeDirectory(new File("generated_codes"));
        Proccessor.removeDirectory(new File("data_generator"));
    }
    
    /**
     * Метод для показа диалога ввода пути к jdk.
     */
    public static void setNewJDKPath() {
        Preferences.setJDKPath(Dialogs.showInputDialog(primaryStage, "Путь:", "Пожалуйста, введите путь к папке с jdk/bin:", "Путь к папке с jdk..."));
        if (Proccessor.checkCompilerAvailable()){
            FXMLguiController.setWithCompiler();
            //открываем последний открытый проект, если он есть
            File proj_file = Preferences.getLastOpenedProject();
            if (proj_file != null) {
                openProject(proj_file);
            }
            //--открываем последний проект, если он есть
        } else {
            CompSys.closeProject();
            FXMLguiController.setWithoutCompiler();
            Dialogs.showErrorDialog(primaryStage, "Укажить пать к папке с jdk/bin (Меню -> Настройки -> Папка с jdk...).", "Невозможно вызвать javac компилятор!", "Ошибка...", Dialogs.DialogOptions.OK);
        }
    }
}
