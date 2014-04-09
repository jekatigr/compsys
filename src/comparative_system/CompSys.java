/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system;

import comparative_system.controller.FXMLguiController;
import comparative_system.gui.CodeEditor;
import comparative_system.model.*;
import java.io.File;
import java.util.ArrayList;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.Worker.State;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Dialogs;
import javafx.stage.Stage;
/**
 *
 * @author TireX
 */
public class CompSys extends Application {
    private static Project project;
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
                //TODO: остановить анализ saveAllBeforeClose();
                Platform.exit();
            }
        });
        //инициализируем интерфейс
        FXMLguiController.initialize();
        //--готовим gui
        //загружаем настройки программы
        Preferences.loadPreferences();
        //--загружаем настройки программы
        //открываем последний открытый проект, если он есть
        File proj_file = Preferences.getLastOpenedProject();
        if (proj_file != null) {
            openProject(proj_file);
        }
        //--открываем последний проект, если он есть
        primaryStage = stage;
        stage.show();
        
        //загружаем файлы библиотек для подсветки кода
        final Task loadCodeMirrorLibsTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                if (CodeEditor.loadCodeMirrorLibs()) {
                    this.updateMessage("true"); 
                } else {
                    this.updateMessage("false"); 
                }
                return null;
            }
        };
        
        loadCodeMirrorLibsTask.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                if (Worker.State.FAILED == newState) {
                    exitWithError("", "Ошибка при загрузке библиотек!", "Загрузка библиотек...");
                }
                if (Worker.State.SUCCEEDED == newState && !loadCodeMirrorLibsTask.getMessage().equals("true")) {
                    exitWithError("", "Ошибка при загрузке библиотек!", "Загрузка библиотек...");
                }
            }
        });
        
        new Thread(loadCodeMirrorLibsTask).start();
        //--загружаем файлы библиотек для подсветки кода
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
     * Открытие проекта из файла и загрузка в пользовательский интерфейс.
     * @param file Файл проекта.
     */
    public static void openProject(File file) {
        project = Project.openProject(file);
        if (project != null) { //все ок, готовим gui
            Preferences.addLastOpenedProject(file);
            FXMLguiController.openProject(project);
        } else {
            //все плохо
        }
    }

    /**
     * Добавление нового алгоритма к проекту с отображением в окне и сохранением в БД.
     * @param name Имя алгоритма.
     * @param codes Исходные коды алгоритма.
     * @param method Метод вызова алгоритма. 
     */
    public static void addAlgorithm(String name, String method, ArrayList<String> codes) {
        project.addAlgorithm(name, method, codes);
        FXMLguiController.addAlgInList(project.getCountOfAlgorithms() - 1, project.getAlgorithm(project.getCountOfAlgorithms() - 1));
    }
    
    public static void saveAlgorithm(int index, String name, String method, ArrayList<String> codes) {
        project.saveAlgorithm(index, name, method, codes);
        FXMLguiController.loadAlgorithmView(Project.getCurrentGuiAlg());
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
    private void exitWithError(String info, String head, String title) {
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
     * Метод возвращает количество алгоритмов в текущем проекте.
     * @return Количество алгоритмов.
     */
    public static int getCountOfAlgorithms() {
        return project.getCountOfAlgorithms();
    }
}
