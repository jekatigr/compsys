/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

import comparative_system.Proccessor;
import comparative_system.gui.CodeEditor;
import java.util.ArrayList;
import javafx.scene.control.Tab;
import javafx.scene.layout.AnchorPane;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.Name;

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
    /** Лист полных названий классов. Если в коде не указан пакет, то по умолчанию он будет "algorithm" + (id в БД проекта). */
    private ArrayList<Name> fullNamesOfClasses;
    /** Флаг для показа счетчиков в коде для GUI. */
    private boolean showCounters = false;
    /** Лист с названиями вкладок для кодов алгоритма. */
    private ArrayList<String> classesTabsNames;
    /** Лист с определениями методов классов алгоритма. */
    private ArrayList<MethodDeclaration> methods;
    
    /**
     * Конструктор класса.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param codes Исходные и сгенерированные коды алгоритма.
     * @param classesTabsNames Имена вкладок классов алгоритма для GUI.
     * @param methods Список деклараций методов в исходных кодах алгоритма.
     */
    public Algorithm(String name, String mainMethod, ArrayList<Code> codes, ArrayList<String> classesTabsNames, ArrayList<MethodDeclaration> methods) {
        this(-1, name, mainMethod, codes, classesTabsNames, methods);
    }
    
    /**
     * Конструктор класса.
     * @param id Индекс алгоритма в БД.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param codes Исходные и сгенерированные коды алгоритма.
     * @param classesTabsNames Имена вкладок классов алгоритма для GUI.
     * @param methods Список деклараций методов в исходных кодах алгоритма.
     */
    public Algorithm(long id, String name, String mainMethod, ArrayList<Code> codes, ArrayList<String> classesTabsNames, ArrayList<MethodDeclaration> methods) {
        this.id = id;
        this.name = name;
        this.mainMethod = mainMethod;
        this.codes = codes;
        this.classesTabsNames = classesTabsNames;
        this.methods = methods;
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
     * Метод сохраняет имя алгоритма.
     * @param name Новое имя.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Метод возвращает метод вызова алгоритма.
     * @return Метод вызова алгоритма.
     */
    public String getMainMethod() {
        return this.mainMethod;
    }
    
    /**
     * Метод задает имя метода вызова алгоритма.
     * @param mainMethod Имя метода вызова алгоритма.
     */
    public void setMainMethod(String mainMethod) {
        this.mainMethod = mainMethod;
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
    
    /**
     * Метод сохраняет коды алгоритма.
     * @param codes Коды для сохранения.
     */
    public void setCodes(ArrayList<Code> codes) {
        this.codes = codes;
    }
    
    /**
     * Метод возвращает флаг для интерфейса.
     * @return {@code true}, если счетчики показаны, иначе {@code false}.
     */
    public boolean getShowCounters() {
        return this.showCounters;
    }
    
    /**
     * Метод задает флаг для интерфейса.
     * @param show {@code true} для показа счетчиков, иначе {@code false}.
     */
    public void setShowCounters(boolean show) {
        this.showCounters = show;
    }

    /**
     * Метод возвращает имя класса-вкладки для кодов алгоритма.
     * @param index Индекс кода в списке.
     * @return Имя класса.
     */
    public String getClassTabName(int index) {
        return classesTabsNames.get(index);
    }
    
    /**
     * Метод задает список имен вкладок для исходных кодов алгоритмов.
     * @param classNames Список имен.
     */
    public void setClassNamesListList(ArrayList<String> classNames) {
        this.classesTabsNames = classNames;
    }
    
    /**
     * Метод задает список деклараций всех методов в исходных кодах алгоритма.
     * @param methods Список методов.
     */
    public void setMethodsList(ArrayList<MethodDeclaration> methods) {
        this.methods = methods;
    }

    /**
     * Метод возвращает список деклараций методов из исходных кодов алгоритма.
     * @return Список деклараций методов.
     */
    public ArrayList<MethodDeclaration> getMethodsList() {
        return this.methods;
    }

    void setFullNamesOfClassesList(ArrayList<Name> fullNamesOfClasses) {
        this.fullNamesOfClasses = fullNamesOfClasses;
    }
    
    ArrayList<Name> getFullNamesOfClassesList() {
        return this.fullNamesOfClasses;
    }
    
    private static ArrayList<String> getSourceCodes(ArrayList<Code> codes) {
        ArrayList<String> resCodes = new ArrayList<>();
        for(Code code : codes) {
            resCodes.add(code.getSourceCode());
        }
        return resCodes;
    }
}










