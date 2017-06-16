package org.cafebabepy.runtime.module.builtins;

import org.cafebabepy.annotation.DefineCafeBabePyFunction;
import org.cafebabepy.annotation.DefineCafeBabePyType;
import org.cafebabepy.runtime.CafeBabePyException;
import org.cafebabepy.runtime.PyObject;
import org.cafebabepy.runtime.Python;
import org.cafebabepy.runtime.module.AbstractCafeBabePyType;

import java.util.Optional;

import static org.cafebabepy.util.ProtocolNames.__bool__;
import static org.cafebabepy.util.ProtocolNames.__eq__;

/**
 * Created by yotchang4s on 2017/05/13.
 */
@DefineCafeBabePyType(name = "builtins.int")
public class PyIntType extends AbstractCafeBabePyType {

    public static final String JAVA_INT_NAME = "int";

    public PyIntType(Python runtime) {
        super(runtime);
    }

    @DefineCafeBabePyFunction(name = __bool__)
    public PyObject __bool__(PyObject self) {
        PyObject intType = this.runtime.typeOrThrow("builtins.int");
        PyObject bool = this.runtime.callFunction("builtins.isinstance", self, intType);

        Optional<Object> intOpt = bool.getJavaObject(JAVA_INT_NAME);
        if (!intOpt.isPresent()
                || !(intOpt.get() instanceof Integer)
                || ((Integer) intOpt.get()) == 0) {
            throw this.runtime.newRaiseTypeError(
                    "descriptor '__bool__' requires a 'int' object but received a '" + self.getName() + "'");
        }

        int intValue = (Integer) intOpt.get();

        return intValue != 0 ? this.runtime.True() : this.runtime.False();
    }

    @DefineCafeBabePyFunction(name = __eq__)
    public PyObject __eq__(PyObject self, PyObject other) {
        PyObject intType = typeOrThrow("builtins.int");

        if (this.runtime.callFunction("builtins.isinstance", self, intType).isFalse()) {
            throw this.runtime.newRaiseTypeError(
                    "'__eq__' requires a 'int' object but received a '" + self.getFullName() + "'");
        }
        PyObject otherType = other.getType();
        if (this.runtime.callFunction("builtins.isinstance", other, other).isFalse()) {
            return this.runtime.NotImplementedType();
        }

        Object selfInt = self.getJavaObject(PyIntType.JAVA_INT_NAME);
        Object otherInt = self.getJavaObject(PyIntType.JAVA_INT_NAME);

        if (!(selfInt instanceof Integer) || !(otherInt instanceof Integer)) {
            throw new CafeBabePyException("'" + JAVA_INT_NAME + "' is not found");
        }

        if (self.getJavaObject(PyIntType.JAVA_INT_NAME)
                .equals(other.getJavaObject(PyIntType.JAVA_INT_NAME))) {
            return this.runtime.True();

        } else {
            return this.runtime.False();
        }
    }

    public int asInt() {
        Object intObject = getJavaObject(JAVA_INT_NAME).orElseThrow(() ->
                new CafeBabePyException("'" + JAVA_INT_NAME + "' Java object is not found")
        );
        if (intObject instanceof Integer) {
            return (int) intObject;
        }

        throw new CafeBabePyException("'" + JAVA_INT_NAME + "' Java object is not int");
    }

    public static PyObject newInt(Python runtime, int value) {
        PyObject object = runtime.newPyObject("builtins.int");

        object.putJavaObject(JAVA_INT_NAME, value);

        return object;
    }
}
