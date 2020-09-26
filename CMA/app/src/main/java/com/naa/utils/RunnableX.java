package com.naa.utils;

/**
 * Created by rkrzmail on 17/10/2017.
 */
public class RunnableX implements Runnable {
    private Object[] objects;
    public RunnableX(){}
    public RunnableX(Object...objects){
        this.objects = objects;
    }
    public void run() {
        run(this.objects);
    }
    public void run(Object...objects) {
    }
    public void runOnUI() {
        runOnUI(this.objects);
    }
    public void runOnUI(Object...objects) {
    }
    public void setObjects(Object...objects){
        this.objects = objects;
    }
    public Object[] getObjects(){
        return this.objects;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
    private String message;
    public Object getResult() {
        return result;
    }
    public Object getResultasString() {
        return String.valueOf(result);
    }
    public void setResult(Object result) {
        this.result = result;
    }

    private Object result;

    private Object[] internalobjects;
    public void setInternalObjects(Object...objects){
        this.internalobjects = objects;
    }
    public Object[] getInternalObjects(){
        return this.internalobjects;
    }
}
