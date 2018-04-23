package com.scut.jsj.exception;

import java.util.LinkedList;
import java.util.List;

public class BeanCreationException extends BeansException {
    private String beanName;
    private String resourceDescription;
    private List<Throwable> relatedCauses;

    public BeanCreationException() {
    }

    public BeanCreationException(String message) {
        super(message);
    }

    public BeanCreationException(String resourceDescription, String beanName, String msg, Throwable cause) {
        this(resourceDescription, beanName, msg);
        this.initCause(cause);
    }

    public BeanCreationException(String resourceDescription, String beanName, String message) {
        super(message);
        this.beanName = beanName;
        this.resourceDescription = resourceDescription;
    }

    public BeanCreationException(String beanName, String msg, Throwable cause) {
        this(beanName, msg);
        this.initCause(cause);
    }

    public BeanCreationException(String beanName, String message) {
        super(message);
        this.beanName = beanName;
    }

    public void addRelatedCause(Throwable ex) {
        if (this.relatedCauses == null) {
            this.relatedCauses = new LinkedList();
        }

        this.relatedCauses.add(ex);
    }
}
