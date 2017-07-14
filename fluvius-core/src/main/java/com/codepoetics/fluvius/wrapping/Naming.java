package com.codepoetics.fluvius.wrapping;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

final class Naming {

  private Naming() {
  }

  static String getKeyName(String typename) {
    return lowercaseFirst(typename);
  }

  static String getOperationName(String className) {
    if (className == null || className.isEmpty()) {
      throw new IllegalArgumentException("Class name must not be null or empty");
    }

    if (className.equals("Step")) {
      return className;
    }

    String trimmed = className.endsWith("Step")
        ? className.substring(0, className.length() - 4)
        : className;

    return formatWords(getWords(trimmed));
  }

  private static String formatWords(List<String> words) {
    StringBuilder operationName = new StringBuilder();
    Iterator<String> iterator = words.iterator();
    operationName.append(uppercaseFirst(iterator.next()));
    while (iterator.hasNext()) {
      operationName.append(" ").append(lowercaseFirst(iterator.next()));
    }
    return operationName.toString();
  }

  private static List<String> getWords(String trimmed) {
    CaseSeparatedReader reader = new CaseSeparatedReader();
    return reader.read(trimmed);
  }

  private static String lowercaseFirst(String name) {
    return name.substring(0, 1).toLowerCase() + name.substring(1);
  }

  private static String uppercaseFirst(String name) {
    return name.substring(0, 1).toUpperCase() + name.substring(1);
  }

  private static final class CaseSeparatedReader {

    private interface StateFunction {
      State apply(char c, CaseSeparatedReader reader);
    }

    private enum State implements StateFunction {
      INITIAL() {
        @Override
        public State apply(char c, CaseSeparatedReader reader) {
          return Character.isUpperCase(c)
              ? READING_UPPERCASE : READING_LOWERCASE;
        }
      },

      READING_LOWERCASE() {
        @Override
        public State apply(char c, CaseSeparatedReader reader) {
          return Character.isUpperCase(c)
              ? reader.pushName(READING_UPPERCASE) : READING_LOWERCASE;
        }
      },

      READING_ACRONYM() {
        @Override
        public State apply(char c, CaseSeparatedReader reader) {
          return Character.isUpperCase(c)
              ? State.READING_ACRONYM : reader.pushAcronym(READING_LOWERCASE);
        }
      },

      READING_UPPERCASE() {
        @Override
        public State apply(char c, CaseSeparatedReader reader) {
          return Character.isUpperCase(c)
              ? READING_ACRONYM : READING_LOWERCASE;
        }
      }
    }

      private State state;
      private List<String> parts;
      private StringBuilder currentName;

      private void clearCurrentName() {
        currentName.setLength(0);
      }

      private void addPart(String part) {
        parts.add(part);
      }

      private void addChar(char c) {
        currentName.append(c);
      }

      private State pushName(State newState) {
        addPart(currentName.toString());
        clearCurrentName();
        return newState;
      }

      private State pushAcronym(State newState) {
        addPart(currentName.substring(0, currentName.length() - 1));
        char lastChar = currentName.charAt(currentName.length() - 1);
        clearCurrentName();
        addChar(lastChar);
        return newState;
      }

      private void initialise() {
        state = State.INITIAL;
        parts = new LinkedList<>();
        currentName = new StringBuilder();
      }

      public List<String> read(String input) {
        initialise();
        for (char c : input.toCharArray()) {
          readCharacter(c);
        }

        if (currentName.length() > 0) {
          pushName(null);
        }

        return parts;
      }

      private void readCharacter(int c) {
        state = state.apply((char) c, this);
        addChar((char) c);
      }

    }
}
