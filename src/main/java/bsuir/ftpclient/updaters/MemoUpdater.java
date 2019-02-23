package main.java.bsuir.ftpclient.updaters;

import javafx.scene.control.TextArea;

public class MemoUpdater {
    private TextArea memo;

    public MemoUpdater(TextArea memo) {
        this.memo = memo;
    }

    synchronized public void addTextToMemo(String text) {
        memo.setText(memo.getText() + text);
    }
}
