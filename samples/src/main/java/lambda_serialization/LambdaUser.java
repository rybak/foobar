package lambda_serialization;

import java.io.Serializable;
import java.util.function.Supplier;

/**
 * @author Andrei Rybak
 */
public class LambdaUser implements Serializable {
    private final int i;
    private final Supplier<String> lambda;

    public LambdaUser(int i, Supplier<String> lambda) {
        this.i = i;
        this.lambda = lambda;
    }

    public String getContent() {
        return String.valueOf(i) + lambda.get();
    }
}
