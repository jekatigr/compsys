/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.model;

import java.util.ArrayList;

/**
 * Класс для представления одного набора исходных данных.
 * @author TireX
 * @param <T> Произвольный тип параметра метода вызова алгоритма.
 */
public class Data<T> {
    /** Индекс набора данных в БД проекта. */
    private int id;
    /** Индекс генератора данного набора исходных данных в БД. */
    private int gen_id;
    /** Исходные данные, соответствующие параметрам методов вызова алгоритмов проекта. */
    private T[] list;

    public Data(int id, int gen_id, T[] params) {
        this.id = id;
        this.gen_id = gen_id;
        list = params.clone();
    }

    /**
     * Метод возвращает индекс генератора исходных данных, к которому относится данный набор.
     * @return Индекс генератора.
     */
    public int getDataGeneratorId() {
        return this.gen_id;
    }
}
