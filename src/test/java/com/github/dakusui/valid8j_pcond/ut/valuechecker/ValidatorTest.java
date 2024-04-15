package com.github.dakusui.valid8j_pcond.ut.valuechecker;

import com.github.dakusui.shared.utils.ut.TestBase;
import com.github.dakusui.valid8j.pcond.validator.ExceptionComposer;
import com.github.dakusui.valid8j.pcond.validator.MessageComposer;
import com.github.dakusui.valid8j.pcond.validator.ReportComposer;
import com.github.dakusui.valid8j.pcond.validator.Validator;
import org.junit.Test;

import java.util.Optional;

public class ValidatorTest extends TestBase {
  public static class TestValidator implements Validator {
    private final Configuration configuration = new Configuration() {
      @Override
      public int summarizedStringLength() {
        return 40;
      }

      @Override
      public boolean useEvaluator() {
        return false;
      }

      @Override
      public ExceptionComposer exceptionComposer() {
        return null;
      }

      @Override
      public Optional<Debugging> debugging() {
        return Optional.empty();
      }

      @Override
      public Builder parentBuilder() {
        throw new UnsupportedOperationException();
      }

      @Override
      public MessageComposer messageComposer() {
        return null;
      }

      @Override
      public ReportComposer reportComposer() {
        return null;
      }
    };


    @Override
    public Configuration configuration() {
      return configuration;
    }
  }

  @Test
  public void test2() {
    System.out.println(TestValidator.class.getName());
    System.setProperty("com.github.dakusui.pcond.provider.AssertionProvider", "com.github.dakusui.pcond.ut.providers.AssertionProviderTest$TestAssertionProvider");
    System.out.println("-->" + Validator.instance().getClass().getCanonicalName());
  }
}
