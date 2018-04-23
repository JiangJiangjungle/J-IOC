package com.scut.jsj.beans;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MutablePropertyValues implements PropertyValues, Serializable {

    private final List<PropertyValue> propertyValueList;

    public MutablePropertyValues() {
        this.propertyValueList = new ArrayList(0);
    }

    public MutablePropertyValues(PropertyValues original) {
        if (original != null) {
            PropertyValue[] pvs = original.getPropertyValues();
            this.propertyValueList = new ArrayList(pvs.length);
            PropertyValue[] var3 = pvs;
            int var4 = pvs.length;

            for (int var5 = 0; var5 < var4; ++var5) {
                PropertyValue pv = var3[var5];
                this.propertyValueList.add(new PropertyValue(pv));
            }
        } else {
            this.propertyValueList = new ArrayList(0);
        }
    }

    public void addPropertyValue(String propertyName, Object propertyValue) {
        this.addPropertyValue(new PropertyValue(propertyName, propertyValue));
    }

    public MutablePropertyValues addPropertyValue(PropertyValue pv) {
        for (int i = 0; i < this.propertyValueList.size(); ++i) {
            PropertyValue currentPv = (PropertyValue) this.propertyValueList.get(i);
            if (currentPv.getName().equals(pv.getName())) {
                this.setPropertyValueAt(pv, i);
                return this;
            }
        }
        this.propertyValueList.add(pv);
        return this;
    }

    public void setPropertyValueAt(PropertyValue pv, int i) {
        this.propertyValueList.set(i, pv);
    }

    @Override
    public PropertyValue[] getPropertyValues() {
        return (PropertyValue[]) this.propertyValueList.toArray(new PropertyValue[this.propertyValueList.size()]);
    }

    @Override
    public PropertyValue getPropertyValue(String propertyName) {
        Iterator var2 = this.propertyValueList.iterator();
        PropertyValue pv;
        do {
            if (!var2.hasNext()) {
                return null;
            }
            pv = (PropertyValue) var2.next();
        } while (!pv.getName().equals(propertyName));
        return pv;
    }

    @Override
    public PropertyValues changesSince(PropertyValues old) {
        MutablePropertyValues changes = new MutablePropertyValues();
        if (old == this) {
            return changes;
        } else {
            Iterator var3 = this.propertyValueList.iterator();

            while (var3.hasNext()) {
                PropertyValue newPv = (PropertyValue) var3.next();
                PropertyValue pvOld = old.getPropertyValue(newPv.getName());
                if (pvOld == null) {
                    changes.addPropertyValue(newPv);
                } else if (!pvOld.equals(newPv)) {
                    changes.addPropertyValue(newPv);
                }
            }

            return changes;
        }
    }

    @Override
    public boolean contains(String var1) {
        return false;
    }

    @Override
    public boolean isEmpty() {
        return false;
    }
}
