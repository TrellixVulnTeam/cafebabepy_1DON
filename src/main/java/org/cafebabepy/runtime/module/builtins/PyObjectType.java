package org.cafebabepy.runtime.module.builtins;

import org.cafebabepy.annotation.DefineCafeBabePyFunction;
import org.cafebabepy.annotation.DefineCafeBabePyType;
import org.cafebabepy.runtime.PyObject;
import org.cafebabepy.runtime.Python;
import org.cafebabepy.runtime.module.AbstractCafeBabePyType;
import org.cafebabepy.runtime.object.PyObjectObject;

import static org.cafebabepy.util.ProtocolNames.*;

/**
 * Created by yotchang4s on 2017/05/13.
 */
@DefineCafeBabePyType(name = "builtins.object")
public final class PyObjectType extends AbstractCafeBabePyType {

    public PyObjectType(Python runtime) {
        super(runtime);
    }

    @DefineCafeBabePyFunction(name = __new__)
    public PyObject __new__(PyObject cls) {
        if (!cls.isType()) {
            throw this.runtime.newRaiseTypeError("object.__new__(X): X is not a type object (" + cls.getFullName() + ")");
        }
        return new PyObjectObject(this.runtime, cls);
    }

    @DefineCafeBabePyFunction(name = __init__)
    public void __init__(PyObject self, PyObject... args) {
    }

    @DefineCafeBabePyFunction(name = __getattribute__)
    public PyObject __getattribute__(PyObject self, PyObject name) {
        PyObject strType = typeOrThrow("builtins.str");

        PyObject builtinsModule = this.runtime.moduleOrThrow("builtins");
        PyObject isinstance = builtinsModule.getScope().getOrThrow("isinstance");

        if (isinstance.call(builtinsModule, name, strType).isFalse()) {
            throw this.runtime.newRaiseTypeError(
                    "attribute name must be string, not '" + name.getFullName() + "'"
            );
        }

        return self.getScope().getOrThrow(name.asJavaString());
    }

    @DefineCafeBabePyFunction(name = __setattr__)
    public void __setattr__(PyObject self, PyObject name, PyObject value) {
        PyObject strType = typeOrThrow("builtins.str");

        PyObject builtinsModule = this.runtime.typeOrThrow("builtins");
        PyObject isinstance = builtinsModule.getScope().getOrThrow("isinstance");

        if (isinstance.call(builtinsModule, name, strType).isFalse()) {
            throw this.runtime.newRaiseTypeError(
                    "attribute name must be string, not '" + name.getFullName() + "'"
            );
        }

        self.getScope().put(name.asJavaString(), value);
    }

    @DefineCafeBabePyFunction(name = __str__)
    public PyObject __str__(PyObject self) {
        return this.runtime.str(self.asJavaString());
    }

    @DefineCafeBabePyFunction(name = __eq__)
    public PyObject __eq__(PyObject self, PyObject other) {
        return this.runtime.NotImplementedType();
    }

    @DefineCafeBabePyFunction(name = __ne__)
    public PyObject __ne__(PyObject self, PyObject other) {
        return this.runtime.NotImplementedType();
    }
}
