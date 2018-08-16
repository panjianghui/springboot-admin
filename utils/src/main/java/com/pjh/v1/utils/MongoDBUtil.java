package com.pjh.v1.utils;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.FindIterable;
import com.mongodb.client.result.UpdateResult;
import com.pjh.v1.entity.MongoDBCursor;
import com.pjh.v1.entity.MongoDBCursorPreparer;
import com.pjh.v1.entity.MongoDBEntity;
import com.pjh.v1.entity.MongoDBUpdate;
import com.pjh.v1.internal.MongoDBConUtil;
import com.pjh.v1.internal.UpdateCallback;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.BasicBSONList;
import org.bson.types.ObjectId;

import java.util.*;

/**
 * mongodb基本语法请参考官方文档
 * https://docs.mongodb.com/manual/reference/
 * add by 潘江辉
 */
public class MongoDBUtil {

    /**
     * 按主键查询单个实体
     *
     * @param id            主键
     * @param mongoDBCursor 查询实体
     * @return DBObject
     */
    public static FindIterable<Document> findById(MongoDBCursor mongoDBCursor, String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_id", new ObjectId(id));
        mongoDBCursor.setFieldMap(map);
        return findOne(mongoDBCursor);
    }

    /**
     * 按条件查询单个
     *
     * @param mongoDBCursor 查询实体
     * @return DBObject
     */
    public static FindIterable<Document> findOne(MongoDBCursor mongoDBCursor) {
        Document dbObject = getMapped(mongoDBCursor.getFieldMap());

        return MongoDBConUtil.getCollection(mongoDBCursor).find(dbObject);
    }

    /**
     * 查询全部
     *
     * @param mongoDBCursor 查询实体
     */
    public static ArrayList<Map<String, Object>> findAll(MongoDBCursor mongoDBCursor) {
        mongoDBCursor.setFieldMap(new HashMap<String, Object>());
        return find(mongoDBCursor);
    }

    /**
     * 按条件查询 支持skip，limit,sort
     *
     * @param mongoDBCursor 查询实体
     */
    public static ArrayList<Map<String, Object>> find(MongoDBCursor mongoDBCursor) {
        Document dbObject = getMapped(mongoDBCursor.getFieldMap());
        Document customField = null;
        // 自定义查询字段
        if (mongoDBCursor.getCustomFieldMap() != null && mongoDBCursor.getCustomFieldMap().size() > 0) {
            customField = new Document();
            for (Map.Entry<String, Object> field : mongoDBCursor.getCustomFieldMap().entrySet()) {
                customField.append(field.getKey(), field.getValue());
            }
        }
        ArrayList<Map<String, Object>> list = new ArrayList<>();
        FindIterable<Document> documents = find(mongoDBCursor, dbObject, customField);
        for (Document d : documents) {
            Map<String, Object> map = new HashMap<>();
            for (String key : d.keySet()) {
                map.put(key, d.get(key));
            }
            list.add(map);
        }

        return list;
    }

    /**
     * 查询（私有方法,检查是否含有skip，limit，sort）
     *
     * @param dbObject      查询条件
     * @param mongoDBCursor 查询实体
     */
    private static FindIterable<Document> find(final MongoDBCursor mongoDBCursor, Document dbObject, Document customField) {
        MongoDBCursorPreparer cursorPreparer = mongoDBCursor == null ? null : new MongoDBCursorPreparer() {
            public DBCursor prepare(DBCursor dbCursor) {
                if (mongoDBCursor.getLimit() <= 0 && mongoDBCursor.getSkip() <= 0 && mongoDBCursor.getSortObject() == null) {
                    return dbCursor;
                }
                DBCursor cursorToUse = dbCursor;
                if (mongoDBCursor.getSkip() > 0) {
                    cursorToUse = cursorToUse.skip(mongoDBCursor.getSkip());
                }
                if (mongoDBCursor.getLimit() > 0) {
                    cursorToUse = cursorToUse.limit(mongoDBCursor.getLimit());
                }
                if (mongoDBCursor.getSortObject() != null) {
                    cursorToUse = cursorToUse.sort(mongoDBCursor.getSortObject());
                }
                return cursorToUse;
            }
        };
        return find(mongoDBCursor, dbObject, cursorPreparer, customField);
    }

