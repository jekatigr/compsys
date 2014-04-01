/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

import comparative_system.gui.CodeEditor;
import java.util.ArrayList;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;

/**
 * Класс, реализующий алгоритм.
 * @author TireX
 */
public class Algorithm {
    /** Индекс алгоритма в БД. */
    private long id;
    /** Имя алгоритма. */
    private String name;
    /** Метод вызова алгоритма. */
    private String mainMethod;
    /** Исходные и сгенерированные коды алгоритма. */
    private ArrayList<Code> codes;
    /** Флаг для показа счетчиков в коде для GUI. */
    private boolean showCounters = false;
    /** Лист с названиями вкладок для кодов алгоритма. */
    private ArrayList<String> classesTabsNames;
    
    private Tab addClassGuiTab;
    /**
     * Конструктор класса.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param codes Исходные и сгенерированные коды алгоритма.
     * @param classesTabsNames Имена вкладок классов алгоритма для GUI.
     */
    public Algorithm(String name, String mainMethod, ArrayList<Code> codes, ArrayList<String> classesTabsNames) {
        this(-1, name, mainMethod, codes, classesTabsNames);
    }
    
    /**
     * Конструктор класса.
     * @param id Индекс алгоритма в БД.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param codes Исходные и сгенерированные коды алгоритма.
     * @param classesTabsNames Имена вкладок классов алгоритма для GUI.
     */
    public Algorithm(long id, String name, String mainMethod, ArrayList<Code> codes, ArrayList<String> classesTabsNames) {
        this.id = id;
        this.name = name;
        this.mainMethod = mainMethod;
        this.codes = codes;
        this.classesTabsNames = classesTabsNames;
    }

    /**
     * Метод для задания id алгоритма в БД.
     * @param id Индекс алгоритма в БД.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Метод возвращает имя алгоритма.
     * @return Имя алгоритма.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Метод возвращает метод вызова алгоритма.
     * @return Метод вызова алгоритма.
     */
    public String getMainMethod() {
        return this.mainMethod;
    }

    /**
     * Метод возвращает id алгоритма в БД.
     * @return id алгоритма в БД.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Метод возвращает коды алгоритма.
     * @return Коды алгоритма.
     */
    public ArrayList<Code> getCodes() {
        return this.codes;
    }
    
    public boolean getShowCounters() {
        return this.showCounters;
    }
    
    public void setShowCounters(boolean show) {
        this.showCounters = show;
    }

    public String getClassTabName(int index) {
        return classesTabsNames.get(index);
    }
    
    public Tab getAddClassTab() {
        return this.addClassGuiTab;
    }

    public void loadAddClassTab() {
        if (addClassGuiTab == null) {
            addClassGuiTab = new Tab();
            addClassGuiTab.setText("+");
                CodeEditor ce = new CodeEditor("/*\n   Вставьте сюда код класса\n   или перетащите файл java\n   в окно программы и нажмите\n   кнопку \"Сохранить\"...     */");
                ce.setId("addCe");
                AnchorPane.setTopAnchor(ce, -5.0);
                AnchorPane.setRightAnchor(ce, 0.0);
                AnchorPane.setBottomAnchor(ce, -5.0);
                AnchorPane.setLeftAnchor(ce, -5.0);
            addClassGuiTab.setContent(ce);
            addClassGuiTab.setClosable(false);
        }
    }
}












