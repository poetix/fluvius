package com.codepoetics.fluvius.test.matchers;

import org.hamcrest.Description;

public final class IndentationControl {

  private IndentationControl() {
  }

  private static final ThreadLocal<Integer> indentationLevel = new ThreadLocal<>();

  public static Description newline(Description description) {
    description.appendText("\n");
    for (int i = 0; i < getIndentationLevel(); i++) {
      description.appendText("\t");
    }
    return description;
  }

  private static int getIndentationLevel() {
    Integer localIndent = indentationLevel.get();
    return localIndent == null ? 0 : localIndent;
  }

  public static void indent() {
    indentationLevel.set(getIndentationLevel() + 1);
  }

  public static void outdent() {
    indentationLevel.set(getIndentationLevel() - 1);
  }
}
