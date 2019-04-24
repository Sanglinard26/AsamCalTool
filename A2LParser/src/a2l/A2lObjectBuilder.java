package a2l;

import java.util.List;

public interface A2lObjectBuilder {

    void build(List<String> parameters, int beginLine, int endLine) throws A2lObjectParsingException;

}
