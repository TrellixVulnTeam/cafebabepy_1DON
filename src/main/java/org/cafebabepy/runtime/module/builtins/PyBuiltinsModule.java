package org.cafebabepy.runtime.module.builtins;

import org.cafebabepy.annotation.DefineCafeBabePyFunction;
import org.cafebabepy.annotation.DefineCafeBabePyModule;
import org.cafebabepy.runtime.PyObject;
import org.cafebabepy.runtime.Python;
import org.cafebabepy.runtime.module.AbstractCafeBabePyModule;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by yotchang4s on 2017/05/12.
 */
@DefineCafeBabePyModule(name = "builtins")
public class PyBuiltinsModule extends AbstractCafeBabePyModule {
    public PyBuiltinsModule(Python runtime) {
        super(runtime);
    }

    @DefineCafeBabePyFunction(name = "isinstance")
    public PyObject isInstance(PyObject object, PyObject classInfo) {
        if (classInfo == null || (!classInfo.isType() && classInfo instanceof PyTupleType)) {
            throw this.runtime.newRaiseTypeError(
                    "isinstance() arg 2 must be a type or tuple of types");
        }

        Set<PyObject> objectTypeSet = new HashSet<>();
        objectTypeSet.addAll(object.getTypes());

        if (classInfo instanceof PyTupleType) {
            this.runtime.iter(classInfo, objectTypeSet::add);
        }

        if (objectTypeSet.contains(classInfo)) {
            return this.runtime.True();
        }

        return this.runtime.False();
    }
}
