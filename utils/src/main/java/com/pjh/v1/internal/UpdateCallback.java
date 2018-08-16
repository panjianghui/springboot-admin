package com.pjh.v1.internal;

import org.bson.Document;

public interface UpdateCallback
{
	Document doCallback(Document valueDBObject);
}