    /**
     * 查询（私有方法，真正的查询操作）
     *
     * @param query          查询条件
     * @param mongoDBCursor  查询实体
     * @param cursorPreparer 查询转换接口
     */
    private static FindIterable<Document> find(MongoDBCursor mongoDBCursor, Document query, MongoDBCursorPreparer cursorPreparer, Document customField) {
        FindIterable<Document> iterable = null;
        if (customField == null) {
            iterable = MongoDBConUtil.getCollection(mongoDBCursor).find(query);
        } else {
            // TODO
            iterable = MongoDBConUtil.getCollection(mongoDBCursor).find(query).projection(customField);

//			iterable = MongoDBConUtil.getCollection(mongoDBCursor).find(eq("title", "MongoDB"));
        }
        if (mongoDBCursor.getSkip() > 0) {
            iterable.skip(mongoDBCursor.getSkip());
        }
        if (mongoDBCursor.getLimit() > 0) {
            iterable.limit(mongoDBCursor.getLimit());
        }
        if (mongoDBCursor.getSortObject() != null) {
            iterable.sort((Bson) mongoDBCursor.getSortObject());
        }
        return iterable;
    }

    /**
     * Count查询
     *
     * @param mongoDBCursor 查询实体
     * @return 总数
     */
    public static long count(MongoDBCursor mongoDBCursor) {
        Document dbObject = getMapped(mongoDBCursor.getFieldMap());
        return MongoDBConUtil.getCollection(mongoDBCursor).count(dbObject);
    }

    /**
     * 把参数Map转换DBObject
     *
     * @param map 查询条件
     * @return DBObject
     */
    private static Document getMapped(Map<String, Object> map) {
        Document dbObject = new Document();
        if (map == null) {
            return dbObject;
        }
        Iterator<Map.Entry<String, Object>> iterable = map.entrySet().iterator();
        while (iterable.hasNext()) {
            Map.Entry<String, Object> entry = iterable.next();
            Object value = entry.getValue();
            String key = entry.getKey();
            if (key.startsWith("$") && value instanceof Map) {
                BasicBSONList basicBSONList = new BasicBSONList();
                Map<String, Object> conditionsMap = ((Map) value);
                for (String k : conditionsMap.keySet()) {
                    Object conditionsValue = conditionsMap.get(k);
                    if (conditionsValue instanceof Collection) {
                        conditionsValue = convertArray(conditionsValue);
                    }
                    DBObject dbObject2 = new BasicDBObject(k, conditionsValue);
                    basicBSONList.add(dbObject2);
                }
                value = basicBSONList;
            } else if (value instanceof Collection) {
                value = convertArray(value);
            } else if (!key.startsWith("$") && value instanceof Map) {
                value = getMapped(((Map) value));
            }
            dbObject.put(key, value);
        }
        return dbObject;
    }

    /**
     * 转换成Object[]
     *
     * @param value 待转换实体
     * @return Object[]
     */
    private static Object[] convertArray(Object value) {
        Object[] values = ((Collection) value).toArray();
        return values;
    }

    /**
     * 添加操作
     *
     * @param mongoDBEntity 实体
     */
    public static void add(MongoDBEntity mongoDBEntity) {
        Document dbObject = new Document(mongoDBEntity.getFieldMap());
        MongoDBConUtil.getCollection(mongoDBEntity).insertOne(dbObject);
    }

    /**
     * 批量处理添加操作
     *
     * @param list          批量字段数据
     * @param mongoDBEntity 实体
     */
    public static void add(MongoDBEntity mongoDBEntity, List<Map<String, Object>> list) {
        for (Map<String, Object> map : list) {
            mongoDBEntity.setFieldMap(map);
            add(mongoDBEntity);
        }
    }

    /**
     * 删除操作
     *
     * @param mongoDBEntity 实体
     */
    public static void delete(MongoDBEntity mongoDBEntity) {
        Document dbObject = new Document(mongoDBEntity.getFieldMap());
        MongoDBConUtil.getCollection(mongoDBEntity).deleteOne(dbObject);
    }

    /**
     * 删除操作,根据主键
     *
     * @param id            主键
     * @param mongoDBEntity 实体
     */
    public static void delete(MongoDBEntity mongoDBEntity, String id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("_id", new ObjectId(id));
        mongoDBEntity.setFieldMap(map);
        delete(mongoDBEntity);
    }

