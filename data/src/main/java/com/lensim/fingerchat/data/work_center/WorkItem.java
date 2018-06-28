package com.lensim.fingerchat.data.work_center;


import com.google.gson.annotations.SerializedName;
import java.io.Serializable;

/**
 * date on 2017/12/22
 * author ll147996
 * describe
 */

public class WorkItem implements Serializable {
    /**
     * funcAddress :
     * funcId : 1
     * funcIdx : 1
     * funcLogo : data_center.png
     * funcName : 数据中心
     * funcType : 1
     * funcTypeIdx : 1
     * funcValid : 1
     * typeName : 数据中心
     * typeValid : 1
     */

    private String funcAddress;
    private int funcId;
    private int funcIdx;
    private String funcLogo;
    private String funcName;
    private int funcType;
    private int funcTypeIdx;
    private int funcValid;
    private String typeName;
    private int typeValid;

    public WorkItem() {}

    public WorkItem(String funcAddress, int funcId, int funcIdx, String funcLogo,
        String funcName, int funcType, int funcTypeIdx, int funcValid, String typeName,
        int typeValid) {
        this.funcAddress = funcAddress;
        this.funcId = funcId;
        this.funcIdx = funcIdx;
        this.funcLogo = funcLogo;
        this.funcName = funcName;
        this.funcType = funcType;
        this.funcTypeIdx = funcTypeIdx;
        this.funcValid = funcValid;
        this.typeName = typeName;
        this.typeValid = typeValid;
    }

    public String getFuncAddress() {
        return funcAddress;
    }

    public void setFuncAddress(String funcAddress) {
        this.funcAddress = funcAddress;
    }

    public int getFuncId() {
        return funcId;
    }

    public void setFuncId(int funcId) {
        this.funcId = funcId;
    }

    public int getFuncIdx() {
        return funcIdx;
    }

    public void setFuncIdx(int funcIdx) {
        this.funcIdx = funcIdx;
    }

    public String getFuncLogo() {
        return funcLogo;
    }

    public void setFuncLogo(String funcLogo) {
        this.funcLogo = funcLogo;
    }

    public String getFuncName() {
        return funcName;
    }

    public void setFuncName(String funcName) {
        this.funcName = funcName;
    }

    public int getFuncType() {
        return funcType;
    }

    public void setFuncType(int funcType) {
        this.funcType = funcType;
    }

    public int getFuncTypeIdx() {
        return funcTypeIdx;
    }

    public void setFuncTypeIdx(int funcTypeIdx) {
        this.funcTypeIdx = funcTypeIdx;
    }

    public int getFuncValid() {
        return funcValid;
    }

    public void setFuncValid(int funcValid) {
        this.funcValid = funcValid;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getTypeValid() {
        return typeValid;
    }

    public void setTypeValid(int typeValid) {
        this.typeValid = typeValid;
    }

}
