package com.baidu.smart.ext

import com.baidu.nmp.base.utils.cache.{CacheService, LocalValueFetcher}

/**
 * Created by changhai on 15/3/13.
 */
class NoCacheService extends CacheService {
  override def connect(): Unit = ()

  override def getSafely[T](key: String, callback: LocalValueFetcher[T], expiredTimeInSecond: Int): T = callback.getLocalValue(key)

  override def set(key: String, value: scala.Any): Unit = ()

  override def set(key: String, value: scala.Any, expiredTime: Int): Unit = ()

  override def disconnect(): Unit = ()

  override def get[T](key: String): T = null.asInstanceOf[T]

  override def replace(key: String, value: scala.Any, expiredTimeInSeconds: Int): Unit = ()

  override def delete(key: String): Unit = ()

  override def flushAll(): Unit = ()
}
