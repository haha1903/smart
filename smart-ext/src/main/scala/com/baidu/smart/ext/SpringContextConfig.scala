package com.scalaone.smart.ext

import java.lang.{Boolean, Double, Float, Long}
import java.util

import com.baidu.fengchao.stargate.assembly.AssemblyConstants
import com.baidu.fengchao.stargate.assembly.model.StarBinding
import com.baidu.fengchao.stargate.assembly.spring.schema.bean._
import com.baidu.fengchao.stargate.common.utils.LocalCacheUtils
import com.baidu.nmp.base.utils.property.{Key, PropertyFileReader}
import com.baidu.nmp.base.utils.{BaseConfig, BaseConfigKey, BaseMessage, BaseMessageKey}
import com.scalaone.smart.core.ConfigSupport
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

import com.scalaone.smart.ext.SpringContextConfig._

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
    // set reference global
    val referenceGlobal = ReferenceGlobalConfig.getInstance
    referenceGlobal.setFilter(getString("stargate.reference.filter"))
    referenceGlobal.setConcurrentTimeout(getInt("stargate.reference.connectionTimeout"))
    referenceGlobal.setCallTimeout(getLong("stargate.reference.callTimeout"))
    referenceGlobal.setConnections(getInt("stargate.reference.connections"))
    referenceGlobal.setLoadBalance(getString("stargate.reference.loadBalance"))
    referenceGlobal.setCluster(getString("stargate.reference.cluster"))
    referenceGlobal.setRetryTimes(getInt("stargate.reference.retryTimes"))
    referenceGlobal.setRetryPeriod(getInt("stargate.reference.retryPeriod"))
    referenceGlobal.setDirectURL(getString("stargate.reference.directURL"))
    referenceGlobal.setPort(getInt("stargate.reference.port"))
    referenceGlobal.setClusterAvailableCheck(getString("stargate.reference.clusterAvailableCheck"))
    referenceGlobal.setStatusExpireTime(getLong("stargate.reference.statusExpireTime"))
    referenceGlobal.setFailedCheck(getInt("stargate.reference.failedCheck"))
    referenceGlobal.setExecutorPoolSize(getLong("stargate.reference.executorPoolSize"))
    referenceGlobal.setExecutorMaxMemorySize(getLong("stargate.reference.executorMaxMemorySize"))
    referenceGlobal.setExecutorChannelMaxMemorySize(getLong("stargate.reference.executorChannelMaxMemorySize"))
    referenceGlobal.setExecutorKeepAliveTime(getLong("stargate.reference.executorKeepAliveTime"))
    val portRange = getString("stargate.reference.portRange")
    val (min, max) = splitPortRange(portRange)
    referenceGlobal.setPortMax(min.toInt)
    referenceGlobal.setPortMin(max.toInt)
    referenceGlobal.setRouter(getString("stargate.reference.router"))
    referenceGlobal.setRouterPriority(getInt("stargate.reference.routerPriority"))
    referenceGlobal.setRouterIdRuleLoc(getString("stargate.reference.routerIdRuleLoc"))
    referenceGlobal.setRouterIdRuleBuild(getString("stargate.reference.routerIdRuleBuild"))
    referenceGlobal.setRourerIdSrcAccess(getString("stargate.reference.rourerIdSrcAccess"))
    referenceGlobal.setRouterIdSrcIndex(getString("stargate.reference.routerIdSrcIndex"))
    referenceGlobal.setGroup(getString("stargate.reference.group"))
    referenceGlobal.setVersion(getString("stargate.reference.version"))
    referenceGlobal.setRegistry(getString("stargate.reference.registry"))

    // set service global
    val serviceGlobal = ServiceGlobalConfig.getInstance
    serviceGlobal.setExecutes(getInt("stargate.service.executes"))
    serviceGlobal.setWeight(getInt("stargate.service.weight"))
    serviceGlobal.setFilter(getString("stargate.service.filter"))
    serviceGlobal.setExecutorPoolSize(getLong("stargate.service.executorPoolSize"))
    serviceGlobal.setExecutorMaxMemorySize(getLong("stargate.service.executorMaxMemorySize"))
    serviceGlobal.setExecutorChannelMaxMemorySize(getLong("stargate.service.executorChannelMaxMemorySize"))
    serviceGlobal.setExecutorKeepAliveTime(getLong("stargate.service.executorKeepAliveTime"))

    serviceGlobal.setRouterClusterTag(getString("stargate.service.routerClusterTag"))
    serviceGlobal.setVersion(getString("stargate.service.version"))
    serviceGlobal.setGroup(getString("stargate.service.group"))
    serviceGlobal.setRegistry(getString("stargate.service.registry"))

    if (hasPath("stargate.service.port")) {
      LocalCacheUtils.set(AssemblyConstants.KEY_PROTOCOL_PORT_DIRECT, getString("port"))
    } else if (hasPath("stargate.service.portRange")) {
      val portRange = getString("stargate.service.portRange")
      val (min, max) = splitPortRange(portRange)
      LocalCacheUtils.set(AssemblyConstants.KEY_PROTOCOL_PORT_MIN, min)
      LocalCacheUtils.set(AssemblyConstants.KEY_PROTOCOL_PORT_MAX, max)
    }

    // registry config
    bean("default") {
      val rc = new RegistryConfig()
      rc.setId(getString("stargate.registry.id"))
      rc.setUsername(getString("stargate.registry.username"))
      rc.setPassword(getString("stargate.registry.password"))
      rc.setAddress(getString("stargate.registry.address"))
      rc.setPath(getString("stargate.registry.path"))
      rc.setBackup(getString("stargate.registry.backup"))
      rc.setConnectionTimeout(getString("stargate.registry.connectionTimeout"))
      rc.setSessionTimeout(getString("stargate.registry.sessionTimeout"))
      rc
    }

    // monitor
    bean() {
      val mb = new MonitorBean()
      mb.setAvailable("false")
    }
  }

  def splitPortRange(portRange: String): (String, String) = {
    val portRangeP = """^([0-9\-]+?)-([0-9\-]+)$""".r
    val portRangeP(min, max) = portRange
    (min, max)
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

  // create stargate service bean
  def service[T](id: String, interfaceName: String, group: String = null, version: String = null, ref: String) = bean(id) {
    val rb = new ServiceBean[T]()
    rb.setBinding(starBinding)
    rb.setId(id)
    rb.setInterfaceName(interfaceName)
    rb.setGroup(group)
    rb.setVersion(version)
    rb.setTargetBeanName(ref)
    rb
  }
}
