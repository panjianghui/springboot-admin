package com.pjh.v1.entity;

import com.mongodb.DBCursor;

public interface MongoDBCursorPreparer
{
	DBCursor prepare(DBCursor cursor);
}
