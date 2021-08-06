package com.azerusteam.wrappers;

import com.comphenix.tinyprotocol.Reflection;

public abstract class AbstractWrapper {

    Reflection.MethodInvoker<?> getMethod(String name, Class<?>... parameterTypes) {
        try {
            Class<?> clazz = (Class<?>) this.getClass().getField("clazz").get(null);
            return Reflection.getMethod(clazz, name, parameterTypes);
        } catch (Exception ex) {
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex);
        }
    }

    <T> Reflection.MethodInvoker<T> getTypedMethod(String name, Class<T> returnType, Class<?>... parameterTypes) {
        try {
            Class<?> clazz = (Class<?>) this.getClass().getField("clazz").get(null);
            return Reflection.getTypedMethod(clazz, name, returnType, parameterTypes);
        } catch (Exception ex) {
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex);
        }
    }

    Reflection.ConstructorInvoker<?> getConstructor(Class<?>... parameterTypes) {
        try {
            Class<?> clazz = (Class<?>) this.getClass().getField("clazz").get(null);
            return Reflection.getConstructor(clazz, parameterTypes);
        } catch (Exception ex) {
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex);
        }
    }

    <T> Reflection.FieldAccessor<T> getField(String name, Class<T> type) {
        try {
            Class<?> clazz = (Class<?>) this.getClass().getField("clazz").get(null);
            return Reflection.getField(clazz, name, type);
        } catch (Exception ex) {
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex);
        }
    }

    Object getHandle0() {
        try {
            return this.getClass().getField("instance").get(this);
        } catch (Exception ex) {
            throw ex instanceof RuntimeException ? (RuntimeException) ex : new RuntimeException(ex);
        }
    }

}
