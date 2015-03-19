package com.baidu.smart.ext

import java.lang.{Boolean, Double, Float, Long}
import java.util

import com.baidu.fengchao.stargate.assembly.model.StarBinding
import com.baidu.fengchao.stargate.assembly.spring.schema.bean.{MonitorBean, ReferenceBean, ReferenceGlobalConfig, RegistryConfig}
import com.baidu.nmp.base.utils.property.{Key, PropertyFileReader}
import com.baidu.nmp.base.utils.{BaseConfig, BaseConfigKey, BaseMessage, BaseMessageKey}
import com.baidu.smart.core.ConfigSupport
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer
import org.springframework.core.env.{MutablePropertySources, PropertySource, StandardEnvironment}
import org.springframework.scala.context.function.FunctionalConfiguration

import scala.collection.JavaConversions._
import scala.reflect.{ClassTag, _}

/**
 * Created by changhai on 15/3/9.
 */
trait HoconConfig extends ConfigSupport {
  def hoconConfig[C: ClassTag, K <: Key] = {
    def get[T](key: String, f: String => T) = getOr(key, f, f(s"$key._"))

    class HoconPropertyReader[K <: Key] extends PropertyFileReader[K]("base-config") {
      override def getString(key: K) = get(key.key, cfg.getString)

      override def getInt(key: K): Int = get(key.key, cfg.getInt)

      override def getBoolean(key: K): Boolean = get(key.key, cfg.getBoolean)

      override def getLong(key: K) = get(key.key, cfg.getLong)

      override def getFloat(key: K) = get(key.key, k => cfg.getDouble(k).toFloat)

      override def getDouble(key: K) = get(key.key, cfg.getDouble)

      override def getStringList(key: K): util.List[String] = get(key.key, cfg.getStringList)

      override def getIntegerList(key: K): util.List[Integer] = get(key.key, cfg.getIntList)

      override def getLongList(key: K): util.List[Long] = get(key.key, cfg.getLongList)

      override def getFloatList(key: K): util.List[Float] = get(key.key, k => cfg.getDoubleList(k).map(d => float2Float(d.toFloat)))

      override def getDoubleList(key: K): util.List[Double] = get(key.key, cfg.getDoubleList)

      override def getString(key: K, args: AnyRef*): String = (get(key.key, cfg.getString) /: args.indices) { case (v, i) => v.replace(s"{$i}", s"%${i}$$s") }.format(args: _*)
    }

    val ct = classTag[C]
    val c = ct.runtimeClass
    val f = c.getDeclaredField("instance")
    f.setAccessible(true)
    f.set(null, new HoconPropertyReader[K])
  }
}

object SpringContextConfig extends HoconConfig {
  val starBinding = {
    new StarBinding(new MockElement)
  }
  // delegate spring property source to application.conf
  val source = new PropertySource[String]("conf") {
    override def getProperty(name: String): AnyRef = {
      getString(name)
    }
  }

  def init() = {
    hoconConfig[BaseConfig, BaseConfigKey]
    hoconConfig[BaseMessage, BaseMessageKey]
  }

  init()
}

import com.baidu.smart.ext.SpringContextConfig._

abstract class SpringContextConfig extends FunctionalConfiguration with HoconConfig {
  // delegate BaseConfig to application.conf
  // register nmp context listener
  bean() {
    new NmpContextListener
  }
  // register property source
  bean() {
    val p = new PropertySourcesPlaceholderConfigurer
    val ps = new MutablePropertySources
    ps.addLast(source)
    p.setPropertySources(ps)
    p
  }

  // set environment
  override protected def environment = {
    new StandardEnvironment() {
      override def customizePropertySources(propertySources: MutablePropertySources) = {
        super.customizePropertySources(propertySources)
        propertySources.addLast(source)
      }
    }
  }

  // stargate global set, registry config, monitor
  def stargate() = {
    val global = ReferenceGlobalConfig.getInstance
    global.setFilter(getStringOr("stargate.filter", null))
    global.setConcurrentTimeout(getIntOr("stargate.connectionTimeout", 0))
    global.setCallTimeout(getLongOr("stargate.callTimeout", 0))
    global.setConnections(getIntOr("stargate.connections", 0))
    global.setLoadBalance(getStringOr("stargate.loadBalance", null))
    global.setCluster(getStringOr("stargate.cluster", null))
    global.setRetryTimes(getIntOr("stargate.retryTimes", 0))
    global.setRetryPeriod(getIntOr("stargate.retryPeriod", 0))
    global.setDirectURL(getStringOr("stargate.directURL", null))
    global.setPort(getIntOr("stargate.port", -1))
    global.setClusterAvailableCheck(getStringOr("stargate.clusterAvailableCheck", null))
    global.setStatusExpireTime(getLongOr("stargate.statusExpireTime", 0))
    global.setFailedCheck(getIntOr("stargate.failedCheck", 0))
    global.setExecutorPoolSize(getLongOr("stargate.executorPoolSize", 0))
    global.setExecutorMaxMemorySize(getLongOr("stargate.executorMaxMemorySize", 0))
    global.setExecutorChannelMaxMemorySize(getLongOr("stargate.executorChannelMaxMemorySize", 0))
    global.setExecutorKeepAliveTime(getLongOr("stargate.executorKeepAliveTime", 0))
    val portRange = getStringOr("starget.portRange", "-1--1")
    val portRangeP = """^([0-9\-]+?)-([0-9\-]+)$""".r
    val portRangeP(min, max) = portRange
    global.setPortMax(min.toInt)
    global.setPortMin(max.toInt)
    global.setRouter(getStringOr("stargate.router", null))
    global.setRouterPriority(getIntOr("stargate.routerPriority", 0))
    global.setRouterIdRuleLoc(getStringOr("stargate.routerIdRuleLoc", null))
    global.setRouterIdRuleBuild(getStringOr("stargate.routerIdRuleBuild", null))
    global.setRourerIdSrcAccess(getStringOr("stargate.rourerIdSrcAccess", null))
    global.setRouterIdSrcIndex(getStringOr("stargate.routerIdSrcIndex", null))
    global.setGroup(getStringOr("stargate.group", null))
    global.setVersion(getStringOr("stargate.version", null))
    global.setRegistry(getStringOr("stargate.registry", null))

    // registry config
    bean("default") {
      val rc = new RegistryConfig()
      rc.setId(getStringOr("stargate.registry.id", "default"))
      rc.setUsername(getStringOr("stargate.registry.username", null))
      rc.setPassword(getStringOr("stargate.registry.password", null))
      rc.setAddress(getStringOr("stargate.registry.address", null))
      rc.setPath(getStringOr("stargate.registry.path", null))
      rc.setBackup(getStringOr("stargate.registry.backup", null))
      rc.setConnectionTimeout(getStringOr("stargate.registry.connectionTimeout", null))
      rc.setSessionTimeout(getStringOr("stargate.registry.sessionTimeout", null))
      rc
    }

    // monitor
    bean() {
      val mb = new MonitorBean()
      mb.setAvailable("false")
    }
  }

  // create stargate reference bean
  def reference[T, S <: T](id: String, interfaceName: String, group: String = null, version: String = null) = bean(id) {
    val rb = new ReferenceBean[T, S]()
    rb.setBinding(starBinding)
    rb.setId(id)
    rb.setInterfaceName(interfaceName)
    rb.setGroup(group)
    rb.setVersion(version)
    rb
  }
}
