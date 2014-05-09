package comparative_system.model;

/**
 * Класс для представления одного набора исходных данных.
 * @author Gromov Evg.
 */
public class Data {
    /* Индекс набора данных в списке проекта. */
    private long index;
    /** Индекс генератора данного набора исходных данных в БД. */
    private long gen_id;
    /** Исходные данные, соответствующие параметрам методов вызова алгоритмов проекта. */
    private Object[] values;

    /**
     * Конструктор класса.
     * @param index Индекс в списке проекта.
     * @param gen_id id генератора в БД проекта.
     * @param values Значения данных.
     */
    public Data(long index, long gen_id, Object[] values) {
        this.index = index;
        this.gen_id = gen_id;
        this.values = values;
    }

    /**
     * Метод возвращает индекс генератора исходных данных в БД, к которому относится данный набор.
     * @return Индекс генератора в БД.
     */
    public long getDataGeneratorDBId() {
        return this.gen_id;
    }
    
    public Object[] getValues() {
        return this.values;
    }
    
    public long getIndex(){
        return this.index;
    }
}
