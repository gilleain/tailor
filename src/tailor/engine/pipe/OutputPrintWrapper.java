package tailor.engine.pipe;

import java.util.List;

import tailor.structure.Chain;

public class OutputPrintWrapper implements Output<List<Chain>> {

    @Override
    public void accept(List<Chain> chains) {
        chains.forEach(c -> System.out.println(c.getName()));
    }
}
