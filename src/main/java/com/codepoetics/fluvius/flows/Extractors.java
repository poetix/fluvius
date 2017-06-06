package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.functional.F1;
import com.codepoetics.fluvius.api.functional.F2;
import com.codepoetics.fluvius.api.functional.F3;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

final class Extractors {

  private Extractors() {
  }

  static <A, OUTPUT> ScratchpadFunction<OUTPUT> make(final Key<A> keyA, final F1<A, OUTPUT> f1) {
    return new ExtractorFunction1<>(f1, keyA);
  }

  static <A, B, OUTPUT> ScratchpadFunction<OUTPUT> make(final Key<A> keyA, final Key<B> keyB, final F2<A, B, OUTPUT> f2) {
    return new ExtractorFunction2<>(f2, keyA, keyB);
  }

  static <A, B, C, OUTPUT> ScratchpadFunction<OUTPUT> make(final Key<A> keyA, final Key<B> keyB, final Key<C> keyC, final F3<A, B, C, OUTPUT> f3) {
    return new ExtractorFunction3<>(f3, keyA, keyB, keyC);
  }

  private static class ExtractorFunction1<A, OUTPUT> implements ScratchpadFunction<OUTPUT> {
    private final F1<A, OUTPUT> f1;
    private final Key<A> source;

    private ExtractorFunction1(final F1<A, OUTPUT> f1, final Key<A> source) {
      this.f1 = f1;
      this.source = source;
    }

    @Override
    public OUTPUT apply(final Scratchpad scratchpad) {
      return f1.apply(scratchpad.get(source));
    }
  }

  private static class ExtractorFunction2<A, B, OUTPUT> implements ScratchpadFunction<OUTPUT> {
    private final F2<A, B, OUTPUT> f2;
    private final Key<A> source1;
    private final Key<B> source2;

    private ExtractorFunction2(final F2<A, B, OUTPUT> f2, final Key<A> source1, final Key<B> source2) {
      this.f2 = f2;
      this.source1 = source1;
      this.source2 = source2;
    }

    @Override
    public OUTPUT apply(final Scratchpad scratchpad) {
      return f2.apply(scratchpad.get(source1), scratchpad.get(source2));
    }
  }

  private static class ExtractorFunction3<A, B, C, OUTPUT> implements ScratchpadFunction<OUTPUT> {
    private final F3<A, B, C, OUTPUT> f3;
    private final Key<A> source1;
    private final Key<B> source2;
    private final Key<C> source3;

    private ExtractorFunction3(final F3<A, B, C, OUTPUT> f3, final Key<A> source1, final Key<B> source2, final Key<C> source3) {
      this.f3 = f3;
      this.source1 = source1;
      this.source2 = source2;
      this.source3 = source3;
    }

    @Override
    public OUTPUT apply(final Scratchpad scratchpad) {
      return f3.apply(scratchpad.get(source1), scratchpad.get(source2), scratchpad.get(source3));
    }
  }
}
