/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.gui;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;

/**
 *
 * @author TireX
 */
public class CodeEditor extends AnchorPane {
    /** a webview used to encapsulate the CodeMirror JavaScript. */
    final WebView webview = new WebView();

    /** a snapshot of the code to be edited kept for easy initilization and reversion of editable code. */
    private String editingCode;

    /**
    * a template for editing code - this can be changed to any template derived from the
    * supported modes at http://codemirror.net to allow syntax highlighted editing of
    * a wide variety of languages.
    */
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
    " mode: \"text/x-java\"" +
    " });" +
    " editor.setSize(\"100%\", \"100%\"); " +
    " function setReadOnlyLines(begin, end) {(editor.getDoc()).markText({line: begin, ch: 0}, {line: end, ch: 0}, {readOnly: true, className: \"readonly\"});}" +
    "</script>" +
    "</body>" +
    "</html>";
    
    private static String codemirrorjs = "";
    private static String codemirrorcss = "";
    private static String clikejs = "";

    /** applies the editing template to the editing code to create the html+javascript source for a code editor. */
    private String applyEditingTemplate() {
        String res = editingTemplate.replace("${code}", editingCode);
        res = res.replace("${codemirrorjs}", codemirrorjs);
        res = res.replace("${codemirrorcss}", codemirrorcss);
        res = res.replace("${clikejs}", clikejs);
        return res;
    }

    /** sets the current code in the editor and creates an editing snapshot of the code which can be reverted to. */
    public void setCode(String newCode) {
        this.editingCode = newCode;
        webview.getEngine().loadContent(applyEditingTemplate());
    }
    
    /** sets the current code in the editor and creates an editing snapshot of the code which can be reverted to. */
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

    /** returns the current code in the editor and updates an editing snapshot of the code which can be reverted to. */
    public String getCodeAndSnapshot() {
        this.editingCode = (String)webview.getEngine().executeScript("editor.getValue();");
        return editingCode;
    }

    /** revert edits of the code to the last edit snapshot taken. */
    public void revertEdits() {
        setCode(editingCode);
    }

    /**
    * Create a new code editor.
    * @param editingCode the initial code to be edited in the code editor.
    */
    public CodeEditor(String editingCode) {
        this.editingCode = editingCode;
        AnchorPane.setTopAnchor(webview, 0.0);
        AnchorPane.setLeftAnchor(webview, 0.0);
        AnchorPane.setBottomAnchor(webview, 0.0);
        AnchorPane.setRightAnchor(webview, 0.0);
        webview.getEngine().loadContent(applyEditingTemplate());
        this.getChildren().add(webview);      
    }
    
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
