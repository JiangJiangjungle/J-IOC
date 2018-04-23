package com.scut.jsj.beans.factory;

import com.scut.jsj.exception.BeansException;

public interface ObjectFactory<T> {
    T getObject() throws BeansException;
}