package org.cafebabepy.runtime.module._ast;

import org.cafebabepy.runtime.PyObject;
import org.cafebabepy.runtime.Python;
import org.cafebabepy.runtime.module.DefinePyFunction;
import org.cafebabepy.runtime.module.DefinePyType;

import static org.cafebabepy.util.ProtocolNames.__init__;

/**
 * Created by yotchang4s on 2017/05/29.
 */
@DefinePyType(name = "_ast.Annassign", parent = {"_ast.stmt"})
public class PyAnnAssignType extends AbstractAST {

    public PyAnnAssignType(Python runtime) {
        super(runtime);
    }

    @DefinePyFunction(name = __init__)
    public void __init__(PyObject self, PyObject... args) {
        if (args.length == 0) {
            return;
        }

        self.getScope().put(this.runtime.str("target"), args[0]);
        self.getScope().put(this.runtime.str("annotation"), args[1]);
        self.getScope().put(this.runtime.str("value"), args[2]);
        self.getScope().put(this.runtime.str("simple"), args[3]);
    }

    @Override
    String[] _fields() {
        return new String[]{"target", "annotation", "value", "simple"};
    }
}
