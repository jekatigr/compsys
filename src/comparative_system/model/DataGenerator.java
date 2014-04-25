/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import comparative_system.CompSys;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
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
    /** Флаг для вкладки код/данные для GUI. */
    private boolean isCodeOpened = true;

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
     * Возвращает имя генератора.
     * @return Имя генератора.
     */
    public String getName() {
        return this.name;
    }
    
    /**
     * Метод возвращает исходный код генератора данных.
     * @return Исходный код.
     */
    public String getCode() {
        return this.code;
    }
    
    /**
     * Добавление одного набора исходных данных к генератору.
     * @param d Набор исходных данных.
     */
    public void addData(Data d) {
        this.data.add(d);
    }
    
    /**
     * Метод возвращает флаг для интерфейса. 
     * @return {@code true}, если вкладка кода показана, иначе {@code false}.
     */
    public boolean getIsCodeOpened() {
        return this.isCodeOpened;
    }
    
    
    /**
     * Метод для очистки списка параметров методов вызова. Параметры также удаляются из БД.
     * Если существуют сгенерированные исходные данные для тестов, то они тоже удаляются.
     */
    private static void clearParamsList() {
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

    public static String getMethodsParamsAsString(boolean withTypes) {
        String res = "";
        for (Param par : params) {
            if (!"".equals(res)) {res += ", ";}//перед каждым параметром кроме первого вставляем запятую с пробелом.
            if (withTypes) {
                res += par.getType() + " " + par.getName();
            } else {
                res += par.getName();
            }
        }
        return res;
    }
    
    /**
     * Метод для сравнения параметров методов вызова алгоритмов.
     * @param parameters Список параметров типа {@code SingleVariableDeclaration} для сравнения.
     * @return {@code true} в случае, когда параметры совпадают по расположению, типу и количеству, {@code false} иначе.
     */
    public static boolean compareListOfParams(List parameters) {
        if (DataGenerator.getCountOfMethodsParams() == parameters.size()) {
            for(int i = 0; i < parameters.size(); i++) {
                if (!((SingleVariableDeclaration)parameters.get(i)).getType().toString().equals(params.get(i).getType())) {
                    return false;
                }
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
        for(Object param : parameters) {//TODO: создать таблицу исх. данных.
            SingleVariableDeclaration p = (SingleVariableDeclaration)param;
            DataGenerator.addMainMethodsParam(p.getType().toString(), p.getName().toString());
        }
        saveMethodParamsInDB();
    }
    
    private static void saveMethodParamsInDB() {
        try {
            //открываем базу данных проекта
            SQLiteConnection db = new SQLiteConnection(CompSys.getProject().file);
            db.open();
            //--открываем базу данных проекта
            //сохраняем
            db.exec("DELETE FROM main_params");
            for (Param par : params) {
                db.exec("INSERT INTO main_params (type, name) VALUES ("+ par.getType() +", '"+ par.getName() +"')");
            }
            //--сохраняем         
        } catch (SQLiteException ex) {
            Logger.getLogger(DataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
