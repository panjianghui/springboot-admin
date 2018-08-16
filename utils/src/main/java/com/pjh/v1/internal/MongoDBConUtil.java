package com.pjh.v1.internal;

import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.pjh.v1.entity.MongoDBCursor;
import com.pjh.v1.entity.MongoDBEntity;
import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 数据库连接工具类
 */
public class MongoDBConUtil {
    private final static int mongoport = 1;
    private final static String mongodb = "mongodb";
    private final static String mongourl = "mongourl";
    private final static String mongoname = "mongoname";
    private final static String mongopassword = "mongopassword";

    private MongoDBConUtil() {
    }

    /**
     * 获取对应的col（对应关系型数据库的表）--查询用
     *
     * @param mongoDBCursor
     * @return add by 潘江辉
     */
    public static MongoCollection<Document> getCollection(MongoDBCursor mongoDBCursor) {
        MongoDatabase db = getConnection();
        MongoCollection<Document> collection = db.getCollection(mongoDBCursor.getCollectionName());
        return collection;
    }

    /**
     * 获取对应的col（对应关系型数据库的表）-- 操作用
     *
     * @param mongoDBEntity
     * @return add by 潘江辉
     */
    public static MongoCollection<Document> getCollection(MongoDBEntity mongoDBEntity) {
        MongoDatabase db = getConnection();
        MongoCollection<Document> collection = db.getCollection(mongoDBEntity.getCollectionName());
        return collection;
    }

    /**
     * 需要验证用户名、密码的连接方式 因为线程安全问题，
     * mongodb 3.0之后开始已经打算废弃DB开头的类的使用。使用新的方式
     *
     * @return mongoDatabase
     */
    @SuppressWarnings("resource")
    public static MongoDatabase getConnection() {
        try {
            // 设置MongoDB日志输出级别，一般的info就不用输出了，只输出错误日志
            Logger mongoLogger = Logger.getLogger("org.mongodb.driver");
            mongoLogger.setLevel(Level.SEVERE);
            // 连接到MongoDB服务
            ServerAddress serverAddress = new ServerAddress(mongourl, mongoport);
            List<ServerAddress> serverAddressList = new ArrayList<ServerAddress>();
            serverAddressList.add(serverAddress);

            // 参数分别为用户名、数据库名称、密码
            MongoCredential mongoCredential = MongoCredential.createScramSha1Credential(mongoname, mongodb, mongopassword.toCharArray());
            List<MongoCredential> mongoCredentialList = new ArrayList<MongoCredential>();
            mongoCredentialList.add(mongoCredential);

            // 通过连接认证获取MongoDB连接
            MongoClient mongoClient = new MongoClient(serverAddressList, mongoCredentialList);

            // 连接数据库
            MongoDatabase mongoDatabase = mongoClient.getDatabase(mongodb);

            return mongoDatabase;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
        }
        return null;
    }

    /**
     * 不需要验证用户名、密码的连接方式
     *
     * @return mongoDatabase
     */
    public MongoDatabase getConnectionBasis() {
        try {
            // 连接到MongoDB服务
            MongoClient mongoClient = new MongoClient(mongourl, mongoport);
            MongoDatabase mongoDatabase = mongoClient.getDatabase(mongodb);
            System.out.println("MongoDB连接成功");
            return mongoDatabase;
        } catch (Exception e) {
            System.out.println(e.getClass().getName() + "：" + e.getMessage());
        }
        return null;
    }

}
