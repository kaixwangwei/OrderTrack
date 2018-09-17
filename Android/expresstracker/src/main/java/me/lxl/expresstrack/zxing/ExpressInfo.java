package me.lxl.expresstrack.zxing;

public class ExpressInfo {
    private  Long id;
    private  String code;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getExpressCode() {
        return code;
    }

    public void setExpressCode(String code) {
        this.code = code;
    }


    public ExpressInfo(Long id,String code){
        super();
        this.id=id;
        this.code=code;
    }

    public ExpressInfo(String code){
        super();
        this.code=code;
    }

    public ExpressInfo(){
        super();
    }

    @Override
    public String toString() {
        return "[序号" +id+", 快递单号："+code+" , 余额:]";
    }
}