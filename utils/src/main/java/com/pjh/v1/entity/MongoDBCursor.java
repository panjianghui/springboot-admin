package com.pjh.v1.entity;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;

public class MongoDBCursor extends MongoDBEntity
{
	/**
	 * skip属性
	 */
	private int skip;

	/**
	 * limit属性
	 */
	private int limit = 0;

	/**
	 * 排序属性
	 * 1 为升序 -1 为降序
	 */
	private Map<String, Object> sort = new LinkedHashMap<String, Object>();

	/**
	 * 自定义查询字段属性
	 */
	private Map<String, Object> customFieldMap = new HashMap<String, Object>();

	public int getSkip()
	{
		return skip;
	}

	public void setSkip(int skip)
	{
		this.skip = skip;
	}

	public int getLimit()
	{
		return limit;
	}

	public void setLimit(int limit)
	{
		this.limit = limit;
	}

	public Map<String, Object> getSort()
	{
		return sort;
	}

	public void setSort(Map<String, Object> sort)
	{
		this.sort = sort;
	}

	public DBObject getSortObject()
	{
		if (this.sort == null)
		{
			return null;
		}
		DBObject dbo = new BasicDBObject();
		for (String k : sort.keySet())
		{
			dbo.put(k, Integer.valueOf(sort.get(k).toString()));
		}
		return dbo;
	}

	public Map<String, Object> getCustomFieldMap()
	{
		return customFieldMap;
	}
	/**
	 * 
	 * @param customFieldMap
		add by 潘江辉
	 */
	public void setCustomFieldMap(Map<String, Object> customFieldMap)
	{
		this.customFieldMap = customFieldMap;
	}
}
