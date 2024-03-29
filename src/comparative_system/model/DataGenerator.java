package comparative_system.model;

import com.almworks.sqlite4java.SQLiteConnection;
import com.almworks.sqlite4java.SQLiteException;
import comparative_system.CompSys;
import comparative_system.Proccessor;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ClassUtils;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.QualifiedType;
import org.eclipse.jdt.core.dom.SimpleType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;

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

    /**
     * Конструктор класса.
     * @param id Индекс генератора в БД.
     * @param name Имя генератора.
     * @param imports Импорты, добваленные пользователем.
     * @param generateImplementation Код генератора.
     */
    DataGenerator(long id, String name, String imports, String generateImplementation) {
        this.data = new ArrayList<>();
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
     * Добавление списка наборов исходных данных к генератору.
     * @param list Список наборов исходных данных.
     */
    private void setData(ArrayList<Data> list) {
        this.data = list;
    }

    /**
     * Добавление одного набора исходных данных к генератору.
     * @param d Набор исходных данных.
     */
    public void addData(Data d) {
        this.data.add(d);
    }

    /**
     * Метод для генерации данных в генераторе.
     */
    public void generateData(HashMap importsMap) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
        String code = this.getImports() + getHeaderInDataGeneratorCode(importsMap) + this.getGenerateImplementation() + getFooterInDataGeneratorCode();
        code = Proccessor.setPackageGenerator(code);
        Proccessor.checkGeneratorCompilable(code);//компилим

        URL url = new File("data_generator/").toURI().toURL();
        URLClassLoader classLoader = new URLClassLoader(new URL[]{url}, IGenerator.class.getClassLoader());
        Class generator = classLoader.loadClass("generator.Generator");      

        IGenerator igen = (IGenerator)generator.newInstance();
        ArrayList<Data> list = igen.getData(this.getId());
        this.data = list;
    }

    /**
     * Метод возвращает список наборов исходных данных этого генератора.
     * @return Список наборов данных.
     */
    public ArrayList<Data> getValues() {
        return this.data;
    }
    
    
    /**
     * Метод для очистки списка параметров методов вызова. Параметры также удаляются из БД.
     * Если существуют сгенерированные исходные данные для тестов, то они тоже удаляются.
     */
    private static void clearParamsList() {
        params.clear();//TODO:удаление из бд и удаление данных
    }
    
    /**
     * Метод для очистки статического списка параметров методов вызова.
     */
    public static void removeParams() {
        params.clear();
    }

    /**
     * Добавление очередного параметра метода вызова алгоритма.
     * @param type Полный тип параметра.
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
        for(Object param : parameters) {
            SingleVariableDeclaration p = (SingleVariableDeclaration)param;
            String type = getTypeName(p.getType());
            DataGenerator.addMainMethodsParam(type, p.getName().toString());
        }
        saveMethodParamsInDB();
    }
    
    /**
     * Метод возвращает полный тип переменной.
     * @param t Тип переменной.
     * @return Полное имя типа.
     */
    private static String getTypeName(Type t) {
        if (t.isArrayType()) {
            ArrayType a = (ArrayType)t;
            String arrQ = "";
            for (int i = 0; i < a.getDimensions(); i++) {
                arrQ += "[]";
            }
            return getTypeName(a.getElementType()) + arrQ;
        }
        if (t.isPrimitiveType()) {
            return t.toString();
        }
        if (t.isParameterizedType()) {
            ParameterizedType p = (ParameterizedType)t;
                        
            return getTypeName(p.getType());
        }
        if (t.isQualifiedType()) {
            QualifiedType q = (QualifiedType)t;
            return getTypeName(q.getQualifier()) + "$" + q.getName().toString();
        }
        if (t.isSimpleType()) {
            SimpleType s = (SimpleType)t;
            if (s.getName().isQualifiedName()) {
                return s.getName().getFullyQualifiedName();
            } else {
                return getQualifidNameOfSimpleType(s.getName().toString());
            }
        }
        return t.toString();
    }

    /**
     * Метод возвращает полное имя типа.
     * @param name Имя типа.
     * @return Полное имя типа.
     */
    private static String getQualifidNameOfSimpleType(String name) {
        ArrayList<String> allClasses = new ArrayList<>();
        
        for(Algorithm alg : CompSys.getProject().getAlgorithms()) {
            for(Code code : alg.getCodes()) {
                allClasses.add(code.getPackageName() + "." + code.getClassName());
                List imports = Proccessor.getAllImportsFromCode(code);
                for(Object imp : imports) {
                    ImportDeclaration i = (ImportDeclaration)imp;
                    if (!i.isOnDemand()) {
                        allClasses.add(i.getName().getFullyQualifiedName());
                    }
                }
            }
        }
        for (String c : allClasses) {
            if (c.substring(c.lastIndexOf(".") + 1, c.length()).equals(name)) {
                return c;
            }
        }
        return "java.lang." + name;
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
    public static String getHeaderInDataGeneratorCode(HashMap importsMap) {
        return composeImportsString(importsMap)
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
                "       \n" +
                "	private static void addData(long second_parameter, "+ getMethodsParamsAsString(true) +") {\n" +
                "        \n" +
                "        Data d = new Data(list.size(), generator_id, second_parameter, new Object[]{"+ getMethodsParamsAsString(false) +"});\n" +
                "        list.add(d);\n" +
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
            if (!"".equals(value.toString())) {
                res += "import " + value + ".*;\n";
            }
        }
        return res;
    }

    /**
     * Метод возвращает массив классов параметров, которые будут передаваться в методы вызова алгоритмов.
     * @return Массив классов.
     */
    public static Class[] getMethodsParamsAsArrayOfClasses() {
        Class[] arr = new Class[params.size()];
        for(int i = 0; i <params.size(); i++) {
            try {
                arr[i] = ClassUtils.getClass(params.get(i).getType());
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(DataGenerator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return arr;
    }
}
