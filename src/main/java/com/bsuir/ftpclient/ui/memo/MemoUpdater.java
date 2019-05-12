package com.bsuir.ftpclient.ui.memo;

import javafx.scene.control.TextArea;

public class MemoUpdater {
    private TextArea memo;

    public MemoUpdater(TextArea memo) {
        this.memo = memo;
    }

    synchronized public void addTextToMemo(String text) {
        memo.appendText(text);
    }
}
