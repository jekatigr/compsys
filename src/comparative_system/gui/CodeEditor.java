package comparative_system.gui;

import comparative_system.controller.FXMLguiController;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;

/**
 * Класс компонента - редактора кода с подсветкой синтаксиса.
 * @author Gromov Evg.
 */
public class CodeEditor extends AnchorPane {
    /** Webview, в который бубут подгружены библиотеки CodeMirror для подсветки синтаксиса.  */
    final WebView webview = new WebView();

    /** Код в редакторе. */
    private String editingCode;

    /** Шаблон, в который будет загружаться остальной код и библиотеки. */
    private final String editingTemplate =
    "<!doctype html>" +
    "<html>" +
    "<head>" +
    " <style type=\"text/css\">"
            + ".readonly{ background-color: #dfdfdf;} "
            + "${codemirrorcss}</style>" +
    " <script>${codemirrorjs}</script>" +
    " <script>${clikejs}</script>" +
    "</head>" +
    "<body style=\"margin:0px;\">" +
    "<form><textarea id=\"code\" name=\"code\">\n" +
    "${code}" +
    "</textarea></form>" +
    "<script>" +
    " var editor = CodeMirror.fromTextArea(document.getElementById(\"code\"), {" +
    " lineNumbers: true," +
    " matchBrackets: true," +
    " mode: \"text/x-java\"," +
    " tabSize: 4" +
    " });" +
    " editor.setSize(\"100%\", \"100%\"); " +
    " function setReadOnlyLines(begin, end) {(editor.getDoc()).markText({line: begin, ch: 0}, {line: end, ch: 0}, {readOnly: true, className: \"readonly\"});}" +
    "</script>" +
    "</body>" +
    "</html>";
    
    /** Переменная для хранения кода библиотеки CodeMirror. */
    private static String codemirrorjs = "";
    /** Переменная для хранения кода библиотеки CodeMirror. */
    private static String codemirrorcss = "";
    /** Переменная для хранения кода библиотеки CodeMirror. */
    private static String clikejs = "";

    /**
     * Метод для вставки библиотек и исходного кода в шаблон.
     * @return Код, готовый для отображения в {@code WebView}.
     */
    private String applyEditingTemplate() {
        String res = editingTemplate.replace("${code}", editingCode);
        res = res.replace("${codemirrorjs}", codemirrorjs);
        res = res.replace("${codemirrorcss}", codemirrorcss);
        res = res.replace("${clikejs}", clikejs);
        return res;
    }

    /**
     * Метод задает код для отображения в редакторе.
     * @param newCode Исходный код.
     */
    public void setCode(String newCode) {
        this.editingCode = newCode;
        webview.getEngine().loadContent(applyEditingTemplate());
    }
    
    /**
     * Метод задает код для отображения. В коде подсвечиваются два участка, которые невозможно изменять.
     * @param newCode Код для отображения.
     * @param beginFirst Строка начала первого нередактируемого участка кода.
     * @param endFirst Строка конца первого нередактируемого участка кода.
     * @param beginSecond Строка начала второго нередактируемого участка кода
     * @param endSecond  Строка конца второго нередактируемого участка кода
     */
    public void setCode(String newCode, final int beginFirst, final int endFirst, final int beginSecond, final int endSecond) {
        this.editingCode = newCode;
        webview.getEngine().loadContent(applyEditingTemplate());
       
        webview.getEngine().documentProperty().addListener(new ChangeListener<Document>() {
            @Override
            public void changed(ObservableValue<? extends Document> ov, Document t, Document t1) {
                if (t1 != null) {
                    webview.getEngine().documentProperty().removeListener(this);
                    webview.getEngine().executeScript("setReadOnlyLines(" + beginFirst + "," + endFirst + ");");
                    webview.getEngine().executeScript("setReadOnlyLines(" + beginSecond + "," + endSecond + ");");
                }
            }
        });
    }

    /**
     * Метод возвращает код из редактора.
     * @return 
     */
    public String getCode() {
        this.editingCode = (String)webview.getEngine().executeScript("editor.getValue();");
        return editingCode;
    }

    /**
    * Конструктор компонента.
    * @param editingCode Код для отображения.
    */
    public CodeEditor(String editingCode) {
        webview.setOnKeyTyped(new EventHandler() {
            @Override
            public void handle(Event t) {
                FXMLguiController.handleKeyTypedInCodeEditor();
            }
        });
        this.editingCode = editingCode;
        AnchorPane.setTopAnchor(webview, 0.0);
        AnchorPane.setLeftAnchor(webview, 0.0);
        AnchorPane.setBottomAnchor(webview, 0.0);
        AnchorPane.setRightAnchor(webview, 0.0);
        webview.getEngine().loadContent(applyEditingTemplate());
        this.getChildren().add(webview);      
    }
    
    /**
     * Метод для загрузки библиотек CodeMirror из файлов.
     * @return {@code true} в случае успешной загрузки библиотек из файлов, {@code false} иначе.
     */
    public static boolean loadCodeMirrorLibs() {
        try {
            if (clikejs.length() == 0) {
                File clike = new File("lib/clike.js");
                StringBuilder temp = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(clike));
                String text;
                while ((text = bufferedReader.readLine()) != null) {
                    temp.append("\n").append(text);
                }
                clikejs = temp.toString();
            }
            
            if (codemirrorjs.length() == 0) {
                File clike = new File("lib/codemirror.js");
                StringBuilder temp = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(clike));
                String text;
                while ((text = bufferedReader.readLine()) != null) {
                    temp.append("\n").append(text);
                }
                codemirrorjs = temp.toString();
            }
            
            if (codemirrorcss.length() == 0) {
                File clike = new File("lib/codemirror.css");
                StringBuilder temp = new StringBuilder();
                BufferedReader bufferedReader = new BufferedReader(new FileReader(clike));
                String text;
                while ((text = bufferedReader.readLine()) != null) {
                    temp.append("\n").append(text);
                }
                codemirrorcss = temp.toString();
            }
            return true;
        } catch (IOException ex) {
            Logger.getLogger(CodeEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }
}
