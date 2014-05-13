package comparative_system.model;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Класс, реализующий результат вычислений трудоемкости для алгоритма.
 * @author Gromov Evg.
 */
public class Result {
    /** Количество операций. */
    long count;
    /** */
    long second_param;
    
    /**
     * Конструктор класса.
     * @param count Количество операций.
     * @param second_param 
     */
    public Result(long count, long second_param) {
        this.count = count;
        this.second_param = second_param;
    }
    
    /**
     * Метод возвращает второй параметр для вывода на график результатов.
     * @return Второй параметр.
     */
    public long getSecondParam() {
        return this.second_param;
    }
    
    /**
     * Метод возвращает количество операций для вывода на график результатов.
     * @return Количество операций.
     */
    public long getCountOfOperations() {
        return this.count;
    }
    
    
    /**
     * Метод для расчета средних значений тех результатов, у которых одинаковые вторые параметры.
     * @param values Полный список результатов.
     * @return Список результатов с вычисленными средними значениями.
     */
    static ArrayList<Result> calculateMedians(ArrayList<Result> values) {
        ArrayList<Result> res = new ArrayList<>();
        
        HashMap<Long, ArrayList<Long>> temp = new HashMap<>();
        ArrayList<Long> spList = new ArrayList<>();
        for (Result r : values) {
            if (temp.containsKey(r.getSecondParam())) {
                temp.get(r.getSecondParam()).add(r.getCountOfOperations());
            } else {
                ArrayList<Long> arr = new ArrayList<>(); 
                spList.add(r.getSecondParam());
                arr.add(r.getCountOfOperations());
                temp.put(r.getSecondParam(), arr);
            }
        }
        for (Long sp : spList) {
            ArrayList<Long> t = temp.get(sp);
            long sum = 0;
            for (Long s : t) {
                sum += s;
            }
            res.add(new Result((long)(sum / t.size()), sp));  
        }
        return res;
    }
}
