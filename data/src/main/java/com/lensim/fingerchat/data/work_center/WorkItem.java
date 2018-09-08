package com.lensim.fingerchat.data.work_center;


import java.io.Serializable;

/**
 * date on 2017/12/22
 * author ll147996
 * describe
 */

public class WorkItem implements Serializable {
    /**
     * funcAddress :
     * funcFlag : 0
     * funcId : 10
     * funcIdx : 1
     * funcLogo : my_express_disabled.png
     * funcName : 我的快递
     * funcType : 3
     * funcTypeIdx : 3
     * funcValid : 1
     * hasNav : 1
     * hasPwd : 0
     * isBase : 0
     * isWeb : 0
     * tokenSys : 0
     * typeName : 行政
     */

    private String funcAddress;
    private int funcFlag;
    private String funcId;
    private int funcIdx;
    private String funcLogo;
    private String funcName;
    private int funcType;
    private int funcTypeIdx;
    private int funcValid;
    private int hasNav;
    private int hasPwd;
    private int isBase;
    private int isWeb;
    private String tokenSys;
    private String typeName;
    private String nodePath;

    public WorkItem(String funcAddress, int funcFlag, String funcId, int funcIdx, String funcLogo, String funcName, int funcType, int funcTypeIdx, int funcValid, int hasNav, int hasPwd, int isBase, int isWeb, String tokenSys, String typeName, String nodePath) {
        this.funcAddress = funcAddress;
        this.funcFlag = funcFlag;
        this.funcId = funcId;
        this.funcIdx = funcIdx;
        this.funcLogo = funcLogo;
        this.funcName = funcName;
        this.funcType = funcType;
        this.funcTypeIdx = funcTypeIdx;
        this.funcValid = funcValid;
        this.hasNav = hasNav;
        this.hasPwd = hasPwd;
        this.isBase = isBase;
        this.isWeb = isWeb;
        this.tokenSys = tokenSys;
        this.typeName = typeName;
        this.nodePath = nodePath;
    }

    public String getFuncAddress() {
        return funcAddress;
    }

    public void setFuncAddress(String funcAddress) {
        this.funcAddress = funcAddress;
    }

    public int getFuncFlag() {
        return funcFlag;
    }

    public void setFuncFlag(int funcFlag) {
        this.funcFlag = funcFlag;
    }

    public String getFuncId() {
        return funcId;
    }

    public void setFuncId(String funcId) {
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

    public int getHasNav() {
        return hasNav;
    }

    public void setHasNav(int hasNav) {
        this.hasNav = hasNav;
    }

    public int getHasPwd() {
        return hasPwd;
    }

    public void setHasPwd(int hasPwd) {
        this.hasPwd = hasPwd;
    }

    public int getIsBase() {
        return isBase;
    }

    public void setIsBase(int isBase) {
        this.isBase = isBase;
    }

    public int getIsWeb() {
        return isWeb;
    }

    public void setIsWeb(int isWeb) {
        this.isWeb = isWeb;
    }

    public String getTokenSys() {
        return tokenSys;
    }

    public void setTokenSys(String tokenSys) {
        this.tokenSys = tokenSys;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getNodePath() {
        return nodePath;
    }

    public void setNodePath(String nodePath) {
        this.nodePath = nodePath;
    }
}
