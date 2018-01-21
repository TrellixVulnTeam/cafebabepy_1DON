package org.cafebabepy.runtime.module.ast;

import org.cafebabepy.runtime.PyObject;
import org.cafebabepy.runtime.Python;
import org.cafebabepy.runtime.module.AbstractCafeBabePyType;
import org.cafebabepy.runtime.module.DefinePyFunction;
import org.cafebabepy.runtime.module.DefinePyType;

/**
 * Created by yotchang4s on 2017/06/07.
 */
@DefinePyType(name = "ast.NodeVisitor")
public class PyNodeVisitorType extends AbstractCafeBabePyType {

    public PyNodeVisitorType(Python runtime) {
        super(runtime);
    }

    @DefinePyFunction(name = "visit")
    public PyObject visit(PyObject self, PyObject node) {
        String method = "visit_" + node.getName();
        PyObject visitor = this.runtime.getattrOptional(self, method).orElse(this.runtime.getattr(this, "generic_visit"));

        return visitor.call(self, node);
    }

    @DefinePyFunction(name = "generic_visit")
    public void generic_visit(PyObject self, PyObject node) {
        PyObject visit = this.runtime.getattr(self, "visit");

        PyObject astModule = this.runtime.moduleOrThrow("ast");
        PyObject iter_fields = this.runtime.getattr(astModule, "iter_fields");

        PyObject list = this.runtime.typeOrThrow("builtins.list");
        PyObject ast = this.runtime.typeOrThrow("_ast.AST");

        PyObject iter_fieldsResult = iter_fields.call(node);

        this.runtime.iter(iter_fieldsResult, fvs -> {
            PyObject[] fieldAndValue = new PyObject[2];
            this.runtime.iterIndex(fvs, (fv, i) -> fieldAndValue[i] = fv);

            PyObject value = fieldAndValue[1];
            if (this.runtime.isInstance(value, list)) {
                this.runtime.iter(value, item -> {
                    if (this.runtime.isInstance(item, ast)) {
                        visit.call(self, item);
                    }
                });

            } else if (this.runtime.isInstance(value, ast)) {
                visit.call(self, value);
            }
        });
    }
}
