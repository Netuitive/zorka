package com.jitlogic.zorka.core.mbeans;

import bsh.Primitive;
import bsh.This;
import java.util.Arrays;

/**
 *
 * @author john.king
 */
public class BshGetter extends AttrGetter {
    
    private final String methodName;

    public BshGetter(Object obj, Object... attrs) {
        super(obj, attrs);
        if(!(obj instanceof This)){
            throw new RuntimeException("BshGetter must use an object of type bsh.This");
        }
        if(attrs.length < 1){
            throw new RuntimeException("BshGetter must have at least 1 attribute");
        }
        if(!(attrs[0] instanceof String)){
            throw new RuntimeException("BshGetter must have a method name as its first attribute");
        }
        methodName = (String) attrs[0];
        this.attrs = Arrays.copyOfRange(attrs, 1, attrs.length);
    }

    //bsh.This only allows the user to invoke a method, so that's all we're going to do here.
    @Override
    public Object get() {
        This t = (This) obj;
        Object ret =  t.invokeMethod(methodName, attrs);
        if(ret instanceof Primitive){
            //bsh wraps primitive types, so we must unwrap them.
            ret = ((Primitive)ret).getValue();
        }
        return ret;
    }

    
}
