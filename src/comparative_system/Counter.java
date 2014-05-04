package comparative_system;

import java.util.HashMap;

/**
 * Класс для подсчета операций в алгоритмах.
 * @author Gromov evg.
 */
public class Counter {
    /** Список значений "название текущего потока" => "количество операций". */
    private static volatile HashMap<String, Integer> count_of_operations = new HashMap<>();
    
    /**
     * Метод для добавления количества подсчитанных операций в список к соответствующему потоку.
     * @param count 
     */
    public static synchronized void add(int count) {
        String th = Thread.currentThread().getName();
        if (count_of_operations.containsKey(th)) {
            count_of_operations.put(th, count_of_operations.get(th) + count);
        } else {
            count_of_operations.put(th, count);
        }
    }
    
    /**
     * Метод для возврата результата по подсчету операций. После выполнения удалится
     * соответствующий потоку элемент в общем списке подсчета. 
     * @param thread_name Имя потока.
     * @return Количество операций.
     */
    public static synchronized int getCountResult(String thread_name) {
        int res = count_of_operations.get(thread_name);
        count_of_operations.remove(thread_name);
        return res;
    }
}
