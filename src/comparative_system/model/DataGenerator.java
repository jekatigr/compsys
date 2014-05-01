package comparative_system.model;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import comparative_system.CompSys;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;

/**
 * Класс для представления генераторов исходных данных вместе с самими данными.
 * @author Gromov Evg.
 */
public class DataGenerator {
    /** Список параметров методов вызова алгоритмов. Для всех генераторов список один и тот же. */
    private static ArrayList<Param> params = new ArrayList<>();

    /** Индекс генератора в БД. */
    private long id;
    /** Имя генератора. */
    private String name;
    /** Код генератора. */
    private String imports;
    /** Код генератора. */
    private String generateImplementation;
    /** Список сгенерированных исходных данных. */
    private ArrayList<Data> data;
    /** Флаг для вкладки код/данные для GUI. */
    private boolean isCodeOpened = true;

    /**
     * Конструктор класса.
     * @param id Индекс генератора в БД.
     * @param name Имя генератора.
     * @param imports Импорты, добваленные пользователем.
     * @param generateImplementation Код генератора.
     */
    DataGenerator(long id, String name, String imports, String generateImplementation) {
        this.id = id;
        this.name = name;
        this.imports = imports;
        this.generateImplementation = generateImplementation;
    }

    /**
     * Конструктор класса.
     * @param name Имя генератора.
     * @param imports Импорты, добваленные пользователем.
     * @param generateImplementation Код генератора.
     */
    public DataGenerator(String name, String imports, String generateImplementation) {
        this(-1, name, imports, generateImplementation);
    }

    /**
     * Возвращает индекс генератора в БД проекта.
     * @return Индекс генератора.
     */
    public long getId() {
        return this.id;
    }

    /**
     * Задает индекс генератора в БД проекта.
     * @param id Индекс генератора.
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     * Возвращает имя генератора.
     * @return Имя генератора.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Задает имя генератора.
     * @param name Новое имя.
     */
    void setName(String name) {
        this.name = name;
    }

    /**
     * Метод возвращает пользовательские импорты из кода генератора данных.
     * @return Пользовательские импорты.
     */
    public String getImports() {
        return this.imports;
    }

    /**
     * Метод задает пользовательские импорты из кода генератора данных.
     * @param imports Новые позовательские импорты.
     */
    void setImports(String imports) {
        this.imports = imports;
    }

    /**
     * Метод возвращает исходный код генератора данных.
     * @return Исходный код метода генерации данных.
     */
    public String getGenerateImplementation() {
        return this.generateImplementation + "\n";
    }

    /**
     * Метод задает исходный код генератора данных.
     * @param generateImplementation Исходный код метода генерации данных.
     */
    public void setGenerateImplementation(String generateImplementation) {
        this.generateImplementation = generateImplementation;
    }

    /**
     * Добавление списка наборов исходных данных к генератору. Здесь же набор сохраняется в БД.
     * @param list Список наборов исходных данных.
     */
    public void setData(ArrayList<Data> list) {
        this.data = list;//TODO: для сериализации пользовательских классов нужны те самые классы, загруженные в CompSys.
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
    
    /**
     * Возвращает параметр.
     * @param index Индекс с списке параметров.
     * @return Параметр.
     */
    public static Param getParam(int index) {
        return params.get(index);
    }

    /**
     * Метод возващает параметры главных методов алгоритмов в виде строки.
     * @param withTypes Флаг - {@code true} для получения параметров с указанными типами, {@code false} для получения без типов.
     * @return Строку с параметрами главных методов алгоритма.
     */
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

    /**
     * Метод для сохранения параметров главных методов алгоритмов в БД проекта.
     */
    private static void saveMethodParamsInDB() {
        try {
            //открываем базу данных проекта
            SQLiteConnection db = new SQLiteConnection(CompSys.getProject().file);
            db.open();
            //--открываем базу данных проекта
            //сохраняем
            db.exec("DELETE FROM main_params");
            for (Param par : params) {
                db.exec("INSERT INTO main_params (type, name) VALUES ('"+ par.getType() +"', '"+ par.getName() +"')");
            }
            //--сохраняем
        } catch (SQLiteException ex) {
            Logger.getLogger(DataGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Метод возвращает часть кода генератора исходных данных.
     * @return Фрагмент кода генератора.
     */
    public static String getHeaderInDataGeneratorCode() {
        return composeImportsString(CompSys.getProject().getAllAlgotithmsAsImportsMap())
                        + " \nimport comparative_system.model.Data;\n" +
                        "import comparative_system.model.IGenerator;\n" +
                        "import java.util.ArrayList;\n" +
                        " \n" +
                        "public class Generator implements IGenerator {\n" +
                        "	private static void generate() {";
    }

    /**
     * Метод возвращает часть кода генератора исходных данных.
     * @return Фрагмент кода генератора.
     */
    public static String getFooterInDataGeneratorCode() {
        return  "    }\n \n    private static ArrayList<Data> list = new ArrayList<Data>();\n" +
                "	static long generator_id;\n" +
                "	\n" +
                "	private static void addData("+ getMethodsParamsAsString(true) +") {\n" +
                "        \n" +
                "        Data d = new Data(generator_id, new Object[]{"+ getMethodsParamsAsString(false) +"});\n"
                                    + "        list.add(d);\n" +
                "	}\n" +
                "	\n" +
                "	public ArrayList<Data> getData(long gen_id) {\n" +
                "		generator_id = gen_id;\n" +
                "		generate();\n" +
                "		return list;\n" +
                "	}\n" +
                "}";
    }

    /**
     * Метод составления кода импортов из списка пакетов алгоритма.
     * @param importsMap Список пакетов алгоритма.
     * @return Фрагмент кода импортов.
     */
    private static String composeImportsString(HashMap importsMap) {
        HashMap map = new HashMap();
        String res = "";
        for (Object value : importsMap.values()) {
            res += "import " + value + ".*;\n";
        }
        return res;
    }

    public ArrayList<Data> getValues() {
        return this.data;
    }
}
