package com.codepoetics.fluvius.flows;

import com.codepoetics.fluvius.api.functional.DoubleParameterStep;
import com.codepoetics.fluvius.api.functional.ScratchpadFunction;
import com.codepoetics.fluvius.api.functional.SingleParameterStep;
import com.codepoetics.fluvius.api.functional.TripleParameterStep;

import com.codepoetics.fluvius.api.scratchpad.Key;
import com.codepoetics.fluvius.api.scratchpad.Scratchpad;

final class Extractors {

  private Extractors() {
  }

  static <A, OUTPUT> ScratchpadFunction<OUTPUT> make(Key<A> keyA, SingleParameterStep<A, OUTPUT> singleParameterStep) {
    return new ExtractorFunction1<>(singleParameterStep, keyA);
  }

  static <A, B, OUTPUT> ScratchpadFunction<OUTPUT> make(Key<A> keyA, Key<B> keyB, DoubleParameterStep<A, B, OUTPUT> doubleParameterStep) {
    return new ExtractorFunction2<>(doubleParameterStep, keyA, keyB);
  }

  static <A, B, C, OUTPUT> ScratchpadFunction<OUTPUT> make(Key<A> keyA, Key<B> keyB, Key<C> keyC, TripleParameterStep<A, B, C, OUTPUT> tripleParameterStep) {
    return new ExtractorFunction3<>(tripleParameterStep, keyA, keyB, keyC);
  }

  private static final class ExtractorFunction1<A, OUTPUT> implements ScratchpadFunction<OUTPUT> {
    private final SingleParameterStep<A, OUTPUT> singleParameterStep;
    private final Key<A> source;

    private ExtractorFunction1(SingleParameterStep<A, OUTPUT> singleParameterStep, Key<A> source) {
      this.singleParameterStep = singleParameterStep;
      this.source = source;
    }

    @Override
    public OUTPUT apply(Scratchpad scratchpad) throws Exception {
      return singleParameterStep.apply(scratchpad.get(source));
    }
  }

  private static final class ExtractorFunction2<A, B, OUTPUT> implements ScratchpadFunction<OUTPUT> {
    private final DoubleParameterStep<A, B, OUTPUT> doubleParameterStep;
    private final Key<A> source1;
    private final Key<B> source2;

    private ExtractorFunction2(DoubleParameterStep<A, B, OUTPUT> doubleParameterStep, Key<A> source1, Key<B> source2) {
      this.doubleParameterStep = doubleParameterStep;
      this.source1 = source1;
      this.source2 = source2;
    }

    @Override
    public OUTPUT apply(Scratchpad scratchpad) throws Exception {
      return doubleParameterStep.apply(scratchpad.get(source1), scratchpad.get(source2));
    }
  }

  private static final class ExtractorFunction3<A, B, C, OUTPUT> implements ScratchpadFunction<OUTPUT> {
    private final TripleParameterStep<A, B, C, OUTPUT> tripleParameterStep;
    private final Key<A> source1;
    private final Key<B> source2;
    private final Key<C> source3;

    private ExtractorFunction3(TripleParameterStep<A, B, C, OUTPUT> tripleParameterStep, Key<A> source1, Key<B> source2, Key<C> source3) {
      this.tripleParameterStep = tripleParameterStep;
      this.source1 = source1;
      this.source2 = source2;
      this.source3 = source3;
    }

    @Override
    public OUTPUT apply(Scratchpad scratchpad) throws Exception {
      return tripleParameterStep.apply(scratchpad.get(source1), scratchpad.get(source2), scratchpad.get(source3));
    }
  }
}
