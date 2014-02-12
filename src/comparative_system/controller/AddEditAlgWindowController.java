 /*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.controller;

import comparative_system.CompSys;
import comparative_system.Proccessor;
import comparative_system.model.DataGenerator;
import comparative_system.model.Project;
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
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialogs;
import javafx.scene.control.Dialogs.DialogResponse;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

/**
 * FXML Controller class
 *
 * @author TireX
 */
public class AddEditAlgWindowController implements Initializable {
    private static Stage primaryStage;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }

    @FXML private static TextField algNameTextField;
    @FXML private static TabPane codesOfAlgorithmsTabPane;
    @FXML private static ProgressIndicator progressIndicator;
    @FXML private static TextArea ta;
    @FXML private static ComboBox methodsComboBox;
    @FXML private static Button saveButton;
    
    /** Коды алгоритма (еще не сгенерированные). */ 
    ArrayList<String> codes = new ArrayList<>();
    /** Объявления методов для добвления в выпадающий список. */
    ArrayList<MethodDeclaration> methods = null;
    
    /**
     * Обработка нажатия кнопки. Добавление вкладки для дополнительного кода.
     */
    @FXML private void handleAddTabButtonClicked() {
        Tab tab = new Tab();
        tab.setText("Новый класс");
        AnchorPane aPane = new AnchorPane();
            TextArea sourceCodeTextArea = new TextArea();
            sourceCodeTextArea.setId("ta");
            sourceCodeTextArea.setPromptText("Исходный код алгоритма (java)...");
            AnchorPane.setTopAnchor(sourceCodeTextArea, 14.0);
            AnchorPane.setRightAnchor(sourceCodeTextArea, 14.0);
            AnchorPane.setBottomAnchor(sourceCodeTextArea, 14.0);
            AnchorPane.setLeftAnchor(sourceCodeTextArea, 14.0);
        aPane.getChildren().add(sourceCodeTextArea);
        tab.setContent(aPane);
        codesOfAlgorithmsTabPane.getTabs().add(tab);
        codesOfAlgorithmsTabPane.getSelectionModel().select(tab);
    }

    /**
     * Обработка нажатия кнопки. Утверждение кода и вывод списка методов для выбора главного.
     */
    @FXML private void handleConfirmButtonClicked() {
        progressIndicator.setVisible(true);
        saveButton.setDisable(true);
        
        Task resolveMethods = new Task<Void>() {
            @Override
            protected Void call() {
                codes.clear();
                for(Tab tab : codesOfAlgorithmsTabPane.getTabs()) {
                    codes.add(((TextArea)tab.getContent().lookup("#ta")).getText());
                }
//TODO: сделать проверку на public класс в каждой вкладке
                methods = Proccessor.getAllMethodsFromCodes(codes);
                return null;                
            }
        };
        
        //реагируем на новый класс
        resolveMethods.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                if (Worker.State.SUCCEEDED == newState || Worker.State.FAILED == newState) {
                    methodsComboBox.getItems().clear();
                    if (methods.size() > 0) {
                        methodsComboBox.setDisable(false);
                        for(MethodDeclaration method : methods) {
                            methodsComboBox.getItems().add(method.getName());
                        }
                    }
                    progressIndicator.setVisible(false);
                }
            }
        });
        
        new Thread(resolveMethods).start();
    }
    
    /** Индикатор ошибки. */
    private static int error = 0;
    /**
     * Обработка нажатия кнопки сохранения алгоритма.
     */
    @FXML private void handleSaveAlgButton() {
        progressIndicator.setVisible(true);
        error = 0;
        Task resolveAddAlg = new Task<Void>() {
            @Override
            protected Void call() {
                if (algNameTextField.getText().length() > 0) {//проверка имени
                    if (checkPublicClassesInTabs()) {//проверка public-классов в вкладках
                        int methodIndex = methodsComboBox.getSelectionModel().selectedIndexProperty().getValue();
                        MethodDeclaration method = methods.get(methodIndex);
                        List parameters = method.parameters();
                        if (parameters.size() > 0) {//количество параметров должно быть > 0 
                            if (Project.methodParamsAreCompatible(parameters)) {//проверка на совместимость параметров методов.
                                error = 0;//все ок, можно сохранять
                            } else {
                                error = 4;//запрос на перезапись или исправление переметров методов
                            }
                        } else {
                            error = 3;//ошибка - параметров в методе нет
                        }
                    } else {
                        error = 2;//ошибка с классами во вкладках
                    }                    
                } else {
                    error = 1;//имя не введено
                }
                return null;                
            }
        };
    
        //реагируем на обработку ошибок при сохранении алгоритма
        resolveAddAlg.stateProperty().addListener(new ChangeListener<Worker.State>() {
            @Override
            public void changed(ObservableValue<? extends Worker.State> ov, Worker.State oldState, Worker.State newState) {
                if (Worker.State.SUCCEEDED == newState || Worker.State.FAILED == newState) {
                    switch (error) {
                        case 0: {//ошибок нет, сохраняем
                            CompSys.addAlgorithm(algNameTextField.getText(), codes, String.valueOf(methodsComboBox.getSelectionModel().selectedItemProperty().getValue()));
                            primaryStage.close();
                            break;
                        }
                        case 1: {//имя
                            showWarningMessage("Название алгоритма...","Ошибка при сохранении алгоритма.","Вы не ввели название алгоритма.");
                            break;
                        }
                        case 2: {//public-классы не во всех вкладках
                            showWarningMessage("Классы алгоритма...","Ошибка при сохранении алгоритма.","Каждая вкладка должна содержать public класс.");
                            break;
                        }
                        case 3: {//нет параметров в методе
                            showWarningMessage("Параметры метода...","Ошибка при сохранении алгоритма.","Метод вызова алгоритма должен содержать один или несколько параметров для передачи данных при последующих вычислениях трудоемкости.");
                            break;
                        }
                        case 4: {//параметры методов не совместимы
                            if (Dialogs.showConfirmDialog(primaryStage, "Параметры метода вызова алгоритма не совпадают с уже сохраненными!", "Перезаписать параметры?", "Параметры метода вызова...", Dialogs.DialogOptions.YES_NO) == DialogResponse.YES) {
                                //перезапись
                                List parameters = (methods.get(methodsComboBox.getSelectionModel().selectedIndexProperty().getValue())).parameters();
                                DataGenerator.saveNewMainMethodParams(parameters);
                                CompSys.addAlgorithm(algNameTextField.getText(), codes, String.valueOf(methodsComboBox.getSelectionModel().selectedItemProperty().getValue()));
                                primaryStage.close();
                            }
                            break;
                        }
                    }
                    progressIndicator.setVisible(false);
                }
            }
        });
        
        new Thread(resolveAddAlg).start();
    }

    /**
     * Обработка изменения выбранного метода в выпадающем списке.
     */
    @FXML private void handleChangeMethod() {
        if (!methodsComboBox.getSelectionModel().isEmpty()) {
            saveButton.setDisable(false);
        }
    }    
    
    
    private static void showInfoMessage(String title, String header, String info) {
        Dialogs.showInformationDialog(primaryStage, info, header, title);
    }
    
    private static void showWarningMessage(String title, String header, String info) {
        Dialogs.showWarningDialog(primaryStage, info, header, title);
    }
    
    public static void setStage(Stage stage) {
        primaryStage = stage;
    }

    private static boolean checkPublicClassesInTabs() {//TODO: реализовать метод
        return true;
    }
}
