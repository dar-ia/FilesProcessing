package model;

public class UserContactMethod {
    private String id;
    private String name;
    private MethodType methodType;

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public MethodType getMethodType() {
        return methodType;
    }

    public String getMethodTypeName() {
        return methodType.getName();
    }
}
