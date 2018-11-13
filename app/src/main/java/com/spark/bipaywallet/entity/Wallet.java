package com.spark.bipaywallet.entity;


import org.litepal.crud.DataSupport;

import java.io.Serializable;

/**
 * 钱包
 */
public class Wallet extends DataSupport implements Serializable {
//    LitePal的数据类型支持int、short、long、float、double、boolean、String、Date
//    LitePal修改表字段，添加删除表等需要修改<version value="1" />版本号
//    使用需继承DataSupport

//存
//    if (Wallet.save()) {
//        Toast.makeText(context, "存储成功", Toast.LENGTH_SHORT).show();
//    } else {
//        Toast.makeText(context, "存储失败", Toast.LENGTH_SHORT).show();
//    }

//    DataSupport.saveAll(WalletList);

//修改
//    ContentValues values = new ContentValues();
//    values.put("title","今日iPhone6发布");
//    第一个参数是Class，传入我们要修改的那个类的Class就好，第二个参数是ContentValues对象，这三个参数是一个指定的id，表示我们要修改哪一行数据
//    DataSupport.update(Wallet.class,values,2);修改一条
//    DataSupport.updateAll(News.class, values, "title = ? and commentcount > ?", "今日iPhone6发布", "0");修改对应条件的数据
//    DataSupport.updateAll(News.class, values);修改所有

//    News updateNews = new News();
//    updateNews.setTitle("今日iPhone6发布");
//    updateNews.update(2);修改id为2的记录

//删除
//    DataSupport.delete(News.class, 2);
//    DataSupport.deleteAll(News.class, "title = ? and commentcount = ?", "今日iPhone6发布", "0");
//    DataSupport.deleteAll(News.class);

//查询
//    News news = DataSupport.find(News.class, 1);
//    News firstNews = DataSupport.findFirst(News.class);获取第一条数据
//    News lastNews = DataSupport.findLast(News.class);获取最后一条数据
//    List<News> newsList = DataSupport.findAll(News.class, 1, 3, 5, 7);根据id
//    List<News> allNews = DataSupport.findAll(News.class);
//    List<News> newsList = DataSupport.where("commentcount > ?", "0").find(News.class);根据条件
//    List<News> newsList = DataSupport.select("title", "content").where("commentcount > ?", "0").find(News.class);

//List<News> newsList = DataSupport.select("title", "content").where("commentcount > ?", "0")
// .order("publishdate desc").find(News.class);asc表示正序排序，desc表示倒序排序

//    List<News> newsList = DataSupport.select("title", "content")
//            .where("commentcount > ?", "0")
//            .order("publishdate desc").limit(10).find(News.class);查询前10条数据

//    List<News> newsList = DataSupport.select("title", "content")
//            .where("commentcount > ?", "0")
//            .order("publishdate desc").limit(10).offset(10)
//            .find(News.class);分页展示，偏移10条

    private String name;//钱包名称
    private String encrypMasterKey;//加密主私钥（根据密码进行加密）
    private String tip;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEncrypMasterKey() {
        return encrypMasterKey;
    }

    public void setEncrypMasterKey(String encrypMasterKey) {
        this.encrypMasterKey = encrypMasterKey;
    }

    public String getTip() {
        return tip;
    }

    public void setTip(String tip) {
        this.tip = tip;
    }
}
