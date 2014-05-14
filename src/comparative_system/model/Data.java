package comparative_system.model;

import org.apache.commons.lang3.SerializationUtils;

/**
 * Класс для представления одного набора исходных данных.
 * @author Gromov Evg.
 */
public class Data {
    /** Индекс набора данных в списке проекта. */
    private long index;
    /** Индекс генератора данного набора исходных данных в БД. */
    private long gen_id;
    /** Исходные данные, соответствующие параметрам методов вызова алгоритмов проекта. */
    private Object[] values;
    /** Второй параметр для отображения на графике результатов. */
    private long second_param;

    /**
     * Конструктор класса.
     * @param index Индекс в списке проекта.
     * @param second_param 
     * @param gen_id id генератора в БД проекта.
     * @param values Значения данных.
     */
    public Data(long index, long gen_id, long second_param, Object[] values) {
        this.index = index;
        this.gen_id = gen_id;
        this.second_param = second_param;
        this.values = values;
    }

    /**
     * Метод возвращает индекс генератора исходных данных в БД, к которому относится данный набор.
     * @return Индекс генератора в БД.
     */
    public long getDataGeneratorDBId() {
        return this.gen_id;
    }
    
    /**
     * Метод возвращает копию данных этого набора.
     * @return Копия массива данных.
     */
    public Object[] getValues() {
        return SerializationUtils.clone(this.values);
    }
    
    /**
     * Метод возвращает индекс этого набора в списке всех наборов генератора.
     * @return Индекс набора.
     */
    public long getIndex(){
        return this.index;
    }

    /**
     * Метод возвращает второй параметр для вывода на график результатов.
     * @return Второй параметр.
     */
    public long getSecondParam() {
        return this.second_param;
    }
}
