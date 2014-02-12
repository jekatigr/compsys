/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

import java.util.ArrayList;

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
    /** Имя счетчика операций в сгенерированных кодах. */
    private String counterName;
    /** Исходные и сгенерированные коды алгоритма. */
    private ArrayList<Code> codes;
    
    /**
     * Конструктор класса.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param counterName Имя счетчика операций в сгенерированных кодах.
     * @param codes Исходные и сгенерированные коды алгоритма.
     */
    public Algorithm(String name, String mainMethod, String counterName, ArrayList<Code> codes) {
        this(-1, name, mainMethod, counterName, codes);
    }
    
    /**
     * Конструктор класса.
     * @param id Индекс алгоритма в БД.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param counterName Имя счетчика операций в сгенерированных кодах.
     * @param codes Исходные и сгенерированные коды алгоритма.
     */
    public Algorithm(long id, String name, String mainMethod, String counterName, ArrayList<Code> codes) {
        this.id = id;
        this.name = name;
        this.mainMethod = mainMethod;
        this.counterName = counterName;
        this.codes = codes;
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
    String getName() {
        return this.name;
    }

    /**
     * Метод возвращает метод вызова алгоритма.
     * @return Метод вызова алгоритма.
     */
    String getMainMethod() {
        return this.mainMethod;
    }

    /**
     * Метод возвращает имя счетчика операций.
     * @return Имя счетчика операций.
     */
    String getCounterName() {
        return this.counterName;
    }

    /**
     * Метод возвращает id алгоритма в БД.
     * @return id алгоритма в БД.
     */
    int getId() {
        return this.id;
    }
}
