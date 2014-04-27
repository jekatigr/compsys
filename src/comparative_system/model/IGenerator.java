package comparative_system.model;

import java.util.ArrayList;

/**
 * Интерфейс для работы с сгенерированными генераторами исходных данных.
 * @author Gromov Evg.
 */
public interface IGenerator {
    /**
     * Метод для создания списка исходных данных, который генерирует генератор.
     * @param gen_id id генератора в БД проекта.
     * @return Список исходных данных.
     */
    public ArrayList<Data> getData(int gen_id);
}
