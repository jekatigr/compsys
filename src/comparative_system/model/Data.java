package comparative_system.model;

import java.io.Serializable;

/**
 * Класс для представления одного набора исходных данных.
 * @author TireX
 * @param <T> Произвольный тип параметра метода вызова алгоритма.
 */
public class Data<T> implements Serializable {
    /** Индекс набора данных в БД проекта. */
    private int id;
    /** Индекс генератора данного набора исходных данных в БД. */
    private int gen_id;
    /** Исходные данные, соответствующие параметрам методов вызова алгоритмов проекта. */
    private T[] list;

    /**
     * Конструктор класса.
     * @param id id в БД проекта.
     * @param gen_id id генератора в БД проекта.
     * @param params Значения данных.
     */
    public Data(int id, int gen_id, T[] params) {
        this.id = id;
        this.gen_id = gen_id;
        list = params;
    }

    /**
     * Метод возвращает индекс генератора исходных данных в БД, к которому относится данный набор.
     * @return Индекс генератора в БД.
     */
    public int getDataGeneratorDBId() {
        return this.gen_id;
    }
}