    /**
     * 删除全部
     *
     * @param mongoDBEntity 实体
     */
    public static void deleteAll(MongoDBEntity mongoDBEntity) {
        MongoDBConUtil.getCollection(mongoDBEntity).drop();
    }

    /**
     * 修改操作 会用一个新文档替换现有文档,文档key结构会发生改变
     * 比如原文档{"_id":"123","name":"zhangsan","age":12}当根据_id修改age
     * value为{"age":12}新建的文档name值会没有,结构发生了改变
     *
     * @param mongoDBUpdate 更新实体
     */
    public static UpdateResult update(MongoDBUpdate mongoDBUpdate) {
        return executeUpdate(mongoDBUpdate, new UpdateCallback() {
            public Document doCallback(Document valueDBObject) {
                return valueDBObject;
            }
        });
    }

    /**
     * 修改操作,使用$set修改器 用来指定一个键值,如果键不存在,则自动创建,会更新原来文档, 不会生成新的, 结构不会发生改变
     *
     * @param mongoDBUpdate 更新实体
     */
    public static UpdateResult updateSet(MongoDBUpdate mongoDBUpdate) {
        return executeUpdate(mongoDBUpdate, new UpdateCallback() {
            public Document doCallback(Document valueDBObject) {
                return new Document("$set", valueDBObject);
            }
        });
    }

    /**
     * 修改操作,使用$inc修改器 修改器键的值必须为数字 如果键存在增加或减少键的值, 如果不存在创建键
     *
     * @param mongoDBUpdate 更新实体
     */
    public static UpdateResult updateInc(MongoDBUpdate mongoDBUpdate) {
        return executeUpdate(mongoDBUpdate, new UpdateCallback() {
            public Document doCallback(Document valueDBObject) {
                return new Document("$inc", valueDBObject);
            }
        });
    }

    /**
     * 修改(私有方法)
     *
     * @param mongoDBUpdate  更新实体
     * @param updateCallback 更新回调
     */
    private static UpdateResult executeUpdate(MongoDBUpdate mongoDBUpdate, UpdateCallback updateCallback) {
        Document whereDBObject = new Document(mongoDBUpdate.getWhereMap());
        Document valueDBObject = new Document(mongoDBUpdate.getValueMap());
        valueDBObject = updateCallback.doCallback(valueDBObject);
        return MongoDBConUtil.getCollection(mongoDBUpdate).updateOne(whereDBObject, valueDBObject);
    }

    public static void main(String[] args) {
        try {
            MongoDBCursor mongoDBCursor = new MongoDBCursor();
            mongoDBCursor.setCollectionName("test"); // 赋值集合名
            // 封装查询条件
            Map<String, Object> fieldMap = new HashMap<String, Object>();
            Map<String, Object> customFieldMap = new HashMap<String, Object>();
            // 且条件集合
            Map<String, Object> andConditionMap = new HashMap<String, Object>();
//            andConditionMap.put("title", "title1");
//            andConditionMap.put("description", "123");
//            andConditionMap.put("likes", new BasicDBObject("$lte", 100));
            // 自定义返回映射集合 1-展示
            customFieldMap.put("likes", "1");
            customFieldMap.put("description", "1");
            // 条件集合
            fieldMap.put("likes", new BasicDBObject("$lte", 100));
//           fieldMap.put("$and", andConditionMap);
//			 fieldMap.put("by","m");
            mongoDBCursor.setFieldMap(fieldMap);
            mongoDBCursor.setCustomFieldMap(customFieldMap);
            // 赋值skip
//            mongoDBCursor.setSkip(1);
            // 赋值limit
//            mongoDBCursor.setLimit(1);
            // 封装Sort
            Map<String, Object> sortMap = new LinkedHashMap<String,
                    Object>();
            sortMap.put("likes", -1);
            mongoDBCursor.setSort(sortMap);
            // 插入新记录
            // MongoDBUtil.add(mongoDBCursor);
            // 查询
            ArrayList<Map<String, Object>> result = MongoDBUtil.find(mongoDBCursor);
            System.out.println("result="+result.toString());
            // DBObject result1 = MongoDBUtil.findOne(mongoDBCursor);
            // System.out.println(result1);
            // List<DBObject> result = MongoDBUtil.findAll(mongoDBCursor);
//            for (Document dbObject : result) {
//                for (String key : dbObject.keySet()) {
//                    System.out.println("key=" + key + ";  value=" + dbObject.get(key));
//                }
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


