//https://docs.oracle.com/javase/8/docs/api/java/lang/reflect/InvocationHandler.html
// dont remember how helpful this was but: https://www.baeldung.com/java-dynamic-proxies

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

public class HandMeDownProxyFactory {
    public static IHandMeDownHistory get(IHandMeDown handDown) {
        if (handDown == null) {
            throw new IllegalArgumentException("subject cannot be null");
        }

        return (IHandMeDownHistory) Proxy.newProxyInstance(
            handDown.getClass().getClassLoader(),
            new Class<?>[]{IHandMeDown.class, IHandMeDownHistory.class},new InvocationHandler() {
                private String currentOwner;
                private final List<String> owners = new ArrayList<>();

                @Override
                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                    var name = method.getName();
                    switch (name) {
                    case "getOwner": return currentOwner;
                    case "getOwners" : return owners;
                    	
                    default: throw new UnsupportedOperationException(String.format("unknown method: %s()", name));
                        case "setOwner":
                            String newOwner = (String) args[0];
                            System.out.println(args);
                            System.out.print(newOwner);
                            if (newOwner == null) {
                                throw new IllegalArgumentException("owner cannot be null");
                            }
                            if (currentOwner == null || !currentOwner.equals(newOwner) || currentOwner =="") {
                                currentOwner = newOwner;
                                owners.add(newOwner);
                            }
                            Object result = method.invoke(handDown, args);
                            return result;
                    }
                }
            }
        );
    }
    
}
