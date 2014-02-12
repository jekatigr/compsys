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
    private int id;
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
     * @param id Индекс алгоритма в БД.
     * @param name Имя алгоритма.
     * @param mainMethod Метод вызова алгоритма.
     * @param counterName Имя счетчика операций в сгенерированных кодах.
     * @param codes Исходные и сгенерированные коды алгоритма.
     */
    public Algorithm(int id, String name, String mainMethod, String counterName, ArrayList<Code> codes) {
        this.id = id;
        this.name = name;
        this.mainMethod = mainMethod;
        this.counterName = counterName;
        this.codes = codes;
    }

}
