/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package comparative_system.gui;

import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebView;

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
    " <link rel=\"stylesheet\" href=\"lib/codemirror.css\">" +
    " <script src=\"lib/codemirror.js\"></script>" +
    " <script src=\"lib/clike.js\"></script>" +
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
    "</script>" +
    "</body>" +
    "</html>";

    /** applies the editing template to the editing code to create the html+javascript source for a code editor. */
    private String applyEditingTemplate() {
    return editingTemplate.replace("${code}", editingCode);
    }

    /** sets the current code in the editor and creates an editing snapshot of the code which can be reverted to. */
    public void setCode(String newCode) {
    this.editingCode = newCode;
    webview.getEngine().loadContent(applyEditingTemplate());
    }

    /** returns the current code in the editor and updates an editing snapshot of the code which can be reverted to. */
    public String getCodeAndSnapshot() {
    this.editingCode = (String ) webview.getEngine().executeScript("editor.getValue();");
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
    
    //webview.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
    //webview.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
    //webview.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    
    //this.setPrefSize(USE_PREF_SIZE, USE_PREF_SIZE);
    //this.setMinSize(USE_PREF_SIZE, USE_PREF_SIZE);
    //this.setMaxSize(USE_PREF_SIZE, USE_PREF_SIZE);
    
    AnchorPane.setTopAnchor(webview, 0.0); 
    AnchorPane.setLeftAnchor(webview, 0.0); 
    AnchorPane.setBottomAnchor(webview, 0.0); 
    AnchorPane.setRightAnchor(webview, 0.0); 
    webview.getEngine().loadContent(applyEditingTemplate());

    this.getChildren().add(webview);
    }
}
