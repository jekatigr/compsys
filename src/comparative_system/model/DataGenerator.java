/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

/**
 * Класс для представления генераторов исходных данных вместе с самими данными.
 * @author TireX
 */
public class DataGenerator {
    /** Список параметров методов вызова алгоритмов. Для всех генераторов список один и тот же. */
    private static ArrayList<Param> params = new ArrayList<>();
        
    /** Индекс генератора в БД. */
    private int id;
    /** Имя генератора. */
    private String name;
    /** Код генератора. */
    private String code;
    /** Список сгенерированных исходных данных. */
    private ArrayList<Data> data;

    /**
     * Конструктор класса.
     * @param id Индекс генератора в БД.
     * @param name Имя генератора.
     * @param code Код генератора.
     */
    DataGenerator(int id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    /**
     * Возвращает индекс генератора в БД проекта.
     * @return Индекс генератора.
     */
    public int getId() {
        return this.id;
    }

    /**
     * Добавление одного набора исходных данных к генератору.
     * @param d Набор исходных данных.
     */
    public void addData(Data d) {
        this.data.add(d);
    }
    
    /**
     * Метод для очистки списка параметров методов вызова. Параметры также удаляются из БД.
     * Если существуют сгенерированные исходные данные для тестов, то они тоже удаляются.
     */
    private static void clearParamsList() {//TODO: сделать удаление данных из бд
        params.clear();//TODO:удаление из бд и удаление данных
    }

    /**
     * Добавление очередного параметра метода вызова алгоритма.
     * @param type Тип параметра.
     * @param name Имя параметра.
     */
    public static void addMainMethodsParam(String type, String name) {
        params.add(new Param(type, name));
    }

    /**
     * Возвращает количество параметров в методах вызова алгоритмов.
     * @return Количество параметров.
     */
    public static int getCountOfMethodsParams() {
        return params.size();
    }

    /**
     * Метод для сравнения параметров методов вызова алгоритмов.
     * @param parameters Список параметров типа {@code SingleVariableDeclaration} для сравнения.
     * @return {@code true} в случае, когда параметры совпадают по типу и количеству, {@code false} иначе.
     */
    public static boolean compareListOfParams(List parameters) {
        if (DataGenerator.getCountOfMethodsParams() == parameters.size()) {
            ArrayList<Param> arr = new ArrayList<>(params);
            for(Object param : parameters) {
                SingleVariableDeclaration p = (SingleVariableDeclaration)param;
                //arr.add(new Param(p.getType().toString(), p.getName().toString()));
                for(int i = params.size() - 1; i >= 0; i--) {
                    if (arr.get(i).getType().equals(((SingleVariableDeclaration)param).getType().toString())) {
                        arr.remove(i);
                        break;
                    }
                }
                return false;
            }  
            return true;
        }
        return false;
    }
    
    /**
     * Метод для сохранения или перезаписи списка параметров методов вызова алгоритмов.
     * Также параметры записываются в БД проекта и создается таблица для исходных данных.
     * @param parameters Список параметров типа {@code SingleVariableDeclaration} для сохранения.
     */
    public static void saveNewMainMethodParams(List parameters) {
        clearParamsList();
        for(Object param : parameters) {//TODO: сохранить параметры в БД и создать таблицу исх. данных.
            SingleVariableDeclaration p = (SingleVariableDeclaration)param;
            DataGenerator.addMainMethodsParam(p.getType().toString(), p.getName().toString());
        }
    }
}
