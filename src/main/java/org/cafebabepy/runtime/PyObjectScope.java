package org.cafebabepy.runtime;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Created by yotchang4s on 2017/05/24.
 */
public class PyObjectScope {

    private final PyObject source;

    private Map<String, PyObject> objectMap;

    private volatile Map<String, PyObject> notAppearObjectMap;

    public PyObjectScope(PyObject source) {
        this.source = source;
    }

    public void put(String name, PyObject object) {
        put(name, object, true);
    }

    public void put(String name, PyObject object, boolean appear) {
        if (appear) {
            if (this.objectMap == null) {
                synchronized (this) {
                    if (this.objectMap == null) {
                        this.objectMap = Collections.synchronizedMap(new LinkedHashMap<>());
                    }
                }
            }

            this.objectMap.put(name, object);

        } else {
            if (this.notAppearObjectMap == null) {
                synchronized (this) {
                    if (this.notAppearObjectMap == null) {
                        this.notAppearObjectMap = Collections.synchronizedMap(new LinkedHashMap<>());
                    }
                }
            }

            this.notAppearObjectMap.put(name, object);
        }
    }

    public PyObject getSource() {
        return this.source;
    }

    public Map<String, PyObject> gets() {
        return gets(true);
    }

    public Map<String, PyObject> gets(boolean appear) {
        Map<String, PyObject> map = Collections.synchronizedMap(new LinkedHashMap<>());

        if (this.objectMap != null) {
            map.putAll(this.objectMap);
        }

        if (!appear && this.notAppearObjectMap != null) {
            map.putAll(this.notAppearObjectMap);
        }

        return map;
    }

    public Optional<PyObject> getThisOnly(String name) {
        return getThisOnly(name, true);
    }

    public Optional<PyObject> getThisOnly(String name, boolean appear) {
        if (this.objectMap != null) {
            PyObject object = this.objectMap.get(name);
            if (object != null) {
                return Optional.of(object);
            }
        }

        if (!appear) {
            return getAppearOnly(name);
        }

        return Optional.empty();
    }

    public Optional<PyObject> get(String name) {
        return get(name, true);
    }

    public final Optional<PyObject> get(String name, boolean appear) {
        Optional<PyObject> objectOpt = getThisOnly(name, appear);
        if (objectOpt.isPresent()) {
            return objectOpt;
        }

        if (!this.source.isType() && !this.source.isModule()) {
            return this.source.getType().getScope().get(name, appear);
        }

        for (PyObject type : this.source.getTypes()) {
            Optional<PyObject> typeObject = type.getScope().getThisOnly(name, appear);
            if (typeObject.isPresent()) {
                return typeObject;
            }
        }

        return Optional.empty();
    }

    public final PyObject getOrThrow(String name) {
        return getOrThrow(name, true);
    }

    public final PyObject getOrThrow(String name, boolean appear) {
        Optional<PyObject> objectOpt = get(name, appear);
        if (objectOpt.isPresent()) {
            return objectOpt.get();
        }

        for (PyObject type : this.source.getTypes()) {
            Optional<PyObject> typeObjectOpt = type.getScope().getThisOnly(name, appear);
            if (typeObjectOpt.isPresent()) {
                return typeObjectOpt.get();
            }
        }

        if (this.source.isModule()) {
            throw this.source.getRuntime().newRaiseException("builtins.NameError",
                    "name '" + name + "' is not defined");

        } else if (this.source.isType()) {
            throw this.source.getRuntime().newRaiseException("builtins.AttributeError",
                    "type object '" + this.source.getFullName() + "' has no attribute '" + name + "'");

        } else {
            throw this.source.getRuntime().newRaiseException("builtins.AttributeError",
                    "'" + this.source.getFullName() + "' object has no attribute '" + name + "'");
        }
    }

    public Optional<PyObject> getAppearOnly(String name) {
        PyObject object = null;
        if (this.notAppearObjectMap != null) {
            object = this.notAppearObjectMap.get(name);
        }
        if (object == null) {
            return Optional.empty();
        }

        return Optional.of(object);
    }

    public boolean containsKey(String name) {
        return containsKey(name, true);
    }

    public boolean containsKey(String name, boolean appear) {
        if (this.objectMap != null && this.objectMap.containsKey(name)) {
            return true;
        }
        if (appear) {
            if (this.notAppearObjectMap != null && this.notAppearObjectMap.containsKey(name)) {
                return true;
            }
        }

        return false;
    }
}
